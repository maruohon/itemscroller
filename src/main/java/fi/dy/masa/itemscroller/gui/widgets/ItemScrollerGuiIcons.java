package fi.dy.masa.itemscroller.gui.widgets;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;

public class ItemScrollerGuiIcons extends BaseGuiIcon
{
    public static final ResourceLocation ITEMSCROLLER_GUI_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_widgets.png");

    public static final ItemScrollerGuiIcons TRADE_LIST_BACKGROUND  = new ItemScrollerGuiIcons(  0,  0, 106, 166, 0, 0);
    public static final ItemScrollerGuiIcons TRADE_ARROW_AVAILABLE  = new ItemScrollerGuiIcons(112,  0,  10,   9, 0, 0);
    public static final ItemScrollerGuiIcons TRADE_ARROW_LOCKED     = new ItemScrollerGuiIcons(112,  9,  10,   9, 0, 0);
    public static final ItemScrollerGuiIcons SCROLL_BAR_6           = new ItemScrollerGuiIcons(106,  0,   6, 167, 0, 0);
    public static final ItemScrollerGuiIcons STAR_5                 = new ItemScrollerGuiIcons(112, 18,   5,   5, 0, 0);

    private ItemScrollerGuiIcons(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0);
    }

    private ItemScrollerGuiIcons(int u, int v, int w, int h, int hoverOffU, int hoverOffV)
    {
        super(u, v, w, h, hoverOffU, hoverOffV);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return ITEMSCROLLER_GUI_TEXTURES;
    }
}
