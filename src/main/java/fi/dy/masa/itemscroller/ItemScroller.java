package fi.dy.masa.itemscroller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.itemscroller.event.forge.ForgeRenderEventHandler;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class ItemScroller
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public ItemScroller()
    {
        MinecraftForge.EVENT_BUS.register(new ForgeRenderEventHandler());

        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }
}
