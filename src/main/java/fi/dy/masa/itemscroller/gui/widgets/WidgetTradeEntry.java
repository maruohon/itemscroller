package fi.dy.masa.itemscroller.gui.widgets;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import fi.dy.masa.itemscroller.villager.VillagerData;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetTradeEntry extends WidgetListEntryBase<MerchantRecipe>
{
    public static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("textures/gui/widgets.png");

    private final VillagerData data;

    public WidgetTradeEntry(int x, int y, int width, int height,
            MerchantRecipe entry, int listIndex, VillagerData data)
    {
        super(x, y, width, height, entry, listIndex);

        this.data = data;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel();
        int width = this.getWidth();
        int height = this.getHeight();
        int v = 66;

        if (this.isMouseOver(mouseX, mouseY))
        {
            v += 20;
        }

        this.bindTexture(BUTTON_TEXTURE);

        // Button background texture for the trades
        RenderUtils.drawTexturedRect(x            , y,   0, v, width - 4, height, z);
        RenderUtils.drawTexturedRect(x + width - 4, y, 196, v,         4, height, z);

        if (selected)
        {
            RenderUtils.drawOutline(x, y, width, height, 1, 0xFFFFB000, z);
        }

        IGuiIcon icon = this.entry.isRecipeDisabled() ? ItemScrollerGuiIcons.TRADE_ARROW_LOCKED : ItemScrollerGuiIcons.TRADE_ARROW_AVAILABLE;

        RenderUtils.setupBlend();
        GlStateManager.enableAlpha();

        // Trade arrow
        icon.renderAt(x + 44, y + 5, z, false, false);

        // This entry has been favorited
        if (this.data.getFavorites().contains(this.getListIndex()))
        {
            ItemScrollerGuiIcons.STAR_5.renderAt(x + 80, y + 2, z, false, false);
        }

        GlStateManager.disableBlend();

        ItemStack buy1 = this.entry.getItemToBuy();
        ItemStack buy2 = this.entry.getSecondItemToBuy();
        ItemStack sell = this.entry.getItemToSell();

        if (buy1.isEmpty() == false)
        {
            InventoryOverlay.renderStackAt(buy1, x +  4, y + 2, z, 1f, this.mc);
        }

        if (buy2.isEmpty() == false)
        {
            InventoryOverlay.renderStackAt(buy2, x + 22, y + 2, z, 1f, this.mc);
        }

        if (sell.isEmpty() == false)
        {
            InventoryOverlay.renderStackAt(sell, x + 60, y + 2, z, 1f, this.mc);
        }
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel() + 1;
        int height = this.getHeight();

        if (mouseY >= y + 2 && mouseY <= y + height - 2)
        {
            if (mouseX >= x + 4 && mouseX <= x + 4 + 16)
            {
                ItemStack buy1 = this.entry.getItemToBuy();

                if (buy1.isEmpty() == false)
                {
                    InventoryOverlay.renderStackToolTip(mouseX, mouseY, z, buy1, this.mc);
                }
            }
            else if (mouseX >= x + 22 && mouseX <= x + 22 + 16)
            {
                ItemStack buy2 = this.entry.getSecondItemToBuy();

                if (buy2.isEmpty() == false)
                {
                    InventoryOverlay.renderStackToolTip(mouseX, mouseY, z, buy2, this.mc);
                }
            }
            else if (mouseX >= x + 60 && mouseX <= x + 60 + 16)
            {
                ItemStack sell = this.entry.getItemToSell();

                if (sell.isEmpty() == false)
                {
                    InventoryOverlay.renderStackToolTip(mouseX, mouseY, z, sell, this.mc);
                }
            }

            if (GuiBase.isAltDown())
            {
                int uses = this.entry.getToolUses();
                int max = this.entry.getMaxTradeUses();
                RenderUtils.drawHoverText(mouseX + 6, mouseY + 18, z, ImmutableList.of(StringUtils.translate("itemscroller.gui.label.trade_uses", uses, max)));
            }
        }
    }
}
