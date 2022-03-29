package fi.dy.masa.itemscroller.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.list.StringListConfig;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.overlay.message.MessageOutput;

public class Configs
{
    public static final int CONFIG_VERSION = 1;

    public static class Generic
    {
        public static final BooleanConfig CARPET_CTRL_Q_CRAFTING                = new BooleanConfig("carpetCtrlQCraftingEnabledOnServer", false);
        public static final BooleanConfig CLIENT_CRAFTING_FIX                   = new BooleanConfig("clientCraftingFix1.12", true);
        public static final BooleanConfig CRAFTING_RECIPES_SAVE_FILE_GLOBAL     = new BooleanConfig("craftingRecipesSaveFileGlobal", false);
        public static final BooleanConfig CRAFTING_RECIPES_SAVE_TO_FILE         = new BooleanConfig("craftingRecipesSaveToFile", true);
        public static final BooleanConfig CRAFTING_RENDER_RECIPE_ITEMS          = new BooleanConfig("craftingRenderRecipeItems", true);
        public static final BooleanConfig REVERSE_SCROLL_DIRECTION_SINGLE       = new BooleanConfig("reverseScrollDirectionSingle", false);
        public static final BooleanConfig REVERSE_SCROLL_DIRECTION_STACKS       = new BooleanConfig("reverseScrollDirectionStacks", false);
        public static final BooleanConfig SLOT_POSITION_AWARE_SCROLL_DIRECTION  = new BooleanConfig("slotPositionAwareScrollDirection", false);
        public static final BooleanConfig VILLAGER_TRADE_LIST_REMEMBER_PAGE     = new BooleanConfig("villagerTradeListRememberPage", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                CARPET_CTRL_Q_CRAFTING,
                CLIENT_CRAFTING_FIX,
                CRAFTING_RENDER_RECIPE_ITEMS,
                CRAFTING_RECIPES_SAVE_TO_FILE,
                CRAFTING_RECIPES_SAVE_FILE_GLOBAL,
                REVERSE_SCROLL_DIRECTION_SINGLE,
                REVERSE_SCROLL_DIRECTION_STACKS,
                SLOT_POSITION_AWARE_SCROLL_DIRECTION,
                VILLAGER_TRADE_LIST_REMEMBER_PAGE
        );
    }

    public static class Toggles
    {
        private static final KeyBindSettings GUI_TOGGLE = KeyBindSettings.GUI_DEFAULT.asBuilder().messageOutput(MessageOutput.MESSAGE_OVERLAY).build();

        public static final HotkeyedBooleanConfig CRAFTING_FEATURES     = new HotkeyedBooleanConfig("craftingFeatures",     true, "", GUI_TOGGLE);
        public static final HotkeyedBooleanConfig MOD_FEATURES_ENABLED  = new HotkeyedBooleanConfig("modFeaturesEnabled",   true, "", GUI_TOGGLE);

        public static final BooleanConfig DROP_MATCHING             = new BooleanConfig("dropMatchingWithKey", true);
        public static final BooleanConfig RIGHT_CLICK_CRAFT_STACK   = new BooleanConfig("rightClickCraftOneStack", true);
        public static final BooleanConfig SCROLL_MOVE_ALL_MATCHING  = new BooleanConfig("scrollMoveAllMatching", true);
        public static final BooleanConfig SCROLL_MOVE_EVERYTHING    = new BooleanConfig("scrollMoveEverything", true);
        public static final BooleanConfig SCROLL_MOVE_ENTIRE_STACKS = new BooleanConfig("scrollMoveEntireStacks", true);
        public static final BooleanConfig SCROLL_MOVE_ONE           = new BooleanConfig("scrollMoveOne", true);
        public static final BooleanConfig SCROLL_STACKS_FALLBACK    = new BooleanConfig("scrollStacksFallback", true);
        public static final BooleanConfig SCROLL_VILLAGER           = new BooleanConfig("scrollVillager", true);
        public static final BooleanConfig SHIFT_DROP_ITEMS          = new BooleanConfig("shiftDropItems", true);
        public static final BooleanConfig SHIFT_PLACE_ITEMS         = new BooleanConfig("shiftPlaceItems", true);
        public static final BooleanConfig VILLAGER_TRADE_LIST       = new BooleanConfig("villagerTradeList", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                MOD_FEATURES_ENABLED,
                CRAFTING_FEATURES,

                DROP_MATCHING,
                RIGHT_CLICK_CRAFT_STACK,
                SCROLL_MOVE_EVERYTHING,
                SCROLL_MOVE_ALL_MATCHING,
                SCROLL_MOVE_ONE,
                SCROLL_MOVE_ENTIRE_STACKS,
                SCROLL_STACKS_FALLBACK,
                SCROLL_VILLAGER,
                SHIFT_DROP_ITEMS,
                SHIFT_PLACE_ITEMS,
                VILLAGER_TRADE_LIST
        );

        public static final ImmutableList<? extends Hotkey> HOTKEYS = ImmutableList.of(
                CRAFTING_FEATURES,
                MOD_FEATURES_ENABLED
        );
    }

    public static class Lists
    {
        public static final StringListConfig GUI_BLACKLIST   = new StringListConfig("guiBlackList", ImmutableList.of());
        public static final StringListConfig SLOT_BLACKLIST  = new StringListConfig("slotBlackList", ImmutableList.of());

        public static final ImmutableList<StringListConfig> OPTIONS = ImmutableList.of(
                GUI_BLACKLIST,
                SLOT_BLACKLIST
        );
    }

    public static final ImmutableList<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
            BaseConfigOptionCategory.normal("Generic", Generic.OPTIONS),
            BaseConfigOptionCategory.normal("Toggles", Toggles.OPTIONS),
            BaseConfigOptionCategory.normal("Hotkeys", Hotkeys.HOTKEY_LIST),
            BaseConfigOptionCategory.normal("Lists",   Lists.OPTIONS)
    );
}
