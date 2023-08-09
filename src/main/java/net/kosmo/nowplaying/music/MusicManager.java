package net.kosmo.nowplaying.music;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kosmo.nowplaying.toast.NowPlayingToast;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MusicManager {
    private Map<String, Entry> music_list = Maps.newHashMap();

    public MusicManager(JsonObject json) {
        this.setEntries(parseJson(json));
    }

    public static Map<String, Entry> parseJson(JsonObject json) {
        Map<String, Entry> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), new Entry(entry.getKey(), (JsonObject) entry.getValue()));
        }
        return map;
    }

    public Map<String, Entry> getEntries() {
        return music_list;
    }

    public void setEntries(Map<String, Entry> map) {
        this.music_list = map;
    }

    public Entry getEntry(String name) {
        return music_list.get(name);
    }


    public static class Entry {
        private final String key;
        private final String title;
        @Nullable
        private final String author;
        @Nullable
        private final String soundtrack;
        @Nullable
        private final NowPlayingToast.AlbumCover albumCover;
        @Nullable
        private final Identifier identifier;

        public Entry(String key, JsonObject json) {
            this.key = key;
            this.title = JsonHelper.getString(json, "title");
            this.author = JsonHelper.getString(json, "author", null);
            this.soundtrack = JsonHelper.getString(json, "soundtrack", null);
            this.albumCover = parseAlbumCover();
            String s = JsonHelper.getString(json, "identifier", null);
            this.identifier = s == null ? null : new Identifier(s);
        }

        private NowPlayingToast.AlbumCover parseAlbumCover() {
            // TODO: Add key for album cover in json
            if (this.soundtrack == null) return NowPlayingToast.AlbumCover.CD;
            if (this.soundtrack.contains("Alpha")) return NowPlayingToast.AlbumCover.ALPHA;
            if (this.soundtrack.contains("Beta")) return NowPlayingToast.AlbumCover.BETA;
            if (this.soundtrack.contains("Axolotl")) return NowPlayingToast.AlbumCover.AXOLOTL;
            if (this.soundtrack.contains("Dragon Fish")) return NowPlayingToast.AlbumCover.DRAGON_FISH;
            if (this.soundtrack.contains("Shuniji")) return NowPlayingToast.AlbumCover.SHUNIJI;
            if (this.soundtrack.contains("Nether")) return NowPlayingToast.AlbumCover.NETHER;
            if (this.soundtrack.contains("Wild")) return NowPlayingToast.AlbumCover.WILD;
            if (this.soundtrack.contains("Caves")) return NowPlayingToast.AlbumCover.CAVES;
            if (this.soundtrack.contains("Trails")) return NowPlayingToast.AlbumCover.TRAILSANDTALES;
            return NowPlayingToast.AlbumCover.CD;
        }

        public String getKey() {
            return key;
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

        public NowPlayingToast.@Nullable AlbumCover getAlbumCover() {
            return albumCover;
        }

        public @Nullable Identifier getIdentifier() {
            return identifier;
        }

        public String toString() {
            return String.format("title: %s, author: %s, soundtrack: %s", title, author, soundtrack);
        }
    }
}
