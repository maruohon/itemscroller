package fi.dy.masa.itemscroller.asm;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import fi.dy.masa.itemscroller.event.InputEvent.KeyboardInputEventPre;
import fi.dy.masa.itemscroller.event.InputEvent.MouseInputEventPre;

public class Hooks
{
    public static boolean fireMouseInputEvent(GuiScreen gui)
    {
        return MinecraftForge.EVENT_BUS.post(new MouseInputEventPre(gui));
    }

    public static boolean fireKeyboardInputEvent(GuiScreen gui)
    {
        return MinecraftForge.EVENT_BUS.post(new KeyboardInputEventPre(gui));
    }
}
