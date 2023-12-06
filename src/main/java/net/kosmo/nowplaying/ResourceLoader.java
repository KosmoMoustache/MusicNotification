package net.kosmo.nowplaying;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
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
        Optional<Resource> resource1 = manager.getResource(new Identifier(MOD_ID, "music_list.json"));
        Resource resource = resource1.orElse(null);

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
            NowPlaying.musicManager.update(ResourceLoader.loader(manager));
        }
    }
}
