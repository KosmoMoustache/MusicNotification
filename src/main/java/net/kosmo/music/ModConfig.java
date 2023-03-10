package net.kosmo.music;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = ClientMusic.MOD_ID)
public class ModConfig implements ConfigData {
    public boolean hideAuthor = false;
    public boolean showSoundtrackName = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public disableToastSoundState disableToastSound = disableToastSoundState.DISABLE_MUSIC;
    public long displayDuration = 5000;
    @ConfigEntry.Gui.Tooltip
    public boolean enableBetaGui = false;

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
    }

    public enum disableToastSoundState {
        ENABLED_ALL,
        DISABLE_MUSIC,
        DISABLE_ALL,
    }
}