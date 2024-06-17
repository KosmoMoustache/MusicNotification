package net.kosmo.music.impl.neoforge.compat;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.kosmo.music.impl.config.ConfigHolder;
import net.minecraft.world.InteractionResult;

public class ConfigHolderNeoForge extends ConfigHolder {

    public ConfigHolderNeoForge(AutoConfigNeoForge config) {
        super(config.SHOW_TITLE_SCREEN_BUTTON,
                config.TOAST_CONFIG.SHOW_AUTHOR,
                config.TOAST_CONFIG.SHOW_ALBUM_NAME,
                config.TOAST_CONFIG.ROTATE_ALBUM_COVER,
                config.TOAST_CONFIG.DISABLE_TOAST_SOUND,
                config.JUKEBOX_CONFIG.MAX_COUNT_HISTORY,
                config.JUKEBOX_CONFIG.DEBUG_MOD
        );
    }

    public static ConfigHolderNeoForge init() {
        AutoConfig.register(AutoConfigNeoForge.class, GsonConfigSerializer::new);
        ConfigHolderNeoForge config = new ConfigHolderNeoForge(AutoConfig.getConfigHolder(AutoConfigNeoForge.class).getConfig());

        AutoConfig.getConfigHolder(AutoConfigNeoForge.class).registerSaveListener((manager, configData) -> {
            config.set(
                    configData.SHOW_TITLE_SCREEN_BUTTON,
                    configData.TOAST_CONFIG.SHOW_AUTHOR,
                    configData.TOAST_CONFIG.SHOW_ALBUM_NAME,
                    configData.TOAST_CONFIG.ROTATE_ALBUM_COVER,
                    configData.TOAST_CONFIG.DISABLE_TOAST_SOUND,
                    configData.JUKEBOX_CONFIG.MAX_COUNT_HISTORY,
                    configData.JUKEBOX_CONFIG.DEBUG_MOD);
            return InteractionResult.SUCCESS;
        });

        return config;
    }
}
