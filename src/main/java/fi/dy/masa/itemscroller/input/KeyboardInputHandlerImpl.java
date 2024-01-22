package fi.dy.masa.itemscroller.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.RayTraceResult;

import malilib.gui.BaseScreen;
import malilib.gui.util.GuiUtils;
import malilib.input.KeyboardInputHandler;
import malilib.input.Keys;
import malilib.util.MathUtils;
import malilib.util.game.wrap.GameUtils;
import malilib.util.inventory.InventoryScreenUtils;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.event.RenderEventHandler;
import fi.dy.masa.itemscroller.gui.widgets.WidgetTradeList;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.itemscroller.util.IGuiMerchant;
import fi.dy.masa.itemscroller.util.InputUtils;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.itemscroller.util.MoveAction;
import fi.dy.masa.itemscroller.villager.VillagerDataStorage;

public class KeyboardInputHandlerImpl implements KeyboardInputHandler
{
    @Override
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        if (InputUtils.isRecipeViewOpen() && eventKeyState)
        {
            int index = -1;
            RecipeStorage recipes = RecipeStorage.INSTANCE;
            int oldIndex = recipes.getSelection();
            int recipesPerPage = recipes.getRecipeCountPerPage();
            int recipeIndexChange = BaseScreen.isShiftDown() ? recipesPerPage : recipesPerPage / 2;

            if (keyCode >= Keys.KEY_1 && keyCode <= Keys.KEY_9)
            {
                index = MathUtils.clamp(keyCode - Keys.KEY_1, 0, 8);
            }
            else if (keyCode == Keys.KEY_UP && oldIndex > 0)
            {
                index = oldIndex - 1;
            }
            else if (keyCode == Keys.KEY_DOWN && oldIndex < (recipes.getTotalRecipeCount() - 1))
            {
                index = oldIndex + 1;
            }
            else if (keyCode == Keys.KEY_LEFT && oldIndex >= recipeIndexChange)
            {
                index = oldIndex - recipeIndexChange;
            }
            else if (keyCode == Keys.KEY_RIGHT && oldIndex < (recipes.getTotalRecipeCount() - recipeIndexChange))
            {
                index = oldIndex + recipeIndexChange;
            }

            if (index >= 0)
            {
                recipes.changeSelectedRecipe(index);
                return true;
            }
        }

        return this.handleInput(keyCode, eventKeyState);
    }

    private boolean handleInput(int keyCode, boolean keyState)
    {
        MoveAction action = InventoryUtils.getActiveMoveAction();

        if (action != MoveAction.NONE && InputUtils.isActionKeyActive(action) == false)
        {
            InventoryUtils.stopDragging();
        }

        Minecraft mc = GameUtils.getClient();
        boolean cancel = false;

        if (Configs.Toggles.MOD_FEATURES_ENABLED.getBooleanValue() && mc.player != null)
        {
            final boolean isAttack = InputUtils.isAttack(keyCode);
            final boolean isUse = InputUtils.isUse(keyCode);
            final boolean isPickBlock = InputUtils.isPickBlock(keyCode);
            final boolean isAttackUseOrPick = isAttack || isUse || isPickBlock;

            if (Configs.Toggles.VILLAGER_TRADE_LIST.getBooleanValue())
            {
                VillagerDataStorage storage = VillagerDataStorage.INSTANCE;
                RayTraceResult hitResult = GameUtils.getHitResult();

                if (GuiUtils.noScreenOpen() && hitResult != null &&
                    hitResult.typeOfHit == RayTraceResult.Type.ENTITY &&
                    hitResult.entityHit instanceof EntityVillager)
                {
                    storage.setLastInteractedUUID(hitResult.entityHit.getUniqueID());
                }
                else if (GuiUtils.getCurrentScreen() instanceof GuiMerchant && storage.hasInteractionTarget())
                {
                    WidgetTradeList widget = ((IGuiMerchant) GuiUtils.getCurrentScreen()).getTradeListWidget();

                    if (widget != null)
                    {
                        final int mouseX = InputUtils.getMouseX();
                        final int mouseY = InputUtils.getMouseY();
                        int mouseButton = isAttack ? 0 : (isUse ? 1 : 2);

                        if (keyState && isAttackUseOrPick && widget.isMouseOver(mouseX, mouseY))
                        {
                            widget.tryMouseClick(mouseX, mouseY, mouseButton);
                            return true;
                        }

                        if (keyState == false)
                        {
                            widget.onMouseReleased(mouseX, mouseY, mouseButton);
                        }
                    }
                }
            }

            if (GuiUtils.getCurrentScreen() instanceof GuiContainer &&
                (GuiUtils.getCurrentScreen() instanceof GuiContainerCreative) == false &&
                Configs.Lists.GUI_BLACKLIST.getValue().contains(GuiUtils.getCurrentScreen().getClass().getName()) == false)
            {
                GuiContainer gui = (GuiContainer) GuiUtils.getCurrentScreen();
                RecipeStorage recipes = RecipeStorage.INSTANCE;

                Slot slot = InventoryScreenUtils.getSlotUnderMouse(gui);
                final boolean isShiftDown = BaseScreen.isShiftDown();

                if (keyState && isAttackUseOrPick)
                {
                    final int mouseX = InputUtils.getMouseX();
                    final int mouseY = InputUtils.getMouseY();
                    int hoveredRecipeId = RenderEventHandler.instance().getHoveredRecipeId(mouseX, mouseY, recipes, gui);

                    // Hovering over an item in the recipe view
                    if (hoveredRecipeId >= 0)
                    {
                        InventoryUtils.handleRecipeClick(gui, mc, recipes, hoveredRecipeId, isAttack, isUse, isPickBlock, isShiftDown);
                        return true;
                    }
                    // Pick-blocking over a crafting output slot with the recipe view open, store the recipe
                    else if (isPickBlock && recipes.storeCraftingRecipeToCurrentSelection(slot, gui, true))
                    {
                        cancel = true;
                    }
                }

                InventoryUtils.checkForItemPickup(gui, mc);

                if (keyState && (isAttack || isUse))
                {
                    InventoryUtils.storeSourceSlotCandidate(slot, mc);
                }

                if (Configs.Toggles.RIGHT_CLICK_CRAFT_STACK.getBooleanValue() &&
                    isUse && keyState &&
                    InventoryUtils.isCraftingSlot(gui, slot))
                {
                    InventoryUtils.rightClickCraftOneStack(gui);
                }
                else if (Configs.Toggles.SHIFT_PLACE_ITEMS.getBooleanValue() &&
                         isAttack && isShiftDown &&
                         InventoryUtils.canShiftPlaceItems(gui))
                {
                    cancel |= InventoryUtils.shiftPlaceItems(slot, gui);
                }
                else if (Configs.Toggles.SHIFT_DROP_ITEMS.getBooleanValue() &&
                         isAttack && isShiftDown &&
                         InputUtils.canShiftDropItems(gui, mc))
                {
                    cancel |= InventoryUtils.shiftDropItems(gui);
                }
            }
        }

        return cancel;
    }
}
