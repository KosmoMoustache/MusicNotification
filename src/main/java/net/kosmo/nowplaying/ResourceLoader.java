package net.kosmo.nowplaying;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.kosmo.nowplaying.music.MusicManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

import static net.kosmo.nowplaying.NowPlaying.MOD_ID;

public class ResourceLoader {

    /**
     * Load music_list.json from resource pack
     */
    public static JsonObject loader(ResourceManager manager) {
        Optional<Resource> resource1 = manager.getResource(new Identifier(MOD_ID, "musics.json"));
        Optional<Resource> resource2 = manager.getResource(new Identifier(MOD_ID, "music_list.json"));
        Resource resource = resource2.orElse(null);

          if (resource1.isPresent() && resource2.isPresent()) {
            NowPlaying.LOGGER.warn("Both musics.json and music_list.json are present in resourcepack: '{}', loading music_list.json", resource1.get().getResourcePackName());
        } else if (resource1.isPresent()) {
            resource = resource1.get();
            NowPlaying.LOGGER.warn("musics.json is deprecated, please rename it to music_list.json in resourcepack: '{}'", resource1.get().getResourcePackName());
        }

        if (resource != null) {
            try (BufferedReader reader = resource.getReader()) {
                return JsonHelper.deserialize(reader);
            } catch (IOException e) {
                NowPlaying.LOGGER.error("Invalid music_list.json in resourcepack: '{}'", resource.getResourcePackName());
            }
        }
        return new JsonObject();
    }

    public static class ResourceReloadListener implements SimpleSynchronousResourceReloadListener {

        @Override
        public Identifier getFabricId() {
            return new Identifier(MOD_ID, "music_list.json");
        }

        @Override
        public void reload(ResourceManager manager) {
           NowPlaying.musicManager.setEntries(MusicManager.parseJson(ResourceLoader.loader(manager)));
        }
    }
}
