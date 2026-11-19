package com.arcanum.item;

import com.arcanum.mana.ManaManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** A staff / wand: right-click spends mana and casts a spell. */
public class SpellItem extends Item {
    @FunctionalInterface
    public interface Spell {
        boolean cast(ServerWorld world, ServerPlayerEntity player, ItemStack stack);
    }

    private final int manaCost;
    private final int cooldown;
    private final Spell spell;

    public SpellItem(Settings settings, int manaCost, int cooldown, Spell spell) {
        super(settings);
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.spell = spell;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(stack, true);
        }
        if (!(user instanceof ServerPlayerEntity player) || !(world instanceof ServerWorld serverWorld)) {
            return TypedActionResult.pass(stack);
        }
        if (!ManaManager.consume(player, manaCost)) {
            player.sendMessage(Text.translatable("message.arcanum.no_mana").formatted(Formatting.RED), true);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.6F, 1.4F);
            return TypedActionResult.fail(stack);
        }
        if (!spell.cast(serverWorld, player, stack)) {
            ManaManager.add(player, manaCost);
            return TypedActionResult.fail(stack);
        }
        player.getItemCooldownManager().set(this, cooldown);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(stack, false);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.arcanum.mana_cost", manaCost).formatted(Formatting.AQUA));
        tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip").formatted(Formatting.GRAY));
    }
}
