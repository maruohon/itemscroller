package fi.dy.masa.itemscroller.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;

public class RenderEventHandler
{
    private static final Vec3d LIGHT0_POS = (new Vec3d( 0.2D, 1.0D, -0.7D)).normalize();
    private static final Vec3d LIGHT1_POS = (new Vec3d(-0.2D, 1.0D,  0.7D)).normalize();
    private static boolean renderRecipes;

    @SubscribeEvent
    public void onRenderGui(GuiScreenEvent.BackgroundDrawnEvent event)
    {
        if (renderRecipes && event.getGui() instanceof GuiContainer)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
            RecipeStorage recipes = InputEventHandler.instance().getRecipes();
            int count = recipes.getRecipeCount();

            for (int recipeId = 0; recipeId < count; recipeId++)
            {
                this.renderStoredRecipeStack(recipeId, recipes.getRecipe(recipeId).getResult(),
                        gui, gui.mc, recipeId == recipes.getSelection());
            }
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

    private void renderStoredRecipeStack(int recipeId, ItemStack stack, GuiContainer gui, Minecraft mc, boolean selected)
    {
        int guiLeft = 0;
        try { guiLeft = InputEventHandler.fieldGuiLeft.getInt(gui); } catch (Exception e) {}

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int height = scaledResolution.getScaledHeight();
        final int gap = 40;
        final int stackBaseHeight = 16;
        final int usableHeight = height - 2 * gap; // leave a gap on the top and bottom
        // height of each entry; 9 stored recipes
        final int entryHeight = (int) (usableHeight / 9);
        // leave 0.25-th of a stack height gap between each entry
        final float scale = entryHeight / (stackBaseHeight * 1.25f);
        // leave a 16 pixel gap from the rendered stack to the gui left edge
        final float xPosition = guiLeft - scale * stackBaseHeight - stackBaseHeight;
        final float yPosition = gap + scale * 0.25f * stackBaseHeight + recipeId * entryHeight;
        FontRenderer font = getFontRenderer(mc, stack);

        //System.out.printf("sw: %d sh: %d scale: %.3f left: %d usable h: %d entry h: %d\n",
        //        scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), scale, guiLeft, usableHeight, entryHeight);

        GlStateManager.pushMatrix();
        GlStateManager.translate(xPosition, yPosition, 0);
        GlStateManager.scale(scale, scale, 1);
        final int w = stackBaseHeight;

        if (selected)
        {
            // Draw a light border around the selected/previously loaded recipe
            Gui.drawRect(    0,     0, w, 1, 0xFFFFFFFF);
            Gui.drawRect(    0,     0, 1, w, 0xFFFFFFFF);
            Gui.drawRect(w - 1,     0, w, w, 0xFFFFFFFF);
            Gui.drawRect(    0, w - 1, w, w, 0xFFFFFFFF);
        }

        if (InputEventHandler.isStackEmpty(stack) == false)
        {
            if (selected)
            {
                Gui.drawRect(1, 1, w - 1, w - 1, 0x30FFFFFF); // light background for the item
            }
            else
            {
                Gui.drawRect(0, 0, w, w, 0x30FFFFFF); // light background for the item
            }

            enableGUIStandardItemLighting(scale);

            stack = stack.copy();
            InputEventHandler.setStackSize(stack, 1);
            mc.getRenderItem().zLevel += 100;
            mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player, stack, 0, 0);
            mc.getRenderItem().renderItemOverlayIntoGUI(font, stack, 0, 0, null);
            mc.getRenderItem().zLevel -= 100;
        }

        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        String indexStr = String.valueOf(recipeId + 1);
        final int strWidth = font.getStringWidth(indexStr);

        font.drawString(indexStr, (int) (xPosition - scale * strWidth - 2), (int) (yPosition + (entryHeight - font.FONT_HEIGHT) / 2 - 2), 0xC0C0C0);
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
        GlStateManager.glLight(16384, 4611, RenderHelper.setColorBuffer((float) LIGHT0_POS.xCoord, (float) LIGHT0_POS.yCoord, (float) LIGHT0_POS.zCoord, 0.0f));

        float lightStrength = 0.3F * scale;
        GlStateManager.glLight(16384, 4609, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(16384, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16384, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16385, 4611, RenderHelper.setColorBuffer((float) LIGHT1_POS.xCoord, (float) LIGHT1_POS.yCoord, (float) LIGHT1_POS.zCoord, 0.0f));
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

        if (InputEventHandler.isStackEmpty(stack) == false)
        {
            fontRenderer = stack.getItem().getFontRenderer(stack);
        }

        return fontRenderer != null ? fontRenderer : mc.fontRendererObj;
    }
}
