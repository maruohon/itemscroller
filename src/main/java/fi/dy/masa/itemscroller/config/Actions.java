package fi.dy.masa.itemscroller.config;

import malilib.action.util.ActionUtils;
import malilib.action.NamedAction;
import malilib.listener.EventListener;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.gui.ConfigScreen;

public class Actions
{
    public static final NamedAction OPEN_CONFIG_SCREEN = register("openConfigScreen", ConfigScreen::open);

    public static void init()
    {
        ActionUtils.registerBooleanConfigActions(Configs.Generic.OPTIONS);
        ActionUtils.registerBooleanConfigActions(Configs.Toggles.OPTIONS);
    }

    private static NamedAction register(String name, EventListener action)
    {
        return ActionUtils.register(Reference.MOD_INFO, name, action);
    }
}
