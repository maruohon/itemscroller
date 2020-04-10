package fi.dy.masa.itemscroller.util;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import fi.dy.masa.itemscroller.ItemScroller;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class AccessorUtils
{
    private static final MethodHandle methodHandle_handleMouseClick = MethodHandleUtils.getMethodHandleVirtual(ContainerScreen.class,
            new String[] { "func_184098_a", "handleMouseClick" }, Slot.class, int.class, int.class, ClickType.class);
    private static final Field field_MerchantScreen_selectedMerchantRecipe = ObfuscationReflectionHelper.findField(MerchantScreen.class, "field_147041_z"); // selectedMerchantRecipe

    public static Slot getSlotUnderMouse(ContainerScreen<?> gui)
    {
        return gui.getSlotUnderMouse();
    }

    public static Slot getSlotAtPosition(ContainerScreen<?> gui, int x, int y)
    {
        Container container = gui.getContainer();

        for (int i = 0; i < container.inventorySlots.size(); ++i)
        {
            Slot slot = container.inventorySlots.get(i);

            if (slot.isEnabled() && isSlotSelected(gui, slot, x, y))
            {
                return slot;
            }
        }

        return null;
    }

    public static void handleMouseClick(ContainerScreen<?> gui, Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        try
        {
            methodHandle_handleMouseClick.invokeExact(gui, slotIn, slotId, mouseButton, type);
        }
        catch (Throwable e)
        {
            ItemScroller.logger.error("Error while trying invoke GuiContainer#handleMouseClick() from {}", gui.getClass().getName(), e);
        }
    }

    public static int getGuiLeft(ContainerScreen<?> gui)
    {
        return gui.getGuiLeft();
    }

    public static int getGuiTop(ContainerScreen<?> gui)
    {
        return gui.getGuiTop();
    }

    public static int getGuiXSize(ContainerScreen<?> gui)
    {
        return gui.getXSize();
    }

    public static int getGuiYSize(ContainerScreen<?> gui)
    {
        return gui.getYSize();
    }

    public static int getSelectedMerchantRecipe(MerchantScreen gui)
    {
        try
        {
            return field_MerchantScreen_selectedMerchantRecipe.getInt(gui);
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    public static int getSlotIndex(Slot slot)
    {
        return slot.getSlotIndex();
    }

    public static boolean isSlotSelected(ContainerScreen<?> gui, Slot slotIn, int mouseX, int mouseY)
    {
        return isPointInRegion(gui, slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }

    public static boolean isPointInRegion(ContainerScreen<?> gui, int x, int y, int width, int height, int mouseX, int mouseY)
    {
        mouseX -= gui.getGuiLeft();
        mouseY -= gui.getGuiTop();

        return mouseX >= (x - 1) && mouseX < (x + width + 1) && mouseY >= (y - 1) && mouseY < (y + height + 1);
    }
}
