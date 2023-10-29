package fi.dy.masa.itemscroller.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;

import malilib.gui.util.GuiUtils;
import malilib.input.MouseScrollHandler;
import malilib.util.game.wrap.GameUtils;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.gui.widgets.WidgetTradeList;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.IGuiMerchant;
import fi.dy.masa.itemscroller.util.InputUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.itemscroller.util.MoveAction;
import fi.dy.masa.itemscroller.villager.VillagerDataStorage;

public class MouseScrollHandlerImpl implements MouseScrollHandler
{
    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double deltaX, double deltaY)
    {
        int dWheel = deltaY > 0 ? 1 : -1;
        MoveAction action = InventoryUtils.getActiveMoveAction();

        if (action != MoveAction.NONE && InputUtils.isActionKeyActive(action) == false)
        {
            InventoryUtils.stopDragging();
        }

        Minecraft mc = GameUtils.getClient();
        boolean cancel = false;

        if (Configs.Toggles.MOD_FEATURES_ENABLED.getBooleanValue() && mc.player != null)
        {
            if (GuiUtils.getCurrentScreen() instanceof GuiContainer &&
                (GuiUtils.getCurrentScreen() instanceof GuiContainerCreative) == false &&
                Configs.Lists.GUI_BLACKLIST.getValue().contains(GuiUtils.getCurrentScreen().getClass().getName()) == false)
            {
                if (Configs.Toggles.VILLAGER_TRADE_LIST.getBooleanValue() &&
                    GuiUtils.getCurrentScreen() instanceof GuiMerchant &&
                    VillagerDataStorage.INSTANCE.hasInteractionTarget())
                {
                    WidgetTradeList widget = ((IGuiMerchant) GuiUtils.getCurrentScreen()).getTradeListWidget();

                    if (widget != null && widget.isMouseOver(mouseX, mouseY))
                    {
                        widget.tryMouseScroll(mouseX, mouseY, dWheel, 0.0);
                        return true;
                    }
                }

                // When scrolling while the recipe view is open, change the selection instead of moving items
                if (InputUtils.isRecipeViewOpen())
                {
                    RecipeStorage.INSTANCE.scrollSelection(dWheel < 0);
                    cancel = true;
                }
                else
                {
                    GuiContainer gui = (GuiContainer) GuiUtils.getCurrentScreen();
                    cancel = InventoryUtils.tryMoveItems(gui, RecipeStorage.INSTANCE, dWheel > 0);
                }
            }
        }

        return cancel;
    }
}
