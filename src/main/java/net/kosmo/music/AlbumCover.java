package net.kosmo.music;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

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
    public static final AlbumCover TRAILS_AND_TALES = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "tost/trails_and_tales"));

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

    public static AlbumCover parseAlbumCover(String cover, String soundtrack) {
        if (cover != null) return new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/" + cover));
        // Try to guess the album cover based on the soundtrack name
        if (soundtrack == null) return GENERIC;
        if (soundtrack.contains("Alpha")) return ALPHA;
        if (soundtrack.contains("Beta")) return BETA;
        if (soundtrack.contains("Axolotl")) return AXOLOTL;
        if (soundtrack.contains("Dragon Fish")) return DRAGON_FISH;
        if (soundtrack.contains("Shuniji")) return SHUNIJI;
        if (soundtrack.contains("Nether")) return NETHER;
        if (soundtrack.contains("Wild")) return WILD;
        if (soundtrack.contains("Caves")) return CAVES;
        if (soundtrack.contains("Trails")) return TRAILS_AND_TALES;
        return GENERIC;
    }
}