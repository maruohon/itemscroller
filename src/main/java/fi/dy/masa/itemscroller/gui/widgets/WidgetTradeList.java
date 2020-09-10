package fi.dy.masa.itemscroller.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import fi.dy.masa.itemscroller.event.InputHandler;
import fi.dy.masa.itemscroller.util.AccessorUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.itemscroller.villager.VillagerData;
import fi.dy.masa.itemscroller.villager.VillagerDataStorage;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.ScrollBarWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetTradeList extends BaseWidget
{
    private final ScrollBarWidget scrollBar;
    private final GuiMerchant parentGui;
    private final VillagerDataStorage storage;
    private final ArrayList<WidgetTradeEntry> entryList = new ArrayList<>();
    private final VillagerData data;
    private MerchantRecipeList recipeList;

    public WidgetTradeList(int x, int y, GuiMerchant parentGui, VillagerData data)
    {
        super(x, y, 106, 166);

        this.scrollBar = (new ScrollBarWidget(x + 93, y + 17, 8, 142, ItemScrollerIcons.SCROLL_BAR_6)).setRenderBackgroundColor(false);
        this.parentGui = parentGui;
        this.storage = VillagerDataStorage.getInstance();
        this.data = data;
    }

    private void lazySetRecipeList()
    {
        if (this.recipeList == null)
        {
            this.recipeList = this.parentGui.getMerchant().getRecipes(this.mc.player);

            if (this.recipeList != null)
            {
                int max = Math.max(0, this.recipeList.size() - 7);
                int scrollBarTotalHeight = Math.max(140, this.recipeList.size() * 20);
                this.scrollBar.setMaxValue(max);
                this.scrollBar.setValue(this.data.getTradeListPosition());
                this.scrollBar.setTotalHeight(scrollBarTotalHeight);

                this.reCreateEntryWidgets();
            }
        }
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int x = this.getX();
        int y = this.getY();

        return mouseX >= x +  5 && mouseX <= x + 99 &&
               mouseY >= y + 18 && mouseY <= y + 157;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.scrollBar.isMouseOver(mouseX, mouseY) && this.scrollBar.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        int x = this.getX();
        int y = this.getY();

        if (mouseX <= x + 92)
        {
            int relY = mouseY - (y + 18);
            int listIndex = relY / 20;
            WidgetTradeEntry entry = listIndex >= 0 && listIndex < this.entryList.size() ? this.entryList.get(listIndex) : null;
            int recipeIndex = entry != null ? entry.getListIndex() : -1;

            if (recipeIndex >= 0)
            {
                // Middle click to toggle favorites
                if (mouseButton == 2)
                {
                    this.storage.toggleFavorite(recipeIndex);
                    this.reCreateEntryWidgets();
                }
                else
                {
                    boolean samePage = AccessorUtils.getSelectedMerchantRecipe(this.parentGui) == recipeIndex;
                    InputHandler.changeTradePage(this.parentGui, recipeIndex);

                    if (BaseScreen.isShiftDown() || samePage || mouseButton == 1)
                    {
                        InventoryUtils.villagerClearTradeInputSlots();

                        if (mouseButton == 1)
                        {
                            InventoryUtils.villagerTradeEverythingPossibleWithCurrentRecipe();
                        }
                        else
                        {
                            InventoryUtils.tryMoveItemsToMerchantBuySlots(this.parentGui, true);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollBar.setIsDragging(false);
    }

    @Override
    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta)
    {
        this.scrollBar.offsetValue(mouseWheelDelta < 0 ? 1 : -1);
        return true;
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        this.lazySetRecipeList();

        if (this.recipeList != null)
        {
            int width = this.getWidth();
            int currentPage = AccessorUtils.getSelectedMerchantRecipe(this.parentGui);

            currentPage = Math.min(currentPage, this.recipeList.size() - 1);
            this.updateDataStorage(currentPage);

            RenderUtils.disableItemLighting();

            // Background
            ItemScrollerIcons.TRADE_LIST_BACKGROUND.renderAt(x, y, this.getZLevel(), false, false);

            String str = StringUtils.translate("itemscroller.gui.label.trades");
            int w = this.getStringWidth(str);
            this.drawString(x + width / 2 - w / 2, y + 6, z, 0xFF404040, str);

            int wx = this.scrollBar.getX();
            int wy = this.scrollBar.getY();
            float wz = this.scrollBar.getZLevel();
            this.scrollBar.renderAt(wx, wy, wz, mouseX, mouseY, isActiveGui, this.scrollBar.isHoveredForRender(mouseX, mouseY));

            // Render the trades
            for (WidgetTradeEntry widget : this.entryList)
            {
                widget.renderAt(widget.getX(), widget.getY(), widget.getZLevel(), mouseX, mouseY, isActiveGui, -1, currentPage == widget.getListIndex());
            }

            for (WidgetTradeEntry widget : this.entryList)
            {
                if (widget.isMouseOver(mouseX, mouseY))
                {
                    widget.postRenderHovered(mouseX, mouseY, isActiveGui, -1);
                }
            }
        }
    }

    private void reCreateEntryWidgets()
    {
        if (this.recipeList != null)
        {
            this.entryList.clear();

            ArrayList<MerchantRecipe> list = new ArrayList<>();
            List<Integer> favorites = this.data.getFavorites();

            // Some favorites defined
            if (favorites.isEmpty() == false)
            {
                // First pick all the favorited recipes, in the order they are in the favorites list
                for (int index : favorites)
                {
                    if (index >= 0 && index < this.recipeList.size())
                    {
                        list.add(this.recipeList.get(index));
                    }
                }

                // Then add the rest of the recipes in their original order
                for (int i = 0; i < this.recipeList.size(); ++i)
                {
                    if (favorites.contains(i) == false)
                    {
                        list.add(this.recipeList.get(i));
                    }
                }
            }
            else
            {
                list.addAll(this.recipeList);
            }

            final int scrollBarPos = this.scrollBar.getValue();
            final int last = Math.min(scrollBarPos + 7, list.size());
            final int x = this.getX() + 5;

            for (int index = scrollBarPos; index < last; ++index)
            {
                int y = this.getY() + (index - scrollBarPos) * 20 + 18;
                MerchantRecipe recipe = list.get(index);

                int listIndex = this.recipeList.indexOf(recipe);
                this.entryList.add(new WidgetTradeEntry(x, y, 88, 20, recipe, listIndex, listIndex, this.data));
            }
        }
    }

    private void updateDataStorage(int currentPage)
    {
        int oldPosition = this.data.getTradeListPosition();
        int newPosition = this.scrollBar.getValue();

        if (this.data.getLastPage() != currentPage)
        {
            this.storage.setLastPage(currentPage);
        }

        if (newPosition != oldPosition)
        {
            this.storage.setTradeListPosition(newPosition);
            this.reCreateEntryWidgets();
        }
    }
}
