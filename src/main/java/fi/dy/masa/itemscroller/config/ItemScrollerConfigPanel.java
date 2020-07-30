package fi.dy.masa.itemscroller.config;

import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.malilib.gui.config.ModConfigScreen;
import fi.dy.masa.malilib.gui.config.liteloader.BaseConfigPanel;

public class ItemScrollerConfigPanel extends BaseConfigPanel
{
    @Override
    protected String getPanelTitlePrefix()
    {
        return Reference.MOD_NAME + " options";
    }

    @Override
    protected void createSubPanels()
    {
        String modId = Reference.MOD_ID;

        this.addSubPanel((new ModConfigScreen(modId, Configs.Toggles.OPTIONS, "itemscroller.gui.button.config_gui.toggles")).setConfigElementsWidth(100));
        this.addSubPanel((new ModConfigScreen(modId, Configs.Generic.OPTIONS, "itemscroller.gui.button.config_gui.generic")).setConfigElementsWidth(160));
        this.addSubPanel((new ModConfigScreen(modId, Hotkeys.HOTKEY_LIST, "itemscroller.gui.button.config_gui.hotkeys")).setConfigElementsWidth(210));
    }
}
