package fi.dy.masa.itemscroller;

import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.event.ClientWorldChangeHandler;
import fi.dy.masa.itemscroller.event.InputHandler;
import fi.dy.masa.itemscroller.event.KeybindCallbacks;
import fi.dy.masa.itemscroller.gui.ConfigScreen;
import fi.dy.masa.itemscroller.config.Actions;
import fi.dy.masa.malilib.config.BaseModConfig;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcher;
import fi.dy.masa.malilib.input.InputDispatcher;
import fi.dy.masa.malilib.input.HotkeyManager;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;

public class InitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(BaseModConfig.createDefaultModConfig(Reference.MOD_INFO, Configs.CONFIG_VERSION, Configs.CATEGORIES));
        ConfigTabRegistry.INSTANCE.registerConfigTabProvider(Reference.MOD_INFO, ConfigScreen::getConfigTabs);

        InputHandler handler = new InputHandler();
        HotkeyManager.INSTANCE.registerHotkeyProvider(handler);
        InputDispatcher.INSTANCE.registerKeyboardInputHandler(handler);
        InputDispatcher.INSTANCE.registerMouseInputHandler(handler);

        ClientWorldChangeHandler listener = new ClientWorldChangeHandler();
        ClientWorldChangeEventDispatcher.INSTANCE.registerClientWorldChangeHandler(listener);

        Actions.init();
        KeybindCallbacks.INSTANCE.setCallbacks();
    }
}
