package fi.dy.masa.itemscroller.event;

import java.lang.invoke.MethodHandle;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.itemscroller.util.MethodHandleUtils;

public class RenderEventHandler
{
    private static final FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    private static final Vec3 LIGHT0_POS = Vec3.createVectorHelper( 0.2D, 1.0D, -0.7D).normalize();
    private static final Vec3 LIGHT1_POS = Vec3.createVectorHelper(-0.2D, 1.0D,  0.7D).normalize();
    private static final MethodHandle METHODHANDLE_GuiScreen_renderTooltip =
            MethodHandleUtils.getMethodHandleVirtual(GuiScreen.class, new String[] { "func_146285_a", "renderToolTip" }, ItemStack.class, int.class, int.class);
    private static boolean renderRecipes;

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        if (renderRecipes && event.gui instanceof GuiContainer)
        {
            GuiContainer gui = (GuiContainer) event.gui;
            RecipeStorage recipes = InputEventHandler.instance().getRecipes();
            int count = recipes.getRecipeCount();

            for (int recipeId = 0; recipeId < count; recipeId++)
            {
                this.renderStoredRecipeStack(recipeId, count, recipes.getRecipe(recipeId).getResult(),
                        gui, gui.mc, recipeId == recipes.getSelection());
            }

            this.renderHoverTooltip(event.mouseX, event.mouseY, recipes, gui, gui.mc);
        }
    }

    public static void setRenderStoredRecipes(boolean render)
    {
        renderRecipes = render;
    }

    public static boolean getRenderStoredRecipes()
    {
        return renderRecipes;
    }

    private void renderHoverTooltip(int mouseX, int mouseY, RecipeStorage recipes, GuiContainer gui, Minecraft mc)
    {
        int guiLeft = 0;
        try { guiLeft = InputEventHandler.fieldGuiLeft.getInt(gui); } catch (Exception e) {}

        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final int gap = 40;
        final int recipesPerColumn = 9;
        final int stackBaseHeight = 16;
        final int usableHeight = scaledResolution.getScaledHeight() - 2 * gap; // leave a gap on the top and bottom
        // height of each entry; 9 stored recipes
        final int entryHeight = (int) (usableHeight / recipesPerColumn);
        // leave 0.25-th of a stack height gap between each entry
        final float scale = entryHeight / (stackBaseHeight * 1.25f);
        final int stackScaledSize = (int) (stackBaseHeight * scale);
        int recipeCount = recipes.getRecipeCount();

        for (int slot = 0; slot < recipeCount; slot++)
        {
            // Leave a small gap from the rendered stack to the gui's left edge
            final int columnOffsetCount = (recipeCount / recipesPerColumn) - (slot / recipesPerColumn);
            final float x = guiLeft - (columnOffsetCount + 0.2f) * stackScaledSize - (columnOffsetCount - 1) * scale * 20;
            final int y = (int) (gap + 0.25f * stackScaledSize + (slot % recipesPerColumn) * entryHeight);

            if (mouseX >= x && mouseX < x + stackScaledSize && mouseY >= y && mouseY < y + stackScaledSize)
            {
                ItemStack stack = recipes.getRecipe(slot).getResult();

                if (InventoryUtils.isStackEmpty(stack) == false)
                {
                    try
                    {
                        //gui.renderToolTip(stack, mouseX, mouseY);
                        METHODHANDLE_GuiScreen_renderTooltip.invokeExact(stack, mouseX, mouseY);
                    }
                    catch (Throwable t)
                    {
                    }
                    //this.renderStackToolTip(mouseX, mouseY, stack, gui, mc);
                }

                break;
            }
        }
    }

    private void renderStoredRecipeStack(int recipeId, int recipeCount, ItemStack stack, GuiContainer gui, Minecraft mc, boolean selected)
    {
        int guiLeft = 0;
        try { guiLeft = InputEventHandler.fieldGuiLeft.getInt(gui); } catch (Exception e) {}

        FontRenderer font = getFontRenderer(mc, stack);
        final String indexStr = String.valueOf(recipeId + 1);
        final int strWidth = font.getStringWidth(indexStr);

        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final int gap = 40;
        final int recipesPerColumn = 9;
        final int stackBaseHeight = 16;
        final int usableHeight = scaledResolution.getScaledHeight() - 2 * gap; // leave a gap on the top and bottom
        // height of each entry; 9 stored recipes
        final int entryHeight = (int) (usableHeight / recipesPerColumn);
        // leave 0.25-th of a stack height gap between each entry
        final float scale = entryHeight / (stackBaseHeight * 1.25f);
        final int stackScaledSize = (int) (stackBaseHeight * scale);
        // Leave a small gap from the rendered stack to the gui's left edge. The +12 is some space for the recipe's number text.
        final int columnOffsetCount = (recipeCount / recipesPerColumn) - (recipeId / recipesPerColumn);
        final float xPosition = guiLeft - (columnOffsetCount + 0.2f) * stackScaledSize - (columnOffsetCount - 1) * scale * 20;
        final float yPosition = gap + 0.25f * stackScaledSize + (recipeId % recipesPerColumn) * entryHeight;

        //System.out.printf("sw: %d sh: %d scale: %.3f left: %d usable h: %d entry h: %d\n",
        //        scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), scale, guiLeft, usableHeight, entryHeight);

        GL11.glPushMatrix();
        GL11.glTranslatef(xPosition, yPosition, 0);
        GL11.glScalef(scale, scale, 1);
        final int w = stackBaseHeight;

        if (selected)
        {
            // Draw a light border around the selected/previously loaded recipe
            Gui.drawRect(    0,     0, w, 1, 0xFFFFFFFF);
            Gui.drawRect(    0,     0, 1, w, 0xFFFFFFFF);
            Gui.drawRect(w - 1,     0, w, w, 0xFFFFFFFF);
            Gui.drawRect(    0, w - 1, w, w, 0xFFFFFFFF);
        }

        if (InventoryUtils.isStackEmpty(stack) == false)
        {
            if (selected)
            {
                Gui.drawRect(1, 1, w - 1, w - 1, 0x20FFFFFF); // light background for the item
            }
            else
            {
                Gui.drawRect(0, 0, w, w, 0x20FFFFFF); // light background for the item
            }

            enableGUIStandardItemLighting(scale);

            stack = stack.copy();
            InventoryUtils.setStackSize(stack, 1);
            RenderItem renderItem = RenderItem.getInstance();
            renderItem.zLevel += 100;
            renderItem.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, 0, 0);
            renderItem.renderItemOverlayIntoGUI(font, mc.getTextureManager(), stack, 0, 0);
            renderItem.zLevel -= 100;
        }

        GL11.glDisable(GL11.GL_BLEND);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();

        font.drawString(indexStr, (int) (xPosition - scale * strWidth), (int) (yPosition + (entryHeight - font.FONT_HEIGHT) / 2 - 2), 0xC0C0C0);
    }

    public static void enableGUIStandardItemLighting(float scale)
    {
        GL11.glPushMatrix();
        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);

        enableStandardItemLighting(scale);

        GL11.glPopMatrix();
    }

    public static void enableStandardItemLighting(float scale)
    {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

        float lightStrength = 0.3F * scale;
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, setColorBuffer((float) LIGHT0_POS.xCoord, (float) LIGHT0_POS.yCoord, (float) LIGHT0_POS.zCoord, 0.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE,  setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT,  setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, setColorBuffer((float) LIGHT1_POS.xCoord, (float) LIGHT1_POS.yCoord, (float) LIGHT1_POS.zCoord, 0.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE,  setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT,  setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glShadeModel(GL11.GL_FLAT);

        float ambientLightStrength = 0.4F;
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(ambientLightStrength, ambientLightStrength, ambientLightStrength, 1.0F));
    }

    private static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_)
    {
        COLOR_BUFFER.clear();
        COLOR_BUFFER.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
        COLOR_BUFFER.flip();
        return COLOR_BUFFER;
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

    /*
    private void renderStackToolTip(int x, int y, ItemStack stack, GuiContainer gui, Minecraft mc)
    {
        List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
            }
            else
            {
                list.set(i, ChatFormatting.GRAY + (String)list.get(i));
            }
        }

        FontRenderer font = stack.getItem().getFontRenderer(stack);

        if (font == null)
        {
            font = mc.fontRenderer;
        }

        GuiUtils.preItemToolTip(stack);
        GuiUtils.drawHoveringText(list, x, y, gui.width, gui.height, -1, font);
        GuiUtils.postItemToolTip();
    }
    */
}
