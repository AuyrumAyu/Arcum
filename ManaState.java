package com.arcanum.mana;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Saved with the world: current mana of every player. */
public class ManaState extends PersistentState {
    public final Map<UUID, Float> mana = new HashMap<>();

    public static ManaState fromNbt(NbtCompound nbt) {
        ManaState state = new ManaState();
        NbtCompound players = nbt.getCompound("players");
        for (String key : players.getKeys()) {
            try {
                state.mana.put(UUID.fromString(key), players.getFloat(key));
            } catch (IllegalArgumentException ignored) {
                // skip broken entries
            }
        }
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound players = new NbtCompound();
        for (Map.Entry<UUID, Float> entry : mana.entrySet()) {
            players.putFloat(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("players", players);
        return nbt;
    }
}
