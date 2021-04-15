package fi.dy.masa.itemscroller.recipes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import fi.dy.masa.itemscroller.LiteModItemScroller;
import fi.dy.masa.malilib.util.data.IntRange;

public class CraftingHandler
{
    private static final Map<CraftingOutputSlot, IntRange> CRAFTING_GRID_SLOTS = new HashMap<>();
    private static final Set<Class<? extends GuiContainer>> CRAFTING_GUIS = new HashSet<>();

    public static void updateGridDefinitions()
    {
        CRAFTING_GRID_SLOTS.clear();
        CRAFTING_GUIS.clear();

        // "net.minecraft.client.gui.inventory.GuiCrafting,net.minecraft.inventory.SlotCrafting,0,1-9", // vanilla Crafting Table
        addCraftingGridDefinition(GuiCrafting.class.getName(), SlotCrafting.class.getName(), 0, new IntRange(1, 9));
        //"net.minecraft.client.gui.inventory.GuiInventory,net.minecraft.inventory.SlotCrafting,0,1-4", // vanilla player inventory crafting grid
        addCraftingGridDefinition(GuiInventory.class.getName(), SlotCrafting.class.getName(), 0, new IntRange(1, 4));
    }

    @SuppressWarnings("unchecked")
    public static boolean addCraftingGridDefinition(String guiClassName, String slotClassName, int outputSlot, IntRange range)
    {
        try
        {
            Class<? extends GuiContainer> guiClass = (Class<? extends GuiContainer>) Class.forName(guiClassName);
            Class<? extends Slot> slotClass = (Class<? extends Slot>) Class.forName(slotClassName);

            CRAFTING_GRID_SLOTS.put(new CraftingOutputSlot(guiClass, slotClass, outputSlot), range);
            CRAFTING_GUIS.add(guiClass);

            return true;
        }
        catch (Exception e)
        {
            LiteModItemScroller.logger.warn("addCraftingGridDefinition(): Failed to find classes for grid definition: gui: '{}', slot: '{}', outputSlot: {}",
                    guiClassName, slotClassName, outputSlot);
        }

        return false;
    }

    public static boolean isCraftingGui(GuiScreen gui)
    {
        return (gui instanceof GuiContainer) && CRAFTING_GUIS.contains(((GuiContainer) gui).getClass());
    }

    /**
     * Gets the crafting grid SlotRange associated with the given slot in the given gui, if any.
     * @param gui
     * @param slot
     * @return the SlotRange of the crafting grid, or null, if the given slot is not a crafting output slot
     */
    @Nullable
    public static IntRange getCraftingGridSlots(GuiContainer gui, Slot slot)
    {
        return CRAFTING_GRID_SLOTS.get(CraftingOutputSlot.from(gui, slot));
    }

    @Nullable
    public static Slot getFirstCraftingOutputSlotForGui(GuiContainer gui)
    {
        if (CRAFTING_GUIS.contains(gui.getClass()))
        {
            for (Slot slot : gui.inventorySlots.inventorySlots)
            {
                if (getCraftingGridSlots(gui, slot) != null)
                {
                    return slot;
                }
            }
        }

        return null;
    }

    public static class CraftingOutputSlot
    {
        private final Class<? extends GuiContainer> guiClass;
        private final Class<? extends Slot> slotClass;
        private final int outputSlot;

        private CraftingOutputSlot (Class<? extends GuiContainer> guiClass, Class<? extends Slot> slotClass, int outputSlot)
        {
            this.guiClass = guiClass;
            this.slotClass = slotClass;
            this.outputSlot = outputSlot;
        }

        public static CraftingOutputSlot from(GuiContainer gui, Slot slot)
        {
            return new CraftingOutputSlot(gui.getClass(), slot.getClass(), slot.slotNumber);
        }

        public Class<? extends GuiContainer> getGuiClass()
        {
            return this.guiClass;
        }

        public Class<? extends Slot> getSlotClass()
        {
            return this.slotClass;
        }

        public int getSlotNumber()
        {
            return this.outputSlot;
        }

        public boolean matches(GuiContainer gui, Slot slot, int outputSlot)
        {
            return outputSlot == this.outputSlot && gui.getClass() == this.guiClass && slot.getClass() == this.slotClass;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) { return true; }
            if (o == null || this.getClass() != o.getClass()) { return false; }

            CraftingOutputSlot that = (CraftingOutputSlot) o;

            if (this.outputSlot != that.outputSlot) { return false; }
            if (!this.guiClass.equals(that.guiClass)) { return false; }
            return this.slotClass.equals(that.slotClass);
        }

        @Override
        public int hashCode()
        {
            int result = this.guiClass.hashCode();
            result = 31 * result + this.slotClass.hashCode();
            result = 31 * result + this.outputSlot;
            return result;
        }
    }
}
