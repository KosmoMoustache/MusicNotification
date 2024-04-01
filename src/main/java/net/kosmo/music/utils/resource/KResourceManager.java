package net.kosmo.music.utils.resource;

import com.google.gson.JsonObject;
import net.kosmo.music.ClientMusic;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;


public class KResourceManager {
    public final SoundManager soundManager;
    public final MusicManager musicManager;
    private final ResourceManager resourceManager;

    public KResourceManager(ResourceManager resourceManager) {
        this.soundManager = new SoundManager();
        this.musicManager = new MusicManager();
        this.resourceManager  = resourceManager;
    }

    public void reload() {
        soundManager.clear();
        musicManager.clear();

        List<Resource> resources = resourceManager.getAllResources(ClientMusic.MUSICS_JSON_ID);
        for (Resource resource : resources) {
            try {
                musicManager.parseJsonObject(KResourceManager.parseJSONResource(resource));
            } catch (IOException e) {
                ClientMusic.LOGGER.warn("Invalid {} in resourcepack: '{}'", ClientMusic.MUSICS_JSON_ID, resource.getResourcePackName());
            }
        }
    }

    /**
     * Parse a JSON resource
     */
    public static JsonObject parseJSONResource(Resource resource) throws IOException {
        BufferedReader reader = resource.getReader();
        return JsonHelper.deserialize(reader);
    }
}
