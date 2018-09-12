package fi.dy.masa.itemscroller.event;

import org.lwjgl.input.Keyboard;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.AccessorUtils;
import fi.dy.masa.itemscroller.util.InputUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.itemscroller.util.MoveAmount;
import fi.dy.masa.itemscroller.util.MoveType;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

public class InputHandler implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler
{
    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
        for (IHotkey hotkey : Hotkeys.HOTKEY_LIST)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
        manager.addHotkeysForCategory(Reference.MOD_NAME, "itemscroller.hotkeys.category.hotkeys", Hotkeys.HOTKEY_LIST);
    }

    @Override
    public boolean onKeyInput(int eventKey, boolean eventKeyState)
    {
        if (InputUtils.isRecipeViewOpen() && eventKey >= Keyboard.KEY_1 && eventKey <= Keyboard.KEY_9)
        {
            int index = MathHelper.clamp(eventKey - Keyboard.KEY_1, 0, 8);
            KeybindCallbacks.getInstance().getRecipes().changeSelectedRecipe(index);
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseInput(int eventButton, int dWheel, boolean eventButtonState)
    {
        Minecraft mc = Minecraft.getMinecraft();
        boolean cancel = false;

        if (KeybindCallbacks.getInstance().functionalityEnabled() &&
            mc != null &&
            mc.player != null &&
            mc.currentScreen instanceof GuiContainer &&
            Configs.GUI_BLACKLIST.contains(mc.currentScreen.getClass().getName()) == false)
        {
            GuiContainer gui = (GuiContainer) mc.currentScreen;
            RecipeStorage recipes = KeybindCallbacks.getInstance().getRecipes();

            if (dWheel != 0)
            {
                // When scrolling while the recipe view is open, change the selection instead of moving items
                if (InputUtils.isRecipeViewOpen())
                {
                    recipes.scrollSelection(dWheel < 0);
                    cancel = true;
                }
                else
                {
                    cancel = InventoryUtils.tryMoveItems(gui, recipes, dWheel > 0);
                }
            }
            else
            {
                Slot slot = AccessorUtils.getSlotUnderMouse(gui);
                final boolean isLeftClick = InputUtils.mouseEventIsLeftClick();
                final boolean isRightClick = InputUtils.mouseEventIsRightClick();
                final boolean isPickBlock = InputUtils.mouseEventIsPickBlock();
                final boolean isShiftDown = GuiScreen.isShiftKeyDown();

                if (eventButtonState && (isLeftClick || isRightClick || isPickBlock))
                {
                    final int mouseX = InputUtils.getMouseX();
                    final int mouseY = InputUtils.getMouseY();
                    int hoveredRecipeId = RenderEventHandler.instance().getHoveredRecipeId(mouseX, mouseY, recipes, gui, mc);

                    // Hovering over an item in the recipe view
                    if (hoveredRecipeId >= 0)
                    {
                        InventoryUtils.handleRecipeClick(gui, mc, recipes, hoveredRecipeId, isLeftClick, isRightClick, isPickBlock, isShiftDown);
                        return true;
                    }
                    // Pick-blocking over a crafting output slot with the recipe view open, store the recipe
                    else if (isPickBlock && InputUtils.isRecipeViewOpen() && InventoryUtils.isCraftingSlot(gui, slot))
                    {
                        recipes.storeCraftingRecipeToCurrentSelection(slot, gui, true);
                        cancel = true;
                    }
                }

                InventoryUtils.checkForItemPickup(gui, mc);

                if (eventButtonState && (isLeftClick || isRightClick))
                {
                    InventoryUtils.storeSourceSlotCandidate(slot, mc);
                }

                if (Configs.Toggles.RIGHT_CLICK_CRAFT_STACK.getBooleanValue() &&
                    isRightClick && eventButtonState &&
                    InventoryUtils.isCraftingSlot(gui, slot))
                {
                    InventoryUtils.rightClickCraftOneStack(gui);
                }
                else if (Configs.Toggles.SHIFT_PLACE_ITEMS.getBooleanValue() &&
                         isLeftClick && isShiftDown &&
                         InventoryUtils.canShiftPlaceItems(gui))
                {
                    cancel |= InventoryUtils.shiftPlaceItems(slot, gui);
                }
                else if (Configs.Toggles.SHIFT_DROP_ITEMS.getBooleanValue() &&
                         isLeftClick && isShiftDown &&
                         InputUtils.canShiftDropItems(gui, mc))
                {
                    cancel |= InventoryUtils.shiftDropItems(gui);
                }
                else if (Configs.Toggles.CLICK_MOVE_EVERYTHING.getBooleanValue() &&
                         Hotkeys.MODIFIER_MOVE_EVERYTHING.getKeybind().isKeybindHeld() &&
                         isLeftClick && eventButtonState &&
                         slot != null && InventoryUtils.isStackEmpty(slot.getStack()) == false)
                {
                    InventoryUtils.tryMoveStacks(slot, gui, false, true, false);
                    cancel = true;
                }
                else if (Configs.Toggles.CLICK_MOVE_MATCHING.getBooleanValue() &&
                         Hotkeys.MODIFIER_MOVE_MATCHING.getKeybind().isKeybindHeld() &&
                         isLeftClick && eventButtonState &&
                         slot != null && InventoryUtils.isStackEmpty(slot.getStack()) == false)
                {
                    InventoryUtils.tryMoveStacks(slot, gui, true, true, false);
                    cancel = true;
                }
                else if (isLeftClick && eventButtonState && InputUtils.shouldMoveVertically())
                {
                    MoveType type = InputUtils.getDragMoveType(mc);
                    MoveAmount amount = InputUtils.getDragMoveAmount(type, mc);
                    InventoryUtils.tryMoveItemsVertically(gui, slot, Keyboard.isKeyDown(Keyboard.KEY_W), amount);
                    InventoryUtils.resetLastSlotNumber();
                    cancel = true;
                }
                else
                {
                    cancel |= this.handleDragging(gui, mc, true);
                }
            }

            if (Configs.Generic.SCROLL_CRAFT_STORE_RECIPES_TO_FILE.getBooleanValue())
            {
                recipes.writeToDisk();
            }
        }

        return cancel;
    }

    @Override
    public void onMouseMoved()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (KeybindCallbacks.getInstance().functionalityEnabled() &&
            mc != null &&
            mc.player != null &&
            mc.currentScreen instanceof GuiContainer &&
            Configs.GUI_BLACKLIST.contains(mc.currentScreen.getClass().getName()) == false)
        {
            this.handleDragging((GuiContainer) mc.currentScreen, mc, false);
        }
    }

    private boolean handleDragging(GuiContainer gui, Minecraft mc, boolean isClick)
    {
        if ((Hotkeys.KEY_DRAG_FULL_STACKS.getKeybind().isKeybindHeld() && Configs.Toggles.DRAG_MOVE_STACKS.getBooleanValue()) ||
            (Hotkeys.KEY_DRAG_LEAVE_ONE.getKeybind().isKeybindHeld() && Configs.Toggles.DRAG_MOVE_LEAVE_ONE.getBooleanValue()) ||
            (Hotkeys.KEY_DRAG_MOVE_ONE.getKeybind().isKeybindHeld() && Configs.Toggles.DRAG_MOVE_ONE.getBooleanValue()) ||
            (Hotkeys.KEY_DRAG_DROP_SINGLE.getKeybind().isKeybindHeld() && Configs.Toggles.DRAG_DROP_SINGLE.getBooleanValue()) ||
            (Hotkeys.KEY_DRAG_DROP_STACKS.getKeybind().isKeybindHeld() && Configs.Toggles.DRAG_DROP_STACKS.getBooleanValue()))
        {
            return InventoryUtils.dragMoveItems(gui, mc, false);
        }

        return false;
    }
}
