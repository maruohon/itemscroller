package fi.dy.masa.itemscroller.gui.widgets;

import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.util.data.Identifier;
import fi.dy.masa.itemscroller.Reference;

public class ItemScrollerIcons
{
    public static final Identifier TEXTURE = new Identifier(Reference.MOD_ID, "textures/gui/gui_widgets.png");

    public static final Icon TRADE_LIST_BACKGROUND  = new BaseIcon(  0,  0, 106, 166, TEXTURE);
    public static final Icon TRADE_ARROW_AVAILABLE  = new BaseIcon(112,  0,  10,   9, TEXTURE);
    public static final Icon TRADE_ARROW_LOCKED     = new BaseIcon(112,  9,  10,   9, TEXTURE);
    public static final Icon SCROLL_BAR_6           = new BaseIcon(106,  0,   6, 167, TEXTURE);
    public static final Icon STAR_5                 = new BaseIcon(112, 18,   5,   5, TEXTURE);
}
