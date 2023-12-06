package net.kosmo.nowplaying;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = NowPlaying.MOD_ID)
public class NowPlayingConfig implements ConfigData {
    public boolean ENABLE_MOD = true;
    public boolean SHOW_AUTHOR = true;
    public boolean SHOW_SOUNDTRACK = false;
    public boolean SHOULD_SHOW_JUKEBOX = true;
    public boolean SHOW_TITLE_SCREEN_BUTTON = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public DisableToastSound DISABLE_TOAST_SOUND = DisableToastSound.MUTE_MOD;
    public boolean ROTATE_ALBUM_COVER = true;

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
    }

    public enum DisableToastSound {
        VANILLA(),
        MUTE_MOD(),
        MUTE_ALL(),
    }
}