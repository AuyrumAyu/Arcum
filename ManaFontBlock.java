package com.arcanum.block;

import com.arcanum.mana.ManaManager;
import com.arcanum.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/** Right-click with a Mana Crystal to restore mana instantly. */
public class ManaFontBlock extends Block {
    public static final int MANA_PER_CRYSTAL = 40;

    public ManaFontBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(ModItems.MANA_CRYSTAL)) {
            return ActionResult.PASS;
        }
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && world instanceof ServerWorld serverWorld) {
            ManaManager.add(serverPlayer, MANA_PER_CRYSTAL);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            serverWorld.spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 30, 0.4, 0.5, 0.4, 0.5);
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0F, 1.0F);
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.8F, 1.3F);
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 2; i++) {
            world.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                    (random.nextDouble() - 0.5) * 1.2, random.nextDouble() * 0.6, (random.nextDouble() - 0.5) * 1.2);
        }
    }
}
