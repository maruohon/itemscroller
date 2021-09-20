package fi.dy.masa.itemscroller;

import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.event.ClientWorldChangeHandler;
import fi.dy.masa.itemscroller.event.InputHandler;
import fi.dy.masa.itemscroller.event.KeybindCallbacks;
import fi.dy.masa.itemscroller.gui.ConfigScreen;
import fi.dy.masa.itemscroller.config.Actions;
import fi.dy.masa.malilib.config.BaseModConfig;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.registry.Registry;

public class InitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        Registry.CONFIG_MANAGER.registerConfigHandler(BaseModConfig.createDefaultModConfig(Reference.MOD_INFO, Configs.CONFIG_VERSION, Configs.CATEGORIES));
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, ConfigScreen::getConfigTabs);

        InputHandler handler = new InputHandler();
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(handler);
        Registry.INPUT_DISPATCHER.registerKeyboardInputHandler(handler);
        Registry.INPUT_DISPATCHER.registerMouseInputHandler(handler);

        ClientWorldChangeHandler listener = new ClientWorldChangeHandler();
        Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER.registerClientWorldChangeHandler(listener);

        Actions.init();
        KeybindCallbacks.INSTANCE.setCallbacks();
    }
}
