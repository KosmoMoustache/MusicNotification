package net.kosmo.music.impl.forge;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.forge.compat.AutoConfigForge;
import net.kosmo.music.impl.forge.compat.ConfigMenuForge;
import net.kosmo.music.impl.gui.JukeboxScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod(ClientMusic.MOD_ID)
public class ClientMusicForge {
    private static final KeyMapping OPEN_SCREEN_KEYMAP = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, "key.musicnotification.categories");

    public ClientMusicForge(IEventBus modEventBus) {
        AutoConfig.register(AutoConfigForge.class, GsonConfigSerializer::new);
        ConfigMenuForge.registerModsPage();

        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::onLoadComplete);
        modEventBus.addListener(this::onTextureAtlas);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, (event) -> {
            Minecraft client = Minecraft.getInstance();
            if (OPEN_SCREEN_KEYMAP.consumeClick()) {
                client.setScreen(new JukeboxScreen(client.screen));
            }
        });

        AutoConfig.getConfigHolder(AutoConfigForge.class).registerSaveListener((manager, configData) -> {
            ClientMusic.LOGGER.info("Loaded config: " + configData);
            ClientMusic.config.set(
                    configData.SHOW_TITLE_SCREEN_BUTTON,
                    configData.TOAST_CONFIG.SHOW_AUTHOR,
                    configData.TOAST_CONFIG.SHOW_ALBUM_NAME,
                    configData.TOAST_CONFIG.ROTATE_ALBUM_COVER,
                    configData.TOAST_CONFIG.DISABLE_TOAST_SOUND,
                    configData.JUKEBOX_CONFIG.MAX_COUNT_HISTORY,
                    configData.JUKEBOX_CONFIG.DEBUG_MOD
            );
            return InteractionResult.SUCCESS;
        });
    }

    public void onLoadComplete(FMLClientSetupEvent event) {

    }

    public void onTextureAtlas(RegisterTextureAtlasSpriteLoadersEvent event) {
        ClientMusic.init(OPEN_SCREEN_KEYMAP, new ForgeModLoader());

        ForgeListeners.ClientResourceListener(Minecraft.getInstance().getResourceManager());
        ForgeListeners.ServerDataResourceListener(Minecraft.getInstance().getResourceManager());

        AutoConfigForge config = AutoConfig.getConfigHolder(AutoConfigForge.class).getConfig();
        ClientMusic.config.set(
                config.SHOW_TITLE_SCREEN_BUTTON,
                config.TOAST_CONFIG.SHOW_AUTHOR,
                config.TOAST_CONFIG.SHOW_ALBUM_NAME,
                config.TOAST_CONFIG.ROTATE_ALBUM_COVER,
                config.TOAST_CONFIG.DISABLE_TOAST_SOUND,
                config.JUKEBOX_CONFIG.MAX_COUNT_HISTORY,
                config.JUKEBOX_CONFIG.DEBUG_MOD
        );
    }

    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SCREEN_KEYMAP);
    }
}
