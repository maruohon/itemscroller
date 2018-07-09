package fi.dy.masa.itemscroller.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import fi.dy.masa.itemscroller.ItemScroller;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.event.InputEventHandler;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.itemscroller.recipes.CraftingHandler.SlotRange;

public class Configs
{
    public static boolean enableClientCraftingFix;

    public static boolean enableAltShiftClickEverything;
    public static boolean enableAltClickMatching;
    public static boolean enableControlShiftDropkeyDropItems;
    public static boolean enableDragDroppingSingle;
    public static boolean enableDragDroppingStacks;
    public static boolean enableDragMovingShiftLeft;
    public static boolean enableDragMovingShiftRight;
    public static boolean enableDragMovingControlLeft;
    public static boolean enableRightClickCraftingOneStack;
    public static boolean enableScrollingCrafting;
    public static boolean enableScrollingEverything;
    public static boolean enableScrollingMatchingStacks;
    public static boolean enableScrollingSingle;
    public static boolean enableScrollingStacks;
    public static boolean enableScrollingStacksFallback;
    public static boolean enableScrollingVillager;
    public static boolean enableShiftDropItems;
    public static boolean enableShiftPlaceItems;
    public static boolean enableWSClicking;

    public static boolean craftingRecipesStoreToFile;
    public static boolean craftingRenderRecipeItems;
    public static boolean craftingScrollingSaveFileIsGlobal;
    public static boolean reverseScrollDirectionSingle;
    public static boolean reverseScrollDirectionStacks;
    public static boolean useSlotPositionAwareScrollDirection;

    public static final Set<String> GUI_BLACKLIST = new HashSet<String>();
    public static final Set<String> SLOT_BLACKLIST = new HashSet<String>();

    public static File configurationFile;
    public static Configuration config;
    
    public static final String CATEGORY_GENERIC = "Generic";
    public static final String CATEGORY_DRAG_ENABLE = "DraggingModesToggle";
    public static final String CATEGORY_SCROLLING_ENABLE = "ScrollingModesToggle";
    public static final String CATEGORY_LISTS = "Lists";

    @SubscribeEvent
    public void onConfigChangedEvent(OnConfigChangedEvent event)
    {
        if (Reference.MOD_ID.equals(event.getModID()) == true)
        {
            loadConfigs(config);
        }
    }

    public static void loadConfigsFromFile(File configFile)
    {
        configurationFile = configFile;
        config = new Configuration(configFile, null, true);
        config.load();

        loadConfigs(config);
    }

    public static void loadConfigs(Configuration conf)
    {
        Property prop;

        String category = CATEGORY_GENERIC;

        prop = conf.get(category, "craftingRecipesStoreToFile", true);
        prop.setComment("Enables saving and loading the stored recipes to a file inside minecraft/itemscroller/recipes_worldorservername.nbt,\n" +
                        "so that they are persistent between game restarts.");
        craftingRecipesStoreToFile = prop.getBoolean();

        prop = conf.get(category, "craftingRenderRecipeItems", true);
        prop.setComment("If enabled, then the recipe items are rendered for the selected or hovered recipe");
        craftingRenderRecipeItems = prop.getBoolean();

        prop = conf.get(category, "craftingScrollingSaveFileIsGlobal", false);
        prop.setComment("If true, then a single file is used for storing the recipes, instead of per-world or per-server files");
        craftingScrollingSaveFileIsGlobal = prop.getBoolean();

        prop = conf.get(category, "enableAltClickMoveMatching", true);
        prop.setComment("Enable Alt + click to move all matching stacks\n(same as the Ctrl + scroll functionality).");
        enableAltClickMatching = prop.getBoolean();

        prop = conf.get(category, "enableAltShiftClickEverything", true);
        prop.setComment("Enable Alt + Shift + click to move everything\n(same as the Ctrl + Shift + scroll functionality).");
        enableAltShiftClickEverything = prop.getBoolean();

        prop = conf.get(category, "enableControlShiftDropkeyDropItems", true);
        prop.setComment("Enable dropping all matching items from the same inventory when pressing Ctrl + Shift + the drop key");
        enableControlShiftDropkeyDropItems = prop.getBoolean();

        prop = conf.get(category, "enableDragDroppingSingle", true);
        prop.setComment("Enable click dragging to drop single items while holding Ctrl + drop key");
        enableDragDroppingSingle = prop.getBoolean();

        prop = conf.get(category, "enableDragDroppingStacks", true);
        prop.setComment("Enable click dragging to drop full stacks while holding Shift + drop key.\n(Left for entire stacks, right for all but one item.)");
        enableDragDroppingStacks = prop.getBoolean();

        prop = conf.get(category, "enableRightClickCraftingOneStack", true);
        prop.setComment("Enable crafting up to one full stack when right clicking on a slot that has been configured as a crafting slot");
        enableRightClickCraftingOneStack = prop.getBoolean();

        prop = conf.get(category, "enableShiftDropItems", true);
        prop.setComment("Enable dropping items while holding shift to drop all the matching items at once.");
        enableShiftDropItems = prop.getBoolean();

        prop = conf.get(category, "enableShiftPlaceItems", true);
        prop.setComment("Enable placing items to an empty slot while holding shift to move all the mathing items to that inventory.");
        enableShiftPlaceItems = prop.getBoolean();

        prop = conf.get(category, "enableWSClicking", true).setRequiresMcRestart(false);
        prop.setComment("Enable clicking on stacks while holding W or S to move the stack up or down in the inventory");
        enableWSClicking = prop.getBoolean();

        prop = conf.get(category, "reverseScrollDirectionSingle", false);
        prop.setComment("Reverse the scrolling direction for single item mode.");
        reverseScrollDirectionSingle = prop.getBoolean();

        prop = conf.get(category, "reverseScrollDirectionStacks", false);
        prop.setComment("Reverse the scrolling direction for full stacks mode.");
        reverseScrollDirectionStacks = prop.getBoolean();

        prop = conf.get(category, "useSlotPositionAwareScrollDirection", false);
        prop.setComment("When enabled, the item movement direction depends on the slots' y-position on screen. Might be derpy with more complex inventories, use with caution!");
        useSlotPositionAwareScrollDirection = prop.getBoolean();


        category = CATEGORY_DRAG_ENABLE;

        prop = conf.get(category, "enableDragMovingShiftLeft", true);
        prop.setComment("Enable moving full stacks of items by holding down Shift and dragging over slots with the left mouse button held down.");
        enableDragMovingShiftLeft = prop.getBoolean();

        prop = conf.get(category, "enableDragMovingShiftRight", true);
        prop.setComment("Enable moving everything but the last item from all stacks by holding down Shift and dragging over slots with the right mouse button held down.");
        enableDragMovingShiftRight = prop.getBoolean();

        prop = conf.get(category, "enableDragMovingControlLeft", true);
        prop.setComment("Enable moving one item from all stacks by holding down Control and dragging over slots with the left mouse button held down.");
        enableDragMovingControlLeft = prop.getBoolean();


        category = CATEGORY_SCROLLING_ENABLE;

        prop = conf.get(category, "enableScrollingCrafting", true);
        prop.setComment("Enable scrolling items to and from crafting grids, with a built-in 9 recipe memory.\n" +
                        "Hold down the Recipe key to see the stored recipes and to change the selection.\n" +
                        "While holding the Recipe key, you can either scroll or press a number key to change the selection.\n" +
                        "A recipe is stored to the currently selected \"recipe slot\" by scrolling over the output slot,\n" +
                        "or by pressing Shift + the Recipe key + a number key.\n" +
                        "The supported crafting grids must be added to the scrollableCraftingGrids list.");
        enableScrollingCrafting = prop.getBoolean();

        prop = conf.get(category, "enableScrollingEverything", true);
        prop.setComment("Enable moving all items at once (while holding ctrl and shift).");
        enableScrollingEverything = prop.getBoolean();

        prop = conf.get(category, "enableScrollingMatchingStacks", true);
        prop.setComment("Enable moving all matching items at once (while holding ctrl).");
        enableScrollingMatchingStacks = prop.getBoolean();

        prop = conf.get(category, "enableScrollingSingle", true);
        prop.setComment("Enable scrolling items one item at a time.");
        enableScrollingSingle = prop.getBoolean();

        prop = conf.get(category, "enableScrollingStacks", true);
        prop.setComment("Enable scrolling full stack (while holding shift).");
        enableScrollingStacks = prop.getBoolean();

        prop = conf.get(category, "enableScrollingStacksFallback", true);
        prop.setComment("Enable a \"fallback\" mode for scrolling entire stacks (for example to a vanilla crafting table, where shift + click doesn't work).");
        enableScrollingStacksFallback = prop.getBoolean();

        prop = conf.get(category, "enableScrollingVillager", true);
        prop.setComment("Enable special handling for Villager GUI (normally you can't shift+click items into them).");
        enableScrollingVillager = prop.getBoolean();


        category = CATEGORY_LISTS;

        prop = conf.get(category, "blackListedGuis", new String[0]);
        prop.setComment("A list of GuiContainer classes where Item Scroller shouldn't do anything");
        GUI_BLACKLIST.clear();
        for (String str : prop.getStringList()) { GUI_BLACKLIST.add(str); }

        prop = conf.get(category, "blackListedSlots", new String[] { "appeng.client.me.SlotME", "slimeknights.mantle.inventory.SlotWrapper" });
        prop.setComment("A list of Slot classes that Item Scroller shouldn't use");
        SLOT_BLACKLIST.clear();
        for (String str : prop.getStringList()) { SLOT_BLACKLIST.add(str); }

        prop = conf.get(category, "craftingGridDefinitions", new String[] {
                "net.minecraft.client.gui.inventory.GuiCrafting,net.minecraft.inventory.SlotCrafting,0,1-9", // vanilla Crafting Table
                "net.minecraft.client.gui.inventory.GuiInventory,net.minecraft.inventory.SlotCrafting,0,1-4", // vanilla player inventory crafting grid
                "fi.dy.masa.enderutilities.gui.client.GuiHandyBag,fi.dy.masa.enderutilities.inventory.slot.SlotItemHandlerCraftResult,100,101-104", // Ender Utilities Handy Bag crafting grid
                "fi.dy.masa.enderutilities.gui.client.GuiCreationStation,fi.dy.masa.enderutilities.inventory.slot.SlotItemHandlerCraftResult,40,31-39", // Ender Utilities Creation Station, left
                "fi.dy.masa.enderutilities.gui.client.GuiCreationStation,fi.dy.masa.enderutilities.inventory.slot.SlotItemHandlerCraftResult,50,41-49", // Ender Utilities Creation Station, right
                "com.xcom.oneblocksolutions.gui.GUICraftingTable,com.xcom.oneblocksolutions.inventory.slot.SlotItemHandlerCraftResult,0,1-9" // One Block Solutions: Better Crafting Table
                });
        prop.setComment("A list of crafting grid specifiers for the crafting grid scrolling feature.\n" +
                        "All the crafting grids that you want to be usable for that feature, must be added in this list.\n" +
                        "The entries must be one per line, in the following format: guiclassname,slotclassname,outputslotnumber,gridfirstslotnumber-gridlastslotnumber\n" +
                        "To find out the class names and slot numbers, you can use the 'Ctrl + Alt + Shift + I' debug key combination\n" +
                        "when hovering over slots (to get the slot info) and while NOT hovering over slots (to get the gui class name).\n" +
                        "What you are looking for are the 'GUI class', 'slot class' and the 'slotNumber' (NOT 'getSlotIndex()'!) values.\n" +
                        "The slot class must be from the crafting output slot!\n" +
                        "NOTE: This feature is actually in no way specific or tied to crafting grids.\n" +
                        "It can be used for other types of inventories as well, where you must move items into specific slots.\n" +
                        "The limitations are special, non-standard slots like AE2, which don't have proper slot numbers.\n" +
                        "The \"recipe\" slots should also form a continuous range, otherwise weirds stuff might happen when scrolling.");

        addCraftingGrids(prop.getStringList());

        if (conf.hasChanged())
        {
            conf.save();
            InputEventHandler.getInstance().initializeRecipeStorage();
        }
    }

    private static void addCraftingGrids(String[] lines)
    {
        CraftingHandler.clearDefinitons();

        try
        {
            Pattern pattern = Pattern.compile("([^,]+),([^,]+),([0-9]+),([0-9]+)-([0-9]+)");

            for (String line : lines)
            {
                Matcher matcher = pattern.matcher(line);

                if (matcher.matches())
                {
                    try
                    {
                        String guiClassName = matcher.group(1);
                        String slotClassName = matcher.group(2);
                        int outputSlot = Integer.parseInt(matcher.group(3));
                        int gridStart = Integer.parseInt(matcher.group(4));
                        int gridSize = Integer.parseInt(matcher.group(5)) - gridStart + 1;

                        if (CraftingHandler.addCraftingGridDefinition(guiClassName, slotClassName, outputSlot, new SlotRange(gridStart, gridSize)))
                        {
                            ItemScroller.logger.info("addCraftingGrids(): Added crafting grid slots for gui: {}, slot: {} @ {}, grid: {} - {}",
                                guiClassName, slotClassName, outputSlot, gridStart, gridStart + gridSize - 1);
                        }
                        else
                        {
                            ItemScroller.logger.warn("addCraftingGrids(): Error adding crafting grid slot definitions from '{}'", line);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        ItemScroller.logger.warn("addCraftingGrids(): Error while parsing crafting grid slot numbers for specifier '{}'", line, e);
                    }
                }
                else
                {
                    ItemScroller.logger.warn("addCraftingGrids(): Invalid crafting grid specifier '{}'", line);
                }
            }
        }
        catch (PatternSyntaxException e)
        {
            ItemScroller.logger.warn("addCraftingGrids(): Pattern syntax exception", e);
        }
    }
}
