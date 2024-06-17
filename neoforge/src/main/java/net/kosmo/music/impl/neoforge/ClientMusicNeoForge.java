package net.kosmo.music.impl.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.gui.JukeboxScreen;
import net.kosmo.music.impl.neoforge.compat.AutoConfigNeoForge;
import net.kosmo.music.impl.neoforge.compat.ConfigHolderNeoForge;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(ClientMusic.MOD_ID)
public class ClientMusicNeoForge {
    private static final KeyMapping OPEN_SCREEN_KEYMAP = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, "key.musicnotification.categories");

    public ClientMusicNeoForge(IEventBus modEventBus) {
        // Register Mod List config screen
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> AutoConfig.getConfigScreen(AutoConfigNeoForge.class, parent).get());

        modEventBus.addListener(this::registerKeyMappings);
//        modEventBus.addListener(this::onLoadComplete);
        modEventBus.addListener(this::onTextureAtlas);

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, (unused) -> {
            Minecraft client = Minecraft.getInstance();
            if (OPEN_SCREEN_KEYMAP.consumeClick()) {
                client.setScreen(new JukeboxScreen(client.screen));
            }
        });
    }

    public static void onMinecraftClientMixin() {
        ClientMusic.init(OPEN_SCREEN_KEYMAP, new NeoForgeModLoader(), ConfigHolderNeoForge.init());
    }

//    public void onLoadComplete(FMLClientSetupEvent event) {
//
//    }

    public void onTextureAtlas(TextureAtlasStitchedEvent event) {
        NeoForgeListeners.ClientResourceListener(Minecraft.getInstance().getResourceManager());
        NeoForgeListeners.ServerDataResourceListener(Minecraft.getInstance().getResourceManager());
    }

    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SCREEN_KEYMAP);
    }
}
