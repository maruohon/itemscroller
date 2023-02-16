package fi.dy.masa.itemscroller.gui.widgets;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import malilib.gui.BaseScreen;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.IconWidget;
import malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.render.ItemRenderUtils;
import malilib.render.RenderUtils;
import malilib.render.ShapeRenderUtils;
import malilib.render.TextRenderUtils;
import malilib.util.StringUtils;
import malilib.util.game.wrap.ItemWrap;
import fi.dy.masa.itemscroller.villager.VillagerData;

public class WidgetTradeEntry extends BaseDataListEntryWidget<MerchantRecipe>
{
    private final VillagerData data;

    public WidgetTradeEntry(int x, int y, int width, int height, int listIndex, int originalListIndex,
                            MerchantRecipe entry, VillagerData data)
    {
        super(entry, new DataListEntryWidgetData(x, y, width, height, listIndex, originalListIndex, null));

        this.data = data;
    }

    public void renderAt(int x, int y, float z, ScreenContext ctx, boolean selected)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        boolean hovered = this.isMouseOver(ctx.mouseX, ctx.mouseY);

        DefaultIcons.BUTTON_BACKGROUND.renderFourSplicedAt(x, y, z, width, height, IconWidget.getVariantIndex(true, hovered));

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
        if (this.data.getFavorites().contains(this.getDataListIndex()))
        {
            ItemScrollerIcons.STAR_5.renderAt(x + 80, y + 2, z);
        }

        GlStateManager.disableBlend();

        ItemStack buy1 = recipe.getItemToBuy();
        ItemStack buy2 = recipe.getSecondItemToBuy();
        ItemStack sell = recipe.getItemToSell();

        if (ItemWrap.notEmpty(buy1))
        {
            ItemRenderUtils.renderStackAt(buy1, x +  4, y + 2, z, 1f);
        }

        if (ItemWrap.notEmpty(buy2))
        {
            ItemRenderUtils.renderStackAt(buy2, x + 22, y + 2, z, 1f);
        }

        if (ItemWrap.notEmpty(sell))
        {
            ItemRenderUtils.renderStackAt(sell, x + 60, y + 2, z, 1f);
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

                if (ItemWrap.notEmpty(buy1))
                {
                    ItemRenderUtils.renderStackToolTip(mouseX, mouseY, z, buy1);
                }
            }
            else if (mouseX >= x + 22 && mouseX <= x + 22 + 16)
            {
                ItemStack buy2 = recipe.getSecondItemToBuy();

                if (ItemWrap.notEmpty(buy2))
                {
                    ItemRenderUtils.renderStackToolTip(mouseX, mouseY, z, buy2);
                }
            }
            else if (mouseX >= x + 60 && mouseX <= x + 60 + 16)
            {
                ItemStack sell = recipe.getItemToSell();

                if (ItemWrap.notEmpty(sell))
                {
                    ItemRenderUtils.renderStackToolTip(mouseX, mouseY, z, sell);
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
