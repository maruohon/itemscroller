package fi.dy.masa.itemscroller.util;

import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import fi.dy.masa.itemscroller.mixin.IMixinGuiContainer;
import fi.dy.masa.itemscroller.mixin.IMixinSlot;

public class AccessorUtils
{
    public static Slot getSlotAtPosition(GuiContainer gui, int x, int y)
    {
        return ((IMixinGuiContainer) gui).getSlotAtPositionInvoker(x, y);
    }

    public static void handleMouseClick(GuiContainer gui, Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        ((IMixinGuiContainer) gui).handleMouseClickInvoker(slotIn, slotId, mouseButton, type);
    }

    public static int getSelectedMerchantRecipe(GuiMerchant gui)
    {
        return ((IGuiMerchant) gui).getSelectedMerchantRecipe();
    }

    public static int getSlotIndex(Slot slot)
    {
        return ((IMixinSlot) slot).getSlotIndex();
    }
}
