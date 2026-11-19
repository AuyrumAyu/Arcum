package com.arcanum.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/** All spell implementations + their costs (shared with the guide book). */
public final class Spells {
    public static final int COST_FLAME = 15;
    public static final int COST_STORM = 35;
    public static final int COST_FROST = 25;
    public static final int COST_LIFE = 30;
    public static final int COST_BLINK = 20;
    public static final int COST_RUIN = 80;

    private Spells() {
    }

    /** Explosive fireball in the look direction. */
    public static boolean flame(ServerWorld world, ServerPlayerEntity player, ItemStack stack) {
        Vec3d look = player.getRotationVec(1.0F);
        FireballEntity fireball = new FireballEntity(world, player, look.x, look.y, look.z, 3);
        fireball.setPosition(
                player.getX() + look.x * 1.5,
                player.getEyeY() - 0.15 + look.y * 1.5,
                player.getZ() + look.z * 1.5);
        world.spawnEntity(fireball);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.8F);
        world.spawnParticles(ParticleTypes.FLAME, player.getX() + look.x, player.getEyeY() + look.y, player.getZ() + look.z,
                12, 0.2, 0.2, 0.2, 0.05);
        return true;
    }

    /** Lightning at the target + 3 more around it, shocks everything nearby. */
    public static boolean storm(ServerWorld world, ServerPlayerEntity player, ItemStack stack) {
        HitResult hit = player.raycast(48.0, 1.0F, false);
        Vec3d target = hit.getPos();
        strike(world, player, target);
        for (int i = 0; i < 3; i++) {
            double ox = (world.random.nextDouble() - 0.5) * 7.0;
            double oz = (world.random.nextDouble() - 0.5) * 7.0;
            strike(world, player, new Vec3d(target.x + ox, target.y, target.z + oz));
        }
        Box area = new Box(target, target).expand(5.0);
        for (LivingEntity victim : world.getEntitiesByClass(LivingEntity.class, area, t -> t != player && t.isAlive())) {
            victim.damage(world.getDamageSources().indirectMagic(player, player), 10.0F);
        }
        return true;
    }

    private static void strike(ServerWorld world, ServerPlayerEntity player, Vec3d pos) {
        LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
        if (bolt == null) {
            return;
        }
        bolt.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
        bolt.setChanneler(player);
        world.spawnEntity(bolt);
    }

    /** Frost nova: damage, heavy slowness and freezing in a 7 block radius. */
    public static boolean frost(ServerWorld world, ServerPlayerEntity player, ItemStack stack) {
        Box area = player.getBoundingBox().expand(7.0, 3.0, 7.0);
        for (LivingEntity victim : world.getEntitiesByClass(LivingEntity.class, area,
                t -> t != player && t.isAlive() && t.squaredDistanceTo(player) <= 49.0)) {
            victim.damage(world.getDamageSources().indirectMagic(player, player), 6.0F);
            victim.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 160, 3));
            victim.setFrozenTicks(victim.getMinFreezeDamageTicks() + 100);
        }
        for (int i = 0; i < 72; i++) {
            double angle = i * (Math.PI * 2.0 / 72.0);
            for (double r = 1.5; r <= 7.0; r += 2.75) {
                world.spawnParticles(ParticleTypes.SNOWFLAKE,
                        player.getX() + Math.cos(angle) * r, player.getY() + 0.3, player.getZ() + Math.sin(angle) * r,
                        1, 0.0, 0.1, 0.0, 0.02);
            }
        }
        world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.5, player.getZ(), 30, 1.5, 0.2, 1.5, 0.05);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS, 1.2F, 0.8F);
        return true;
    }

    /** Heals you and nearby players, adds regeneration and absorption. */
    public static boolean life(ServerWorld world, ServerPlayerEntity player, ItemStack stack) {
        Box area = player.getBoundingBox().expand(6.0);
        for (PlayerEntity ally : world.getEntitiesByClass(PlayerEntity.class, area, t -> t.isAlive())) {
            ally.heal(8.0F);
            ally.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
            ally.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 400, 1));
            world.spawnParticles(ParticleTypes.HEART, ally.getX(), ally.getBodyY(0.8), ally.getZ(), 7, 0.4, 0.4, 0.4, 0.02);
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, ally.getX(), ally.getBodyY(0.5), ally.getZ(), 12, 0.5, 0.6, 0.5, 0.02);
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.5F, 1.2F);
        return true;
    }

    /** Teleports you up to 24 blocks forward. */
    public static boolean blink(ServerWorld world, ServerPlayerEntity player, ItemStack stack) {
        double range = 24.0;
        Vec3d look = player.getRotationVec(1.0F);
        HitResult hit = player.raycast(range, 1.0F, false);
        Vec3d dest;
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            Direction side = blockHit.getSide();
            BlockPos pos = blockHit.getBlockPos().offset(side);
            dest = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        } else {
            dest = player.getEyePos().add(look.multiply(range)).subtract(0.0, player.getStandingEyeHeight(), 0.0);
        }

        double[] yOffsets = {0.0, -1.0, -2.0, 1.0};
        Vec3d finalPos = null;
        for (double dy : yOffsets) {
            Vec3d candidate = dest.add(0.0, dy, 0.0);
            Box moved = player.getBoundingBox().offset(candidate.subtract(player.getPos()));
            if (world.isSpaceEmpty(player, moved)) {
                finalPos = candidate;
                break;
            }
        }
        if (finalPos == null) {
            return false;
        }

        world.spawnParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.4, 0.8, 0.4, 0.3);
        player.teleport(world, finalPos.x, finalPos.y, finalPos.z, player.getYaw(), player.getPitch());
        player.fallDistance = 0.0F;
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, finalPos.x, finalPos.y + 1.0, finalPos.z, 40, 0.4, 0.8, 0.4, 0.1);
        world.playSound(null, finalPos.x, finalPos.y, finalPos.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.1F);
        return true;
    }

    /** The ultimate spell: huge blast, knockback, fire and 4 lightning bolts. */
    public static boolean ruin(ServerWorld world, ServerPlayerEntity player, ItemStack stack) {
        Box area = player.getBoundingBox().expand(10.0);
        for (LivingEntity victim : world.getEntitiesByClass(LivingEntity.class, area,
                t -> t != player && t.isAlive() && t.squaredDistanceTo(player) <= 100.0)) {
            victim.damage(world.getDamageSources().indirectMagic(player, player), 18.0F);
            victim.setOnFireFor(6);
            Vec3d push = victim.getPos().subtract(player.getPos()).multiply(1.0, 0.0, 1.0).normalize()
                    .multiply(1.4).add(0.0, 0.6, 0.0);
            victim.addVelocity(push.x, push.y, push.z);
            victim.velocityModified = true;
        }
        double[][] offsets = {{6, 0}, {-6, 0}, {0, 6}, {0, -6}};
        for (double[] o : offsets) {
            strike(world, player, new Vec3d(player.getX() + o[0], player.getY(), player.getZ() + o[1]));
        }
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY() + 1.0, player.getZ(), 3, 1.5, 0.5, 1.5, 0.0);
        for (int i = 0; i < 90; i++) {
            double angle = i * (Math.PI * 2.0 / 90.0);
            world.spawnParticles(ParticleTypes.END_ROD,
                    player.getX() + Math.cos(angle) * 9.0, player.getY() + 0.5, player.getZ() + Math.sin(angle) * 9.0,
                    2, 0.1, 0.8, 0.1, 0.02);
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0F, 0.7F);
        return true;
    }
}
