package fi.dy.masa.itemscroller.input;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;

import malilib.gui.util.GuiUtils;
import malilib.input.MouseMoveHandler;
import fi.dy.masa.itemscroller.util.InputUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.itemscroller.util.MoveAction;

public class MouseMoveHandlerImpl implements MouseMoveHandler
{
    @Override
    public void onMouseMove(int mouseX, int mouseY)
    {
        GuiScreen screen = GuiUtils.getCurrentScreen();

        if (MouseClickHandlerImpl.canOperateIn(screen))
        {
            MoveAction action = InventoryUtils.getActiveMoveAction();

            if (InputUtils.isActionKeyActive(action))
            {
                InventoryUtils.dragMoveItems((GuiContainer) screen, action, false, mouseX, mouseY);
            }
            else if (action != MoveAction.NONE)
            {
                InventoryUtils.stopDragging();
            }
        }
    }
}
