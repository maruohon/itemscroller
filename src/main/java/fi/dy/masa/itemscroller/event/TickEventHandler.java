package fi.dy.masa.itemscroller.event;

import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;

public class TickEventHandler implements IClientTickHandler
{
    @Override
    public void onClientTick(Minecraft mc)
    {
        KeybindCallbacks.getInstance().onTick(mc);
    }
}
