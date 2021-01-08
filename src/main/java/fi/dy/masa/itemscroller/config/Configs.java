package fi.dy.masa.itemscroller.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.ModConfig;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.ConfigOption;
import fi.dy.masa.malilib.config.option.StringListConfig;

public class Configs implements ModConfig
{
    public static class Generic
    {
        public static final BooleanConfig CARPET_CTRL_Q_CRAFTING                = new BooleanConfig("carpetCtrlQCraftingEnabledOnServer", false);
        public static final BooleanConfig CLIENT_CRAFTING_FIX                   = new BooleanConfig("clientCraftingFixOn1.12", true);
        public static final BooleanConfig CRAFTING_RENDER_RECIPE_ITEMS          = new BooleanConfig("craftingRenderRecipeItems", true);
        public static final BooleanConfig SCROLL_CRAFT_STORE_RECIPES_TO_FILE    = new BooleanConfig("craftingRecipesSaveToFile", true);
        public static final BooleanConfig SCROLL_CRAFT_RECIPE_FILE_GLOBAL       = new BooleanConfig("craftingRecipesSaveFileIsGlobal", false);
        public static final BooleanConfig REVERSE_SCROLL_DIRECTION_SINGLE       = new BooleanConfig("reverseScrollDirectionSingle", false);
        public static final BooleanConfig REVERSE_SCROLL_DIRECTION_STACKS       = new BooleanConfig("reverseScrollDirectionStacks", false);
        public static final BooleanConfig SLOT_POSITION_AWARE_SCROLL_DIRECTION  = new BooleanConfig("useSlotPositionAwareScrollDirection", false);
        public static final BooleanConfig VILLAGER_TRADE_LIST_REMEMBER_PAGE     = new BooleanConfig("villagerTradeListRememberPage", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
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
        public static final BooleanConfig CRAFTING_FEATURES         = new BooleanConfig("enableCraftingFeatures", true);
        public static final BooleanConfig DROP_MATCHING             = new BooleanConfig("enableDropkeyDropMatching", true);
        public static final BooleanConfig MAIN_TOGGLE               = new BooleanConfig("mainToggle", true);
        public static final BooleanConfig RIGHT_CLICK_CRAFT_STACK   = new BooleanConfig("enableRightClickCraftingOneStack", true);
        public static final BooleanConfig SCROLL_EVERYTHING         = new BooleanConfig("enableScrollingEverything", true);
        public static final BooleanConfig SCROLL_MATCHING           = new BooleanConfig("enableScrollingMatchingStacks", true);
        public static final BooleanConfig SCROLL_SINGLE             = new BooleanConfig("enableScrollingSingle", true);
        public static final BooleanConfig SCROLL_STACKS             = new BooleanConfig("enableScrollingStacks", true);
        public static final BooleanConfig SCROLL_STACKS_FALLBACK    = new BooleanConfig("enableScrollingStacksFallback", true);
        public static final BooleanConfig SCROLL_VILLAGER           = new BooleanConfig("enableScrollingVillager", true);
        public static final BooleanConfig SHIFT_DROP_ITEMS          = new BooleanConfig("enableShiftDropItems", true);
        public static final BooleanConfig SHIFT_PLACE_ITEMS         = new BooleanConfig("enableShiftPlaceItems", true);
        public static final BooleanConfig VILLAGER_TRADE_LIST       = new BooleanConfig("enableVillagerTradeList", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
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
        public static final StringListConfig GUI_BLACKLIST   = new StringListConfig("guiBlacklist", ImmutableList.of());
        public static final StringListConfig SLOT_BLACKLIST  = new StringListConfig("slotBlacklist", ImmutableList.of());

        public static final ImmutableList<StringListConfig> OPTIONS = ImmutableList.of(
                GUI_BLACKLIST,
                SLOT_BLACKLIST
        );
    }

    private static final ImmutableList<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
            BaseConfigOptionCategory.normal("Generic", Generic.OPTIONS),
            BaseConfigOptionCategory.normal("Toggles", Toggles.OPTIONS),
            BaseConfigOptionCategory.normal("Hotkeys", Hotkeys.HOTKEY_LIST),
            BaseConfigOptionCategory.normal("Lists",   Lists.OPTIONS)
    );

    @Override
    public String getModId()
    {
        return Reference.MOD_ID;
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
    public List<ConfigOptionCategory> getConfigOptionCategories()
    {
        return CATEGORIES;
    }

    @Override
    public void onPostLoad()
    {
        CraftingHandler.updateGridDefinitions();
    }
}
