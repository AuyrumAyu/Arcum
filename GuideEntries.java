package com.arcanum.client;

import com.arcanum.item.Spells;
import com.arcanum.registry.ModBlocks;
import com.arcanum.registry.ModItems;
import net.minecraft.item.Item;

import java.util.List;

/** Every block and item shown in the Grimoire. Text keys: guide.arcanum.<id> */
public final class GuideEntries {
    public record Entry(Item item, String id, int cost, int gain, boolean craftable) {
    }

    public static final List<Entry> ALL = List.of(
            // blocks
            new Entry(ModBlocks.MANA_CRYSTAL_ORE.asItem(), "mana_crystal_ore", 0, 0, false),
            new Entry(ModBlocks.DEEPSLATE_MANA_CRYSTAL_ORE.asItem(), "deepslate_mana_crystal_ore", 0, 0, false),
            new Entry(ModBlocks.MANA_CRYSTAL_BLOCK.asItem(), "mana_crystal_block", 0, 0, true),
            new Entry(ModBlocks.ARCANE_BRICKS.asItem(), "arcane_bricks", 0, 0, true),
            new Entry(ModBlocks.ARCANE_LANTERN.asItem(), "arcane_lantern", 0, 0, true),
            new Entry(ModBlocks.MANA_FONT.asItem(), "mana_font", 0, 40, true),
            // materials
            new Entry(ModItems.MANA_CRYSTAL, "mana_crystal", 0, 0, false),
            new Entry(ModItems.ARCANE_INGOT, "arcane_ingot", 0, 0, true),
            new Entry(ModItems.ARCANE_TOME, "arcane_tome", 0, 0, true),
            // staffs
            new Entry(ModItems.FIRE_WAND, "fire_wand", Spells.COST_FLAME, 0, true),
            new Entry(ModItems.STORM_STAFF, "storm_staff", Spells.COST_STORM, 0, true),
            new Entry(ModItems.FROST_STAFF, "frost_staff", Spells.COST_FROST, 0, true),
            new Entry(ModItems.LIFE_STAFF, "life_staff", Spells.COST_LIFE, 0, true),
            new Entry(ModItems.BLINK_STAFF, "blink_staff", Spells.COST_BLINK, 0, true),
            new Entry(ModItems.RUIN_SCEPTER, "ruin_scepter", Spells.COST_RUIN, 0, true),
            // food
            new Entry(ModItems.ARCANE_APPLE, "arcane_apple", 0, 40, true),
            new Entry(ModItems.STARLIGHT_COOKIE, "starlight_cookie", 0, 15, true),
            new Entry(ModItems.LUNAR_PIE, "lunar_pie", 0, 80, true),
            // armor
            new Entry(ModItems.ARCANE_HELMET, "arcane_helmet", 0, 0, true),
            new Entry(ModItems.ARCANE_CHESTPLATE, "arcane_chestplate", 0, 0, true),
            new Entry(ModItems.ARCANE_LEGGINGS, "arcane_leggings", 0, 0, true),
            new Entry(ModItems.ARCANE_BOOTS, "arcane_boots", 0, 0, true)
    );

    private GuideEntries() {
    }
}
