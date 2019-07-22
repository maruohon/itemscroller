package fi.dy.masa.itemscroller.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;
import fi.dy.masa.malilib.config.gui.ConfigGuiTabBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;

public class GuiConfigs extends GuiConfigsBase
{
    private static final ConfigGuiTabBase GENERIC = new ConfigGuiTabBase("itemscroller.gui.button.config_gui.generic",  100, false, Configs.Generic.OPTIONS);
    private static final ConfigGuiTabBase TOGGLES = new ConfigGuiTabBase("itemscroller.gui.button.config_gui.toggles",  100, false, Configs.Toggles.OPTIONS);
    private static final ConfigGuiTabBase HOTKEYS = new ConfigGuiTabBase("itemscroller.gui.button.config_gui.hotkeys",  204, true, Hotkeys.HOTKEY_LIST);
    private static final ConfigGuiTabBase LISTS   = new ConfigGuiTabBase("itemscroller.gui.button.config_gui.lists",    204, false, Configs.Lists.OPTIONS);

    private static final ImmutableList<IConfigGuiTab> TABS = ImmutableList.of(
            GENERIC,
            TOGGLES,
            HOTKEYS,
            LISTS
    );

    private static IConfigGuiTab tab = GENERIC;

    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, TABS, "itemscroller.gui.title.configs");
    }

    @Override
    public IConfigGuiTab getCurrentTab()
    {
        return tab;
    }

    @Override
    public void setCurrentTab(IConfigGuiTab tab)
    {
        GuiConfigs.tab = tab;
    }
}
