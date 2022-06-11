package fi.dy.masa.itemscroller.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import fi.dy.masa.malilib.util.game.wrap.NbtWrap;
import fi.dy.masa.malilib.util.nbt.NbtUtils;
import fi.dy.masa.itemscroller.util.Constants;

public class VillagerData
{
    private final UUID uuid;
    private final List<Integer> favorites = new ArrayList<>();
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
        NbtWrap.putInt(tag, "ListPosition", this.tradeListPosition);
        NbtWrap.putInt(tag, "CurrentPage", this.lastPage);

        NBTTagList tagList = new NBTTagList();

        for (Integer val : this.favorites)
        {
            NbtWrap.addTag(tagList, NbtWrap.asIntTag(val.intValue()));
        }

        NbtWrap.putTag(tag, "Favorites", tagList);

        return tag;
    }

    @Nullable
    public static VillagerData fromNBT(NBTTagCompound tag)
    {
        if (NbtWrap.hasUUID(tag))
        {
            VillagerData data = new VillagerData(NbtUtils.readUUID(tag));

            data.tradeListPosition = NbtWrap.getInt(tag, "ListPosition");
            data.lastPage = NbtWrap.getInt(tag, "CurrentPage");
            NBTTagList tagList = NbtWrap.getList(tag, "Favorites", Constants.NBT.TAG_INT);

            final int count = NbtWrap.getListSize(tagList);
            data.favorites.clear();

            for (int i = 0; i < count; ++i)
            {
                data.favorites.add(NbtWrap.getIntAt(tagList, i));
            }

            return data;
        }

        return null;
    }
}
