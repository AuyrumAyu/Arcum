package com.arcanum.client;

import com.arcanum.mana.ManaClientData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Draws the glowing mana bar above the armor row. */
public final class ManaHud {
    private static float shown = -1.0F;

    private ManaHud() {
    }

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> render(context));
    }

    private static void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden || mc.player.isSpectator()) {
            return;
        }
        float max = Math.max(1.0F, ManaClientData.max);
        float mana = Math.min(ManaClientData.mana, max);
        if (shown < 0.0F) {
            shown = mana;
        }
        shown += (mana - shown) * 0.2F;

        int width = 81;
        int height = 9;
        int x = mc.getWindow().getScaledWidth() / 2 - 91;
        boolean survival = mc.interactionManager != null && mc.interactionManager.hasStatusBars();
        int y = mc.getWindow().getScaledHeight() - (survival ? 60 : 39);

        // frame
        context.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF120A24);
        context.fill(x, y, x + width, y + height, 0xFF2A1850);

        // fill
        int filled = Math.round(width * (shown / max));
        if (filled > 0) {
            context.fillGradient(x, y, x + filled, y + height, 0xFF8BE9FF, 0xFF2F6BFF);
            context.fill(x, y, x + filled, y + 1, 0x88FFFFFF);
            // moving shimmer
            int shimmer = (int) ((System.currentTimeMillis() / 40L) % (width + 12)) - 6;
            int sx1 = Math.max(x, x + shimmer);
            int sx2 = Math.min(x + filled, x + shimmer + 4);
            if (sx2 > sx1) {
                context.fill(sx1, y + 1, sx2, y + height - 1, 0x33FFFFFF);
            }
        }

        String label = (int) mana + " / " + (int) max;
        context.drawCenteredTextWithShadow(mc.textRenderer, label, x + width / 2, y + 1, 0xFFFFFF);
    }
}
