package fi.dy.masa.itemscroller.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;

public class ConfigScreen
{
    public static final ModInfo MOD_INFO = Reference.MOD_INFO;

    private static final BaseConfigTab GENERIC = new BaseConfigTab(MOD_INFO, "generic",  -1, Configs.Generic.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab HOTKEYS = new BaseConfigTab(MOD_INFO, "hotkeys", 200, Hotkeys.HOTKEY_LIST,     ConfigScreen::create);
    private static final BaseConfigTab LISTS   = new BaseConfigTab(MOD_INFO, "lists",   200, Configs.Lists.OPTIONS,   ConfigScreen::create);
    private static final BaseConfigTab TOGGLES = new BaseConfigTab(MOD_INFO, "toggles",  -1, Configs.Toggles.OPTIONS, ConfigScreen::create);

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            TOGGLES,
            HOTKEYS,
            LISTS
    );

    public static void open()
    {
        BaseScreen.openScreen(create());
    }

    public static BaseConfigScreen create()
    {
        // The parent screen should not be set here, to prevent infinite recursion via
        // the call to the parent's setWorldAndResolution -> initScreen -> switch tab -> etc.
        return new BaseConfigScreen(MOD_INFO, TABS, GENERIC, "itemscroller.title.screen.configs", Reference.MOD_VERSION);
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return TABS;
    }
}
