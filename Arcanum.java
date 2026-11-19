package com.arcanum;

import com.arcanum.mana.ManaManager;
import com.arcanum.registry.ModBlocks;
import com.arcanum.registry.ModItemGroups;
import com.arcanum.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Arcanum implements ModInitializer {
    public static final String MOD_ID = "arcanum";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModItemGroups.register();

        // Mana crystal ore generation (configured/placed features live in data/arcanum/worldgen)
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("mana_crystal_ore")));

        // Mana regeneration + sync every half second
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 10 == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    ManaManager.tick(player);
                }
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ManaManager.sync(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> ManaManager.refill(newPlayer));

        LOGGER.info("Arcanum loaded. May your mana never run dry.");
    }
}
