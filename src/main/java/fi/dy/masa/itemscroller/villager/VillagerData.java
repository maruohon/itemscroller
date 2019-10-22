package fi.dy.masa.itemscroller.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import fi.dy.masa.itemscroller.util.Constants;

public class VillagerData
{
    private final UUID uuid;
    private List<Integer> favorites = new ArrayList<>();
    private int tradeListPosition;
    private int lastPage;

    VillagerData(UUID uuid)
    {
        this.uuid = uuid;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public int getTradeListPosition()
    {
        return this.tradeListPosition;
    }

    void setTradeListPosition(int position)
    {
        this.tradeListPosition = position;
    }

    public int getLastPage()
    {
        return this.lastPage;
    }

    void setLastPage(int page)
    {
        this.lastPage = page;
    }

    void toggleFavorite(int tradeIndex)
    {
        if (this.favorites.contains(tradeIndex))
        {
            this.favorites.remove(Integer.valueOf(tradeIndex));
        }
        else
        {
            this.favorites.add(tradeIndex);
        }
    }

    public List<Integer> getFavorites()
    {
        return this.favorites;
    }

    public CompoundNBT toNBT()
    {
        CompoundNBT tag = new CompoundNBT();

        tag.putLong("UUIDM", this.uuid.getMostSignificantBits());
        tag.putLong("UUIDL", this.uuid.getLeastSignificantBits());
        tag.putInt("ListPosition", this.tradeListPosition);
        tag.putInt("CurrentPage", this.lastPage);

        ListNBT tagList = new ListNBT();

        for (Integer val : this.favorites)
        {
            tagList.add(new IntNBT(val.intValue()));
        }

        tag.put("Favorites", tagList);

        return tag;
    }

    @Nullable
    public static VillagerData fromNBT(CompoundNBT tag)
    {
        if (tag.contains("UUIDM", Constants.NBT.TAG_LONG) && tag.contains("UUIDL", Constants.NBT.TAG_LONG))
        {
            VillagerData data = new VillagerData(new UUID(tag.getLong("UUIDM"), tag.getLong("UUIDL")));

            data.tradeListPosition = tag.getInt("ListPosition");
            data.lastPage = tag.getInt("CurrentPage");
            ListNBT tagList = tag.getList("Favorites", Constants.NBT.TAG_INT);

            final int count = tagList.size();
            data.favorites.clear();

            for (int i = 0; i < count; ++i)
            {
                data.favorites.add(tagList.getInt(i));
            }

            return data;
        }

        return null;
    }
}
