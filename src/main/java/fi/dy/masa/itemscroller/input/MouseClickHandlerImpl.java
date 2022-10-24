package fi.dy.masa.itemscroller.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.RayTraceResult;

import malilib.gui.BaseScreen;
import malilib.gui.util.GuiUtils;
import malilib.input.MouseClickHandler;
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

public class MouseClickHandlerImpl implements MouseClickHandler
{
    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton, boolean buttonState)
    {
        int keyCode = mouseButton - 100;
        boolean keyState = buttonState;
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

                if (GuiUtils.getCurrentScreen() == null && hitResult != null &&
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

    public static boolean canOperateIn(GuiScreen screen)
    {
        return Configs.Toggles.MOD_FEATURES_ENABLED.getBooleanValue() &&
               GameUtils.getClientPlayer() != null &&
               GameUtils.getClientWorld() != null &&
               screen instanceof GuiContainer &&
               Configs.Lists.GUI_BLACKLIST.getValue().contains(screen.getClass().getName()) == false;
    }
}
