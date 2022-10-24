package fi.dy.masa.itemscroller.gui;

import malilib.gui.config.liteloader.RedirectingConfigPanel;

public class ItemScrollerConfigPanel extends RedirectingConfigPanel
{
    public ItemScrollerConfigPanel()
    {
        super(ConfigScreen::create);
    }
}
