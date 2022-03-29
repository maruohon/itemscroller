package fi.dy.masa.itemscroller.util;

import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.inventory.InventoryScreenUtils;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;

public class InputUtils
{
    public static int getMouseX()
    {
        int width = GuiUtils.getScaledWindowWidth();
        return Mouse.getX() * width / GuiUtils.getDisplayWidth();
    }

    public static int getMouseY()
    {
        int height = GuiUtils.getScaledWindowHeight();
        return height - Mouse.getY() * height / GuiUtils.getDisplayHeight() - 1;
    }

    public static boolean isRecipeViewOpen()
    {
        return GuiUtils.getCurrentScreen() != null &&
               Hotkeys.SHOW_RECIPES.getKeyBind().isKeyBindHeld() &&
               Configs.Toggles.MOD_FEATURES_ENABLED.getBooleanValue() &&
               CraftingHandler.isCraftingGui(GuiUtils.getCurrentScreen());
    }

    public static boolean canShiftDropItems(GuiContainer gui, Minecraft mc)
    {
        if (InventoryUtils.isStackEmpty(mc.player.inventory.getItemStack()) == false)
        {
            int left = InventoryScreenUtils.getGuiPosX(gui);
            int top = InventoryScreenUtils.getGuiPosY(gui);
            int xSize = InventoryScreenUtils.getGuiSizeX(gui);
            int ySize = InventoryScreenUtils.getGuiSizeY(gui);
            int mouseAbsX = Mouse.getEventX() * gui.width / GuiUtils.getDisplayWidth();
            int mouseAbsY = gui.height - Mouse.getEventY() * gui.height / GuiUtils.getDisplayHeight() - 1;
            boolean isOutsideGui = mouseAbsX < left || mouseAbsY < top || mouseAbsX >= left + xSize || mouseAbsY >= top + ySize;

            return isOutsideGui && AccessorUtils.getSlotAtPosition(gui, mouseAbsX - left, mouseAbsY - top) == null;
        }

        return false;
    }

    public static MoveAction getDragMoveAction(KeyBind key)
    {
             if (key == Hotkeys.DRAG_MOVE_ENTIRE_STACKS.getKeyBind())       { return MoveAction.MOVE_TO_OTHER_STACKS;       }
        else if (key == Hotkeys.DRAG_MOVE_LEAVE_ONE.getKeyBind())           { return MoveAction.MOVE_TO_OTHER_LEAVE_ONE;    }
        else if (key == Hotkeys.DRAG_MOVE_ONE.getKeyBind())                 { return MoveAction.MOVE_TO_OTHER_MOVE_ONE;     }
        else if (key == Hotkeys.DRAG_MOVE_ALL_MATCHING.getKeyBind())        { return MoveAction.MOVE_TO_OTHER_MATCHING;     }

        else if (key == Hotkeys.DRAG_DROP_ENTIRE_STACKS.getKeyBind())       { return MoveAction.DROP_STACKS;                }
        else if (key == Hotkeys.DRAG_DROP_LEAVE_ONE.getKeyBind())           { return MoveAction.DROP_LEAVE_ONE;             }
        else if (key == Hotkeys.DRAG_DROP_ONE.getKeyBind())                 { return MoveAction.DROP_ONE;                   }

        else if (key == Hotkeys.WS_MOVE_UP_ENTIRE_STACKS.getKeyBind())      { return MoveAction.MOVE_UP_STACKS;             }
        else if (key == Hotkeys.WS_MOVE_UP_ALL_MATCHING.getKeyBind())       { return MoveAction.MOVE_UP_MATCHING;           }
        else if (key == Hotkeys.WS_MOVE_UP_LEAVE_ONE.getKeyBind())          { return MoveAction.MOVE_UP_LEAVE_ONE;          }
        else if (key == Hotkeys.WS_MOVE_UP_ONE.getKeyBind())                { return MoveAction.MOVE_UP_MOVE_ONE;           }
        else if (key == Hotkeys.WS_MOVE_DOWN_ENTIRE_STACKS.getKeyBind())    { return MoveAction.MOVE_DOWN_STACKS;           }
        else if (key == Hotkeys.WS_MOVE_DOWN_ALL_MATCHING.getKeyBind())     { return MoveAction.MOVE_DOWN_MATCHING;         }
        else if (key == Hotkeys.WS_MOVE_DOWN_LEAVE_ONE.getKeyBind())        { return MoveAction.MOVE_DOWN_LEAVE_ONE;        }
        else if (key == Hotkeys.WS_MOVE_DOWN_ONE.getKeyBind())              { return MoveAction.MOVE_DOWN_MOVE_ONE;         }

        return MoveAction.NONE;
    }

    public static boolean isActionKeyActive(MoveAction action)
    {
        switch (action)
        {
            case MOVE_TO_OTHER_STACKS:          return Hotkeys.DRAG_MOVE_ENTIRE_STACKS.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_LEAVE_ONE:       return Hotkeys.DRAG_MOVE_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_MOVE_ONE:        return Hotkeys.DRAG_MOVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_MATCHING:        return Hotkeys.DRAG_MOVE_ALL_MATCHING.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_EVERYTHING:      return Hotkeys.MOVE_EVERYTHING.getKeyBind().isKeyBindHeld();
            case DROP_STACKS:                   return Hotkeys.DRAG_DROP_ENTIRE_STACKS.getKeyBind().isKeyBindHeld();
            case DROP_LEAVE_ONE:                return Hotkeys.DRAG_DROP_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case DROP_ONE:                      return Hotkeys.DRAG_DROP_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_UP_STACKS:                return Hotkeys.WS_MOVE_UP_ENTIRE_STACKS.getKeyBind().isKeyBindHeld();
            case MOVE_UP_MATCHING:              return Hotkeys.WS_MOVE_UP_ALL_MATCHING.getKeyBind().isKeyBindHeld();
            case MOVE_UP_LEAVE_ONE:             return Hotkeys.WS_MOVE_UP_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_UP_MOVE_ONE:              return Hotkeys.WS_MOVE_UP_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_STACKS:              return Hotkeys.WS_MOVE_DOWN_ENTIRE_STACKS.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_MATCHING:            return Hotkeys.WS_MOVE_DOWN_ALL_MATCHING.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_LEAVE_ONE:           return Hotkeys.WS_MOVE_DOWN_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_MOVE_ONE:            return Hotkeys.WS_MOVE_DOWN_ONE.getKeyBind().isKeyBindHeld();
            default:
        }

        return false;
    }

    public static MoveAmount getMoveAmount(MoveAction action)
    {
        switch (action)
        {
            case SCROLL_TO_OTHER_MOVE_ONE:
            case MOVE_TO_OTHER_MOVE_ONE:
            case DROP_ONE:
            case MOVE_DOWN_MOVE_ONE:
            case MOVE_UP_MOVE_ONE:
                return MoveAmount.MOVE_ONE;

            case MOVE_TO_OTHER_LEAVE_ONE:
            case DROP_LEAVE_ONE:
            case MOVE_DOWN_LEAVE_ONE:
            case MOVE_UP_LEAVE_ONE:
                return MoveAmount.LEAVE_ONE;

            case SCROLL_TO_OTHER_STACKS:
            case MOVE_TO_OTHER_STACKS:
            case DROP_STACKS:
            case MOVE_DOWN_STACKS:
            case MOVE_UP_STACKS:
                return MoveAmount.FULL_STACKS;

            case SCROLL_TO_OTHER_MATCHING:
            case MOVE_TO_OTHER_MATCHING:
            case DROP_ALL_MATCHING:
            case MOVE_UP_MATCHING:
            case MOVE_DOWN_MATCHING:
                return MoveAmount.ALL_MATCHING;

            case MOVE_TO_OTHER_EVERYTHING:
            case SCROLL_TO_OTHER_EVERYTHING:
                return MoveAmount.EVERYTHING;

            default:
        }

        return MoveAmount.NONE;
    }

    public static boolean isAttack(int keyCode)
    {
        return keyCode == GameUtils.getClient().gameSettings.keyBindAttack.getKeyCode();
    }

    public static boolean isUse(int keyCode)
    {
        return keyCode == GameUtils.getClient().gameSettings.keyBindUseItem.getKeyCode();
    }

    public static boolean isPickBlock(int keyCode)
    {
        return keyCode == GameUtils.getClient().gameSettings.keyBindPickBlock.getKeyCode();
    }
}
