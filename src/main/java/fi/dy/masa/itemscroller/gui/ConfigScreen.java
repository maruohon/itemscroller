package fi.dy.masa.itemscroller.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class ConfigScreen extends BaseConfigScreen
{
    private static final BaseConfigTab GENERIC = new BaseConfigTab("itemscroller.gui.button.config_gui.generic", 100, false, Configs.Generic.OPTIONS);
    private static final BaseConfigTab TOGGLES = new BaseConfigTab("itemscroller.gui.button.config_gui.toggles", 100, false, Configs.Toggles.OPTIONS);
    private static final BaseConfigTab HOTKEYS = new BaseConfigTab("itemscroller.gui.button.config_gui.hotkeys", 204, true, Hotkeys.HOTKEY_LIST);
    private static final BaseConfigTab LISTS   = new BaseConfigTab("itemscroller.gui.button.config_gui.lists", 204, false, Configs.Lists.OPTIONS);

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            TOGGLES,
            HOTKEYS,
            LISTS
    );

    private static ConfigTab tab = GENERIC;

    public ConfigScreen()
    {
        super(10, 50, Reference.MOD_ID, null, TABS, "itemscroller.gui.title.configs");
    }

    @Override
    public ConfigTab getCurrentTab()
    {
        return tab;
    }

    @Override
    public void setCurrentTab(ConfigTab tab)
    {
        ConfigScreen.tab = tab;
    }
}
