package fi.dy.masa.itemscroller.gui;

import fi.dy.masa.malilib.gui.config.liteloader.RedirectingConfigPanel;

public class ItemScrollerConfigPanel extends RedirectingConfigPanel
{
    public ItemScrollerConfigPanel()
    {
        super(ConfigScreen::create);
    }
}
