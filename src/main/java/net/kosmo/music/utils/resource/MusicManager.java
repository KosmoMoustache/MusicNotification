package net.kosmo.music.utils.resource;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.kosmo.music.ClientMusic;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class MusicManager {
    public Map<Identifier, MusicManager.Music> musics;

    public MusicManager() {
        this.musics = Maps.newHashMap();
    }

    public void parseJsonObject(JsonObject json) {
        this.musics.clear();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            try {
                Music m = Music.parseJsonObject(entry.getValue().getAsJsonObject());
                this.musics.put(m.identifier, m);
            } catch (Exception e) {
                ClientMusic.LOGGER.warn("Failed to parse music entry: " + entry.getKey());
            }
        }
    }

    public void clear() {
        this.musics.clear();
    }

    public Music get(Identifier id) {
        return this.musics.get(id);
    }

    public static class Music {
        @Nullable
        public final Identifier identifier;
        public final String title;
        public final String author;
        public final String album;
        public final AlbumCover albumCover;
        public final boolean isRandom;

        public Music(@Nullable Identifier identifier, String title, String author, String album, AlbumCover albumCover, boolean isRandom) {
            this.identifier = identifier;
            this.title = title;
            this.author = author;
            this.album = album;
            this.albumCover = albumCover;
            this.isRandom = isRandom;
        }

        public static Music parseJsonObject(JsonObject json) throws JsonSyntaxException {
            String strId = JsonHelper.getString(json, "identifier", null);
            Identifier identifier = strId != null ? new Identifier(strId) : null;
            String title = JsonHelper.getString(json, "title");
            String author = JsonHelper.getString(json, "author");
            String album = JsonHelper.getString(json, "album", null);
            if (album == null) {
                album = JsonHelper.getString(json, "soundtrack");
                ClientMusic.LOGGER.warn("{} is using the old 'soundtrack' key, please update it use 'album'", title);
            }
            AlbumCover cover = AlbumCover.parseAlbumCover(JsonHelper.getString(json, "cover", null), album);
            boolean isRandom = JsonHelper.getBoolean(json, "isRandom", false);

            return new Music(
                    identifier,
                    title,
                    author,
                    album,
                    cover,
                    isRandom
            );
        }

        public String toString() {
            return String.format("title: %s, author: %s, album: %s, cover: %s, identifier: %s, isRandom: %s", title, author, album, albumCover, identifier, isRandom);
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

        public Identifier getAlbumCover() {
            if (albumCover.textureId != null) return albumCover.textureId;
            if (this.identifier != null && !Objects.equals(this.identifier.getNamespace(), "minecraft")) {
                return AlbumCover.MODDED.textureId;
            }
            return AlbumCover.GENERIC.textureId;
        }
    }
}
