package net.kosmo.music.impl.config;

public interface IConfig {
    public boolean SHOW_TITLE_SCREEN_BUTTON = false;

    public boolean SHOW_AUTHOR = false;
    public boolean SHOW_ALBUM_NAME = false;
    public boolean ROTATE_ALBUM_COVER = false;
    public DisableToastSound DISABLE_TOAST_SOUND = DisableToastSound.VANILLA;

    public int MAX_COUNT_HISTORY = 10;
    public boolean DEBUG_MOD = false;

    ConfigHolder DEFAULT = new ConfigHolder(
            true,
            true,
            false,
            true,
            ConfigHolder.DisableToastSound.MUTE_SELF,
            10,
            false
    );

    default void set(boolean SHOW_TITLE_SCREEN_BUTTON, boolean SHOW_AUTHOR, boolean SHOW_ALBUM_NAME, boolean ROTATE_ALBUM_COVER, DisableToastSound DISABLE_TOAST_SOUND, int MAX_COUNT_HISTORY, boolean DEBUG_MOD) {
    }

    enum DisableToastSound {
        VANILLA(),
        MUTE_SELF(),
        MUTE_ALL(),
    }
}
