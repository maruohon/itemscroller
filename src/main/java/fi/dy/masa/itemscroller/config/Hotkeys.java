package fi.dy.masa.itemscroller.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.Context;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindSettings;

public class Hotkeys
{
    private static final KeyBindSettings GUI_RELAXED        = KeyBindSettings.create(Context.GUI, KeyAction.PRESS, true, false, false, CancelCondition.NEVER);
    private static final KeyBindSettings GUI_RELAXED_CANCEL = KeyBindSettings.create(Context.GUI, KeyAction.PRESS, true, false, false, CancelCondition.ALWAYS);
    private static final KeyBindSettings GUI_NO_ORDER       = KeyBindSettings.create(Context.GUI, KeyAction.PRESS, false, false, false, CancelCondition.ALWAYS);

    public static final HotkeyConfig KEY_OPEN_CONFIG_GUI            = new HotkeyConfig("openConfigGui", "I,C", "Open the in-game config GUI");

    public static final HotkeyConfig KEY_CRAFT_EVERYTHING           = new HotkeyConfig("craftEverything", "LCONTROL,C", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_DROP_ALL_MATCHING          = new HotkeyConfig("dropAllMatching", "LCONTROL,LSHIFT,Q", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_MAIN_TOGGLE                = new HotkeyConfig("itemScrollerMainToggle", "", KeyBindSettings.GUI_DEFAULT);
    public static final HotkeyConfig KEY_MASS_CRAFT                 = new HotkeyConfig("massCraft", "LCONTROL,LMENU,C", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_MOVE_CRAFT_RESULTS         = new HotkeyConfig("moveCraftResults", "LCONTROL,M", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_MOVE_STACK_TO_OFFHAND      = new HotkeyConfig("moveStackToOffhand", "F", KeyBindSettings.GUI_DEFAULT);
    public static final HotkeyConfig KEY_RECIPE_VIEW                = new HotkeyConfig("recipeView", "A", GUI_RELAXED);
    public static final HotkeyConfig KEY_SLOT_DEBUG                 = new HotkeyConfig("slotDebug", "LCONTROL,LMENU,LSHIFT,I", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_STORE_RECIPE               = new HotkeyConfig("storeRecipe", "BUTTON2", GUI_RELAXED_CANCEL);
    public static final HotkeyConfig KEY_THROW_CRAFT_RESULTS        = new HotkeyConfig("throwCraftResults", "LCONTROL,T", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_VILLAGER_TRADE_FAVORITES   = new HotkeyConfig("villagerTradeFavorites", "", KeyBindSettings.GUI_DEFAULT);

    public static final HotkeyConfig KEY_DRAG_LEAVE_ONE             = new HotkeyConfig("keyDragMoveLeaveOne", "LSHIFT,BUTTON1", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_DRAG_MATCHING              = new HotkeyConfig("keyDragMoveMatching", "LMENU,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_DRAG_MOVE_ONE              = new HotkeyConfig("keyDragMoveOne", "LCONTROL,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_DRAG_FULL_STACKS           = new HotkeyConfig("keyDragMoveStacks", "LSHIFT,BUTTON0", GUI_NO_ORDER);

    public static final HotkeyConfig KEY_DRAG_DROP_LEAVE_ONE        = new HotkeyConfig("keyDragDropLeaveOne", "LSHIFT,Q,BUTTON1", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_DRAG_DROP_SINGLE           = new HotkeyConfig("keyDragDropSingle", "Q,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_DRAG_DROP_STACKS           = new HotkeyConfig("keyDragDropStacks", "LSHIFT,Q,BUTTON0", GUI_NO_ORDER);

    public static final HotkeyConfig KEY_MOVE_EVERYTHING            = new HotkeyConfig("keyMoveEverything", "LMENU,LSHIFT,BUTTON0", GUI_NO_ORDER);

    public static final HotkeyConfig KEY_WS_MOVE_DOWN_LEAVE_ONE     = new HotkeyConfig("wsMoveDownLeaveOne", "S,BUTTON1", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_WS_MOVE_DOWN_MATCHING      = new HotkeyConfig("wsMoveDownMatching", "LMENU,S,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_WS_MOVE_DOWN_SINGLE        = new HotkeyConfig("wsMoveDownSingle", "S,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_WS_MOVE_DOWN_STACKS        = new HotkeyConfig("wsMoveDownStacks", "LSHIFT,S,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_WS_MOVE_UP_LEAVE_ONE       = new HotkeyConfig("wsMoveUpLeaveOne", "W,BUTTON1", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_WS_MOVE_UP_MATCHING        = new HotkeyConfig("wsMoveUpMatching", "LMENU,W,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_WS_MOVE_UP_SINGLE          = new HotkeyConfig("wsMoveUpSingle", "W,BUTTON0", GUI_NO_ORDER);
    public static final HotkeyConfig KEY_WS_MOVE_UP_STACKS          = new HotkeyConfig("wsMoveUpStacks", "LSHIFT,W,BUTTON0", GUI_NO_ORDER);

    public static final HotkeyConfig MODIFIER_MOVE_EVERYTHING       = new HotkeyConfig("modifierMoveEverything", "LMENU,LSHIFT", GUI_NO_ORDER);
    public static final HotkeyConfig MODIFIER_MOVE_MATCHING         = new HotkeyConfig("modifierMoveMatching", "LMENU", GUI_NO_ORDER);
    public static final HotkeyConfig MODIFIER_MOVE_STACK            = new HotkeyConfig("modifierMoveStack", "LSHIFT", GUI_NO_ORDER);

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
}
