package fi.dy.masa.itemscroller;

import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.event.ClientWorldChangeHandler;
import fi.dy.masa.itemscroller.event.InputHandler;
import fi.dy.masa.itemscroller.event.KeybindCallbacks;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.InputDispatcher;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;

public class InitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new Configs());

        InputHandler handler = new InputHandler();
        KeyBindManager.INSTANCE.registerKeyBindProvider(handler);
        InputDispatcher.INSTANCE.registerKeyboardInputHandler(handler);
        InputDispatcher.INSTANCE.registerMouseInputHandler(handler);

        ClientWorldChangeHandler listener = new ClientWorldChangeHandler();
        ClientWorldChangeEventDispatcher.INSTANCE.registerClientWorldChangeHandler(listener);

        KeybindCallbacks.getInstance().setCallbacks();
    }
}
