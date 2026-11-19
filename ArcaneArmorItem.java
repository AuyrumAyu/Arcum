package com.arcanum.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArcaneArmorItem extends ArmorItem {
    public ArcaneArmorItem(ArmorItem.Type type, Settings settings) {
        super(ArcaneArmorMaterial.INSTANCE, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.arcanum.armor").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("tooltip.arcanum.set").formatted(Formatting.LIGHT_PURPLE));
    }
}
