package fi.dy.masa.itemscroller.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class ConfigScreen
{
    private static final BaseConfigTab GENERIC = new BaseConfigTab("itemscroller.gui.button.config_gui.generic", Reference.MOD_NAME, 100, Configs.Generic.OPTIONS);
    private static final BaseConfigTab TOGGLES = new BaseConfigTab("itemscroller.gui.button.config_gui.toggles", Reference.MOD_NAME,  60, Configs.Toggles.OPTIONS);
    private static final BaseConfigTab HOTKEYS = new BaseConfigTab("itemscroller.gui.button.config_gui.hotkeys", Reference.MOD_NAME, 200, Hotkeys.HOTKEY_LIST);
    private static final BaseConfigTab LISTS   = new BaseConfigTab("itemscroller.gui.button.config_gui.lists",   Reference.MOD_NAME, 200, Configs.Lists.OPTIONS);

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            TOGGLES,
            HOTKEYS,
            LISTS
    );

    public static BaseConfigScreen create()
    {
        return new BaseConfigScreen(10, 50, Reference.MOD_ID, null, TABS, GENERIC, "itemscroller.gui.title.configs");
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return TABS;
    }
}
