package net.kosmo.music.utils.resource;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonParseException;
import net.kosmo.music.ClientMusic;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MusicManager {
    private final ResourceManager resourceManager;
    public Map<ResourceLocation, MusicManager.Music> musics;

    public MusicManager(ResourceManager resourceManager) {
        this.musics = Maps.newHashMap();
        this.resourceManager = resourceManager;
    }

    public void reload() {
        musics.clear();

        List<Resource> resources = resourceManager.getResourceStack(ClientMusic.MUSICS_JSON_ID);
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
                ClientMusic.LOGGER.error("Error when reading {} in resourcepack: '{}'\nMessage: {}", ClientMusic.MUSICS_JSON_ID, resource.sourcePackId(), e.getMessage());
            }
        }
    }

    public @Nullable Music get(ResourceLocation id) {
        Music m = this.musics.get(id);
        // When a disc is played using playsound command identifier is <namespace>:records/<disc_name>
        if (m == null) {
            m = this.musics.get(new ResourceLocation(id.getNamespace(), id.getPath().replace("records/", "music_disc.")));
        }
        return m;
    }

    public static class Music {
        public final ResourceLocation identifier;
        @Nullable
        public final ResourceLocation customId;
        public final String title;
        public final String author;
        @Nullable
        public final String album;
        public final AlbumCover albumCover;
        public final boolean isRandom;

        public Music(ResourceLocation identifier, @Nullable ResourceLocation customId, String title, String author, @Nullable String album, AlbumCover albumCover, boolean isRandom) {
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
            ResourceLocation identifier = new ResourceLocation(json.getKey());
            String rawCustomId = GsonHelper.getAsString(jsonObject, "customId", null);
            ResourceLocation customId = rawCustomId == null ? null : new ResourceLocation(rawCustomId);
            String title = GsonHelper.getAsString(jsonObject, "title");
            String author = GsonHelper.getAsString(jsonObject, "author");
            String album = GsonHelper.getAsString(jsonObject, "album", null);
            if (album == null) {
                album = GsonHelper.getAsString(jsonObject, "soundtrack");
                ClientMusic.LOGGER.error("Key 'soundtrack' of '{}' is deprecated, use 'album' instead", title);
            }
            AlbumCover cover = AlbumCover.parseAlbumCover(GsonHelper.getAsString(jsonObject, "cover", null));
            boolean isRandom = GsonHelper.getAsBoolean(jsonObject, "isRandom", false);

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
            return String.format("title: %s, author: %s, album: %s, cover: %s, identifier: %s, customId: %s, isRandom: %s", title, author, album, albumCover.textureId, identifier, customId, isRandom);
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

        public ResourceLocation getAlbumCoverTextureId() {
            if (albumCover.textureId != null) return albumCover.textureId;
            if (this.identifier != null && !Objects.equals(this.identifier.getNamespace(), "minecraft")) {
                return AlbumCover.MODDED.textureId;
            }
            return AlbumCover.GENERIC.textureId;
        }

        public @Nullable SoundEvent getSoundEvent(SoundManager soundManager) {
            ResourceLocation id = this.customId == null ? this.identifier : this.customId;
            if (soundManager.getSoundEvent(id) != null) {
                return SoundEvent.createVariableRangeEvent(id);
            } else {
                return null;
            }
        }
    }

    public static class Sound {
        public final ResourceLocation identifier;

        public Sound(ResourceLocation identifier) {
            this.identifier = identifier;
        }

        public @Nullable SoundEvent getSoundEvent(SoundManager soundManager) {
            if (soundManager.getSoundEvent(this.identifier) != null) {
                return SoundEvent.createVariableRangeEvent(identifier);
            } else {
                return null;
            }
        }
    }
}
