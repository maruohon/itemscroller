package fi.dy.masa.itemscroller.util;

import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.util.inventory.InventoryScreenUtils;

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
               Hotkeys.KEY_RECIPE_VIEW.getKeyBind().isKeyBindHeld() &&
               Configs.Toggles.MAIN_TOGGLE.getBooleanValue() &&
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
             if (key == Hotkeys.KEY_DRAG_FULL_STACKS.getKeyBind())      { return MoveAction.MOVE_TO_OTHER_STACKS;       }
        else if (key == Hotkeys.KEY_DRAG_LEAVE_ONE.getKeyBind())        { return MoveAction.MOVE_TO_OTHER_LEAVE_ONE;    }
        else if (key == Hotkeys.KEY_DRAG_MOVE_ONE.getKeyBind())         { return MoveAction.MOVE_TO_OTHER_MOVE_ONE;     }
        else if (key == Hotkeys.KEY_DRAG_MATCHING.getKeyBind())         { return MoveAction.MOVE_TO_OTHER_MATCHING;     }

        else if (key == Hotkeys.KEY_DRAG_DROP_STACKS.getKeyBind())      { return MoveAction.DROP_STACKS;                }
        else if (key == Hotkeys.KEY_DRAG_DROP_LEAVE_ONE.getKeyBind())   { return MoveAction.DROP_LEAVE_ONE;             }
        else if (key == Hotkeys.KEY_DRAG_DROP_SINGLE.getKeyBind())      { return MoveAction.DROP_ONE;                   }

        else if (key == Hotkeys.KEY_WS_MOVE_UP_STACKS.getKeyBind())     { return MoveAction.MOVE_UP_STACKS;             }
        else if (key == Hotkeys.KEY_WS_MOVE_UP_MATCHING.getKeyBind())   { return MoveAction.MOVE_UP_MATCHING;           }
        else if (key == Hotkeys.KEY_WS_MOVE_UP_LEAVE_ONE.getKeyBind())  { return MoveAction.MOVE_UP_LEAVE_ONE;          }
        else if (key == Hotkeys.KEY_WS_MOVE_UP_SINGLE.getKeyBind())     { return MoveAction.MOVE_UP_MOVE_ONE;           }
        else if (key == Hotkeys.KEY_WS_MOVE_DOWN_STACKS.getKeyBind())   { return MoveAction.MOVE_DOWN_STACKS;           }
        else if (key == Hotkeys.KEY_WS_MOVE_DOWN_MATCHING.getKeyBind()) { return MoveAction.MOVE_DOWN_MATCHING;         }
        else if (key == Hotkeys.KEY_WS_MOVE_DOWN_LEAVE_ONE.getKeyBind()){ return MoveAction.MOVE_DOWN_LEAVE_ONE;        }
        else if (key == Hotkeys.KEY_WS_MOVE_DOWN_SINGLE.getKeyBind())   { return MoveAction.MOVE_DOWN_MOVE_ONE;         }

        return MoveAction.NONE;
    }

    public static boolean isActionKeyActive(MoveAction action)
    {
        switch (action)
        {
            case MOVE_TO_OTHER_STACKS:          return Hotkeys.KEY_DRAG_FULL_STACKS.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_LEAVE_ONE:       return Hotkeys.KEY_DRAG_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_MOVE_ONE:        return Hotkeys.KEY_DRAG_MOVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_MATCHING:        return Hotkeys.KEY_DRAG_MATCHING.getKeyBind().isKeyBindHeld();
            case MOVE_TO_OTHER_EVERYTHING:      return Hotkeys.KEY_MOVE_EVERYTHING.getKeyBind().isKeyBindHeld();
            case DROP_STACKS:                   return Hotkeys.KEY_DRAG_DROP_STACKS.getKeyBind().isKeyBindHeld();
            case DROP_LEAVE_ONE:                return Hotkeys.KEY_DRAG_DROP_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case DROP_ONE:                      return Hotkeys.KEY_DRAG_DROP_SINGLE.getKeyBind().isKeyBindHeld();
            case MOVE_UP_STACKS:                return Hotkeys.KEY_WS_MOVE_UP_STACKS.getKeyBind().isKeyBindHeld();
            case MOVE_UP_MATCHING:              return Hotkeys.KEY_WS_MOVE_UP_MATCHING.getKeyBind().isKeyBindHeld();
            case MOVE_UP_LEAVE_ONE:             return Hotkeys.KEY_WS_MOVE_UP_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_UP_MOVE_ONE:              return Hotkeys.KEY_WS_MOVE_UP_SINGLE.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_STACKS:              return Hotkeys.KEY_WS_MOVE_DOWN_STACKS.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_MATCHING:            return Hotkeys.KEY_WS_MOVE_DOWN_MATCHING.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_LEAVE_ONE:           return Hotkeys.KEY_WS_MOVE_DOWN_LEAVE_ONE.getKeyBind().isKeyBindHeld();
            case MOVE_DOWN_MOVE_ONE:            return Hotkeys.KEY_WS_MOVE_DOWN_SINGLE.getKeyBind().isKeyBindHeld();
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
        return keyCode == Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode();
    }

    public static boolean isUse(int keyCode)
    {
        return keyCode == Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode();
    }

    public static boolean isPickBlock(int keyCode)
    {
        return keyCode == Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode();
    }
}
