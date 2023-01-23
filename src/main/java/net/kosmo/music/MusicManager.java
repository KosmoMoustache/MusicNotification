package net.kosmo.music;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kosmo.music.toast.MusicToast;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MusicManager {
    public Map<String, Entry> music = Maps.newHashMap();

    public MusicManager(JsonObject json) {
        if (json != null) {
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                music.put(entry.getKey(), new Entry((JsonObject) entry.getValue()));
            }
        }
    }

    public Map<String, Entry> getEntries() {
        return music;
    }

    public Entry getEntry(String name) {
        return music.get(name);
    }

    public static class Entry {
        private final String title;
        @Nullable
        private final String author;
        @Nullable
        private final String soundtrack;
        @Nullable
        private final MusicToast.AlbumCover albumCover;

        public Entry(JsonObject json) {
            this.title = JsonHelper.getString(json, "title");
            this.author = JsonHelper.getString(json, "author", null);
            this.soundtrack = JsonHelper.getString(json, "soundtrack", null);
            this.albumCover = parseAlbumCover();
        }

        private MusicToast.AlbumCover parseAlbumCover() {
            if (this.soundtrack.contains("Alpha")) return MusicToast.AlbumCover.ALPHA;
            if (this.soundtrack.contains("Beta")) return MusicToast.AlbumCover.BETA;
            if (this.soundtrack.contains("Axolotl")) return MusicToast.AlbumCover.AXOLOTL;
            if (this.soundtrack.contains("Dragon Fish")) return MusicToast.AlbumCover.DRAGON_FISH;
            if (this.soundtrack.contains("Shuniji")) return MusicToast.AlbumCover.SHUNIJI;
            if (this.soundtrack.contains("Nether"))  return MusicToast.AlbumCover.NETHER;
            if (this.soundtrack.contains("Wild")) return MusicToast.AlbumCover.WILD;
            if (this.soundtrack.contains("Caves")) return MusicToast.AlbumCover.CAVES;
            return MusicToast.AlbumCover.CD;
        }

        public String getTitle() {
            return title;
        }

        public @Nullable String getAuthor() {
            return author;
        }

        public @Nullable String getSoundtrack() {
            return soundtrack;
        }

        public MusicToast.AlbumCover getAlbumCover() {
            return albumCover;
        }

        public String toString() {
            return String.format("title: %s, author: %s, soundtrack: %s", title, author, soundtrack);
        }
    }
}



