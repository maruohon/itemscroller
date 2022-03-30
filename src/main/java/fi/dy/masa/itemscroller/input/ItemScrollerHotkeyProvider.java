package fi.dy.masa.itemscroller.input;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyProvider;
import fi.dy.masa.itemscroller.Reference;
import fi.dy.masa.itemscroller.config.Configs;
import fi.dy.masa.itemscroller.config.Hotkeys;

public class ItemScrollerHotkeyProvider implements HotkeyProvider
{
    public static final ItemScrollerHotkeyProvider INSTANCE = new ItemScrollerHotkeyProvider();

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return Hotkeys.ALL_HOTKEYS;
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        return ImmutableList.of(new HotkeyCategory(Reference.MOD_INFO, "itemscroller.hotkeys.category.hotkeys", Hotkeys.HOTKEY_LIST),
                                new HotkeyCategory(Reference.MOD_INFO, "itemscroller.hotkeys.category.toggles", Configs.Toggles.HOTKEYS));
    }
}
