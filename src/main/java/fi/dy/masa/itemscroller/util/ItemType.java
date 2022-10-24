package fi.dy.masa.itemscroller.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import malilib.util.game.wrap.ItemWrap;

/**
 * Wrapper class for ItemStack, which implements equals()
 * for the item, damage and NBT, but not stackSize.
 */
public class ItemType
{
    private final ItemStack stack;

    public ItemType(@Nonnull ItemStack stack)
    {
        this.stack = stack.copy();
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        //result = prime * result + ((stack == null) ? 0 : stack.hashCode());
        result = prime * result + this.stack.getMetadata();
        result = prime * result + this.stack.getItem().hashCode();
        NBTTagCompound tag = ItemWrap.getTag(this.stack);
        result = prime * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        ItemType other = (ItemType) obj;

        if (InventoryUtils.isStackEmpty(this.stack) || InventoryUtils.isStackEmpty(other.stack))
        {
            return InventoryUtils.isStackEmpty(this.stack) == InventoryUtils.isStackEmpty(other.stack);
        }
        else
        {
            if (this.stack.getMetadata() != other.stack.getMetadata())
            {
                return false;
            }

            if (this.stack.getItem() != other.stack.getItem())
            {
                return false;
            }

            return ItemStack.areItemStackTagsEqual(this.stack, other.stack);
        }
    }

    /**
     * Returns a map that has a list of the indices for each different item in the input list
     * @param stacks
     * @return
     */
    public static Map<ItemType, List<Integer>> getSlotsPerItem(ItemStack[] stacks)
    {
        Map<ItemType, List<Integer>> mapSlots = new HashMap<>();

        for (int i = 0; i < stacks.length; i++)
        {
            ItemStack stack = stacks[i];

            if (InventoryUtils.isStackEmpty(stack) == false)
            {
                ItemType item = new ItemType(stack);
                List<Integer> slots = mapSlots.computeIfAbsent(item, k -> new ArrayList<>());

                slots.add(i);
            }
        }

        return mapSlots;
    }
}
