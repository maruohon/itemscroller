package fi.dy.masa.itemscroller;

import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.proxy.CommonProxy;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION,
    guiFactory = "fi.dy.masa.itemscroller.config.ItemScrollerGuiFactory",
    acceptedMinecraftVersions = "[1.7.10]")
public class ItemScroller
{
    @Mod.Instance(Reference.MOD_ID)
    public static ItemScroller instance;

    @SidedProxy(clientSide = "fi.dy.masa.itemscroller.proxy.ClientProxy", serverSide = "fi.dy.masa.itemscroller.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.registerEventHandlers();
        Configs.loadConfigsFromFile(event.getSuggestedConfigurationFile());
    }
}
