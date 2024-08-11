package net.kosmo.music.impl.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.gui.JukeboxScreen;
import net.kosmo.music.impl.neoforge.compat.AutoConfigNeoForge;
import net.kosmo.music.impl.neoforge.compat.ConfigHolderNeoForge;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.lwjgl.glfw.GLFW;

@Mod(ClientMusic.MOD_ID)
public class ClientMusicNeoForge {
    private static final KeyMapping OPEN_SCREEN_KEYMAP = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, "key.musicnotification.categories");

    public ClientMusicNeoForge(IEventBus modEventBus) {
        // Register Mod List config screen
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> AutoConfig.getConfigScreen(AutoConfigNeoForge.class, parent).get());

        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::addBuiltinPacks);
        modEventBus.addListener(this::addReloadListener);

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, (event) -> {
            Minecraft client = Minecraft.getInstance();
            if (OPEN_SCREEN_KEYMAP.consumeClick()) {
                client.setScreen(new JukeboxScreen(client.screen));
            }
        });
    }

    public static void onMinecraftClientMixin() {
        ClientMusic.init(OPEN_SCREEN_KEYMAP, new NeoForgeModLoader(), ConfigHolderNeoForge.init());
    }

    void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SimplePreparableReloadListener<>() {
            @Override
            protected Object prepare(ResourceManager arg, ProfilerFiller arg2) {
                return null;
            }

            @Override
            protected void apply(Object pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                NeoForgeListeners.ClientResourceListener(Minecraft.getInstance().getResourceManager());
                NeoForgeListeners.ServerDataResourceListener(Minecraft.getInstance().getResourceManager());
            }
        });
    }

    void addBuiltinPacks(AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "resourcepacks/dark_mode"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Dark Mode"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }

    void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SCREEN_KEYMAP);
    }
}
