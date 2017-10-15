package fi.dy.masa.itemscroller.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotMerchantResult;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import fi.dy.masa.itemscroller.ItemScroller;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Configs.SlotRange;
import fi.dy.masa.itemscroller.event.InputEventHandler;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;

public class InventoryUtils
{
    public static final Field fieldSelectedMerchantRecipe = ReflectionHelper.findField(GuiMerchant.class, "field_147041_z", "selectedMerchantRecipe");

    public static String getStackString(ItemStack stack)
    {
        if (isStackEmpty(stack) == false)
        {
            return String.format("[%s @ %d - display: %s - NBT: %s] (%s)",
                    stack.getItem().getRegistryName(), stack.getMetadata(), stack.getDisplayName(),
                    stack.getTagCompound() != null ? stack.getTagCompound().toString() : "<no NBT>",
                    stack.toString());
        }

        return "<empty>";
    }

    public static boolean isValidSlot(Slot slot, GuiContainer gui, boolean requireItems)
    {
        return gui.inventorySlots != null && gui.inventorySlots.inventorySlots != null &&
                slot != null && gui.inventorySlots.inventorySlots.contains(slot) &&
                (requireItems == false || slot.getHasStack()) &&
                Configs.SLOT_BLACKLIST.contains(slot.getClass().getName()) == false;
    }

    public static boolean isCraftingSlot(GuiContainer gui, Slot slot)
    {
        return slot != null && Configs.getCraftingGridSlots(gui, slot) != null;
    }

    /**
     * Checks if there are slots belonging to another inventory on screen above the given slot
     */
    public static boolean inventoryExistsAbove(Slot slot, Container container)
    {
        for (Slot slotTmp : container.inventorySlots)
        {
            if (slotTmp.yPos < slot.yPos && areSlotsInSameInventory(slot, slotTmp) == false)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean canShiftPlaceItems(GuiContainer gui)
    {
        if (GuiScreen.isShiftKeyDown() == false || Mouse.getEventButton() != 0)
        {
            return false;
        }

        Slot slot = gui.getSlotUnderMouse();
        ItemStack stackCursor = gui.mc.player.inventory.getItemStack();

        // The target slot needs to be an empty, valid slot, and there needs to be items in the cursor
        return slot != null && isStackEmpty(stackCursor) == false && isValidSlot(slot, gui, false) &&
               slot.getHasStack() == false && slot.isItemValid(stackCursor);
    }

    public static boolean tryMoveItems(GuiContainer gui, RecipeStorage recipes, boolean scrollingUp)
    {
        Slot slot = gui.getSlotUnderMouse();

        // We require an empty cursor
        if (slot == null || isStackEmpty(gui.mc.player.inventory.getItemStack()) == false)
        {
            return false;
        }

        // Villager handling only happens when scrolling over the trade output slot
        boolean villagerHandling = Configs.enableScrollingVillager && gui instanceof GuiMerchant && slot instanceof SlotMerchantResult;
        boolean craftingHandling = Configs.enableScrollingCrafting && isCraftingSlot(gui, slot);
        boolean isCtrlDown = GuiContainer.isCtrlKeyDown();
        boolean isShiftDown = GuiContainer.isShiftKeyDown();
        boolean moveToOtherInventory = scrollingUp;

        if (Configs.useSlotPositionAwareScrollDirection)
        {
            boolean above = inventoryExistsAbove(slot, gui.inventorySlots);
            // so basically: (above && scrollingUp) || (above == false && scrollingUp == false)
            moveToOtherInventory = above == scrollingUp;
        }

        if ((Configs.reverseScrollDirectionSingle && isShiftDown == false) ||
            (Configs.reverseScrollDirectionStacks && isShiftDown))
        {
            moveToOtherInventory = ! moveToOtherInventory;
        }

        // Check that the slot is valid, (don't require items in case of the villager output slot or a crafting slot)
        if (isValidSlot(slot, gui, villagerHandling || craftingHandling ? false : true) == false)
        {
            // Not a valid proper slot, but set as a crafting slot, store the recipe (mainly for storing a recipe from JEI etc)
            if (craftingHandling && moveToOtherInventory)
            {
                recipes.storeCraftingRecipeToCurrentSelection(gui, slot);
            }

            return false;
        }

        if (craftingHandling)
        {
            return tryMoveItemsCrafting(gui, slot, recipes, moveToOtherInventory, isShiftDown, isCtrlDown);
        }

        if (villagerHandling)
        {
            return tryMoveItemsVillager((GuiMerchant) gui, slot, moveToOtherInventory, isShiftDown);
        }

        if ((Configs.enableScrollingSingle == false && isShiftDown == false && isCtrlDown == false) ||
            (Configs.enableScrollingStacks == false && isShiftDown && isCtrlDown == false) ||
            (Configs.enableScrollingMatchingStacks == false && isShiftDown == false && isCtrlDown) ||
            (Configs.enableScrollingEverything == false && isShiftDown && isCtrlDown))
        {
            return false;
        }

        if (isShiftDown)
        {
            // Ctrl + Shift + scroll: move everything
            if (isCtrlDown)
            {
                tryMoveStacks(slot, gui, false, moveToOtherInventory, false);
            }
            // Shift + scroll: move one matching stack
            else
            {
                tryMoveStacks(slot, gui, true, moveToOtherInventory, true);
            }

            return true;
        }
        // Ctrl + scroll: Move all matching stacks
        else if (isCtrlDown)
        {
            tryMoveStacks(slot, gui, true, moveToOtherInventory, false);
            return true;
        }
        // No Ctrl or Shift
        else
        {
            // Ensure there is a stack
            if (slot.getHasStack())
            {
                ItemStack stack = slot.getStack();
                
                // Scrolling items from this slot/inventory into the other inventory
                if (moveToOtherInventory)
                {
                    return tryMoveSingleItemToOtherInventory(slot, gui);
                }
                // Scrolling items from the other inventory into this slot/inventory
                else if (getStackSize(stack) < slot.getItemStackLimit(stack))
                {
                    return tryMoveSingleItemToThisInventory(slot, gui);
                }
            }
        }

        return false;
    }

    public static void dropStacks(GuiContainer gui, ItemStack stackReference, Slot sourceInvSlot)
    {
        if (sourceInvSlot != null && isStackEmpty(stackReference) == false)
        {
            Container container = gui.inventorySlots;
            stackReference = stackReference.copy();

            for (Slot slot : container.inventorySlots)
            {
                // If this slot is in the same inventory that the items were picked up to the cursor from
                // and the stack is identical to the one in the cursor, then this stack will get dropped.
                if (areSlotsInSameInventory(slot, sourceInvSlot) && areStacksEqual(slot.getStack(), stackReference))
                {
                    // Drop the stack
                    dropStack(gui, slot.slotNumber);
                }
            }
        }
    }

    public static boolean tryMoveItemsVillager(GuiMerchant gui, Slot slot, boolean moveToOtherInventory, boolean isShiftDown)
    {
        if (isShiftDown)
        {
            // Try to fill the merchant's buy slots from the player inventory
            if (moveToOtherInventory == false)
            {
                tryMoveItemsToMerchantBuySlots(gui, true);
            }
            // Move items from sell slot to player inventory
            else if (slot.getHasStack())
            {
                tryMoveStacks(slot, gui, true, true, true);
            }
            // Scrolling over an empty output slot, clear the buy slots
            else
            {
                tryMoveStacks(slot, gui, false, true, false);
            }
        }
        else
        {
            // Scrolling items from player inventory into merchant buy slots
            if (moveToOtherInventory == false)
            {
                tryMoveItemsToMerchantBuySlots(gui, false);
            }
            // Scrolling items from this slot/inventory into the other inventory
            else if (slot.getHasStack())
            {
                moveOneSetOfItemsFromSlotToOtherInventory(gui, slot);
            }
        }

        return false;
    }

    public static boolean tryMoveSingleItemToOtherInventory(Slot slot, GuiContainer gui)
    {
        ItemStack stackOrig = slot.getStack();
        Container container = gui.inventorySlots;

        if (isStackEmpty(gui.mc.player.inventory.getItemStack()) == false || slot.canTakeStack(gui.mc.player) == false ||
            (getStackSize(stackOrig) > 1 && slot.isItemValid(stackOrig) == false))
        {
            return false;
        }

        // Can take all the items to the cursor at once, use a shift-click method to move one item from the slot
        if (getStackSize(stackOrig) <= stackOrig.getMaxStackSize())
        {
            return clickSlotsToMoveSingleItemByShiftClick(gui, slot.slotNumber);
        }

        ItemStack stack = stackOrig.copy();
        setStackSize(stack, 1);

        ItemStack[] originalStacks = getOriginalStacks(container);

        // Try to move the temporary single-item stack via the shift-click handler method
        slot.putStack(stack);
        container.transferStackInSlot(gui.mc.player, slot.slotNumber);

        // Successfully moved the item somewhere, now we want to check where it went
        if (slot.getHasStack() == false)
        {
            int targetSlot = getTargetSlot(container, originalStacks);

            // Found where the item went
            if (targetSlot >= 0)
            {
                // Remove the dummy item from the target slot (on the client side)
                container.inventorySlots.get(targetSlot).decrStackSize(1);

                // Restore the original stack to the slot under the cursor (on the client side)
                restoreOriginalStacks(container, originalStacks);

                // Do the slot clicks to actually move the items (on the server side)
                return clickSlotsToMoveSingleItem(gui, slot.slotNumber, targetSlot);
            }
        }

        // Restore the original stack to the slot under the cursor (on the client side)
        slot.putStack(stackOrig);

        return false;
    }

    public static boolean tryMoveAllButOneItemToOtherInventory(Slot slot, GuiContainer gui)
    {
        EntityPlayer player = gui.mc.player;
        ItemStack stackOrig = slot.getStack().copy();

        if (getStackSize(stackOrig) == 1 || getStackSize(stackOrig) > stackOrig.getMaxStackSize() ||
            slot.canTakeStack(player) == false || slot.isItemValid(stackOrig) == false)
        {
            return true;
        }

        // Take half of the items from the original slot to the cursor
        rightClickSlot(gui, slot.slotNumber);

        ItemStack stackInCursor = player.inventory.getItemStack();
        if (isStackEmpty(stackInCursor))
        {
            return false;
        }

        int stackInCursorSizeOrig = getStackSize(stackInCursor);
        int tempSlotNum = -1;

        // Find some other slot where to store one of the items temporarily
        for (Slot slotTmp : gui.inventorySlots.inventorySlots)
        {
            if (slotTmp.slotNumber != slot.slotNumber && slotTmp.isItemValid(stackInCursor))
            {
                ItemStack stackInSlot = slotTmp.getStack();

                if (isStackEmpty(stackInSlot) || areStacksEqual(stackInSlot, stackInCursor))
                {
                    // Try to put one item into the temporary slot
                    rightClickSlot(gui, slotTmp.slotNumber);

                    stackInCursor = player.inventory.getItemStack();

                    // Successfully stored one item
                    if (isStackEmpty(stackInCursor) || getStackSize(stackInCursor) < stackInCursorSizeOrig)
                    {
                        tempSlotNum = slotTmp.slotNumber;
                        break;
                    }
                }
            }
        }

        // Return the rest of the items into the original slot
        leftClickSlot(gui, slot.slotNumber);

        // Successfully stored one item in a temporary slot
        if (tempSlotNum != -1)
        {
            // Shift click the stack from the original slot
            shiftClickSlot(gui, slot.slotNumber);

            // Take half a stack from the temporary slot
            rightClickSlot(gui, tempSlotNum);

            // Return one item into the original slot
            rightClickSlot(gui, slot.slotNumber);

            // Return the rest of the items to the temporary slot, if any
            if (isStackEmpty(player.inventory.getItemStack()) == false)
            {
                leftClickSlot(gui, tempSlotNum);
            }

            return true;
        }

        return false;
    }

    public static boolean tryMoveSingleItemToThisInventory(Slot slot, GuiContainer gui)
    {
        Container container = gui.inventorySlots;
        ItemStack stackOrig = slot.getStack();

        if (slot.isItemValid(stackOrig) == false)
        {
            return false;
        }

        for (int slotNum = container.inventorySlots.size() - 1; slotNum >= 0; slotNum--)
        {
            Slot slotTmp = container.inventorySlots.get(slotNum);
            ItemStack stackTmp = slotTmp.getStack();

            if (areSlotsInSameInventory(slotTmp, slot) == false &&
                isStackEmpty(stackTmp) == false && slotTmp.canTakeStack(gui.mc.player) &&
                (getStackSize(stackTmp) == 1 || slotTmp.isItemValid(stackTmp)))
            {
                if (areStacksEqual(stackTmp, stackOrig))
                {
                    return clickSlotsToMoveSingleItem(gui, slotTmp.slotNumber, slot.slotNumber);
                }
            }
        }

        // If we weren't able to move any items from another inventory, then try to move items
        // within the same inventory (mostly between the hotbar and the player inventory)
        /*
        for (Slot slotTmp : container.inventorySlots)
        {
            ItemStack stackTmp = slotTmp.getStack();

            if (slotTmp.slotNumber != slot.slotNumber &&
                isStackEmpty(stackTmp) == false && slotTmp.canTakeStack(gui.mc.player) &&
                (getStackSize(stackTmp) == 1 || slotTmp.isItemValid(stackTmp)))
            {
                if (areStacksEqual(stackTmp, stackOrig))
                {
                    return this.clickSlotsToMoveSingleItem(gui, slotTmp.slotNumber, slot.slotNumber);
                }
            }
        }
        */

        return false;
    }

    public static void tryMoveStacks(Slot slot, GuiContainer gui, boolean matchingOnly, boolean toOtherInventory, boolean firstOnly)
    {
        Container container = gui.inventorySlots;
        ItemStack stackReference = slot.getStack();

        for (Slot slotTmp : container.inventorySlots)
        {
            if (slotTmp.slotNumber != slot.slotNumber &&
                areSlotsInSameInventory(slotTmp, slot) == toOtherInventory && slotTmp.getHasStack() &&
                (matchingOnly == false || areStacksEqual(stackReference, slotTmp.getStack())))
            {
                boolean success = shiftClickSlotWithCheck(gui, slotTmp.slotNumber);

                // Failed to shift-click items, try a manual method
                if (success == false && Configs.enableScrollingStacksFallback)
                {
                    clickSlotsToMoveItemsFromSlot(slotTmp, gui, toOtherInventory);
                }

                if (firstOnly)
                {
                    return;
                }
            }
        }

        // If moving to the other inventory, then move the hovered slot's stack last
        if (toOtherInventory && shiftClickSlotWithCheck(gui, slot.slotNumber) == false)
        {
            clickSlotsToMoveItemsFromSlot(slot, gui, toOtherInventory);
        }
    }

    private static void tryMoveItemsToMerchantBuySlots(GuiMerchant gui, boolean fillStacks)
    {
        MerchantRecipeList list = gui.getMerchant().getRecipes(gui.mc.player);
        int index = 0;

        try
        {
            index = fieldSelectedMerchantRecipe.getInt(gui);
        }
        catch (IllegalAccessException e)
        {
            ItemScroller.logger.warn("Failed to get the value of GuiMerchant.selectedMerchantRecipe");
        }

        if (list == null || list.size() <= index)
        {
            return;
        }

        MerchantRecipe recipe = list.get(index);
        if (recipe == null)
        {
            return;
        }

        ItemStack buy1 = recipe.getItemToBuy();
        ItemStack buy2 = recipe.getSecondItemToBuy();

        if (isStackEmpty(buy1) == false)
        {
            fillBuySlot(gui, 0, buy1, fillStacks);
        }

        if (isStackEmpty(buy2) == false)
        {
            fillBuySlot(gui, 1, buy2, fillStacks);
        }
    }

    public static void fillBuySlot(GuiContainer gui, int slotNum, ItemStack buyStack, boolean fillStacks)
    {
        Slot slot = gui.inventorySlots.getSlot(slotNum);
        ItemStack existingStack = slot.getStack();

        // If there are items not matching the merchant recipe, move them out first
        if (isStackEmpty(existingStack) == false && areStacksEqual(buyStack, existingStack) == false)
        {
            shiftClickSlot(gui, slotNum);
        }

        existingStack = slot.getStack();

        if (isStackEmpty(existingStack) || areStacksEqual(buyStack, existingStack))
        {
            moveItemsFromInventory(gui, slotNum, gui.mc.player.inventory, buyStack, fillStacks);
        }
    }

    public static void storeOrLoadRecipe(GuiContainer gui, int index)
    {
        Slot slot = gui.getSlotUnderMouse();
        RecipeStorage recipes = InputEventHandler.instance().getRecipes();

        // A crafting output slot with a stack under the cursor, store a recipe
        if (GuiScreen.isShiftKeyDown() && slot != null && isCraftingSlot(gui, slot))
        {
            if (slot.getHasStack())
            {
                recipes.storeCraftingRecipe(index, gui, slot);
            }
            else
            {
                recipes.clearRecipe(index);
            }
        }
        // Not a crafting output slot with a stack, load a stored recipe
        else
        {
            recipes.changeSelectedRecipe(index);

            if (slot != null && isStackEmpty(recipes.getSelectedRecipe().getResult()) == false)
            {
                tryMoveItemsToCraftingGridSlots(gui, slot, recipes, false);
            }
        }
    }

    private static boolean tryMoveItemsCrafting(GuiContainer gui, Slot slot, RecipeStorage recipes,
            boolean moveToOtherInventory, boolean isShiftDown, boolean isCtrlDown)
    {
        if (isShiftDown)
        {
            // Try to fill the crafting grid
            if (moveToOtherInventory == false)
            {
                if (Configs.craftingScrollingStoreRecipeOnFill && slot.getHasStack())
                {
                    recipes.storeCraftingRecipeToCurrentSelection(gui, slot);
                }

                if (isStackEmpty(recipes.getSelectedRecipe().getResult()) == false)
                {
                    tryMoveItemsToCraftingGridSlots(gui, slot, recipes, true);
                }
            }
            // Move items from the crafting output slot
            else if (slot.getHasStack())
            {
                recipes.storeCraftingRecipeToCurrentSelection(gui, slot);

                if (isCtrlDown)
                {
                    craftAsManyItemsAsPossible(gui, slot, recipes);
                }
                else
                {
                    shiftClickSlot(gui, slot.slotNumber);
                }
            }
            // Scrolling over an empty crafting output slot, clear the crafting grid
            else
            {
                SlotRange range = Configs.getCraftingGridSlots(gui, slot);

                if (range != null)
                {
                    for (int i = 0, s = range.getFirst(); i < range.getSlotCount(); i++, s++)
                    {
                        shiftClickSlot(gui, s);
                    }
                }
            }
        }
        else
        {
            // Scrolling items from player inventory into crafting grid slots
            if (moveToOtherInventory == false)
            {
                if (Configs.craftingScrollingStoreRecipeOnFill && slot.getHasStack())
                {
                    recipes.storeCraftingRecipeToCurrentSelection(gui, slot);
                }

                if (isStackEmpty(recipes.getSelectedRecipe().getResult()) == false)
                {
                    tryMoveItemsToCraftingGridSlots(gui, slot, recipes, false);
                }
            }
            // Scrolling items from this crafting slot into the other inventory
            else if (slot.getHasStack())
            {
                recipes.storeCraftingRecipeToCurrentSelection(gui, slot);
                moveOneSetOfItemsFromSlotToOtherInventory(gui, slot);
            }
            // Scrolling over an empty crafting output slot, clear the crafting grid
            else
            {
                SlotRange range = Configs.getCraftingGridSlots(gui, slot);

                if (range != null)
                {
                    for (int i = 0, s = range.getFirst(); i < range.getSlotCount(); i++, s++)
                    {
                        shiftClickSlot(gui, s);
                    }
                }
            }
        }

        return false;
    }

    private static void craftAsManyItemsAsPossible(GuiContainer gui, Slot slot, RecipeStorage recipes)
    {
        ItemStack result = recipes.getSelectedRecipe().getResult();
        int failSafe = 1024;

        while (failSafe > 0 && slot.getHasStack() && areStacksEqual(slot.getStack(), result))
        {
            shiftClickSlot(gui, slot.slotNumber);

            // Ran out of some or all ingredients for the recipe
            if (slot.getHasStack() == false || areStacksEqual(slot.getStack(), result) == false)
            {
                tryMoveItemsToCraftingGridSlots(gui, slot, recipes, true);
            }
            // No change in the result slot after shift clicking, let's assume the craft failed and stop here
            else
            {
                break;
            }

            failSafe--;
        }
    }

    private static boolean clearCraftingGridOfItems(GuiContainer gui, SlotRange range, RecipeStorage recipes, boolean nonMatchingOnly)
    {
        int numSlots = gui.inventorySlots.inventorySlots.size();

        for (int i = 0, slotNum = range.getFirst();
            i < range.getSlotCount() && i < recipes.getSelectedRecipe().getRecipeLength() && slotNum < numSlots;
            i++, slotNum++)
        {
            Slot slotTmp = gui.inventorySlots.getSlot(slotNum);

            if (slotTmp != null && slotTmp.getHasStack() &&
                (nonMatchingOnly == false || areStacksEqual(recipes.getSelectedRecipe().getRecipe()[i], slotTmp.getStack()) == false))
            {
                shiftClickSlot(gui, slotNum);

                // Failed to clear the slot
                if (slotTmp.getHasStack())
                {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean tryMoveItemsToCraftingGridSlots(GuiContainer gui, Slot slot, RecipeStorage recipes, boolean fillStacks)
    {
        Container container = gui.inventorySlots;
        int numSlots = container.inventorySlots.size();
        SlotRange range = Configs.getCraftingGridSlots(gui, slot);

        // Check that the slot range is valid and that the recipe can fit into this type of crafting grid
        if (range != null && range.getLast() < numSlots && recipes.getSelectedRecipe().getRecipeLength() <= range.getSlotCount())
        {
            // Clear non-matching items from the grid first
            if (clearCraftingGridOfItems(gui, range, recipes, true) == false)
            {
                return false;
            }

            // This slot is used to check that we get items from a DIFFERENT inventory than where this slot is in
            Slot slotGridFirst = container.getSlot(range.getFirst());
            Map<ItemType, List<Integer>> ingredientSlots = ItemType.getSlotsPerItem(recipes.getSelectedRecipe().getRecipe());

            for (Map.Entry<ItemType, List<Integer>> entry : ingredientSlots.entrySet())
            {
                ItemStack ingredientReference = entry.getKey().getStack();
                List<Integer> recipeSlots = entry.getValue();
                List<Integer> targetSlots = new ArrayList<Integer>();

                // Get the actual target slot numbers based on the grid's start and the relative positions inside the grid
                for (int s : recipeSlots)
                {
                    targetSlots.add(s + range.getFirst());
                }

                if (fillStacks)
                {
                    fillCraftingGrid(gui, slotGridFirst, ingredientReference, targetSlots);
                }
                else
                {
                    moveOneRecipeItemIntoCraftingGrid(gui, slotGridFirst, ingredientReference, targetSlots);
                }
            }
        }

        return false;
    }

    public static void fillCraftingGrid(GuiContainer gui, Slot slotGridFirst, ItemStack ingredientReference, List<Integer> targetSlots)
    {
        Container container = gui.inventorySlots;
        EntityPlayer player = gui.mc.player;
        int slotNum = -1;
        int slotReturn = -1;
        int sizeOrig = 0;

        if (isStackEmpty(ingredientReference))
        {
            return;
        }

        while (true)
        {
            slotNum = getSlotNumberOfLargestMatchingStackFromDifferentInventory(container, slotGridFirst, ingredientReference);

            // Didn't find ingredient items
            if (slotNum < 0)
            {
                break;
            }

            if (slotReturn == -1)
            {
                slotReturn = slotNum;
            }

            // Pick up the ingredient stack from the found slot
            leftClickSlot(gui, slotNum);

            ItemStack stackCursor = player.inventory.getItemStack();

            // Successfully picked up ingredient items
            if (areStacksEqual(ingredientReference, stackCursor))
            {
                sizeOrig = getStackSize(stackCursor);
                dragSplitItemsIntoSlots(gui, targetSlots);
                stackCursor = player.inventory.getItemStack();

                // Items left in cursor
                if (isStackEmpty(stackCursor) == false)
                {
                    // Didn't manage to move any items anymore
                    if (getStackSize(stackCursor) >= sizeOrig)
                    {
                        break;
                    }

                    // Collect all the remaining items into the first found slot, as long as possible
                    leftClickSlot(gui, slotReturn);

                    // All of them didn't fit into the first slot anymore, switch into the current source slot
                    if (isStackEmpty(player.inventory.getItemStack()) == false)
                    {
                        slotReturn = slotNum;
                        leftClickSlot(gui, slotReturn);
                    }
                }
            }
            // Failed to pick up the stack, break to avoid infinite loops
            // TODO: we could also "blacklist" this slot and try to continue...?
            else
            {
                break;
            }

            // Somehow items were left in the cursor, break here
            if (isStackEmpty(player.inventory.getItemStack()) == false)
            {
                break;
            }
        }

        // Return the rest of the items to the original slot
        if (slotNum >= 0 && isStackEmpty(player.inventory.getItemStack()) == false)
        {
            leftClickSlot(gui, slotNum);
        }
    }

    public static void rightClickCraftOneStack(GuiContainer gui)
    {
        Slot slot = gui.getSlotUnderMouse();
        InventoryPlayer inv = gui.mc.player.inventory;
        ItemStack stackCursor = inv.getItemStack();

        if (slot == null || slot.getHasStack() == false ||
            (isStackEmpty(stackCursor) == false) && areStacksEqual(slot.getStack(), stackCursor) == false)
        {
            return;
        }

        int sizeLast = 0;

        while (true)
        {
            rightClickSlot(gui, slot.slotNumber);
            stackCursor = inv.getItemStack();

            // Failed to craft items, or the stack became full, or ran out of ingredients
            if (isStackEmpty(stackCursor) || getStackSize(stackCursor) <= sizeLast ||
                getStackSize(stackCursor) >= stackCursor.getMaxStackSize() ||
                areStacksEqual(slot.getStack(), stackCursor) == false)
            {
                break;
            }

            sizeLast = getStackSize(stackCursor);
        }
    }

    private static int putSingleItemIntoSlots(GuiContainer gui, List<Integer> targetSlots, int startIndex)
    {
        ItemStack stackInCursor = gui.mc.player.inventory.getItemStack();

        if (isStackEmpty(stackInCursor))
        {
            return 0;
        }

        int numSlots = gui.inventorySlots.inventorySlots.size();
        int numItems = getStackSize(stackInCursor);
        int loops = Math.min(numItems, targetSlots.size() - startIndex);
        int count = 0;

        for (int i = 0; i < loops; i++)
        {
            int slotNum = targetSlots.get(startIndex + i);

            if (slotNum >= numSlots)
            {
                break;
            }

            rightClickSlot(gui, slotNum);
            count++;
        }

        return count;
    }

    public static void moveOneSetOfItemsFromSlotToOtherInventory(GuiContainer gui, Slot slot)
    {
        leftClickSlot(gui, slot.slotNumber);

        ItemStack stackCursor = gui.mc.player.inventory.getItemStack();

        if (isStackEmpty(stackCursor) == false)
        {
            List<Integer> slots = getSlotNumbersOfMatchingStacksFromDifferentInventory(gui.inventorySlots, slot, stackCursor, true);

            if (moveItemFromCursorToSlots(gui, slots) == false)
            {
                slots = getSlotNumbersOfEmptySlotsFromDifferentInventory(gui.inventorySlots, slot);
                moveItemFromCursorToSlots(gui, slots);
            }
        }
    }

    public static void moveOneRecipeItemIntoCraftingGrid(GuiContainer gui, Slot slotGridFirst, ItemStack ingredientReference, List<Integer> targetSlots)
    {
        Container container = gui.inventorySlots;
        int index = 0;
        int slotNum = -1;
        int slotCount = targetSlots.size();

        while (index < slotCount)
        {
            slotNum = getSlotNumberOfSmallestStackFromDifferentInventory(container, slotGridFirst, ingredientReference, slotCount);

            // Didn't find ingredient items
            if (slotNum < 0)
            {
                break;
            }

            // Pick up the ingredient stack from the found slot
            leftClickSlot(gui, slotNum);

            // Successfully picked up ingredient items
            if (areStacksEqual(ingredientReference, gui.mc.player.inventory.getItemStack()))
            {
                int filled = putSingleItemIntoSlots(gui, targetSlots, index);
                index += filled;

                if (filled < 1)
                {
                    break;
                }
            }
            // Failed to pick up the stack, break to avoid infinite loops
            // TODO: we could also "blacklist" this slot and try to continue...?
            else
            {
                break;
            }
        }

        // Return the rest of the items to the original slot
        if (slotNum >= 0 && isStackEmpty(gui.mc.player.inventory.getItemStack()) == false)
        {
            leftClickSlot(gui, slotNum);
        }
    }

    private static boolean moveItemFromCursorToSlots(GuiContainer gui, List<Integer> slotNumbers)
    {
        InventoryPlayer inv = gui.mc.player.inventory;

        for (int slotNum : slotNumbers)
        {
            leftClickSlot(gui, slotNum);

            if (isStackEmpty(inv.getItemStack()))
            {
                return true;
            }
        }

        return false;
    }

    private static void moveItemsFromInventory(GuiContainer gui, int slotTo, IInventory invSrc, ItemStack stackTemplate, boolean fillStacks)
    {
        Container container = gui.inventorySlots;

        for (Slot slot : container.inventorySlots)
        {
            if (slot == null)
            {
                continue;
            }

            if (slot.inventory == invSrc && areStacksEqual(stackTemplate, slot.getStack()))
            {
                if (fillStacks)
                {
                    if (clickSlotsToMoveItems(gui, slot.slotNumber, slotTo) == false)
                    {
                        break;
                    }
                }
                else
                {
                    clickSlotsToMoveSingleItem(gui, slot.slotNumber, slotTo);
                    break;
                }
            }
        }
    }

    private static int getSlotNumberOfLargestMatchingStackFromDifferentInventory(Container container, Slot slotReference, ItemStack stackReference)
    {
        int slotNum = -1;
        int largest = 0;

        for (Slot slot : container.inventorySlots)
        {
            if (areSlotsInSameInventory(slot, slotReference) == false && slot.getHasStack() &&
                areStacksEqual(stackReference, slot.getStack()))
            {
                int stackSize = getStackSize(slot.getStack());

                if (stackSize > largest)
                {
                    slotNum = slot.slotNumber;
                    largest = stackSize;
                }
            }
        }

        return slotNum;
    }

    /**
     * Returns the slot number of the slot that has the smallest stackSize that is still equal to or larger
     * than idealSize. The slot must also NOT be in the same inventory as slotReference.
     * If an adequately large stack is not found, then the largest one is selected.
     * @param container
     * @param slotReference
     * @param stackReference
     * @return
     */
    private static int getSlotNumberOfSmallestStackFromDifferentInventory(Container container, Slot slotReference, ItemStack stackReference, int idealSize)
    {
        int slotNum = -1;
        int smallest = Integer.MAX_VALUE;

        for (Slot slot : container.inventorySlots)
        {
            if (areSlotsInSameInventory(slot, slotReference) == false && slot.getHasStack() &&
                areStacksEqual(stackReference, slot.getStack()))
            {
                int stackSize = getStackSize(slot.getStack());

                if (stackSize < smallest && stackSize >= idealSize)
                {
                    slotNum = slot.slotNumber;
                    smallest = stackSize;
                }
            }
        }

        // Didn't find an adequately sized stack, now try to find at least some items...
        if (slotNum == -1)
        {
            int largest = 0;

            for (Slot slot : container.inventorySlots)
            {
                if (areSlotsInSameInventory(slot, slotReference) == false && slot.getHasStack() &&
                    areStacksEqual(stackReference, slot.getStack()))
                {
                    int stackSize = getStackSize(slot.getStack());

                    if (stackSize > largest)
                    {
                        slotNum = slot.slotNumber;
                        largest = stackSize;
                    }
                }
            }
        }

        return slotNum;
    }

    /**
     * Return the slot numbers of slots that have items identical to stackReference, that are NOT in the same
     * inventory as slotReference. If preferPartial is true, then stacks with a stackSize less that getMaxStackSize() are
     * at the beginning of the list (not ordered though) and full stacks are at the end, otherwise the reverse is true.
     * @param container
     * @param slotReference
     * @param stackReference
     * @param preferPartial
     * @return
     */
    private static List<Integer> getSlotNumbersOfMatchingStacksFromDifferentInventory(Container container, Slot slotReference,
            ItemStack stackReference, boolean preferPartial)
    {
        List<Integer> slots = new ArrayList<Integer>(64);

        for (int i = container.inventorySlots.size() - 1; i >= 0; i--)
        {
            Slot slot = container.getSlot(i);

            if (slot != null && slot.getHasStack() &&
                areSlotsInSameInventory(slot, slotReference) == false &&
                areStacksEqual(slot.getStack(), stackReference))
            {
                if ((getStackSize(slot.getStack()) < stackReference.getMaxStackSize()) == preferPartial)
                {
                    slots.add(0, slot.slotNumber);
                }
                else
                {
                    slots.add(slot.slotNumber);
                }
            }
        }

        return slots;
    }

    private static List<Integer> getSlotNumbersOfEmptySlotsFromDifferentInventory(Container container, Slot slotReference)
    {
        List<Integer> slots = new ArrayList<Integer>(64);

        for (int i = container.inventorySlots.size() - 1; i >= 0; i--)
        {
            Slot slot = container.getSlot(i);

            if (slot != null && slot.getHasStack() == false && areSlotsInSameInventory(slot, slotReference) == false)
            {
                slots.add(slot.slotNumber);
            }
        }

        return slots;
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    private static boolean areSlotsInSameInventory(Slot slot1, Slot slot2)
    {
        return slot1.isSameInventory(slot2);
    }

    private static ItemStack[] getOriginalStacks(Container container)
    {
        ItemStack[] originalStacks = new ItemStack[container.inventorySlots.size()];

        for (int i = 0; i < originalStacks.length; i++)
        {
            originalStacks[i] = container.inventorySlots.get(i).getStack().copy();
        }

        return originalStacks;
    }

    private static void restoreOriginalStacks(Container container, ItemStack[] originalStacks)
    {
        for (int i = 0; i < originalStacks.length; i++)
        {
            ItemStack stackSlot = container.getSlot(i).getStack();

            if (areStacksEqual(stackSlot, originalStacks[i]) == false ||
                (isStackEmpty(stackSlot) == false && getStackSize(stackSlot) != getStackSize(originalStacks[i])))
            {
                container.putStackInSlot(i, originalStacks[i]);
            }
        }
    }

    private static int getTargetSlot(Container container, ItemStack[] originalStacks)
    {
        List<Slot> slots = container.inventorySlots;

        for (int i = 0; i < originalStacks.length; i++)
        {
            ItemStack stackOrig = originalStacks[i];
            ItemStack stackNew = slots.get(i).getStack();

            if ((isStackEmpty(stackOrig) && isStackEmpty(stackNew) == false) ||
               (isStackEmpty(stackOrig) == false && isStackEmpty(stackNew) == false &&
               getStackSize(stackNew) == (getStackSize(stackOrig) + 1)))
            {
                return i;
            }
        }

        return -1;
    }

    /*
    private void clickSlotsToMoveItems(Slot slot, GuiContainer gui, boolean matchingOnly, boolean toOtherInventory)
    {
        for (Slot slotTmp : gui.inventorySlots.inventorySlots)
        {
            if (slotTmp.slotNumber != slot.slotNumber && areSlotsInSameInventory(slotTmp, slot) == toOtherInventory &&
                slotTmp.getHasStack() && (matchingOnly == false || areStacksEqual(slot.getStack(), slotTmp.getStack())))
            {
                this.clickSlotsToMoveItemsFromSlot(slotTmp, gui, toOtherInventory);
                return;
            }
        }

        // Move the hovered-over slot's stack last
        if (toOtherInventory)
        {
            this.clickSlotsToMoveItemsFromSlot(slot, gui, toOtherInventory);
        }
    }
    */

    private static void clickSlotsToMoveItemsFromSlot(Slot slotFrom, GuiContainer gui, boolean toOtherInventory)
    {
        EntityPlayer player = gui.mc.player;
        // Left click to pick up the found source stack
        leftClickSlot(gui, slotFrom.slotNumber);

        if (isStackEmpty(player.inventory.getItemStack()))
        {
            return;
        }

        for (Slot slotDst : gui.inventorySlots.inventorySlots)
        {
            ItemStack stackDst = slotDst.getStack();

            if (areSlotsInSameInventory(slotDst, slotFrom) != toOtherInventory &&
                (isStackEmpty(stackDst) || areStacksEqual(stackDst, player.inventory.getItemStack())))
            {
                // Left click to (try and) place items to the slot
                leftClickSlot(gui, slotDst.slotNumber);
            }

            if (isStackEmpty(player.inventory.getItemStack()))
            {
                return;
            }
        }

        // Couldn't fit the entire stack to the target inventory, return the rest of the items
        if (isStackEmpty(player.inventory.getItemStack()) == false)
        {
            leftClickSlot(gui, slotFrom.slotNumber);
        }
    }

    private static boolean clickSlotsToMoveSingleItem(GuiContainer gui, int slotFrom, int slotTo)
    {
        //System.out.println("clickSlotsToMoveSingleItem(from: " + slotFrom + ", to: " + slotTo + ")");
        ItemStack stack = gui.inventorySlots.inventorySlots.get(slotFrom).getStack();

        if (isStackEmpty(stack))
        {
            return false;
        }

        // Click on the from-slot to take items to the cursor - if there is more than one item in the from-slot,
        // right click on it, otherwise left click.
        if (getStackSize(stack) > 1)
        {
            rightClickSlot(gui, slotFrom);
        }
        else
        {
            leftClickSlot(gui, slotFrom);
        }

        // Right click on the target slot to put one item to it
        rightClickSlot(gui, slotTo);

        // If there are items left in the cursor, then return them back to the original slot
        if (isStackEmpty(gui.mc.player.inventory.getItemStack()) == false)
        {
            // Left click again on the from-slot to return the rest of the items to it
            leftClickSlot(gui, slotFrom);
        }

        return true;
    }

    private static boolean clickSlotsToMoveSingleItemByShiftClick(GuiContainer gui, int slotFrom)
    {
        Slot slot = gui.inventorySlots.inventorySlots.get(slotFrom);
        ItemStack stack = slot.getStack();

        if (isStackEmpty(stack))
        {
            return false;
        }

        if (getStackSize(stack) > 1)
        {
            // Left click on the from-slot to take all the items to the cursor
            leftClickSlot(gui, slotFrom);

            // Still items left in the slot, put the stack back and abort
            if (slot.getHasStack())
            {
                leftClickSlot(gui, slotFrom);
                return false;
            }
            else
            {
                // Right click one item back to the slot
                rightClickSlot(gui, slotFrom);
            }
        }

        // ... and then shift-click on the slot
        shiftClickSlot(gui, slotFrom);

        if (isStackEmpty(gui.mc.player.inventory.getItemStack()) == false)
        {
            // ... and then return the rest of the items
            leftClickSlot(gui, slotFrom);
        }

        return true;
    }

    /**
     * Try move items from slotFrom to slotTo
     * @return true if at least some items were moved
     */
    private static boolean clickSlotsToMoveItems(GuiContainer gui, int slotFrom, int slotTo)
    {
        EntityPlayer player = gui.mc.player;
        //System.out.println("clickSlotsToMoveItems(from: " + slotFrom + ", to: " + slotTo + ")");

        // Left click to take items
        leftClickSlot(gui, slotFrom);

        // Couldn't take the items, bail out now
        if (isStackEmpty(player.inventory.getItemStack()))
        {
            return false;
        }

        boolean ret = true;
        int size = getStackSize(player.inventory.getItemStack());

        // Left click on the target slot to put the items to it
        leftClickSlot(gui, slotTo);

        // If there are items left in the cursor, then return them back to the original slot
        if (isStackEmpty(player.inventory.getItemStack()) == false)
        {
            ret = getStackSize(player.inventory.getItemStack()) != size;

            // Left click again on the from-slot to return the rest of the items to it
            leftClickSlot(gui, slotFrom);
        }

        return ret;
    }

    private static boolean shiftClickSlotWithCheck(GuiContainer gui, int slotNum)
    {
        Slot slot = gui.inventorySlots.getSlot(slotNum);

        if (slot == null || slot.getHasStack() == false)
        {
            return false;
        }

        int sizeOrig = getStackSize(slot.getStack());
        shiftClickSlot(gui, slotNum);

        return slot.getHasStack() == false || getStackSize(slot.getStack()) != sizeOrig;
    }

    public static void leftClickSlot(GuiContainer gui, int slot)
    {
        gui.mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 0, ClickType.PICKUP, gui.mc.player);
    }

    private static void rightClickSlot(GuiContainer gui, int slot)
    {
        gui.mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 1, ClickType.PICKUP, gui.mc.player);
    }

    public static void shiftClickSlot(GuiContainer gui, int slot)
    {
        gui.mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 0, ClickType.QUICK_MOVE, gui.mc.player);
    }

    public static void dropItemsFromCursor(GuiContainer gui)
    {
        gui.mc.playerController.windowClick(gui.inventorySlots.windowId, -999, 0, ClickType.PICKUP, gui.mc.player);
    }

    private static void dropStack(GuiContainer gui, int slot)
    {
        gui.mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 1, ClickType.THROW, gui.mc.player);
    }

    private static void dragSplitItemsIntoSlots(GuiContainer gui, List<Integer> targetSlots)
    {
        ItemStack stackInCursor = gui.mc.player.inventory.getItemStack();

        if (isStackEmpty(stackInCursor))
        {
            return;
        }

        if (targetSlots.size() == 1)
        {
            leftClickSlot(gui, targetSlots.get(0));
            return;
        }

        int numSlots = gui.inventorySlots.inventorySlots.size();
        int loops = targetSlots.size();

        // Start the drag
        gui.mc.playerController.windowClick(gui.inventorySlots.windowId, -999, 0, ClickType.QUICK_CRAFT, gui.mc.player);

        for (int i = 0; i < loops; i++)
        {
            int slotNum = targetSlots.get(i);

            if (slotNum >= numSlots)
            {
                break;
            }

            gui.mc.playerController.windowClick(gui.inventorySlots.windowId, targetSlots.get(i), 1, ClickType.QUICK_CRAFT, gui.mc.player);
        }

        // End the drag
        gui.mc.playerController.windowClick(gui.inventorySlots.windowId, -999, 2, ClickType.QUICK_CRAFT, gui.mc.player);
    }

    /**************************************************************
     * Compatibility code for pre-1.11 vs. 1.11+
     * Well kind of, as in make the differences minimal,
     * only requires changing these things for the ItemStack
     * related changes.
     *************************************************************/

    public static final ItemStack EMPTY_STACK = ItemStack.EMPTY;

    public static boolean isStackEmpty(ItemStack stack)
    {
        return stack.isEmpty();
    }

    public static int getStackSize(ItemStack stack)
    {
        return stack.getCount();
    }

    public static void setStackSize(ItemStack stack, int size)
    {
        stack.setCount(size);
    }
}
