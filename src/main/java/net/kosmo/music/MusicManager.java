package net.kosmo.music;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class MusicManager {
    public Map<Identifier, Entry> music = Maps.newHashMap();

    public MusicManager() {    }

    public void clear() {
        music.clear();
    }

    public void addFromJson(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Identifier id = new Identifier(entry.getKey());
            music.put(id, new Entry(entry.getValue().getAsJsonObject()));
        }
    }

    public Entry get(Identifier name) {
        return music.get(name);
    }

    public static class Entry {
        @Nullable
        public final String title;
        @Nullable
        public final String author;
        @Nullable
        public final String soundtrack;
        public final AlbumCover albumCover;
        // wip gui related
        @Nullable
        public final Identifier identifier;
        public final boolean isRandom;

        public Entry(JsonObject json) {
            this.title = JsonHelper.getString(json, "title", null);
            this.author = JsonHelper.getString(json, "author", null);
            this.soundtrack = JsonHelper.getString(json, "soundtrack", null);
            this.albumCover = AlbumCover.parseAlbumCover(JsonHelper.getString(json, "cover", null), this.soundtrack);
            String strId = JsonHelper.getString(json, "identifier", null);
            this.identifier = (strId != null) ? new Identifier(strId) : null;
            this.isRandom = JsonHelper.getBoolean(json, "isRandom", false);
        }

        public String toString() {
            return String.format("title: %s, author: %s, soundtrack: %s, cover: %s, identifier: %s, isRandom: %s", title, author, soundtrack, albumCover, identifier, isRandom);
        }

        public String getTitle() {
            return title == null ? "Unknown" : title;
        }

        public String getAuthor() {
            return author == null ? "Unknown" : author;
        }

        public String getSoundtrack() {
            return soundtrack == null ? "Unknown" : soundtrack;
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



