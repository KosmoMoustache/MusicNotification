package net.kosmo.music.impl.config;

public class ConfigHolder {
    public boolean SHOW_TITLE_SCREEN_BUTTON;

    public boolean SHOW_AUTHOR;
    public boolean SHOW_ALBUM_NAME;
    public boolean ROTATE_ALBUM_COVER;
    public DisableToastSound DISABLE_TOAST_SOUND;

    public int MAX_COUNT_HISTORY;
    public boolean DEBUG_MOD;

    public ConfigHolder(boolean SHOW_TITLE_SCREEN_BUTTON, boolean SHOW_AUTHOR, boolean SHOW_ALBUM_NAME, boolean ROTATE_ALBUM_COVER, DisableToastSound DISABLE_TOAST_SOUND, int MAX_COUNT_HISTORY, boolean DEBUG_MOD) {
        set(SHOW_TITLE_SCREEN_BUTTON, SHOW_AUTHOR, SHOW_ALBUM_NAME, ROTATE_ALBUM_COVER, DISABLE_TOAST_SOUND, MAX_COUNT_HISTORY, DEBUG_MOD);
    }

    public void set(boolean SHOW_TITLE_SCREEN_BUTTON, boolean SHOW_AUTHOR, boolean SHOW_ALBUM_NAME, boolean ROTATE_ALBUM_COVER, DisableToastSound DISABLE_TOAST_SOUND, int MAX_COUNT_HISTORY, boolean DEBUG_MOD) {
        this.SHOW_TITLE_SCREEN_BUTTON = SHOW_TITLE_SCREEN_BUTTON;
        this.SHOW_AUTHOR = SHOW_AUTHOR;
        this.SHOW_ALBUM_NAME = SHOW_ALBUM_NAME;
        this.ROTATE_ALBUM_COVER = ROTATE_ALBUM_COVER;
        this.DISABLE_TOAST_SOUND = DISABLE_TOAST_SOUND;
        this.MAX_COUNT_HISTORY = MAX_COUNT_HISTORY;
        this.DEBUG_MOD = DEBUG_MOD;
    }

    public enum DisableToastSound {
        VANILLA(),
        MUTE_SELF(),
        MUTE_ALL(),
    }
}
