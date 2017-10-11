package fi.dy.masa.itemscroller.asm;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import fi.dy.masa.itemscroller.event.CraftingEventSlotChanged;

public class Hooks
{
    public static boolean fireCraftingEventSlotChanged(Container container, World world,
            InventoryCrafting craftingInventory, InventoryCraftResult craftResultInventory)
    {
        return MinecraftForge.EVENT_BUS.post(new CraftingEventSlotChanged(container, world, craftingInventory, craftResultInventory));
    }
}
