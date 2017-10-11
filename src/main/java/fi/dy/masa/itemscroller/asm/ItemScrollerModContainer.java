package fi.dy.masa.itemscroller.asm;

import java.util.Arrays;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import fi.dy.masa.itemscroller.Reference;

public class ItemScrollerModContainer extends DummyModContainer
{
    public ItemScrollerModContainer()
    {
        super(new ModMetadata());

        ModMetadata meta = this.getMetadata();
        meta.modId = Reference.MOD_ID + "-coremod";
        meta.name = Reference.MOD_NAME + " CoreMod";
        meta.description = "Adds a crafting event hook";
        meta.version = Reference.MOD_VERSION;
        meta.authorList = Arrays.asList("masa");
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }
}