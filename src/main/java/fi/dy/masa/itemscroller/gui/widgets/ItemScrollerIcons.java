package fi.dy.masa.itemscroller.gui.widgets;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.malilib.gui.icon.BaseIcon;

public class ItemScrollerIcons extends BaseIcon
{
    public static final ResourceLocation ITEMSCROLLER_GUI_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_widgets.png");

    public static final ItemScrollerIcons TRADE_LIST_BACKGROUND  = new ItemScrollerIcons(0, 0, 106, 166, 0, 0);
    public static final ItemScrollerIcons TRADE_ARROW_AVAILABLE  = new ItemScrollerIcons(112, 0, 10, 9, 0, 0);
    public static final ItemScrollerIcons TRADE_ARROW_LOCKED     = new ItemScrollerIcons(112, 9, 10, 9, 0, 0);
    public static final ItemScrollerIcons SCROLL_BAR_6           = new ItemScrollerIcons(106, 0, 6, 167, 0, 0);
    public static final ItemScrollerIcons STAR_5                 = new ItemScrollerIcons(112, 18, 5, 5, 0, 0);

    private ItemScrollerIcons(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0);
    }

    private ItemScrollerIcons(int u, int v, int w, int h, int hoverOffU, int hoverOffV)
    {
        super(u, v, w, h, hoverOffU, hoverOffV);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return ITEMSCROLLER_GUI_TEXTURES;
    }
}
