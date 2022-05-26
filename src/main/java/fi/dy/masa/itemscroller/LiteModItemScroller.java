package fi.dy.masa.itemscroller;

import java.io.File;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.itemscroller.gui.ItemScrollerConfigPanel;
import fi.dy.masa.itemscroller.input.KeybindCallbacks;

public class LiteModItemScroller implements LiteMod, Configurable, Tickable
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public LiteModItemScroller()
    {
    }

    @Override
    public String getName()
    {
        return Reference.MOD_NAME;
    }

    @Override
    public String getVersion()
    {
        return Reference.MOD_VERSION;
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass()
    {
        return ItemScrollerConfigPanel.class;
    }

    @Override
    public void init(File configPath)
    {
        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(new InitHandler());
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }

    @Override
    public void onTick(Minecraft mc, float partialTicks, boolean inGame, boolean clock)
    {
        KeybindCallbacks.INSTANCE.onTick(mc);
    }
}
