package net.kosmo.music.utils.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kosmo.music.ClientMusic;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AlbumCover {
    public static final AlbumCover GENERIC = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/generic"));
    public static final AlbumCover MODDED = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/modded"));
    public static final AlbumCover ALPHA = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/alpha"));
    public static final AlbumCover BETA = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/beta"));
    public static final AlbumCover AXOLOTL = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/axolotl"));
    public static final AlbumCover DRAGON_FISH = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/dragon_fish"));
    public static final AlbumCover SHUNIJI = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/shuniji"));
    public static final AlbumCover NETHER = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/nether"));
    public static final AlbumCover WILD = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/wild"));
    public static final AlbumCover CAVES = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/caves"));
    public static final AlbumCover TRAILS_AND_TALES = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/trails_and_tales"));

    public final Identifier textureId;

    public AlbumCover(Identifier texture) {
        this.textureId = texture;
    }

    public static int getWidth() {
        return 32;
    }

    public static int getHeight() {
        return 32;
    }

    public void drawAlbumCover(DrawContext context, int x, int y) {
        RenderSystem.enableBlend();
        context.drawGuiTexture(this.textureId, x, y, 20, 20);
    }

    public static AlbumCover parseAlbumCover(@Nullable String cover, String album) {
        if (cover != null) return new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/" + cover));
        // Try to guess the album cover based on the album name
        if (album == null) return GENERIC;
        if (album.contains("Alpha")) return ALPHA;
        if (album.contains("Beta")) return BETA;
        if (album.contains("Axolotl")) return AXOLOTL;
        if (album.contains("Dragon Fish")) return DRAGON_FISH;
        if (album.contains("Shuniji")) return SHUNIJI;
        if (album.contains("Nether")) return NETHER;
        if (album.contains("Wild")) return WILD;
        if (album.contains("Caves")) return CAVES;
        if (album.contains("Trails")) return TRAILS_AND_TALES;
        return GENERIC;
    }
}