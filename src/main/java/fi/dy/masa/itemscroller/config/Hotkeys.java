package fi.dy.masa.itemscroller.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.Context;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.ListUtils;

public class Hotkeys
{
    private static final KeyBindSettings CANCEL_SUCCESS       = KeyBindSettings.create(Context.GUI, KeyAction.PRESS, false, false, false, CancelCondition.ON_SUCCESS);
    private static final KeyBindSettings CANCEL_SUCCESS_EXTRA = KeyBindSettings.create(Context.GUI, KeyAction.PRESS, true , false, false, CancelCondition.ON_SUCCESS);

    public static final HotkeyConfig KEY_OPEN_CONFIG_GUI            = new HotkeyConfig("openConfigGui",             "I,C");

    public static final HotkeyConfig KEY_CRAFT_EVERYTHING           = new HotkeyConfig("craftEverything",           "L_CTRL,C",                 CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_DROP_ALL_MATCHING          = new HotkeyConfig("dropAllMatching",           "L_CTRL,L_SHIFT,Q",         CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_MASS_CRAFT                 = new HotkeyConfig("massCraft",                 "L_CTRL,L_ALT,C",           CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_MAIN_TOGGLE                = new HotkeyConfig("modFeaturesMainToggle",     "",                         KeyBindSettings.GUI_DEFAULT);
    public static final HotkeyConfig KEY_MOVE_CRAFT_RESULTS         = new HotkeyConfig("moveCraftResults",          "L_CTRL,M",                 CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_MOVE_STACK_TO_OFFHAND      = new HotkeyConfig("moveStackToOffhand",        "F",                        CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_RECIPE_VIEW                = new HotkeyConfig("recipeView",                "A",                        KeyBindSettings.GUI_MODIFIER);
    public static final HotkeyConfig KEY_SLOT_DEBUG                 = new HotkeyConfig("slotDebug",                 "L_CTRL,L_ALT,L_SHIFT,I",   CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_STORE_RECIPE               = new HotkeyConfig("storeRecipe",               "MMB",                      CANCEL_SUCCESS_EXTRA);
    public static final HotkeyConfig KEY_THROW_CRAFT_RESULTS        = new HotkeyConfig("throwCraftResults",         "L_CTRL,T",                 CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_VILLAGER_TRADE_FAVORITES   = new HotkeyConfig("villagerTradeFavorites",    "",                         CANCEL_SUCCESS);

    public static final HotkeyConfig KEY_DRAG_LEAVE_ONE             = new HotkeyConfig("keyDragMoveLeaveOne",       "L_SHIFT,RMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_DRAG_MATCHING              = new HotkeyConfig("keyDragMoveMatching",       "L_ALT,LMB",                CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_DRAG_MOVE_ONE              = new HotkeyConfig("keyDragMoveOne",            "L_CTRL,LMB",               CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_DRAG_FULL_STACKS           = new HotkeyConfig("keyDragMoveStacks",         "L_SHIFT,LMB",              CANCEL_SUCCESS);

    public static final HotkeyConfig KEY_DRAG_DROP_LEAVE_ONE        = new HotkeyConfig("keyDragDropLeaveOne",       "L_SHIFT,Q,RMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_DRAG_DROP_SINGLE           = new HotkeyConfig("keyDragDropSingle",         "Q,LMB",                    CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_DRAG_DROP_STACKS           = new HotkeyConfig("keyDragDropStacks",         "L_SHIFT,Q,LMB",            CANCEL_SUCCESS);

    public static final HotkeyConfig KEY_MOVE_EVERYTHING            = new HotkeyConfig("keyMoveEverything",         "L_ALT,L_SHIFT,LMB",        CANCEL_SUCCESS);

    public static final HotkeyConfig KEY_WS_MOVE_DOWN_LEAVE_ONE     = new HotkeyConfig("wsMoveDownLeaveOne",        "S,RMB",                    CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_WS_MOVE_DOWN_MATCHING      = new HotkeyConfig("wsMoveDownMatching",        "L_ALT,S,LMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_WS_MOVE_DOWN_SINGLE        = new HotkeyConfig("wsMoveDownSingle",          "S,LMB",                    CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_WS_MOVE_DOWN_STACKS        = new HotkeyConfig("wsMoveDownStacks",          "L_SHIFT,S,LMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_WS_MOVE_UP_LEAVE_ONE       = new HotkeyConfig("wsMoveUpLeaveOne",          "W,RMB",                    CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_WS_MOVE_UP_MATCHING        = new HotkeyConfig("wsMoveUpMatching",          "L_ALT,W,LMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_WS_MOVE_UP_SINGLE          = new HotkeyConfig("wsMoveUpSingle",            "W,LMB",                    CANCEL_SUCCESS);
    public static final HotkeyConfig KEY_WS_MOVE_UP_STACKS          = new HotkeyConfig("wsMoveUpStacks",            "L_SHIFT,W,LMB",            CANCEL_SUCCESS);

    public static final HotkeyConfig MODIFIER_MOVE_EVERYTHING       = new HotkeyConfig("modifierMoveEverything",    "L_ALT,L_SHIFT",            CANCEL_SUCCESS);
    public static final HotkeyConfig MODIFIER_MOVE_MATCHING         = new HotkeyConfig("modifierMoveMatching",      "L_ALT",                    CANCEL_SUCCESS);
    public static final HotkeyConfig MODIFIER_MOVE_STACK            = new HotkeyConfig("modifierMoveStack",         "L_SHIFT",                  CANCEL_SUCCESS);

    public static final List<HotkeyConfig> HOTKEY_LIST = ImmutableList.of(
            KEY_OPEN_CONFIG_GUI,

            MODIFIER_MOVE_EVERYTHING,
            MODIFIER_MOVE_MATCHING,
            MODIFIER_MOVE_STACK,

            KEY_MOVE_EVERYTHING,

            KEY_DRAG_FULL_STACKS,
            KEY_DRAG_LEAVE_ONE,
            KEY_DRAG_MATCHING,
            KEY_DRAG_MOVE_ONE,

            KEY_DRAG_DROP_LEAVE_ONE,
            KEY_DRAG_DROP_SINGLE,
            KEY_DRAG_DROP_STACKS,

            KEY_CRAFT_EVERYTHING,
            KEY_DROP_ALL_MATCHING,
            KEY_MAIN_TOGGLE,
            KEY_MASS_CRAFT,
            KEY_MOVE_CRAFT_RESULTS,
            KEY_MOVE_STACK_TO_OFFHAND,
            KEY_RECIPE_VIEW,
            KEY_SLOT_DEBUG,
            KEY_STORE_RECIPE,
            KEY_THROW_CRAFT_RESULTS,
            KEY_VILLAGER_TRADE_FAVORITES,

            KEY_WS_MOVE_DOWN_LEAVE_ONE,
            KEY_WS_MOVE_DOWN_MATCHING,
            KEY_WS_MOVE_DOWN_SINGLE,
            KEY_WS_MOVE_DOWN_STACKS,
            KEY_WS_MOVE_UP_LEAVE_ONE,
            KEY_WS_MOVE_UP_MATCHING,
            KEY_WS_MOVE_UP_SINGLE,
            KEY_WS_MOVE_UP_STACKS
    );

    public static final ImmutableList<? extends Hotkey> ALL_HOTKEYS = ListUtils.getAppendedList(Hotkeys.HOTKEY_LIST, Configs.Toggles.HOTKEYS);
}
