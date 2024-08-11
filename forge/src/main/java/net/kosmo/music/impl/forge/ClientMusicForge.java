package net.kosmo.music.impl.forge;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.forge.compat.AutoConfigForge;
import net.kosmo.music.impl.forge.compat.ConfigHolderForge;
import net.kosmo.music.impl.forge.compat.ConfigMenuForge;
import net.kosmo.music.impl.gui.JukeboxScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod(ClientMusic.MOD_ID)
public class ClientMusicForge {
    private static final KeyMapping OPEN_SCREEN_KEYMAP = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, "key.musicnotification.categories");
    private boolean altasInit = false;

    public ClientMusicForge(IEventBus modEventBus) {
        AutoConfig.register(AutoConfigForge.class, GsonConfigSerializer::new);
        ConfigMenuForge.registerModsPage();

        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::onTextureStitchEvent);
        modEventBus.addListener(this::addBuiltinPacks);

        MinecraftForge.EVENT_BUS.addListener(this::addReloadListener);
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
                    configData.TOAST_CONFIG.IGNORE_SOUND_EVENT,
                    configData.JUKEBOX_CONFIG.MAX_COUNT_HISTORY,
                    configData.JUKEBOX_CONFIG.DEBUG_MOD
            );
            return InteractionResult.SUCCESS;
        });
    }

    public static void onMinecraftClientMixin() {
        ClientMusic.init(OPEN_SCREEN_KEYMAP, new ForgeModLoader(), ConfigHolderForge.init());
    }

    void addReloadListener(AddReloadListenerEvent event) {
        ForgeListeners.ClientResourceListener(Minecraft.getInstance().getResourceManager());
        ForgeListeners.ServerDataResourceListener(Minecraft.getInstance().getResourceManager());
    }

    void addBuiltinPacks(AddPackFindersEvent event) {
//        event.addRepositorySource(
//                ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "resourcepacks/dark_mode"),
//                PackType.CLIENT_RESOURCES,
//                Component.literal("Dark Mode"),
//                PackSource.BUILT_IN,
//                false,
//                Pack.Position.TOP
//        );
    }

    void onTextureStitchEvent(TextureStitchEvent event) {
        if (!altasInit) {
            ForgeListeners.ClientResourceListener(Minecraft.getInstance().getResourceManager());
            ForgeListeners.ServerDataResourceListener(Minecraft.getInstance().getResourceManager());
            altasInit = true;
        }
    }

    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SCREEN_KEYMAP);
    }
}
