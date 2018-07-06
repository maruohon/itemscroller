package fi.dy.masa.itemscroller.event;

import java.util.List;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.recipes.CraftingRecipe;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.InventoryUtils;

public class RenderEventHandler
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();
    private static final Vec3d LIGHT0_POS = (new Vec3d( 0.2D, 1.0D, -0.7D)).normalize();
    private static final Vec3d LIGHT1_POS = (new Vec3d(-0.2D, 1.0D,  0.7D)).normalize();

    private ScaledResolution scaledResolution;

    public static RenderEventHandler getInstance()
    {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onDrawBackground(GuiScreenEvent.BackgroundDrawnEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (InputEventHandler.getInstance().isRecipeViewOpen() && mc.currentScreen instanceof GuiContainer)
        {
            GuiContainer gui = (GuiContainer) mc.currentScreen;
            RecipeStorage recipes = InputEventHandler.getInstance().getRecipes();
            final int count = recipes.getRecipeCount();

            gui.mc.fontRenderer.drawString("Item Scroller quick crafting", 4, 4, 0xC0C0C0C0);

            for (int recipeId = 0; recipeId < count; recipeId++)
            {
                this.renderStoredRecipeStack(recipeId, count, recipes.getRecipe(recipeId).getResult(),
                        gui, mc, recipeId == recipes.getSelection());
            }
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (InputEventHandler.getInstance().isRecipeViewOpen() && mc.currentScreen instanceof GuiContainer)
        {
            GuiContainer gui = (GuiContainer) mc.currentScreen;
            RecipeStorage recipes = InputEventHandler.getInstance().getRecipes();

            final int mouseX = this.getMouseX();
            final int mouseY = this.getMouseY();
            final int recipeId = this.getHoveredRecipeId(mouseX, mouseY, recipes, gui, mc);

            if (recipeId >= 0)
            {
                CraftingRecipe recipe = recipes.getRecipe(recipeId);

                if (Configs.craftingRenderRecipeItems)
                {
                    this.renderRecipeItems(recipe, recipes.getRecipeCount(), gui, mc);
                }

                this.renderHoverTooltip(mouseX, mouseY, recipe, gui, mc);
            }
            else if (Configs.craftingRenderRecipeItems)
            {
                CraftingRecipe recipe = recipes.getSelectedRecipe();
                this.renderRecipeItems(recipe, recipes.getRecipeCount(), gui, mc);

                ItemStack stack = this.getHoveredRecipeIngredient(mouseX, mouseY, recipe, recipes.getRecipeCount(), gui, mc);

                if (InventoryUtils.isStackEmpty(stack) == false)
                {
                    this.renderStackToolTip(mouseX, mouseY, stack, gui, mc);
                }
            }
        }
    }

    public ScaledResolution getScaledResolution()
    {
        if (this.scaledResolution == null)
        {
            this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        }

        return this.scaledResolution;
    }

    public int getMouseX()
    {
        final ScaledResolution scaledresolution = this.getScaledResolution();
        final int w = scaledresolution.getScaledWidth();
        return Mouse.getX() * w / Minecraft.getMinecraft().displayWidth;
    }

    public int getMouseY()
    {
        final ScaledResolution scaledresolution = this.getScaledResolution();
        final int h = scaledresolution.getScaledHeight();
        return h - Mouse.getY() * h / Minecraft.getMinecraft().displayHeight - 1;
    }

    private void renderHoverTooltip(int mouseX, int mouseY, CraftingRecipe recipe, GuiContainer gui, Minecraft mc)
    {
        ItemStack stack = recipe.getResult();

        if (InventoryUtils.isStackEmpty(stack) == false)
        {
            this.renderStackToolTip(mouseX, mouseY, stack, gui, mc);
        }
    }

    public int getHoveredRecipeId(int mouseX, int mouseY, RecipeStorage recipes, GuiContainer gui, Minecraft mc)
    {
        if (InputEventHandler.getInstance().isRecipeViewOpen())
        {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            final int gap = 40;
            final int recipesPerColumn = 9;
            final int stackBaseHeight = 16;
            final int usableHeight = scaledResolution.getScaledHeight() - 2 * gap; // leave a gap on the top and bottom
            // height of each entry; 9 stored recipes
            final int entryHeight = (int) (usableHeight / recipesPerColumn);
            // leave 0.25-th of a stack height gap between each entry
            final float scale = entryHeight / (stackBaseHeight * 1.25f);
            final int stackScaledSize = (int) (stackBaseHeight * scale);
            final int recipeCount = recipes.getRecipeCount();

            for (int recipeId = 0; recipeId < recipeCount; recipeId++)
            {
                // Leave a small gap from the rendered stack to the gui's left edge
                final int columnOffsetCount = (recipeCount / recipesPerColumn) - (recipeId / recipesPerColumn);
                final float x = gui.getGuiLeft() - (columnOffsetCount + 0.2f) * stackScaledSize - (columnOffsetCount - 1) * scale * 20;
                final int y = (int) (gap + 0.25f * stackScaledSize + (recipeId % recipesPerColumn) * entryHeight);

                if (mouseX >= x && mouseX < x + stackScaledSize && mouseY >= y && mouseY < y + stackScaledSize)
                {
                    return recipeId;
                }
            }
        }

        return -1;
    }

    private void renderStoredRecipeStack(int recipeId, int recipeCount, ItemStack stack, GuiContainer gui, Minecraft mc, boolean selected)
    {
        FontRenderer font = getFontRenderer(mc, stack);
        final String indexStr = String.valueOf(recipeId + 1);
        final int strWidth = font.getStringWidth(indexStr);

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int verticalGap = 40;
        final int recipesPerColumn = 9;
        final int stackBaseHeight = 16;
        final int usableHeight = scaledResolution.getScaledHeight() - 2 * verticalGap; // leave a gap on the top and bottom
        // height of each entry; 9 stored recipes
        final int entryHeight = (int) (usableHeight / recipesPerColumn);
        // leave 0.25-th of a stack height gap between each entry
        final float scale = entryHeight / (stackBaseHeight * 1.25f);
        final int stackScaledSize = (int) (stackBaseHeight * scale);
        // Leave a small gap from the rendered stack to the gui's left edge. The +12 is some space for the recipe's number text.
        final int columnOffsetCount = (recipeCount / recipesPerColumn) - (recipeId / recipesPerColumn);
        final float x = gui.getGuiLeft() - (columnOffsetCount + 0.2f) * stackScaledSize - (columnOffsetCount - 1) * scale * 20;
        final float y = verticalGap + 0.25f * stackScaledSize + (recipeId % recipesPerColumn) * entryHeight;

        //System.out.printf("sw: %d sh: %d scale: %.3f left: %d usable h: %d entry h: %d\n",
        //        scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), scale, guiLeft, usableHeight, entryHeight);

        this.renderStackAt(stack, x, y, selected, scale, stackBaseHeight, mc);

        font.drawString(indexStr, (int) (x - scale * strWidth), (int) (y + (entryHeight - font.FONT_HEIGHT) / 2 - 2), 0xC0C0C0);
    }

    private void renderRecipeItems(CraftingRecipe recipe, int recipeCount, GuiContainer gui, Minecraft mc)
    {
        RecipeLocation location = this.getRecipeLocation(recipe, recipeCount, gui, mc);

        ItemStack[] items = recipe.getRecipeItems();

        for (int i = 0, row = 0; row < location.recipeDimensions; row++)
        {
            for (int col = 0; col < location.recipeDimensions; col++, i++)
            {
                float xOff = col * ((location.stackBaseHeight + 1) * location.scale);
                float yOff = row * ((location.stackBaseHeight + 1) * location.scale);
                this.renderStackAt(items[i], location.x + xOff, location.y + yOff, false, location.scale, location.stackBaseHeight, mc);
            }
        }
    }

    private RecipeLocation getRecipeLocation(CraftingRecipe recipe, int recipeCount, GuiContainer gui, Minecraft mc)
    {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int verticalGap = 40;
        final int recipesPerColumn = 9;
        final int stackBaseHeight = 16;
        final int usableHeight = scaledResolution.getScaledHeight() - 2 * verticalGap; // leave a gap on the top and bottom
        // height of each entry; 9 stored recipes
        final int entryHeight = (int) (usableHeight / recipesPerColumn);
        // leave 0.25-th of a stack height gap between each entry
        final float scale = entryHeight / (stackBaseHeight * 1.25f);
        final int stackScaledSize = (int) (stackBaseHeight * scale);
        // Leave a small gap from the rendered stack to the gui's left edge. The +12 is some space for the recipe's number text.
        final int columnOffsetCount = (recipeCount / recipesPerColumn);

        final int recipeDimensions = (int) Math.ceil(Math.sqrt(recipe.getRecipeLength()));
        final float x = gui.getGuiLeft() - (columnOffsetCount + 0.2f) * stackScaledSize - (columnOffsetCount - 1) * scale * 20 - (stackBaseHeight + 1) * (recipeDimensions + 1) * scale;
        final float y = scaledResolution.getScaledHeight() / 2 - (stackBaseHeight + 1) * recipeDimensions * scale * 0.5f;

        return new RecipeLocation(x, y, scale, stackBaseHeight, recipeDimensions);
    }

    private ItemStack getHoveredRecipeIngredient(int mouseX, int mouseY, CraftingRecipe recipe, int recipeCount, GuiContainer gui, Minecraft mc)
    {
        RecipeLocation location = this.getRecipeLocation(recipe, recipeCount, gui, mc);
        final float stackWidth = location.scale * location.stackBaseHeight;

        for (int i = 0, row = 0; row < location.recipeDimensions; row++)
        {
            for (int col = 0; col < location.recipeDimensions; col++, i++)
            {
                float xOff = col * ((location.stackBaseHeight + 1) * location.scale);
                float yOff = row * ((location.stackBaseHeight + 1) * location.scale);
                float xStart = location.x + xOff;
                float yStart = location.y + yOff;

                if (mouseX >= xStart && mouseX <= xStart + stackWidth && mouseY >= yStart && mouseY <= yStart + stackWidth)
                {
                    return recipe.getRecipeItems()[i];
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static class RecipeLocation
    {
        public final float x;
        public final float y;
        public final float scale;
        public final int stackBaseHeight;
        public final int recipeDimensions;

        public RecipeLocation(float x, float y, float scale, int stackBaseHeight, int recipeDimensions)
        {
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.stackBaseHeight = stackBaseHeight;
            this.recipeDimensions = recipeDimensions;
        }
    }

    private void renderStackAt(ItemStack stack, float x, float y, boolean border, float scale, int stackBaseHeight, Minecraft mc)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.disableLighting();
        final int w = stackBaseHeight;

        if (border)
        {
            // Draw a light/white border around the stack
            Gui.drawRect(    0,     0, w, 1, 0xFFFFFFFF);
            Gui.drawRect(    0,     0, 1, w, 0xFFFFFFFF);
            Gui.drawRect(w - 1,     0, w, w, 0xFFFFFFFF);
            Gui.drawRect(    0, w - 1, w, w, 0xFFFFFFFF);

            Gui.drawRect(1, 1, w - 1, w - 1, 0x20FFFFFF); // light background for the item
        }
        else
        {
            Gui.drawRect(0, 0, w, w, 0x20FFFFFF); // light background for the item
        }

        if (InventoryUtils.isStackEmpty(stack) == false)
        {
            enableGUIStandardItemLighting(scale);

            stack = stack.copy();
            InventoryUtils.setStackSize(stack, 1);
            mc.getRenderItem().zLevel += 100;
            mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player, stack, 0, 0);
            //mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0, 0, null);
            mc.getRenderItem().zLevel -= 100;
        }

        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static void enableGUIStandardItemLighting(float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);

        enableStandardItemLighting(scale);

        GlStateManager.popMatrix();
    }

    public static void enableStandardItemLighting(float scale)
    {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        GlStateManager.glLight(16384, 4611, RenderHelper.setColorBuffer((float) LIGHT0_POS.x, (float) LIGHT0_POS.y, (float) LIGHT0_POS.z, 0.0f));

        float lightStrength = 0.3F * scale;
        GlStateManager.glLight(16384, 4609, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(16384, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16384, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16385, 4611, RenderHelper.setColorBuffer((float) LIGHT1_POS.x, (float) LIGHT1_POS.y, (float) LIGHT1_POS.z, 0.0f));
        GlStateManager.glLight(16385, 4609, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(16385, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16385, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.shadeModel(7424);

        float ambientLightStrength = 0.4F;
        GlStateManager.glLightModel(2899, RenderHelper.setColorBuffer(ambientLightStrength, ambientLightStrength, ambientLightStrength, 1.0F));
    }

    private static FontRenderer getFontRenderer(Minecraft mc, ItemStack stack)
    {
        FontRenderer fontRenderer = null;

        if (InventoryUtils.isStackEmpty(stack) == false)
        {
            fontRenderer = stack.getItem().getFontRenderer(stack);
        }

        return fontRenderer != null ? fontRenderer : mc.fontRenderer;
    }

    private void renderStackToolTip(int x, int y, ItemStack stack, GuiContainer gui, Minecraft mc)
    {
        List<String> list = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
            }
            else
            {
                list.set(i, TextFormatting.GRAY + (String)list.get(i));
            }
        }

        FontRenderer font = getFontRenderer(mc, stack);

        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, x, y, gui.width, gui.height, -1, font);
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }
}
