package net.kosmo.music.utils.resource;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.kosmo.music.ClientMusic;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MusicManager {
    private final ResourceManager resourceManager;
    public Map<Identifier, MusicManager.Music> musics;

    public MusicManager(ResourceManager resourceManager) {
        this.musics = Maps.newHashMap();
        this.resourceManager = resourceManager;
    }

    public void reload() {
        musics.clear();

        List<Resource> resources = resourceManager.getAllResources(ClientMusic.MUSICS_JSON_ID);
        for (Resource resource : resources) {
            try {
                for (Map.Entry<String, JsonElement> entry : ClientMusic.parseJSONResource(resource).entrySet()) {
                    try {
                        Music m = Music.parseJsonObject(entry);
                        this.musics.put(m.identifier, m);
                    } catch (Exception e) {
                        ClientMusic.LOGGER.error("Failed to parse music entry: {}\nMessage: {}", entry.getKey(), e.getMessage());
                    }
                }
            } catch (IOException | JsonParseException e) {
                ClientMusic.LOGGER.error("Error when reading {} in resourcepack: '{}'\nMessage: {}", ClientMusic.MUSICS_JSON_ID, resource.getResourcePackName(), e.getMessage());
            }
        }
    }

    public @Nullable Music get(Identifier id) {
        Music m = this.musics.get(id);
        // When a disc is played using playsound command identifier is <namespace>:records/<disc_name>
        if (m == null) {
            m = this.musics.get(new Identifier(id.getNamespace(), id.getPath().replace("records/", "music_disc.")));
        }
        return m;
    }

    public static class Music {
        public final Identifier identifier;
        @Nullable
        public final Identifier customId;
        public final String title;
        public final String author;
        @Nullable
        public final String album;
        public final AlbumCover albumCover;
        public final boolean isRandom;

        public Music(Identifier identifier, @Nullable Identifier customId, String title, String author, @Nullable String album, AlbumCover albumCover, boolean isRandom) {
            this.identifier = identifier;
            this.customId = customId;
            this.title = title;
            this.author = author;
            this.album = album;
            this.albumCover = albumCover;
            this.isRandom = isRandom;
        }

        public static Music parseJsonObject(Map.Entry<String, JsonElement> json) throws JsonSyntaxException {
            JsonObject jsonObject = json.getValue().getAsJsonObject();
            Identifier identifier = new Identifier(json.getKey());
            String rawCustomId = JsonHelper.getString(jsonObject, "customId", null);
            Identifier customId = rawCustomId == null ? null : new Identifier(rawCustomId);
            String title = JsonHelper.getString(jsonObject, "title");
            String author = JsonHelper.getString(jsonObject, "author");
            String album = JsonHelper.getString(jsonObject, "album", null);
            if (album == null) {
                album = JsonHelper.getString(jsonObject, "soundtrack");
                ClientMusic.LOGGER.error("Key 'soundtrack' of '{}' is deprecated, use 'album' instead", title);
            }

            int coverTextureSlotX = JsonHelper.getInt(jsonObject, "coverTextureSlotX", 0);
            int coverTextureSlotY = JsonHelper.getInt(jsonObject, "coverTextureSlotY", 0);
            AlbumCover cover = AlbumCover.parseAlbumCover(JsonHelper.getString(jsonObject, "cover", null), coverTextureSlotX, coverTextureSlotY);

            boolean isRandom = JsonHelper.getBoolean(jsonObject, "isRandom", false);

            return new Music(
                    identifier,
                    customId,
                    title,
                    author,
                    album,
                    cover,
                    isRandom
            );
        }

        public String toString() {
            return String.format("title: %s, author: %s, albumX: %s, albumY: %s, cover: %s, identifier: %s, customId: %s, isRandom: %s", title, author, album, albumCover.textureSlotX, albumCover.textureSlotY, identifier, customId, isRandom);
        }

        public String getTitle() {
            return title == null ? "Unknown" : title;
        }

        public String getAuthor() {
            return author == null ? "Unknown" : author;
        }

        public String getAlbumName() {
            return album == null ? "Unknown" : album;
        }

        public @Nullable SoundEvent getSoundEvent(SoundManager soundManager) {
            Identifier id = this.customId == null ? this.identifier : this.customId;
            if (soundManager.get(id) != null) {
                return SoundEvent.of(id);
            } else {
                return null;
            }
        }
    }

    public static class Sound {
        public final Identifier identifier;

        public Sound(Identifier identifier) {
            this.identifier = identifier;
        }

        public @Nullable SoundEvent getSoundEvent(SoundManager soundManager) {
            if (soundManager.get(this.identifier) != null) {
                return SoundEvent.of(identifier);
            } else {
                return null;
            }
        }
    }
}
