package fi.dy.masa.itemscroller.config;

import java.util.List;
import com.google.common.collect.ImmutableList;

import malilib.config.option.HotkeyConfig;
import malilib.input.CancelCondition;
import malilib.input.Context;
import malilib.input.Hotkey;
import malilib.input.KeyAction;
import malilib.input.KeyBindSettings;
import malilib.util.ListUtils;

public class Hotkeys
{
    private static final KeyBindSettings CANCEL_SUCCESS       = new KeyBindSettings(Context.GUI, KeyAction.PRESS, false, true, CancelCondition.ON_SUCCESS);
    private static final KeyBindSettings CANCEL_SUCCESS_EXTRA = new KeyBindSettings(Context.GUI, KeyAction.PRESS, true , true, CancelCondition.ON_SUCCESS);

    public static final HotkeyConfig CRAFT_EVERYTHING               = new HotkeyConfig("craftEverything",           "L_CTRL,C",                 CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_DROP_ALL_MATCHING         = new HotkeyConfig("dragDropAllMatching",       "L_ALT,Q,LMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_DROP_ENTIRE_STACKS        = new HotkeyConfig("dragDropEntireStacks",      "L_SHIFT,Q,LMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_DROP_LEAVE_ONE            = new HotkeyConfig("dragDropLeaveOne",          "L_SHIFT,Q,RMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_DROP_ONE                  = new HotkeyConfig("dragDropOne",               "Q,LMB",                    CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_MOVE_ALL_MATCHING         = new HotkeyConfig("dragMoveAllMatching",       "L_ALT,LMB",                CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_MOVE_ENTIRE_STACKS        = new HotkeyConfig("dragMoveEntireStacks",      "L_SHIFT,LMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_MOVE_LEAVE_ONE            = new HotkeyConfig("dragMoveLeaveOne",          "L_SHIFT,RMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig DRAG_MOVE_ONE                  = new HotkeyConfig("dragMoveOne",               "L_CTRL,LMB",               CANCEL_SUCCESS);
    public static final HotkeyConfig DROP_ALL_MATCHING              = new HotkeyConfig("dropAllMatching",           "L_CTRL,L_SHIFT,Q",         CANCEL_SUCCESS);
    public static final HotkeyConfig MASS_CRAFT                     = new HotkeyConfig("massCraft",                 "L_CTRL,L_ALT,C",           CANCEL_SUCCESS);
    public static final HotkeyConfig MODIFIER_MOVE_ALL_MATCHING     = new HotkeyConfig("modifierMoveAllMatching",   "L_ALT",                    KeyBindSettings.GUI_MODIFIER);
    public static final HotkeyConfig MODIFIER_MOVE_EVERYTHING       = new HotkeyConfig("modifierMoveEverything",    "L_ALT,L_SHIFT",            KeyBindSettings.GUI_MODIFIER);
    public static final HotkeyConfig MODIFIER_MOVE_ENTIRE_STACK     = new HotkeyConfig("modifierMoveEntireStack",   "L_SHIFT",                  KeyBindSettings.GUI_MODIFIER);
    public static final HotkeyConfig MOVE_CRAFT_RESULTS             = new HotkeyConfig("moveCraftResults",          "L_CTRL,M",                 CANCEL_SUCCESS);
    public static final HotkeyConfig MOVE_EVERYTHING                = new HotkeyConfig("moveEverything",            "L_ALT,L_SHIFT,LMB",        CANCEL_SUCCESS);
    public static final HotkeyConfig MOVE_STACK_TO_OFFHAND          = new HotkeyConfig("moveStackToOffhand",        "F",                        CANCEL_SUCCESS);
    public static final HotkeyConfig OPEN_CONFIG_SCREEN             = new HotkeyConfig("openConfigScreen",          "I,C",                      KeyBindSettings.builder().context(Context.ANY).build());
    public static final HotkeyConfig SHOW_RECIPES                   = new HotkeyConfig("showRecipes",               "A",                        KeyBindSettings.GUI_MODIFIER);
    public static final HotkeyConfig SLOT_DEBUG                     = new HotkeyConfig("slotDebug",                 "L_CTRL,L_ALT,L_SHIFT,I",   CANCEL_SUCCESS);
    public static final HotkeyConfig STORE_RECIPE                   = new HotkeyConfig("storeRecipe",               "MMB",                      CANCEL_SUCCESS_EXTRA);
    public static final HotkeyConfig THROW_CRAFT_RESULTS            = new HotkeyConfig("throwCraftResults",         "L_CTRL,T",                 CANCEL_SUCCESS);
    public static final HotkeyConfig VILLAGER_TRADE_FAVORITES       = new HotkeyConfig("villagerTradeFavorites",    "",                         CANCEL_SUCCESS);

    public static final HotkeyConfig WS_MOVE_DOWN_ALL_MATCHING      = new HotkeyConfig("wsMoveDownAllMatching",     "L_ALT,S,LMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig WS_MOVE_DOWN_ENTIRE_STACKS     = new HotkeyConfig("wsMoveDownEntireStacks",    "L_SHIFT,S,LMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig WS_MOVE_DOWN_LEAVE_ONE         = new HotkeyConfig("wsMoveDownLeaveOne",        "L_SHIFT,S,RMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig WS_MOVE_DOWN_ONE               = new HotkeyConfig("wsMoveDownOne",             "S,LMB",                    CANCEL_SUCCESS);
    public static final HotkeyConfig WS_MOVE_UP_ALL_MATCHING        = new HotkeyConfig("wsMoveUpAllMatching",       "L_ALT,W,LMB",              CANCEL_SUCCESS);
    public static final HotkeyConfig WS_MOVE_UP_ENTIRE_STACKS       = new HotkeyConfig("wsMoveUpEntireStacks",      "L_SHIFT,W,LMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig WS_MOVE_UP_LEAVE_ONE           = new HotkeyConfig("wsMoveUpLeaveOne",          "L_SHIFT,W,RMB",            CANCEL_SUCCESS);
    public static final HotkeyConfig WS_MOVE_UP_ONE                 = new HotkeyConfig("wsMoveUpOne",               "W,LMB",                    CANCEL_SUCCESS);

    public static final List<HotkeyConfig> HOTKEY_LIST = ImmutableList.of(
            CRAFT_EVERYTHING,
            DRAG_DROP_LEAVE_ONE,
            DRAG_DROP_ONE,
            DRAG_DROP_ENTIRE_STACKS,
            DRAG_MOVE_ENTIRE_STACKS,
            DRAG_MOVE_LEAVE_ONE,
            DRAG_MOVE_ALL_MATCHING,
            DRAG_MOVE_ONE,
            DROP_ALL_MATCHING,
            MASS_CRAFT,
            MODIFIER_MOVE_EVERYTHING,
            MODIFIER_MOVE_ALL_MATCHING,
            MODIFIER_MOVE_ENTIRE_STACK,
            MOVE_CRAFT_RESULTS,
            MOVE_EVERYTHING,
            MOVE_STACK_TO_OFFHAND,
            OPEN_CONFIG_SCREEN,
            SHOW_RECIPES,
            SLOT_DEBUG,
            STORE_RECIPE,
            THROW_CRAFT_RESULTS,
            VILLAGER_TRADE_FAVORITES,
            WS_MOVE_DOWN_LEAVE_ONE,
            WS_MOVE_DOWN_ALL_MATCHING,
            WS_MOVE_DOWN_ONE,
            WS_MOVE_DOWN_ENTIRE_STACKS,
            WS_MOVE_UP_LEAVE_ONE,
            WS_MOVE_UP_ALL_MATCHING,
            WS_MOVE_UP_ONE,
            WS_MOVE_UP_ENTIRE_STACKS
    );

    public static final ImmutableList<? extends Hotkey> ALL_HOTKEYS = ListUtils.getAppendedList(Hotkeys.HOTKEY_LIST, Configs.Toggles.HOTKEYS);
}
