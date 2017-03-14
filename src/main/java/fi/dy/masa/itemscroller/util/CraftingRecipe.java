package fi.dy.masa.itemscroller.util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Configs.SlotRange;
import fi.dy.masa.itemscroller.event.InputEventHandler;

public class CraftingRecipe
{
    private ItemStack result = InputEventHandler.EMPTY_STACK;
    private ItemStack[] recipe = new ItemStack[9];

    public CraftingRecipe()
    {
        this.ensureRecipeSizeAndClearRecipe(9);
    }

    public void ensureRecipeSize(int size)
    {
        if (this.getRecipeLength() != size)
        {
            this.recipe = new ItemStack[size];
        }
    }

    public void clearRecipe()
    {
        for (int i = 0; i < this.recipe.length; i++)
        {
            this.recipe[i] = InputEventHandler.EMPTY_STACK;
        }

        this.result = InputEventHandler.EMPTY_STACK;
    }

    public void ensureRecipeSizeAndClearRecipe(int size)
    {
        this.ensureRecipeSize(size);
        this.clearRecipe();
    }

    public void storeCraftingRecipe(GuiContainer gui, Slot slot)
    {
        SlotRange range = Configs.getCraftingGridSlots(gui, slot);

        if (range != null && slot.getHasStack())
        {
            if (InputEventHandler.areStacksEqual(this.getResult(), slot.getStack()) == false ||
                this.getRecipeLength() != range.getSlotCount())
            {
                int gridSize = range.getSlotCount();
                int numSlots = gui.inventorySlots.inventorySlots.size();

                this.ensureRecipeSizeAndClearRecipe(gridSize);

                for (int i = 0, s = range.getFirst(); i < gridSize && s < numSlots; i++, s++)
                {
                    Slot slotTmp = gui.inventorySlots.getSlot(s);
                    this.recipe[i] = slotTmp.getHasStack() ? slotTmp.getStack().copy() : InputEventHandler.EMPTY_STACK;
                }

                this.result = slot.getStack().copy();
            }
        }
    }

    public void copyRecipeFrom(CraftingRecipe other)
    {
        int size = other.getRecipeLength();
        ItemStack[] otherRecipe = other.getRecipe();

        this.ensureRecipeSizeAndClearRecipe(size);

        for (int i = 0; i < size; i++)
        {
            this.recipe[i] = InputEventHandler.isStackEmpty(otherRecipe[i]) == false ? otherRecipe[i].copy() : InputEventHandler.EMPTY_STACK;
        }

        this.result = InputEventHandler.isStackEmpty(other.getResult()) == false ? other.getResult().copy() : InputEventHandler.EMPTY_STACK;
    }

    public ItemStack getResult()
    {
        return this.result;
    }

    public int getRecipeLength()
    {
        return this.recipe.length;
    }

    public ItemStack[] getRecipe()
    {
        return this.recipe;
    }

    public boolean isValid()
    {
        return InputEventHandler.isStackEmpty(this.getResult()) == false;
    }
}
