package fi.dy.masa.itemscroller.asm;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import fi.dy.masa.itemscroller.Reference;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "fi.dy.masa.itemscroller.asm.", "fi.dy.masa.itemscroller.asm.ItemScrollerLoadingPlugin" })
public class ItemScrollerLoadingPlugin implements IFMLLoadingPlugin
{
    static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME + " CoreMod");
    public static boolean isObfuscated;

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] { GuiScreenTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass()
    {
        return ItemScrollerModContainer.class.getName();
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        isObfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
