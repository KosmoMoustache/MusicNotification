package net.kosmo.nowplaying;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = NowPlaying.MOD_ID)
public class NowPlayingConfig implements ConfigData {
    public boolean ENABLE_MOD = true;
    public boolean HIDE_AUTHOR = false;
    public boolean SHOW_SOUNDTRACK_NAME = false;
    public boolean SHOULD_SHOW_JUKEBOX = true;
    public boolean SHOW_TITLE_SCREEN_BUTTON = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public DisableToastSound DISABLE_TOAST_SOUND = DisableToastSound.DISABLE_THIS;
    public boolean ROTATE_ALBUM_COVER = true;

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
    }

    public enum DisableToastSound {
        VANILLA(),
        DISABLE_THIS(),
        DISABLE_ALL(),
    }
}