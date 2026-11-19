package com.arcanum.registry;

import com.arcanum.Arcanum;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class ModItemGroups {
    public static final ItemGroup ARCANUM = Registry.register(Registries.ITEM_GROUP, Arcanum.id("arcanum"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.arcanum"))
                    .icon(() -> new ItemStack(ModItems.MANA_CRYSTAL))
                    .entries((context, entries) -> {
                        entries.add(ModItems.ARCANE_TOME);
                        // materials
                        entries.add(ModItems.MANA_CRYSTAL);
                        entries.add(ModItems.ARCANE_INGOT);
                        // blocks
                        entries.add(ModBlocks.MANA_CRYSTAL_ORE);
                        entries.add(ModBlocks.DEEPSLATE_MANA_CRYSTAL_ORE);
                        entries.add(ModBlocks.MANA_CRYSTAL_BLOCK);
                        entries.add(ModBlocks.ARCANE_BRICKS);
                        entries.add(ModBlocks.ARCANE_LANTERN);
                        entries.add(ModBlocks.MANA_FONT);
                        // staffs
                        entries.add(ModItems.FIRE_WAND);
                        entries.add(ModItems.STORM_STAFF);
                        entries.add(ModItems.FROST_STAFF);
                        entries.add(ModItems.LIFE_STAFF);
                        entries.add(ModItems.BLINK_STAFF);
                        entries.add(ModItems.RUIN_SCEPTER);
                        // food
                        entries.add(ModItems.ARCANE_APPLE);
                        entries.add(ModItems.STARLIGHT_COOKIE);
                        entries.add(ModItems.LUNAR_PIE);
                        // armor
                        entries.add(ModItems.ARCANE_HELMET);
                        entries.add(ModItems.ARCANE_CHESTPLATE);
                        entries.add(ModItems.ARCANE_LEGGINGS);
                        entries.add(ModItems.ARCANE_BOOTS);
                    })
                    .build());

    public static void register() {
        Arcanum.LOGGER.debug("Registering Arcanum creative tab");
    }

    private ModItemGroups() {
    }
}
