package fi.dy.masa.itemscroller.event;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class CraftingEventSlotChanged extends Event
{
    private final World world;
    private final InventoryCrafting craftingInventory;
    private final InventoryCraftResult craftResultInventory;
    private final Container container;

    public CraftingEventSlotChanged(Container container, World world, InventoryCrafting craftingInventory, InventoryCraftResult craftResultInventory)
    {
        this.container = container;
        this.world = world;
        this.craftingInventory = craftingInventory;
        this.craftResultInventory = craftResultInventory;
    }

    public Container getContainer()
    {
        return this.container;
    }

    public World getWorld()
    {
        return this.world;
    }

    public InventoryCrafting getCraftingInventory()
    {
        return this.craftingInventory;
    }

    public InventoryCraftResult getCraftResultInventory()
    {
        return this.craftResultInventory;
    }
}
