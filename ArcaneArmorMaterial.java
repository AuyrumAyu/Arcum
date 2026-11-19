package com.arcanum.item;

import com.arcanum.Arcanum;
import com.arcanum.registry.ModItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ArcaneArmorMaterial implements ArmorMaterial {
    public static final ArcaneArmorMaterial INSTANCE = new ArcaneArmorMaterial();

    @Override
    public int getDurability(ArmorItem.Type type) {
        switch (type) {
            case HELMET:
                return 11 * 38;
            case CHESTPLATE:
                return 16 * 38;
            case LEGGINGS:
                return 15 * 38;
            default:
                return 13 * 38;
        }
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        switch (type) {
            case HELMET:
                return 3;
            case CHESTPLATE:
                return 8;
            case LEGGINGS:
                return 6;
            default:
                return 3;
        }
    }

    @Override
    public int getEnchantability() {
        return 28;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_GOLD;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.ARCANE_INGOT);
    }

    @Override
    public String getName() {
        // texture: assets/arcanum/textures/models/armor/arcane_layer_1.png / _layer_2.png
        return Arcanum.MOD_ID + ":arcane";
    }

    @Override
    public float getToughness() {
        return 3.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.05F;
    }
}
