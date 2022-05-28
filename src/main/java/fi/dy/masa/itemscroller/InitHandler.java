package fi.dy.masa.itemscroller;

import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.config.JsonModConfig.ConfigDataUpdater;
import fi.dy.masa.malilib.config.util.ConfigUpdateUtils.KeyBindSettingsResetter;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.itemscroller.config.Actions;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.event.ClientWorldChangeHandler;
import fi.dy.masa.itemscroller.gui.ConfigScreen;
import fi.dy.masa.itemscroller.input.ItemScrollerHotkeyProvider;
import fi.dy.masa.itemscroller.input.KeybindCallbacks;
import fi.dy.masa.itemscroller.input.KeyboardInputHandlerImpl;
import fi.dy.masa.itemscroller.input.MouseClickHandlerImpl;
import fi.dy.masa.itemscroller.input.MouseMoveHandlerImpl;
import fi.dy.masa.itemscroller.input.MouseScrollHandlerImpl;

public class InitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        // Reset all KeyBindSettings when updating to the first post-malilib-refactor version
        ConfigDataUpdater updater = new KeyBindSettingsResetter(ItemScrollerHotkeyProvider.INSTANCE::getAllHotkeys, 0);
        Registry.CONFIG_MANAGER.registerConfigHandler(JsonModConfig.createJsonModConfig(Reference.MOD_INFO, Configs.CURRENT_VERSION, Configs.CATEGORIES, updater));

        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, ConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, ConfigScreen::getConfigTabs);

        Registry.HOTKEY_MANAGER.registerHotkeyProvider(ItemScrollerHotkeyProvider.INSTANCE);
        Registry.INPUT_DISPATCHER.registerKeyboardInputHandler(new KeyboardInputHandlerImpl());
        Registry.INPUT_DISPATCHER.registerMouseClickHandler(new MouseClickHandlerImpl());
        Registry.INPUT_DISPATCHER.registerMouseScrollHandler(new MouseScrollHandlerImpl());
        Registry.INPUT_DISPATCHER.registerMouseMoveHandler(new MouseMoveHandlerImpl());

        ClientWorldChangeHandler listener = new ClientWorldChangeHandler();
        Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER.registerClientWorldChangeHandler(listener);

        Actions.init();
        KeybindCallbacks.INSTANCE.setCallbacks();
    }
}
