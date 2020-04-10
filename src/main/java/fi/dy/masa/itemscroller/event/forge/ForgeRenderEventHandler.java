package fi.dy.masa.itemscroller.event.forge;

import fi.dy.masa.itemscroller.event.RenderEventHandler;
import fi.dy.masa.itemscroller.util.InputUtils;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeRenderEventHandler
{
    @SubscribeEvent
    public void onDrawBackgroundPost(GuiContainerEvent.DrawBackground event)
    {
        RenderEventHandler.instance().onDrawBackgroundPost();
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        RenderEventHandler.instance().onDrawScreenPost();
    }

    @SubscribeEvent
    public void onPotionShift(PotionShiftEvent event)
    {
        if (InputUtils.isRecipeViewOpen())
        {
            event.setCanceled(true);
        }
    }
}
