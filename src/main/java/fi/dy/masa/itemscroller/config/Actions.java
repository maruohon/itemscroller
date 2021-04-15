package fi.dy.masa.itemscroller.config;

import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.gui.ConfigScreen;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.listener.EventListener;

public class Actions
{
    public static final NamedAction OPEN_CONFIG_SCREEN = register("openConfigScreen", ConfigScreen::open);

    public static void init()
    {
        register("toggleModFeaturesEnabled", Configs.Toggles.MAIN_TOGGLE);
    }

    private static NamedAction register(String name, EventListener action)
    {
        return NamedAction.register(Reference.MOD_INFO, name, action);
    }

    private static NamedAction register(String name, HotkeyedBooleanConfig config)
    {
        return NamedAction.registerToggleKey(Reference.MOD_INFO, name, config);
    }
}
