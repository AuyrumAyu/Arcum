package com.arcanum.mana;

import com.arcanum.Arcanum;
import com.arcanum.item.ArcaneArmorItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ManaManager {
    public static final float BASE_MAX = 100.0F;
    public static final float PIECE_MAX = 25.0F;
    public static final float SET_MAX = 50.0F;
    /** Mana regenerated every 10 ticks (0.5 sec). */
    public static final float BASE_REGEN = 1.0F;
    public static final float PIECE_REGEN = 0.5F;

    private ManaManager() {
    }

    private static ManaState state(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        return server.getOverworld().getPersistentStateManager()
                .getOrCreate(ManaState::fromNbt, ManaState::new, Arcanum.MOD_ID + "_mana");
    }

    public static int armorPieces(PlayerEntity player) {
        int count = 0;
        for (ItemStack stack : player.getArmorItems()) {
            if (stack.getItem() instanceof ArcaneArmorItem) {
                count++;
            }
        }
        return count;
    }

    public static float getMax(PlayerEntity player) {
        int pieces = armorPieces(player);
        float max = BASE_MAX + pieces * PIECE_MAX;
        if (pieces == 4) {
            max += SET_MAX;
        }
        return max;
    }

    public static float get(ServerPlayerEntity player) {
        float max = getMax(player);
        float value = state(player).mana.getOrDefault(player.getUuid(), max);
        return Math.min(value, max);
    }

    public static void set(ServerPlayerEntity player, float value) {
        float clamped = Math.max(0.0F, Math.min(value, getMax(player)));
        ManaState state = state(player);
        state.mana.put(player.getUuid(), clamped);
        state.markDirty();
        sync(player);
    }

    public static void add(ServerPlayerEntity player, float amount) {
        set(player, get(player) + amount);
    }

    public static boolean consume(ServerPlayerEntity player, float cost) {
        if (player.isCreative()) {
            return true;
        }
        float current = get(player);
        if (current < cost) {
            return false;
        }
        set(player, current - cost);
        return true;
    }

    public static void refill(ServerPlayerEntity player) {
        set(player, getMax(player));
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(get(player));
        buf.writeFloat(getMax(player));
        ServerPlayNetworking.send(player, ManaNetworking.SYNC_ID, buf);
    }

    /** Called every 10 ticks for every player. */
    public static void tick(ServerPlayerEntity player) {
        int pieces = armorPieces(player);
        float max = getMax(player);
        float current = get(player);

        if (pieces == 4) {
            // Full set bonus: permanent subtle Speed I
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 0, true, false, false));
        }

        if (current < max) {
            float regen = BASE_REGEN + pieces * PIECE_REGEN;
            if (pieces == 4) {
                regen *= 2.0F;
            }
            set(player, current + regen);
        } else {
            sync(player);
        }
    }
}
