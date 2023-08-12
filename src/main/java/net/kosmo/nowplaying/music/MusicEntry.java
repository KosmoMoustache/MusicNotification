package net.kosmo.nowplaying.music;

import com.google.gson.JsonObject;
import net.kosmo.nowplaying.toast.NowPlayingToast;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class MusicEntry {
    public final String key;
    public final Identifier identifier;
    public final @Nullable String title;
    public final @Nullable String author;
    public final @Nullable String soundtrack;
    public final NowPlayingToast.AlbumCover albumCover;

    public MusicEntry(String key, JsonObject json) {
        this(
                key,
                new Identifier(JsonHelper.getString(json, "identifier", "nowplaying:missing")),
                JsonHelper.getString(json, "title"), JsonHelper.getString(json, "author", null),
                JsonHelper.getString(json, "soundtrack", null)
        );
    }

    public MusicEntry(String key, Identifier identifier, @Nullable String title, @Nullable String author, @Nullable String soundtrack) {
        this.key = key;
        this.identifier = identifier;
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = parseAlbumCover();
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

    public String toString() {
        return String.format("title: %s, author: %s, soundtrack: %s", title, author, soundtrack);
    }
}
