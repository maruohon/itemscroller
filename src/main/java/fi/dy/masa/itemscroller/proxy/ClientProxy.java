package fi.dy.masa.itemscroller.proxy;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.event.InputEventHandler;
import fi.dy.masa.itemscroller.event.RenderEventHandler;

public class ClientProxy extends CommonProxy
{
    public static final KeyBinding KEY_DISABLE = new KeyBinding("itemscroller.desc.toggledisable", Keyboard.KEY_G, "itemscroller.category");
    public static final KeyBinding KEY_RECIPE  = new KeyBinding("itemscroller.desc.recipe", Keyboard.KEY_G, "itemscroller.category");

    @Override
    public void registerEventHandlers()
    {
        InputEventHandler ih = new InputEventHandler();

        FMLCommonHandler.instance().bus().register(new Configs());
        FMLCommonHandler.instance().bus().register(ih);

        MinecraftForge.EVENT_BUS.register(ih);
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());

        ClientRegistry.registerKeyBinding(KEY_DISABLE);
        ClientRegistry.registerKeyBinding(KEY_RECIPE);
    }
}
