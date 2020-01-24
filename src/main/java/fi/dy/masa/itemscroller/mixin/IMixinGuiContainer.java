package fi.dy.masa.itemscroller.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.client.gui.inventory.GuiContainer.class)
public interface IMixinGuiContainer
{
    @Invoker("getSlotAtPosition")
    net.minecraft.inventory.Slot getSlotAtPositionInvoker(int x, int y);

    @Invoker("handleMouseClick")
    void handleMouseClickInvoker(net.minecraft.inventory.Slot slotIn, int slotId, int mouseButton, net.minecraft.inventory.ClickType type);
}
