package fi.dy.masa.itemscroller;

import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.event.ClientWorldChangeHandler;
import fi.dy.masa.itemscroller.event.InputHandler;
import fi.dy.masa.itemscroller.event.KeybindCallbacks;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.event.IInitializationHandler;

public class InitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(Reference.MOD_ID, new Configs());

        InputHandler handler = new InputHandler();
        InputEventDispatcher.getKeyBindManager().registerKeyBindProvider(handler);
        InputEventDispatcher.getInputManager().registerKeyboardInputHandler(handler);
        InputEventDispatcher.getInputManager().registerMouseInputHandler(handler);

        ClientWorldChangeHandler listener = new ClientWorldChangeHandler();
        ClientWorldChangeEventDispatcher.INSTANCE.registerClientWorldChangeHandler(listener);

        KeybindCallbacks.getInstance().setCallbacks();
    }
}
