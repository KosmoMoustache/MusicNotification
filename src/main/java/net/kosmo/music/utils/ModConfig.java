package net.kosmo.music.utils;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.kosmo.music.ClientMusic;

@Config(name = ClientMusic.MOD_ID)
public class ModConfig implements ConfigData {
    public boolean HIDE_AUTHOR = false;
    public boolean SHOW_SOUNDTRACK_NAME = false;
    public boolean ROTATE_ALBUM_COVER = true;
    public boolean SHOW_TITLE_SCREEN_BUTTON = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public DisableToastSound DISABLE_TOAST_SOUND = DisableToastSound.MUTE_SELF;

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
    }

    public enum DisableToastSound {
        VANILLA(),
        MUTE_SELF(),
        MUTE_ALL(),
    }
}