package fi.dy.masa.itemscroller.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import fi.dy.masa.malilib.util.nbt.NbtUtils;
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

    public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        NbtUtils.writeUUID(tag, this.uuid);
        NbtUtils.putInt(tag, "ListPosition", this.tradeListPosition);
        NbtUtils.putInt(tag, "CurrentPage", this.lastPage);

        NBTTagList tagList = new NBTTagList();

        for (Integer val : this.favorites)
        {
            NbtUtils.addTag(tagList, NbtUtils.asIntTag(val.intValue()));
        }

        NbtUtils.putTag(tag, "Favorites", tagList);

        return tag;
    }

    @Nullable
    public static VillagerData fromNBT(NBTTagCompound tag)
    {
        if (NbtUtils.hasUUID(tag))
        {
            VillagerData data = new VillagerData(NbtUtils.readUUID(tag));

            data.tradeListPosition = NbtUtils.getInt(tag, "ListPosition");
            data.lastPage = NbtUtils.getInt(tag, "CurrentPage");
            NBTTagList tagList = NbtUtils.getList(tag, "Favorites", Constants.NBT.TAG_INT);

            final int count = NbtUtils.getListSize(tagList);
            data.favorites.clear();

            for (int i = 0; i < count; ++i)
            {
                data.favorites.add(NbtUtils.getIntAt(tagList, i));
            }

            return data;
        }

        return null;
    }
}
