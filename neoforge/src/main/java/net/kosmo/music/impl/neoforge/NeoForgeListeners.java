package net.kosmo.music.impl.neoforge;

import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.Listeners;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.server.packs.resources.ResourceManager;

public class NeoForgeListeners {
    public static void ClientResourceListener(ResourceManager manager) {
        if (!ClientMusic.initialized) {
            ClientMusic.onClientInit(manager);
            SoundEventListener soundEventListener = new Listeners.SoundManagerSoundEventListener();
            ClientMusic.soundManager.addListener(soundEventListener);
            ClientMusic.initialized = true;
        }
        ClientMusic.LOGGER.debug("ClientResource: Reloading MusicManager");
        ClientMusic.musicManager.reload();
        ClientMusic.isDarkModeEnabled = false /*manager.listPacks().anyMatch(resourcePack -> resourcePack.packId().equals(ResourceLocation.fromNamespaceAndPath(MOD_ID, "dark_mode").toString()))*/;
    }

    public static void ServerDataResourceListener(ResourceManager manager) {
            ClientMusic.LOGGER.debug("ServerData: Reloading MusicManager");
            ClientMusic.musicManager.reload();
    }
}

