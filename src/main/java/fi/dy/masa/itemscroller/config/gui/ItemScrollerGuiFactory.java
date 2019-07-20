package fi.dy.masa.itemscroller.config.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.gui.GuiConfigs;

public class ItemScrollerGuiFactory extends DefaultGuiFactory
{
    public ItemScrollerGuiFactory()
    {
        super(Reference.MOD_ID, Reference.MOD_NAME + " configs");
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent)
    {
        GuiConfigs gui = new GuiConfigs();
        gui.setParent(parent);
        return gui;
    }
}
