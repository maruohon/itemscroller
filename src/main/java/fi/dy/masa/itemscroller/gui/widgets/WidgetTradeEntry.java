package fi.dy.masa.itemscroller.gui.widgets;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import fi.dy.masa.itemscroller.villager.VillagerData;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.render.ItemRenderUtils;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.TextRenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetTradeEntry extends BaseDataListEntryWidget<MerchantRecipe>
{
    private final VillagerData data;

    public WidgetTradeEntry(int x, int y, int width, int height, int listIndex, int originalListIndex,
                            MerchantRecipe entry, VillagerData data)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry, null);

        this.data = data;
    }

    public void renderAt(int x, int y, float z, ScreenContext ctx, boolean selected)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        boolean hovered = this.isMouseOver(ctx.mouseX, ctx.mouseY);

        DefaultIcons.BUTTON_BACKGROUND.renderFourSplicedAt(x, y, z, width, height, true, hovered);

        if (selected)
        {
            ShapeRenderUtils.renderOutline(x, y, z, width, height, 1, 0xFFFFB000);
        }

        MerchantRecipe recipe = this.getData();
        Icon icon = recipe.isRecipeDisabled() ? ItemScrollerIcons.TRADE_ARROW_LOCKED : ItemScrollerIcons.TRADE_ARROW_AVAILABLE;

        RenderUtils.setupBlend();
        GlStateManager.enableAlpha();

        // Trade arrow
        icon.renderAt(x + 44, y + 5, z);

        // This entry has been favorited
        if (this.data.getFavorites().contains(this.getListIndex()))
        {
            ItemScrollerIcons.STAR_5.renderAt(x + 80, y + 2, z);
        }

        GlStateManager.disableBlend();

        ItemStack buy1 = recipe.getItemToBuy();
        ItemStack buy2 = recipe.getSecondItemToBuy();
        ItemStack sell = recipe.getItemToSell();

        if (buy1.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(buy1, x +  4, y + 2, z, 1f, this.mc);
        }

        if (buy2.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(buy2, x + 22, y + 2, z, 1f, this.mc);
        }

        if (sell.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(sell, x + 60, y + 2, z, 1f, this.mc);
        }
    }

    @Override
    public void postRenderHovered(ScreenContext ctx)
    {
        int x = this.getX();
        int y = this.getY();
        float z = this.getZ() + 1;
        int height = this.getHeight();
        int mouseX = ctx.mouseX;
        int mouseY = ctx.mouseY;

        if (mouseY >= y + 2 && mouseY <= y + height - 2)
        {
            MerchantRecipe recipe = this.getData();

            if (mouseX >= x + 4 && mouseX <= x + 4 + 16)
            {
                ItemStack buy1 = recipe.getItemToBuy();

                if (buy1.isEmpty() == false)
                {
                    ItemRenderUtils.renderStackToolTip(mouseX, mouseY, z, buy1, this.mc);
                }
            }
            else if (mouseX >= x + 22 && mouseX <= x + 22 + 16)
            {
                ItemStack buy2 = recipe.getSecondItemToBuy();

                if (buy2.isEmpty() == false)
                {
                    ItemRenderUtils.renderStackToolTip(mouseX, mouseY, z, buy2, this.mc);
                }
            }
            else if (mouseX >= x + 60 && mouseX <= x + 60 + 16)
            {
                ItemStack sell = recipe.getItemToSell();

                if (sell.isEmpty() == false)
                {
                    ItemRenderUtils.renderStackToolTip(mouseX, mouseY, z, sell, this.mc);
                }
            }

            if (BaseScreen.isAltDown())
            {
                int uses = recipe.getToolUses();
                int max = recipe.getMaxTradeUses();
                String text = StringUtils.translate("itemscroller.label.misc.trade_uses", uses, max);
                TextRenderUtils.renderHoverText(mouseX + 6, mouseY + 18, z, text);
            }
        }
    }
}
