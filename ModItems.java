package com.arcanum.registry;

import com.arcanum.Arcanum;
import com.arcanum.item.ArcaneArmorItem;
import com.arcanum.item.GuideItem;
import com.arcanum.item.ManaFoodItem;
import com.arcanum.item.SpellItem;
import com.arcanum.item.Spells;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public final class ModItems {
    // ----- materials -----
    public static final Item MANA_CRYSTAL = register("mana_crystal",
            new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item ARCANE_INGOT = register("arcane_ingot",
            new Item(new FabricItemSettings().rarity(Rarity.RARE)));
    public static final Item ARCANE_TOME = register("arcane_tome",
            new GuideItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

    // ----- staffs -----
    public static final Item FIRE_WAND = register("fire_wand",
            new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), Spells.COST_FLAME, 10, Spells::flame));
    public static final Item STORM_STAFF = register("storm_staff",
            new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), Spells.COST_STORM, 30, Spells::storm));
    public static final Item FROST_STAFF = register("frost_staff",
            new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), Spells.COST_FROST, 20, Spells::frost));
    public static final Item LIFE_STAFF = register("life_staff",
            new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), Spells.COST_LIFE, 40, Spells::life));
    public static final Item BLINK_STAFF = register("blink_staff",
            new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), Spells.COST_BLINK, 20, Spells::blink));
    public static final Item RUIN_SCEPTER = register("ruin_scepter",
            new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC), Spells.COST_RUIN, 80, Spells::ruin));

    // ----- magic food -----
    public static final Item ARCANE_APPLE = register("arcane_apple",
            new ManaFoodItem(new FabricItemSettings().rarity(Rarity.RARE).food(
                    new FoodComponent.Builder().hunger(6).saturationModifier(1.2F).alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1), 1.0F)
                            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 1), 1.0F)
                            .build()), 40));
    public static final Item STARLIGHT_COOKIE = register("starlight_cookie",
            new ManaFoodItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).food(
                    new FoodComponent.Builder().hunger(3).saturationModifier(0.6F).alwaysEdible().snack()
                            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 1), 1.0F)
                            .build()), 15));
    public static final Item LUNAR_PIE = register("lunar_pie",
            new ManaFoodItem(new FabricItemSettings().rarity(Rarity.EPIC).food(
                    new FoodComponent.Builder().hunger(10).saturationModifier(1.6F).alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 1), 1.0F)
                            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), 1.0F)
                            .build()), 80));

    // ----- armor -----
    public static final Item ARCANE_HELMET = register("arcane_helmet",
            new ArcaneArmorItem(ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item ARCANE_CHESTPLATE = register("arcane_chestplate",
            new ArcaneArmorItem(ArmorItem.Type.CHESTPLATE, new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item ARCANE_LEGGINGS = register("arcane_leggings",
            new ArcaneArmorItem(ArmorItem.Type.LEGGINGS, new FabricItemSettings().rarity(Rarity.EPIC)));
    public static final Item ARCANE_BOOTS = register("arcane_boots",
            new ArcaneArmorItem(ArmorItem.Type.BOOTS, new FabricItemSettings().rarity(Rarity.EPIC)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Arcanum.id(name), item);
    }

    public static void register() {
        Arcanum.LOGGER.debug("Registering Arcanum items");
    }

    private ModItems() {
    }
}
