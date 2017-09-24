package fi.dy.masa.itemscroller.event;

import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public class InputEvent extends Event
{
    protected GuiScreen gui;

    @Cancelable
    public static class MouseInputEventPre extends InputEvent
    {
        public MouseInputEventPre(GuiScreen gui)
        {
            this.gui = gui;
        }
    }

    @Cancelable
    public static class KeyboardInputEventPre extends InputEvent
    {
        public KeyboardInputEventPre(GuiScreen gui)
        {
            this.gui = gui;
        }
    }

    public GuiScreen getGui()
    {
        return this.gui;
    }
}
