package fi.dy.masa.itemscroller.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraftforge.common.config.ConfigElement;
import fi.dy.masa.itemscroller.Reference;

public class ItemScrollerConfigGui extends GuiConfig
{
    public ItemScrollerConfigGui(GuiScreen parent)
    {
        super(parent, getConfigElements(), Reference.MOD_ID, false, false, getTitle(parent));
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> configElements = new ArrayList<IConfigElement>();

        configElements.add(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_GENERIC)));
        configElements.add(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_LISTS)));
        configElements.add(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_DRAG_ENABLE)));
        configElements.add(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_SCROLLING_ENABLE)));

        return configElements;
    }

    private static String getTitle(GuiScreen parent)
    {
        return GuiConfig.getAbridgedConfigPath(Configs.configurationFile.toString());
    }
}
