package fi.dy.masa.itemscroller.event;

import java.lang.invoke.MethodHandle;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraftforge.event.world.WorldEvent;
import fi.dy.masa.itemscroller.ItemScroller;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.proxy.ClientProxy;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils.MoveType;
import fi.dy.masa.itemscroller.util.MethodHandleUtils;

public class InputEventHandler
{
    private static InputEventHandler instance;
    private boolean disabled;
    private int lastPosX;
    private int lastPosY;
    private int slotNumberLast;
    private final Set<Integer> draggedSlots = new HashSet<Integer>();
    private WeakReference<Slot> sourceSlotCandidate = new WeakReference<Slot>(null);
    private WeakReference<Slot> sourceSlot = new WeakReference<Slot>(null);
    private ItemStack stackInCursorLast = InventoryUtils.EMPTY_STACK;
    private RecipeStorage recipes;

    private static final MethodHandle methodHandle_getSlotAtPosition = MethodHandleUtils.getMethodHandleVirtual(GuiContainer.class,
            new String[] { "func_146975_c", "getSlotAtPosition" }, int.class, int.class);
    public static final Field fieldGuiLeft = ReflectionHelper.findField(GuiContainer.class, "field_147003_i", "guiLeft");
    public static final Field fieldGuiTop = ReflectionHelper.findField(GuiContainer.class, "field_147009_r", "guiTop");
    public static final Field fieldGuiXSize = ReflectionHelper.findField(GuiContainer.class, "field_146999_f", "xSize");
    public static final Field fieldGuiYSize = ReflectionHelper.findField(GuiContainer.class, "field_147000_g", "ySize");
    public static final Field field_theSlot = ReflectionHelper.findField(GuiContainer.class, "field_147006_u", "theSlot");

    public InputEventHandler()
    {
        this.initializeRecipeStorage();
        instance = this;
    }

    public static InputEventHandler instance()
    {
        return instance;
    }

    @SubscribeEvent
    public void onMouseInputEventPre(InputEvent.MouseInputEventPre event)
    {
        //System.out.printf("onMouseInputEventPre()\n");
        GuiScreen guiScreen = event.getGui();

        if (this.disabled ||
            (guiScreen instanceof GuiContainer) == false ||
            guiScreen instanceof GuiContainerCreative ||
            guiScreen.mc == null || guiScreen.mc.thePlayer == null ||
            Configs.GUI_BLACKLIST.contains(guiScreen.getClass().getName()))
        {
            return;
        }

        GuiContainer gui = (GuiContainer) guiScreen;
        int dWheel = Mouse.getEventDWheel();
        boolean cancel = false;

        if (dWheel != 0)
        {
            // When scrolling while the recipe view is open, change the selection instead of moving items
            if (RenderEventHandler.getRenderStoredRecipes())
            {
                this.recipes.scrollSelection(dWheel < 0);
            }
            else
            {
                cancel = InventoryUtils.tryMoveItems(gui, this.recipes, dWheel > 0);
            }
        }
        else
        {
            this.checkForItemPickup(gui);
            this.storeSourceSlotCandidate(gui);

            if (Configs.enableRightClickCraftingOneStack && Mouse.getEventButton() == 1 &&
                InventoryUtils.isCraftingSlot(gui, getSlotUnderMouse(gui)))
            {
                InventoryUtils.rightClickCraftOneStack(gui);
            }
            else if (Configs.enableShiftPlaceItems && InventoryUtils.canShiftPlaceItems(gui))
            {
                cancel = this.shiftPlaceItems(gui);
            }
            else if (Configs.enableShiftDropItems && this.canShiftDropItems(gui))
            {
                cancel = this.shiftDropItems(gui);
            }
            else if (Configs.enableDragMovingShiftLeft || Configs.enableDragMovingShiftRight || Configs.enableDragMovingControlLeft)
            {
                cancel = this.dragMoveItems(gui, this.shouldMoveVertically());
            }
            else if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0 && this.shouldMoveVertically())
            {
                InventoryUtils.tryMoveItemsVertically(gui, getSlotUnderMouse(gui),
                        this.recipes, Keyboard.isKeyDown(Keyboard.KEY_W), MoveType.MOVE_STACK);
                this.slotNumberLast = -1;
                cancel = true;
            }
        }

        if (cancel)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onKeyInputEventPre(InputEvent.KeyboardInputEventPre event)
    {
        GuiScreen guiScreen = event.getGui();

        if ((guiScreen instanceof GuiContainer) == false || guiScreen.mc == null || guiScreen.mc.thePlayer == null)
        {
            return;
        }

        GuiContainer gui = (GuiContainer) guiScreen;

        if (Keyboard.getEventKey() == Keyboard.KEY_I && Keyboard.getEventKeyState() &&
            isAltKeyDown() && GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown())
        {
            Slot slot = getSlotUnderMouse(gui);

            if (slot != null)
            {
                debugPrintSlotInfo(gui, slot);
            }
            else
            {
                ItemScroller.logger.info("GUI class: {}", gui.getClass().getName());
            }
        }
        // Drop all matching stacks from the same inventory when pressing Ctrl + Shift + Drop key
        else if (Configs.enableControlShiftDropkeyDropItems && Keyboard.getEventKeyState() &&
            Configs.GUI_BLACKLIST.contains(gui.getClass().getName()) == false &&
            GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown() &&
            gui.mc.gameSettings.keyBindDrop.getKeyCode() == Keyboard.getEventKey())
        {
            Slot slot = getSlotUnderMouse(gui);

            if (slot != null && slot.getHasStack())
            {
                InventoryUtils.dropStacks(gui, slot.getStack(), slot);
            }
        }
        // Toggle mouse functionality on/off
        else if (Keyboard.getEventKeyState() && ClientProxy.KEY_DISABLE.getKeyCode() == Keyboard.getEventKey())
        {
            this.disabled = ! this.disabled;

            if (this.disabled)
            {
                gui.mc.thePlayer.playSound("note.bassattack", 0.8f, 0.8f);
            }
            else
            {
                gui.mc.thePlayer.playSound("note.harp", 0.5f, 1.0f);
            }
        }
        // Show or hide the recipe selection
        else if (Keyboard.getEventKey() == ClientProxy.KEY_RECIPE.getKeyCode())
        {
            if (Keyboard.getEventKeyState())
            {
                RenderEventHandler.setRenderStoredRecipes(true);
            }
            else
            {
                RenderEventHandler.setRenderStoredRecipes(false);
            }
        }
        // Store or load a recipe
        else if (Keyboard.getEventKeyState() && Keyboard.isKeyDown(ClientProxy.KEY_RECIPE.getKeyCode()) &&
                 Keyboard.getEventKey() >= Keyboard.KEY_1 && Keyboard.getEventKey() <= Keyboard.KEY_9)
        {
            int index = MathHelper.clamp_int(Keyboard.getEventKey() - Keyboard.KEY_1, 0, 8);
            InventoryUtils.storeOrLoadRecipe(gui, index);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        this.recipes.readFromDisk();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Load event)
    {
        this.recipes.writeToDisk();
    }

    public void initializeRecipeStorage()
    {
        this.recipes = new RecipeStorage(18, Configs.craftingScrollingSaveFileIsGlobal);
    }

    public RecipeStorage getRecipes()
    {
        return this.recipes;
    }

    /**
     * Store a reference to the slot when a slot is left or right clicked on.
     * The slot is then later used to determine which inventory an ItemStack was
     * picked up from, if the stack from the cursor is dropped while holding shift.
     */
    private void storeSourceSlotCandidate(GuiContainer gui)
    {
        // Left or right mouse button was pressed
        if (Mouse.getEventButtonState() && (Mouse.getEventButton() == 0 || Mouse.getEventButton() == 1))
        {
            Slot slot = getSlotUnderMouse(gui);

            if (slot != null)
            {
                ItemStack stackCursor = gui.mc.thePlayer.inventory.getItemStack();
                ItemStack stack = InventoryUtils.EMPTY_STACK;

                if (InventoryUtils.isStackEmpty(stackCursor) == false)
                {
                    // Do a cheap copy without NBT data
                    stack = new ItemStack(stackCursor.getItem(), InventoryUtils.getStackSize(stackCursor), stackCursor.getItemDamage());
                }

                this.stackInCursorLast = stack;
                this.sourceSlotCandidate = new WeakReference<Slot>(slot);
            }
        }
    }

    /**
     * Check if the (previous) mouse event resulted in picking up a new ItemStack to the cursor
     */
    private void checkForItemPickup(GuiContainer gui)
    {
        ItemStack stackCursor = gui.mc.thePlayer.inventory.getItemStack();

        // Picked up or swapped items to the cursor, grab a reference to the slot that the items came from
        // Note that we are only checking the item and metadata here!
        if (InventoryUtils.isStackEmpty(stackCursor) == false && InventoryUtils.areStacksEqual(stackCursor, this.stackInCursorLast) == false)
        {
            this.sourceSlot = new WeakReference<Slot>(this.sourceSlotCandidate.get());
        }
    }

    private static void debugPrintSlotInfo(GuiContainer gui, Slot slot)
    {
        if (slot == null)
        {
            ItemScroller.logger.info("slot was null");
            return;
        }

        boolean hasSlot = gui.inventorySlots.inventorySlots.contains(slot);
        Object inv = slot.inventory;
        String stackStr = InventoryUtils.getStackString(slot.getStack());

        ItemScroller.logger.info(String.format("slot: slotNumber: %d, getSlotIndex(): %d, getHasStack(): %s, " +
                "slot class: %s, inv class: %s, Container's slot list has slot: %s, stack: %s",
                slot.slotNumber, slot.getSlotIndex(), slot.getHasStack(), slot.getClass().getName(),
                inv != null ? inv.getClass().getName() : "<null>", hasSlot ? " true" : "false", stackStr));
    }

    private boolean shiftPlaceItems(GuiContainer gui)
    {
        Slot slot = getSlotUnderMouse(gui);

        // Left click to place the items from the cursor to the slot
        InventoryUtils.leftClickSlot(gui, slot.slotNumber);

        // Ugly fix to prevent accidentally drag-moving the stack from the slot that it was just placed into...
        this.draggedSlots.add(slot.slotNumber);

        InventoryUtils.tryMoveStacks(slot, gui, true, false, false);

        return true;
    }

    private boolean shiftDropItems(GuiContainer gui)
    {
        ItemStack stackReference = gui.mc.thePlayer.inventory.getItemStack();

        if (InventoryUtils.isStackEmpty(stackReference) == false)
        {
            stackReference = stackReference.copy();

            // First drop the existing stack from the cursor
            InventoryUtils.dropItemsFromCursor(gui);

            InventoryUtils.dropStacks(gui, stackReference, this.sourceSlot.get());
            return true;
        }

        return false;
    }

    private boolean canShiftDropItems(GuiContainer gui)
    {
        if (GuiScreen.isShiftKeyDown() == false || Mouse.getEventButton() != 0 ||
            InventoryUtils.isStackEmpty(gui.mc.thePlayer.inventory.getItemStack()))
        {
            return false;
        }

        try
        {
            int left = fieldGuiLeft.getInt(gui);
            int top = fieldGuiTop.getInt(gui);
            int xSize = fieldGuiXSize.getInt(gui);
            int ySize = fieldGuiYSize.getInt(gui);
            int mouseAbsX = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
            int mouseAbsY = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
            boolean isOutsideGui = mouseAbsX < left || mouseAbsY < top || mouseAbsX >= left + xSize || mouseAbsY >= top + ySize;

            return isOutsideGui && this.getSlotAtPosition(gui, mouseAbsX - left, mouseAbsY - top) == null;
        }
        catch (IllegalAccessException e)
        {
            ItemScroller.logger.warn("Failed to reflect GuiContainer#guiLeft or guiTop or xSize or ySize");
        }

        return false;
    }

    private boolean shouldMoveVertically()
    {
        return Configs.enableWSClicking && (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_S));
    }

    private MoveType getMoveType()
    {
        boolean leftButtonDown = Mouse.isButtonDown(0);
        boolean isShiftDown = GuiScreen.isShiftKeyDown();

        if (isShiftDown == false)           { return MoveType.MOVE_ONE;   }
        else if (leftButtonDown == false)   { return MoveType.LEAVE_ONE;  }
        else                                { return MoveType.MOVE_STACK; }
    }

    private boolean dragMoveItems(GuiContainer gui, boolean moveVertically)
    {
        int mouseX = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
        int mouseY = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;

        if (InventoryUtils.isStackEmpty(gui.mc.thePlayer.inventory.getItemStack()) == false)
        {
            // Updating these here is part of the fix to preventing a drag after shift + place
            this.lastPosX = mouseX;
            this.lastPosY = mouseY;
            return false;
        }

        boolean eventKeyIsLeftButton = Mouse.getEventButton() == 0;
        boolean eventKeyIsRightButton = Mouse.getEventButton() == 1;
        boolean leftButtonDown = Mouse.isButtonDown(0);
        boolean rightButtonDown = Mouse.isButtonDown(1);
        boolean isShiftDown = GuiScreen.isShiftKeyDown();
        boolean isControlDown = GuiScreen.isCtrlKeyDown();
        boolean eitherMouseButtonDown = leftButtonDown || rightButtonDown;

        if ((isShiftDown && leftButtonDown && Configs.enableDragMovingShiftLeft == false) ||
            (isShiftDown && rightButtonDown && Configs.enableDragMovingShiftRight == false) ||
            (isControlDown && eitherMouseButtonDown && Configs.enableDragMovingControlLeft == false))
        {
            return false;
        }

        boolean cancel = false;
        MoveType moveType = this.getMoveType();

        if (Mouse.getEventButtonState())
        {
            if (((eventKeyIsLeftButton || eventKeyIsRightButton) && isControlDown && Configs.enableDragMovingControlLeft) ||
                (eventKeyIsRightButton && isShiftDown && Configs.enableDragMovingShiftRight) ||
                (eventKeyIsLeftButton && moveVertically))
            {
                // Allow moving entire stack with just W or S down, (without Shift),
                // but only when first clicking the left button down, and when not holding Control
                if (moveVertically && eventKeyIsLeftButton && isControlDown == false)
                {
                    moveType = MoveType.MOVE_STACK;
                }

                // Reset this or the method call won't do anything...
                this.slotNumberLast = -1;
                cancel = this.dragMoveFromSlotAtPosition(gui, mouseX, mouseY, moveType, moveVertically);
            }
        }

        // Check that either mouse button is down
        if (cancel == false && (isShiftDown || isControlDown) && eitherMouseButtonDown)
        {
            int distX = mouseX - this.lastPosX;
            int distY = mouseY - this.lastPosY;
            int absX = Math.abs(distX);
            int absY = Math.abs(distY);

            if (absX > absY)
            {
                int inc = distX > 0 ? 1 : -1;

                for (int x = this.lastPosX; ; x += inc)
                {
                    int y = absX != 0 ? this.lastPosY + ((x - this.lastPosX) * distY / absX) : mouseY;
                    this.dragMoveFromSlotAtPosition(gui, x, y, moveType, moveVertically);

                    if (x == mouseX)
                    {
                        break;
                    }
                }
            }
            else
            {
                int inc = distY > 0 ? 1 : -1;

                for (int y = this.lastPosY; ; y += inc)
                {
                    int x = absY != 0 ? this.lastPosX + ((y - this.lastPosY) * distX / absY) : mouseX;
                    this.dragMoveFromSlotAtPosition(gui, x, y, moveType, moveVertically);

                    if (y == mouseY)
                    {
                        break;
                    }
                }
            }
        }

        this.lastPosX = mouseX;
        this.lastPosY = mouseY;

        // Always update the slot under the mouse.
        // This should prevent a "double click/move" when shift + left clicking on slots that have more
        // than one stack of items. (the regular slotClick() + a "drag move" from the slot that is under the mouse
        // when the left mouse button is pressed down and this code runs).
        Slot slot = this.getSlotAtPosition(gui, mouseX, mouseY);
        this.slotNumberLast = slot != null ? slot.slotNumber : -1;

        if (eitherMouseButtonDown == false)
        {
            this.draggedSlots.clear();
        }

        return cancel;
    }

    private boolean dragMoveFromSlotAtPosition(GuiContainer gui, int x, int y, MoveType moveType, boolean moveVertically)
    {
        Slot slot = this.getSlotAtPosition(gui, x, y);
        boolean flag = slot != null && InventoryUtils.isValidSlot(slot, gui, true) && slot.canTakeStack(gui.mc.thePlayer);
        boolean cancel = flag && (moveType == MoveType.LEAVE_ONE || moveType == MoveType.MOVE_ONE);

        if (flag && slot.slotNumber != this.slotNumberLast && this.draggedSlots.contains(slot.slotNumber) == false)
        {
            if (moveVertically)
            {
                InventoryUtils.tryMoveItemsVertically(gui, slot, this.recipes, Keyboard.isKeyDown(Keyboard.KEY_W), moveType);
                cancel = true;
            }
            else
            {
                if (moveType == MoveType.MOVE_ONE)
                {
                    cancel = InventoryUtils.tryMoveSingleItemToOtherInventory(slot, gui);
                }
                else if (moveType == MoveType.LEAVE_ONE)
                {
                    cancel = InventoryUtils.tryMoveAllButOneItemToOtherInventory(slot, gui);
                }
                else
                {
                    InventoryUtils.shiftClickSlot(gui, slot.slotNumber);
                    cancel = true;
                }
            }

            this.draggedSlots.add(slot.slotNumber);
        }

        return cancel;
    }

    public static boolean isAltKeyDown()
    {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    private Slot getSlotAtPosition(GuiContainer gui, int x, int y)
    {
        try
        {
            return (Slot) methodHandle_getSlotAtPosition.invokeExact(gui, x, y);
        }
        catch (Throwable e)
        {
            ItemScroller.logger.error("Error while trying invoke GuiContainer#getSlotAtPosition() from {}", gui.getClass().getSimpleName(), e);
        }

        return null;
    }

    @Nullable
    public static Slot getSlotUnderMouse(GuiContainer gui)
    {
        try
        {
            return (Slot) field_theSlot.get(gui);
        }
        catch (Throwable t)
        {
            ItemScroller.logger.error("Error while trying to get the slot under the cursor from {}", gui.getClass().getSimpleName(), t);
        }

        return null;
    }
}
