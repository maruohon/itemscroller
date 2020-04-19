package fi.dy.masa.itemscroller.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.config.options.IConfigValue;

public class Configs implements IConfigHandler
{
    public static class Generic
    {
        public static final ConfigBoolean CARPET_CTRL_Q_CRAFTING                = new ConfigBoolean("carpetCtrlQCraftingEnabledOnServer",   false, "Set to true if the server is running the Carpet mod,\nand has the ctrlQCrafting option enabled.\nThis just changes which method Item Scroller uses\nfor the Drop key + Shift + Right click crafting.");
        public static final ConfigBoolean CLIENT_CRAFTING_FIX                   = new ConfigBoolean("clientCraftingFixOn1.12",              true, "Enable updating the crafting recipe output directly on the client side.\nThis fixes the quick/mass crafting and right-click-to-craft-a-stack\nfeatures othrwise being broken in 1.12.");
        public static final ConfigBoolean CRAFTING_RENDER_RECIPE_ITEMS          = new ConfigBoolean("craftingRenderRecipeItems",            true, "If enabled, then the recipe items are also rendered\nin the crafting recipe view.");
        public static final ConfigBoolean SCROLL_CRAFT_STORE_RECIPES_TO_FILE    = new ConfigBoolean("craftingRecipesSaveToFile",            true, "If enabled, then the crafting features recipes are saved to a file\ninside minecraft/itemscroller/recipes_worldorservername.nbt.\nThis makes the recipes persistent across game restarts.");
        public static final ConfigBoolean SCROLL_CRAFT_RECIPE_FILE_GLOBAL       = new ConfigBoolean("craftingRecipesSaveFileIsGlobal",      false, "If true, then the recipe file is global, instead\n of being saved per-world or server");
        public static final ConfigBoolean REVERSE_SCROLL_DIRECTION_SINGLE       = new ConfigBoolean("reverseScrollDirectionSingle",         false, "Reverse the scrolling direction for single item mode.");
        public static final ConfigBoolean REVERSE_SCROLL_DIRECTION_STACKS       = new ConfigBoolean("reverseScrollDirectionStacks",         false, "Reverse the scrolling direction for full stacks mode.");
        public static final ConfigBoolean SLOT_POSITION_AWARE_SCROLL_DIRECTION  = new ConfigBoolean("useSlotPositionAwareScrollDirection",  false, "When enabled, the item movement direction depends\non the slots' y-position on screen. Might be derpy with more\ncomplex inventories, use with caution!");
        public static final ConfigBoolean VILLAGER_TRADE_LIST_REMEMBER_PAGE     = new ConfigBoolean("villagerTradeListRememberPage",        true, "Remember and restore the last looked at page/trade when re-opening the GUI");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                CARPET_CTRL_Q_CRAFTING,
                CLIENT_CRAFTING_FIX,
                CRAFTING_RENDER_RECIPE_ITEMS,
                SCROLL_CRAFT_STORE_RECIPES_TO_FILE,
                SCROLL_CRAFT_RECIPE_FILE_GLOBAL,
                REVERSE_SCROLL_DIRECTION_SINGLE,
                REVERSE_SCROLL_DIRECTION_STACKS,
                SLOT_POSITION_AWARE_SCROLL_DIRECTION,
                VILLAGER_TRADE_LIST_REMEMBER_PAGE
        );
    }

    public static class Toggles
    {
        public static final ConfigBoolean CRAFTING_FEATURES         = new ConfigBoolean("enableCraftingFeatures",           true, "Enables scrolling items to and from crafting grids,\nwith a built-in 18 recipe memory.\nHold down the Recipe key to see the stored recipes and\nto change the selection. While holding the Recipe key,\nyou can either scroll or press a number key to change the selection.\nA recipe is stored to the currently selected \"recipe slot\"\n by clicking pick block over a configured crafting output slot.\nThe supported crafting grids must be added to the scrollableCraftingGrids list.");
        public static final ConfigBoolean DROP_MATCHING             = new ConfigBoolean("enableDropkeyDropMatching",        true, "Enables dropping all matching items from the same\ninventory with the hotkey");
        public static final ConfigBoolean MAIN_TOGGLE               = new ConfigBoolean("mainToggle",                       true, "Turn off all Item Scroller functionality");
        public static final ConfigBoolean RIGHT_CLICK_CRAFT_STACK   = new ConfigBoolean("enableRightClickCraftingOneStack", true, "Enables crafting up to one full stack when right clicking on\na slot that has been configured as a crafting output slot.");
        public static final ConfigBoolean SCROLL_EVERYTHING         = new ConfigBoolean("enableScrollingEverything",        true, "Enables scroll moving all items at once while\nholding the modifierMoveEverything keybind");
        public static final ConfigBoolean SCROLL_MATCHING           = new ConfigBoolean("enableScrollingMatchingStacks",    true, "Enables scroll moving all matching stacks at once\nwhile holding the modifierMoveMatching keybind");
        public static final ConfigBoolean SCROLL_SINGLE             = new ConfigBoolean("enableScrollingSingle",            true, "Enables moving items one item at a time by scrolling over a stack");
        public static final ConfigBoolean SCROLL_STACKS             = new ConfigBoolean("enableScrollingStacks",            true, "Enables moving entire stacks at a time by scrolling over a stack");
        public static final ConfigBoolean SCROLL_STACKS_FALLBACK    = new ConfigBoolean("enableScrollingStacksFallback",    true, "Enables a \"fallback\" mode for scrolling entire stacks\n(for example to a vanilla crafting table,\nwhere shift + click doesn't work).");
        public static final ConfigBoolean SCROLL_VILLAGER           = new ConfigBoolean("enableScrollingVillager",          true, "Enables special handling for the Villager GUIs.\n(Normally you can't shift click items in them.)\nHold shift and scroll up/down over the trade output slot.");
        public static final ConfigBoolean SHIFT_DROP_ITEMS          = new ConfigBoolean("enableShiftDropItems",             true, "Enables dropping all matching items at once by holding\nshift while clicking to drop a stack");
        public static final ConfigBoolean SHIFT_PLACE_ITEMS         = new ConfigBoolean("enableShiftPlaceItems",            true, "Enables moving all matching stacks at once by holding\nshift while placing items to an empty slot");
        public static final ConfigBoolean VILLAGER_TRADE_LIST       = new ConfigBoolean("enableVillagerTradeList",          true, "Render a 1.14-style trade list in villager GUIs");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                CRAFTING_FEATURES,
                DROP_MATCHING,
                MAIN_TOGGLE,
                RIGHT_CLICK_CRAFT_STACK,
                SCROLL_EVERYTHING,
                SCROLL_MATCHING,
                SCROLL_SINGLE,
                SCROLL_STACKS,
                SCROLL_STACKS_FALLBACK,
                SCROLL_VILLAGER,
                SHIFT_DROP_ITEMS,
                SHIFT_PLACE_ITEMS,
                VILLAGER_TRADE_LIST
        );
    }

    public static class Lists
    {
        public static final ConfigStringList GUI_BLACKLIST   = new ConfigStringList("guiBlacklist", ImmutableList.of(), "A list of GUIs where Item Scroller shouldn't be active");
        public static final ConfigStringList SLOT_BLACKLIST  = new ConfigStringList("slotBlacklist", ImmutableList.of(), "A list of slots that Item Scroller should not operate on");

        public static final ImmutableList<ConfigStringList> OPTIONS = ImmutableList.of(
                GUI_BLACKLIST,
                SLOT_BLACKLIST
        );
    }

    @Override
    public String getModName()
    {
        return Reference.MOD_NAME;
    }

    @Override
    public String getConfigFileName()
    {
        return Reference.MOD_ID + ".json";
    }

    @Override
    public Map<String, List<? extends IConfigBase>> getConfigsPerCategories()
    {
        Map<String, List<? extends IConfigBase>> map = new HashMap<>();

        map.put("Generic", Generic.OPTIONS);
        map.put("Toggles", Toggles.OPTIONS);
        map.put("Hotkeys", Hotkeys.HOTKEY_LIST);
        map.put("Lists",   Lists.OPTIONS);

        return map;
    }

    @Override
    public void onPostLoad()
    {
        CraftingHandler.updateGridDefinitions();
    }
}
