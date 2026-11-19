package com.arcanum.item;

import com.arcanum.mana.ManaManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Magic food: normal food effects plus instant mana. */
public class ManaFoodItem extends Item {
    private final int mana;

    public ManaFoodItem(Settings settings, int mana) {
        super(settings);
        this.mana = mana;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ManaManager.add(player, mana);
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.arcanum.mana_gain", mana).formatted(Formatting.AQUA));
    }
}
