package net.kosmo.music.impl.neoforge.compat;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.config.ConfigHolder.DisableToastSound;

import java.util.List;

@Config(name = ClientMusic.MOD_ID)
public class AutoConfigNeoForge implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public ToastConfig TOAST_CONFIG = new ToastConfig();
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public JukeboxConfig JUKEBOX_CONFIG = new JukeboxConfig();
    public boolean SHOW_TITLE_SCREEN_BUTTON = true;

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
    }

    public static class ToastConfig {
        public boolean SHOW_AUTHOR = true;
        public boolean SHOW_ALBUM_NAME = false;
        public boolean ROTATE_ALBUM_COVER = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public DisableToastSound DISABLE_TOAST_SOUND = DisableToastSound.MUTE_SELF;
        public List<String> IGNORE_SOUND_EVENT = List.of("minecraft:block.note_block.*");
    }

    public static class JukeboxConfig {
        public int MAX_COUNT_HISTORY = 10;
        @ConfigEntry.Gui.Tooltip
        public boolean DEBUG_MOD = false;
    }
}