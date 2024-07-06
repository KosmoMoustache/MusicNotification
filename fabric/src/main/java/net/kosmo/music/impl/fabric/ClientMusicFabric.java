package net.kosmo.music.impl.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.fabric.compat.ConfigHolderFabric;
import net.kosmo.music.impl.gui.JukeboxScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.lwjgl.glfw.GLFW;

import static net.kosmo.music.impl.ClientMusic.MOD_ID;

@Environment(EnvType.CLIENT)
public class ClientMusicFabric implements ClientModInitializer {
    public static KeyMapping keyMapping;

    public static void onMinecraftClientMixin() {
        ClientMusic.init(keyMapping, new ModLoaderFabric(), ConfigHolderFabric.init());
    }

    @Override
    public void onInitializeClient() {
        keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.musicnotification.open_screen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.musicnotification.categories"));

        // Register built-in resource pack
        FabricLoader.getInstance().getModContainer(MOD_ID)
                .ifPresent(container -> {
                    ResourceManagerHelper.registerBuiltinResourcePack(
                            ResourceLocation.fromNamespaceAndPath(MOD_ID, "dark_mode"),
                            container,
                            Component.translatable("text.musicnotification.resourcepack.dark_mode.name"),
                            ResourcePackActivationType.NORMAL);
                });

        // Event Listeners
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ListenersFabric.ClientResourceListener());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ListenersFabric.ServerDataResourceListener());
        ClientPlayConnectionEvents.JOIN.register(new ListenersFabric.ClientPlayConnectionEventJoin());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ClientMusic.keyBinding.consumeClick()) {
                client.setScreen(new JukeboxScreen(client.screen));
            }
        });
    }
}