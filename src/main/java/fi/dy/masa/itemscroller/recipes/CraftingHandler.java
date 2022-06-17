package fi.dy.masa.itemscroller.recipes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import fi.dy.masa.malilib.util.data.IntRange;
import fi.dy.masa.itemscroller.LiteModItemScroller;

public class CraftingHandler
{
    private static final Pattern CRAFTING_SCREEN_PATTERN = Pattern.compile("(?<screenclass>[a-zA-Z0-9.$_]+),(?<slotclass>[a-zA-Z0-9.$_]+),(?<outputslot>[0-9]+),(?<rangestart>[0-9]+)-(?<rangeend>[0-9]+)");

    private static final Map<CraftingOutputSlot, IntRange> CRAFTING_GRID_SLOTS = new HashMap<>();
    private static final Set<Class<? extends GuiContainer>> CRAFTING_GUIS = new HashSet<>();

    private static final ImmutableMap<String, Class<? extends GuiContainer>> INVENTORY_SCREEN_CLASS_OVERRIDES = ImmutableMap.of("net.minecraft.client.gui.inventory.GuiCrafting", GuiCrafting.class, "net.minecraft.client.gui.inventory.GuiInventory", GuiInventory.class);
    private static final ImmutableMap<String, Class<? extends Slot>> SLOT_CLASS_OVERRIDES = ImmutableMap.of("net.minecraft.inventory.SlotCrafting", SlotCrafting.class);

    public static void updateGridDefinitions(List<String> definitions)
    {
        CRAFTING_GRID_SLOTS.clear();
        CRAFTING_GUIS.clear();

        for (String str : definitions)
        {
            addCraftingGridDefinition(str);
        }

        // "net.minecraft.client.gui.inventory.GuiCrafting,net.minecraft.inventory.SlotCrafting,0,1-9", // vanilla Crafting Table
        // "net.minecraft.client.gui.inventory.GuiInventory,net.minecraft.inventory.SlotCrafting,0,1-4", // vanilla player inventory crafting grid

        //addCraftingGridDefinition(GuiCrafting.class.getName(), SlotCrafting.class.getName(), 0, new IntRange(1, 9));
        //addCraftingGridDefinition(GuiInventory.class.getName(), SlotCrafting.class.getName(), 0, new IntRange(1, 4));
    }

    protected static void addCraftingGridDefinition(String str)
    {
        try
        {
            Matcher matcher = CRAFTING_SCREEN_PATTERN.matcher(str);

            if (matcher.matches())
            {
                String guiClassName = matcher.group("screenclass");
                String slotClassName = matcher.group("slotclass");
                int outputSlot = Integer.parseInt(matcher.group("outputslot"));
                IntRange range = new IntRange(Integer.parseInt(matcher.group("rangestart")),
                                              Integer.parseInt(matcher.group("rangeend")));

                addCraftingGridDefinition(guiClassName, slotClassName, outputSlot, range);
            }
            else
            {
                LiteModItemScroller.logger.warn("addCraftingGridDefinition(): Failed to parse definition: '{}'", str);
            }
        }
        catch (Exception e)
        {
            LiteModItemScroller.logger.warn("addCraftingGridDefinition(): Failed to parse definition: '{}'", str);
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean addCraftingGridDefinition(String screenClassName, String slotClassName, int outputSlot, IntRange range)
    {
        try
        {
            Class<? extends GuiContainer> screenClass = INVENTORY_SCREEN_CLASS_OVERRIDES.get(screenClassName);
            Class<? extends Slot> slotClass = SLOT_CLASS_OVERRIDES.get(slotClassName);

            if (screenClass == null)
            {
                screenClass = (Class<? extends GuiContainer>) Class.forName(screenClassName);
            }

            if (slotClass == null)
            {
                slotClass = (Class<? extends Slot>) Class.forName(slotClassName);
            }

            if (screenClass == null || slotClass == null)
            {
                return false;
            }

            CRAFTING_GRID_SLOTS.put(new CraftingOutputSlot(screenClass, slotClass, outputSlot), range);
            CRAFTING_GUIS.add(screenClass);

            return true;
        }
        catch (Exception e)
        {
            LiteModItemScroller.logger.warn("addCraftingGridDefinition(): Failed to find classes for grid definition: screen: '{}', slot: '{}', outputSlot: {}, grid slot range: {}",
                                            screenClassName, slotClassName, outputSlot, range);
        }

        return false;
    }

    public static boolean isCraftingGui(GuiScreen gui)
    {
        return (gui instanceof GuiContainer) && CRAFTING_GUIS.contains(gui.getClass());
    }

    /**
     * Gets the crafting grid SlotRange associated with the given slot in the given gui, if any.
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
