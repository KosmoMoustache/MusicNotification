package net.kosmo.music;

import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class DataManager {
    private final ResourceManager resourceManager;


    public DataManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * Get the last segment of a Identifier path
     */
    public static String getLastSegmentOfPath(Identifier identifier) {
        String[] path = identifier.getPath().split("/");
        return path[path.length - 1];
    }

    public JsonObject reader(Type type) {
        return reader(type, ClientMusic.MOD_ID);
    }

    public JsonObject reader(Type type, @Nullable String namespace) {
        List<Resource> list = resourceManager.getAllResources(new Identifier(namespace, type.namepath));
        for (Resource resource : list) {
            try (BufferedReader reader = resource.getReader()) {
                return JsonHelper.deserialize(reader);
            } catch (IOException e) {
                ClientMusic.LOGGER.warn("Invalid {} in resourcepack: '{}'", type, resource.getResourcePackName());
            }
        }
        return null;
    }

    public enum Type {
        SOUND("sounds.json"),
        MUSIC("musics.json");

        private final String namepath;
        Type(String namepath) {
            this.namepath = namepath;
        }
    }
}
