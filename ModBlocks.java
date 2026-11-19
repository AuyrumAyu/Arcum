package com.arcanum.registry;

import com.arcanum.Arcanum;
import com.arcanum.block.ManaFontBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public final class ModBlocks {
    public static final Block MANA_CRYSTAL_ORE = registerBlock("mana_crystal_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.DIAMOND_ORE).strength(3.5F, 3.0F).requiresTool().luminance(state -> 5)));

    public static final Block DEEPSLATE_MANA_CRYSTAL_ORE = registerBlock("deepslate_mana_crystal_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_DIAMOND_ORE).strength(4.5F, 3.0F).requiresTool().luminance(state -> 5)));

    public static final Block MANA_CRYSTAL_BLOCK = registerBlock("mana_crystal_block",
            new Block(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).strength(3.0F, 6.0F).requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK).luminance(state -> 7)));

    public static final Block ARCANE_BRICKS = registerBlock("arcane_bricks",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).strength(2.5F, 6.0F).requiresTool()));

    public static final Block ARCANE_LANTERN = registerBlock("arcane_lantern",
            new Block(FabricBlockSettings.copyOf(Blocks.GLOWSTONE).strength(1.0F).luminance(state -> 15)
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block MANA_FONT = registerBlock("mana_font",
            new ManaFontBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).strength(4.0F, 20.0F).requiresTool().luminance(state -> 10)));

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registries.ITEM, Arcanum.id(name), new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, Arcanum.id(name), block);
    }

    public static void register() {
        Arcanum.LOGGER.debug("Registering Arcanum blocks");
    }

    private ModBlocks() {
    }
}
