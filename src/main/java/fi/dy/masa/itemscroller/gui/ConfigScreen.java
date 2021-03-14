package fi.dy.masa.itemscroller.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class ConfigScreen
{
    private static final BaseConfigTab GENERIC = new BaseConfigTab("itemscroller.gui.button.config_gui.generic", Reference.MOD_NAME,  -1, Configs.Generic.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab TOGGLES = new BaseConfigTab("itemscroller.gui.button.config_gui.toggles", Reference.MOD_NAME,  -1, Configs.Toggles.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab HOTKEYS = new BaseConfigTab("itemscroller.gui.button.config_gui.hotkeys", Reference.MOD_NAME, 200, Hotkeys.HOTKEY_LIST, ConfigScreen::create);
    private static final BaseConfigTab LISTS   = new BaseConfigTab("itemscroller.gui.button.config_gui.lists",   Reference.MOD_NAME, 200, Configs.Lists.OPTIONS, ConfigScreen::create);

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            TOGGLES,
            HOTKEYS,
            LISTS
    );

    public static BaseConfigScreen create(@Nullable GuiScreen currentScreen)
    {
        return new BaseConfigScreen(Reference.MOD_ID, null, TABS, GENERIC, "itemscroller.gui.title.configs");
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return TABS;
    }
}
