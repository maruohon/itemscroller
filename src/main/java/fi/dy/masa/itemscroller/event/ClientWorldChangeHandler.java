package fi.dy.masa.itemscroller.event;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.WorldClient;

import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.villager.VillagerDataStorage;

public class ClientWorldChangeHandler implements malilib.event.ClientWorldChangeHandler
{
    @Override
    public void onPreClientWorldChange(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter)
    {
        // Quitting to main menu, save the settings before the integrated server gets shut down
        if (worldBefore != null && worldAfter == null)
        {
            RecipeStorage.INSTANCE.writeToDisk();
            VillagerDataStorage.INSTANCE.writeToDisk();
        }
    }

    @Override
    public void onPostClientWorldChange(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter)
    {
        // Logging in to a world, load the data
        if (worldBefore == null && worldAfter != null)
        {
            RecipeStorage.INSTANCE.readFromDisk();
            VillagerDataStorage.INSTANCE.readFromDisk();
        }
    }
}
