package com.arcanum.client;

import com.arcanum.Arcanum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

/** The in-game Arcanum Grimoire: list of every block/item, description, mana numbers and live crafting recipe. */
public class GuideScreen extends Screen {
    private static final Identifier BG = Arcanum.id("textures/gui/guide_book.png");
    private static final int W = 256;
    private static final int H = 180;
    private static final int PER_PAGE = 7;
    private static final int INK = 0xFF3B2A1A;
    private static final int TITLE = 0xFF4B1F7A;
    private static final int MANA = 0xFF1B6FB5;

    private static int page = 0;
    private static int selected = 0;

    private int ox;
    private int oy;
    private Recipe<?> recipe;

    public GuideScreen() {
        super(Text.translatable("guide.arcanum.title"));
    }

    private static int pageCount() {
        return (GuideEntries.ALL.size() + PER_PAGE - 1) / PER_PAGE;
    }

    @Override
    protected void init() {
        ox = (this.width - W) / 2;
        oy = (this.height - H) / 2;
        recipe = GuideEntries.ALL.get(selected).craftable() ? findRecipe(GuideEntries.ALL.get(selected)) : null;

        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < GuideEntries.ALL.size(); i++) {
            final int index = start + i;
            String name = GuideEntries.ALL.get(index).item().getName().getString();
            name = this.textRenderer.trimToWidth(name, 86);
            ButtonWidget button = ButtonWidget.builder(Text.literal(name), b -> {
                selected = index;
                refresh();
            }).dimensions(ox + 14, oy + 26 + i * 17, 96, 15).build();
            button.active = index != selected;
            this.addDrawableChild(button);
        }

        ButtonWidget prev = ButtonWidget.builder(Text.literal("<"), b -> {
            page = Math.max(0, page - 1);
            refresh();
        }).dimensions(ox + 14, oy + 150, 24, 15).build();
        prev.active = page > 0;
        this.addDrawableChild(prev);

        ButtonWidget next = ButtonWidget.builder(Text.literal(">"), b -> {
            page = Math.min(pageCount() - 1, page + 1);
            refresh();
        }).dimensions(ox + 86, oy + 150, 24, 15).build();
        next.active = page < pageCount() - 1;
        this.addDrawableChild(next);
    }

    private void refresh() {
        this.clearChildren();
        this.init();
    }

    private static Recipe<?> findRecipe(GuideEntries.Entry entry) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            return null;
        }
        for (Recipe<?> candidate : mc.world.getRecipeManager().values()) {
            if (candidate.getType() == RecipeType.CRAFTING
                    && candidate.getOutput(mc.world.getRegistryManager()).isOf(entry.item())) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawTexture(BG, ox, oy, 0, 0, W, H);
        super.render(context, mouseX, mouseY, delta);

        // left page: title + page counter
        context.drawText(this.textRenderer, Text.translatable("guide.arcanum.title"), ox + 14, oy + 10, TITLE, false);
        Text counter = Text.translatable("guide.arcanum.page", page + 1, pageCount());
        context.drawText(this.textRenderer, counter, ox + 62 - this.textRenderer.getWidth(counter) / 2, oy + 154, INK, false);

        drawEntry(context, mouseX, mouseY);
    }

    private void drawEntry(DrawContext context, int mouseX, int mouseY) {
        GuideEntries.Entry entry = GuideEntries.ALL.get(selected);
        ItemStack icon = new ItemStack(entry.item());

        // big icon
        context.getMatrices().push();
        context.getMatrices().translate(ox + 128, oy + 14, 0);
        context.getMatrices().scale(2.0F, 2.0F, 1.0F);
        context.drawItem(icon, 0, 0);
        context.getMatrices().pop();

        // name
        List<OrderedText> nameLines = this.textRenderer.wrapLines(icon.getName(), 80);
        for (int i = 0; i < Math.min(2, nameLines.size()); i++) {
            context.drawText(this.textRenderer, nameLines.get(i), ox + 166, oy + 17 + i * 10, TITLE, false);
        }

        // mana numbers
        int textY = oy + 49;
        if (entry.cost() > 0) {
            context.drawText(this.textRenderer, Text.translatable("guide.arcanum.cost", entry.cost()), ox + 128, textY, MANA, false);
        } else if (entry.gain() > 0) {
            context.drawText(this.textRenderer, Text.translatable("guide.arcanum.gain", entry.gain()), ox + 128, textY, MANA, false);
        }

        // description
        List<OrderedText> desc = this.textRenderer.wrapLines(Text.translatable("guide.arcanum." + entry.id()), 114);
        for (int i = 0; i < Math.min(5, desc.size()); i++) {
            context.drawText(this.textRenderer, desc.get(i), ox + 128, oy + 61 + i * 9, INK, false);
        }

        // recipe / info
        if (recipe == null) {
            List<OrderedText> note = this.textRenderer.wrapLines(Text.translatable("guide.arcanum.no_craft"), 114);
            for (int i = 0; i < Math.min(4, note.size()); i++) {
                context.drawText(this.textRenderer, note.get(i), ox + 128, oy + 114 + i * 9, INK, false);
            }
            return;
        }

        context.drawText(this.textRenderer, Text.translatable("guide.arcanum.crafting"), ox + 128, oy + 109, TITLE, false);
        ItemStack hovered = ItemStack.EMPTY;
        int gx = ox + 132;
        int gy = oy + 121;

        // slot backgrounds
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                slot(context, gx + col * 18, gy + row * 18);
            }
        }

        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        int cols = 3;
        if (recipe instanceof ShapedRecipe shaped) {
            cols = shaped.getWidth();
        }
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack[] options = ingredients.get(i).getMatchingStacks();
            if (options.length == 0) {
                continue;
            }
            ItemStack shown = options[(int) ((Util.getMeasuringTimeMs() / 1000L) % options.length)];
            int x = gx + (i % cols) * 18;
            int y = gy + (i / cols) * 18;
            context.drawItem(shown, x, y);
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                hovered = shown;
            }
        }

        // arrow + result
        context.drawText(this.textRenderer, "\u2192", gx + 60, gy + 22, INK, false);
        int rx = gx + 78;
        int ry = gy + 18;
        slot(context, rx, ry);
        ItemStack result = recipe.getOutput(this.client.world.getRegistryManager()).copy();
        context.drawItem(result, rx, ry);
        context.drawItemInSlot(this.textRenderer, result, rx, ry);
        if (mouseX >= rx && mouseX < rx + 16 && mouseY >= ry && mouseY < ry + 16) {
            hovered = result;
        }

        if (!hovered.isEmpty()) {
            context.drawItemTooltip(this.textRenderer, hovered, mouseX, mouseY);
        }
    }

    private static void slot(DrawContext context, int x, int y) {
        context.fill(x - 1, y - 1, x + 17, y + 17, 0xFF8B7355);
        context.fill(x, y, x + 16, y + 16, 0xFFC9B58C);
    }
}
