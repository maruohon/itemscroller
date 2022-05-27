package fi.dy.masa.itemscroller.recipes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.wrap.NbtWrap;
import fi.dy.masa.itemscroller.LiteModItemScroller;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.util.Constants;
import fi.dy.masa.itemscroller.util.InputUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils;

public class RecipeStorage
{
    private static final RecipeStorage INSTANCE = new RecipeStorage(8 * 18);

    private final CraftingRecipe[] recipes;
    private int selected;
    private boolean dirty;

    public static RecipeStorage getInstance()
    {
        return INSTANCE;
    }

    public RecipeStorage(int recipeCount)
    {
        this.recipes = new CraftingRecipe[recipeCount];
        this.initRecipes();
    }

    private void initRecipes()
    {
        for (int i = 0; i < this.recipes.length; i++)
        {
            this.recipes[i] = new CraftingRecipe();
        }
    }

    public int getSelection()
    {
        return this.selected;
    }

    public void changeSelectedRecipe(int index)
    {
        if (index >= 0 && index < this.recipes.length)
        {
            this.selected = index;
            this.dirty = true;
        }
    }

    public void scrollSelection(boolean forward)
    {
        this.changeSelectedRecipe(this.selected + (forward ? 1 : -1));
    }

    public int getFirstVisibleRecipeId()
    {
        return this.getCurrentRecipePage() * this.getRecipeCountPerPage();
    }

    public int getTotalRecipeCount()
    {
        return this.recipes.length;
    }

    public int getRecipeCountPerPage()
    {
        return 18;
    }

    public int getCurrentRecipePage()
    {
        return this.getSelection() / this.getRecipeCountPerPage();
    }

    /**
     * Returns the recipe for the given index.
     * If the index is invalid, then the first recipe is returned, instead of null.
     */
    @Nonnull
    public CraftingRecipe getRecipe(int index)
    {
        if (index >= 0 && index < this.recipes.length)
        {
            return this.recipes[index];
        }

        return this.recipes[0];
    }

    @Nonnull
    public CraftingRecipe getSelectedRecipe()
    {
        return this.getRecipe(this.getSelection());
    }

    public boolean storeCraftingRecipeToCurrentSelection(Slot slot, GuiContainer gui, boolean clearIfEmpty)
    {
        if (InputUtils.isRecipeViewOpen() && InventoryUtils.isCraftingSlot(gui, slot))
        {
            this.storeCraftingRecipe(this.getSelection(), slot, gui, clearIfEmpty);
            return true;
        }

        return false;
    }

    public void storeCraftingRecipe(int index, Slot slot, GuiContainer gui, boolean clearIfEmpty)
    {
        this.getRecipe(index).storeCraftingRecipe(slot, gui, clearIfEmpty);
        this.dirty = true;
    }

    public void clearRecipe(int index)
    {
        this.getRecipe(index).clearRecipe();
        this.dirty = true;
    }

    private void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt == null || NbtWrap.containsList(nbt, "Recipes") == false)
        {
            return;
        }

        for (int i = 0; i < this.recipes.length; i++)
        {
            this.recipes[i].clearRecipe();
        }

        NBTTagList tagList = NbtWrap.getList(nbt, "Recipes", Constants.NBT.TAG_COMPOUND);
        int count = NbtWrap.getListSize(tagList);

        for (int i = 0; i < count; i++)
        {
            NBTTagCompound tag = NbtWrap.getCompoundAt(tagList, i);

            int index = NbtWrap.getByte(tag, "RecipeIndex");

            if (index >= 0 && index < this.recipes.length)
            {
                this.recipes[index].readFromNBT(tag);
            }
        }

        this.changeSelectedRecipe(NbtWrap.getByte(nbt, "Selected"));
    }

    private NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt)
    {
        NBTTagList tagRecipes = new NBTTagList();

        for (int i = 0; i < this.recipes.length; i++)
        {
            if (this.recipes[i].isValid())
            {
                NBTTagCompound tag = new NBTTagCompound();
                NbtWrap.putByte(tag, "RecipeIndex", (byte) i);
                this.recipes[i].writeToNBT(tag);
                NbtWrap.addTag(tagRecipes, tag);
            }
        }

        NbtWrap.putTag(nbt, "Recipes", tagRecipes);
        NbtWrap.putByte(nbt, "Selected", (byte) this.selected);

        return nbt;
    }

    private String getFileName()
    {
        if (Configs.Generic.CRAFTING_RECIPES_SAVE_FILE_GLOBAL.getBooleanValue() == false)
        {
            String worldName = StringUtils.getWorldOrServerName();

            if (worldName != null)
            {
                return "recipes_" + worldName + ".nbt";
            }
        }

        return "recipes.nbt";
    }

    private File getSaveDir()
    {
        return new File(FileUtils.getMinecraftDirectory(), Reference.MOD_ID);
    }

    public void readFromDisk()
    {
        if (Configs.Generic.CRAFTING_RECIPES_SAVE_TO_FILE.getBooleanValue())
        {
            try
            {
                File saveDir = this.getSaveDir();

                if (saveDir != null)
                {
                    File file = new File(saveDir, this.getFileName());

                    if (file.exists() && file.isFile() && file.canRead())
                    {
                        FileInputStream is = new FileInputStream(file);
                        this.readFromNBT(CompressedStreamTools.readCompressed(is));
                        is.close();
                        //ItemScroller.logger.info("Read recipes from file '{}'", file.getPath());
                    }
                }
            }
            catch (Exception e)
            {
                LiteModItemScroller.logger.warn("Failed to read recipes from file", e);
            }
        }
    }

    public void writeToDisk()
    {
        if (this.dirty && Configs.Generic.CRAFTING_RECIPES_SAVE_TO_FILE.getBooleanValue())
        {
            try
            {
                File saveDir = this.getSaveDir();

                if (saveDir == null)
                {
                    return;
                }

                if (saveDir.exists() == false)
                {
                    if (saveDir.mkdirs() == false)
                    {
                        LiteModItemScroller.logger.warn("Failed to create the recipe storage directory '{}'", saveDir.getPath());
                        return;
                    }
                }

                File fileTmp  = new File(saveDir, this.getFileName() + ".tmp");
                File fileReal = new File(saveDir, this.getFileName());
                FileOutputStream os = new FileOutputStream(fileTmp);
                CompressedStreamTools.writeCompressed(this.writeToNBT(new NBTTagCompound()), os);
                os.close();

                if (fileReal.exists())
                {
                    fileReal.delete();
                }

                fileTmp.renameTo(fileReal);
                this.dirty = false;
            }
            catch (Exception e)
            {
                LiteModItemScroller.logger.warn("Failed to write recipes to file!", e);
            }
        }
    }
}
