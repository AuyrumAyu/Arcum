package com.arcanum.item;

import com.arcanum.client.GuideOpener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** The Arcanum Grimoire: right-click to open the in-game guide. */
public class GuideItem extends Item {
    public GuideItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            GuideOpener.open();
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
