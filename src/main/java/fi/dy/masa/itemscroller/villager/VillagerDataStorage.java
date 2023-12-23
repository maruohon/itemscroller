package fi.dy.masa.itemscroller.villager;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import malilib.util.FileUtils;
import malilib.util.StringUtils;
import malilib.util.game.wrap.NbtWrap;
import fi.dy.masa.itemscroller.ItemScroller;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.util.Constants;

public class VillagerDataStorage
{
    public static final VillagerDataStorage INSTANCE = new VillagerDataStorage();

    private final Map<UUID, VillagerData> data = new HashMap<>();
    private UUID lastInteractedUUID;
    private boolean dirty;

    public void setLastInteractedUUID(UUID uuid)
    {
        this.lastInteractedUUID = uuid;
    }

    public boolean hasInteractionTarget()
    {
        return this.lastInteractedUUID != null;
    }

    @Nullable
    public VillagerData getDataForLastInteractionTarget()
    {
        return this.getDataFor(this.lastInteractedUUID, true);
    }

    public VillagerData getDataFor(@Nullable UUID uuid, boolean create)
    {
        VillagerData data = uuid != null ? this.data.get(uuid) : null;

        if (data == null && uuid != null && create)
        {
            this.setLastInteractedUUID(uuid);
            data = new VillagerData(uuid);
            this.data.put(uuid, data);
            this.dirty = true;
        }

        return data;
    }

    public void setTradeListPosition(int position)
    {
        VillagerData data = this.getDataFor(this.lastInteractedUUID, true);

        if (data != null)
        {
            data.setTradeListPosition(position);
            this.dirty = true;
        }
    }

    public void setLastPage(int page)
    {
        VillagerData data = this.getDataFor(this.lastInteractedUUID, true);

        if (data != null)
        {
            data.setLastPage(page);
            this.dirty = true;
        }
    }

    public void toggleFavorite(int tradeIndex)
    {
        VillagerData data = this.getDataFor(this.lastInteractedUUID, true);

        if (data != null)
        {
            data.toggleFavorite(tradeIndex);
            this.dirty = true;
        }
    }

    private void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt == null || NbtWrap.containsList(nbt, "VillagerData") == false)
        {
            return;
        }

        NBTTagList tagList = NbtWrap.getList(nbt, "VillagerData", Constants.NBT.TAG_COMPOUND);
        final int count = NbtWrap.getListSize(tagList);

        for (int i = 0; i < count; i++)
        {
            NBTTagCompound tag = NbtWrap.getCompoundAt(tagList, i);
            VillagerData data = VillagerData.fromNBT(tag);

            if (data != null)
            {
                this.data.put(data.getUUID(), data);
            }
        }
    }

    private NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt)
    {
        NBTTagList tagList = new NBTTagList();

        for (VillagerData data : this.data.values())
        {
            NbtWrap.addTag(tagList, data.toNBT());
        }

        NbtWrap.putTag(nbt, "VillagerData", tagList);

        this.dirty = false;

        return nbt;
    }

    private String getFileName()
    {
        String worldName = StringUtils.getWorldOrServerName();

        if (worldName != null)
        {
            return "villager_data_" + worldName + ".nbt";
        }

        return "villager_data.nbt";
    }

    private Path getSaveDir()
    {
        return FileUtils.getMinecraftDirectory().resolve(Reference.MOD_ID);
    }

    public void readFromDisk()
    {
        this.data.clear();

        try
        {
            Path saveDir = this.getSaveDir();
            Path file = saveDir.resolve(this.getFileName());

            if (Files.isRegularFile(file) && Files.isReadable(file))
            {
                InputStream is = Files.newInputStream(file);
                this.readFromNBT(CompressedStreamTools.readCompressed(is));
                is.close();
            }
        }
        catch (Exception e)
        {
            ItemScroller.LOGGER.warn("Failed to read villager data from file", e);
        }
    }

    public void writeToDisk()
    {
        if (this.dirty)
        {
            try
            {
                Path saveDir = this.getSaveDir();

                if (FileUtils.createDirectoriesIfMissing(saveDir) == false)
                {
                    ItemScroller.LOGGER.warn("Failed to create the data storage directory '{}'",
                                             saveDir.toAbsolutePath().toString());
                    return;
                }

                Path fileTmp  = saveDir.resolve(this.getFileName() + ".tmp");
                Path fileReal = saveDir.resolve(this.getFileName());
                OutputStream os = Files.newOutputStream(fileTmp);
                CompressedStreamTools.writeCompressed(this.writeToNBT(new NBTTagCompound()), os);
                os.close();

                if (Files.exists(fileReal))
                {
                    FileUtils.delete(fileReal);
                }

                FileUtils.move(fileTmp, fileReal);
                this.dirty = false;
            }
            catch (Exception e)
            {
                ItemScroller.LOGGER.warn("Failed to write villager data to file!", e);
            }
        }
    }
}
