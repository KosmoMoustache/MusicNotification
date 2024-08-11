package net.kosmo.music.impl.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.Listeners;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import static net.kosmo.music.impl.ClientMusic.MOD_ID;

public class ListenersFabric {
    public static class ClientResourceListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, "client_resource_listener");
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            if (!ClientMusic.initialized) {
                ClientMusic.onClientInit(manager);
                SoundEventListener soundEventListener = new Listeners.SoundManagerSoundEventListener();
                ClientMusic.soundManager.addListener(soundEventListener);
                ClientMusic.initialized = true;
            }
            ClientMusic.LOGGER.debug("ClientResource: Reloading MusicManager");
            ClientMusic.musicManager.reload();
            ClientMusic.isDarkModeEnabled = manager.listPacks().anyMatch(resourcePack -> resourcePack.packId().equals(ResourceLocation.fromNamespaceAndPath(MOD_ID, "dark_mode").toString()));
        }
    }

    public static class ServerDataResourceListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, "server_data_listener");
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            ClientMusic.LOGGER.debug("ServerData: Reloading MusicManager");
            ClientMusic.musicManager.reload();
        }
    }

    public static class ClientPlayConnectionEventJoin implements ClientPlayConnectionEvents.Join {
        @Override
        public void onPlayReady(ClientPacketListener handler, PacketSender sender, Minecraft client) {
            ClientMusic.LOGGER.debug("ClientPlayConnectionEventJoin: Reloading MusicManager");
            ClientMusic.musicManager.reload();
        }
    }

}
