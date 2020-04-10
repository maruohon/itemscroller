package fi.dy.masa.itemscroller;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.itemscroller.event.forge.ForgeRenderEventHandler;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(Reference.MOD_ID)
public class ItemScroller
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public ItemScroller()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientInit);

        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    private void onClientInit(final FMLClientSetupEvent event)
    {
        // Make sure the mod being absent on the other network side does not cause
        // the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        MinecraftForge.EVENT_BUS.register(new ForgeRenderEventHandler());
    }
}
