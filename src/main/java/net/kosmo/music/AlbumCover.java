package net.kosmo.music;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AlbumCover {
    public static final AlbumCover GENERIC = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/generic.png"));
    public static final AlbumCover MODDED = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/modded.png"));
    public static final AlbumCover ALPHA = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/alpha.png"));
    public static final AlbumCover BETA = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/beta.png"));
    public static final AlbumCover AXOLOTL = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/axolotl.png"));
    public static final AlbumCover DRAGON_FISH = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/dragon_fish.png"));
    public static final AlbumCover SHUNIJI = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/shuniji.png"));
    public static final AlbumCover NETHER = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/nether.png"));
    public static final AlbumCover WILD = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/wild.png"));
    public static final AlbumCover CAVES = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/caves.png"));
    public static final AlbumCover TRAILS_AND_TALES = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/trails_and_tales.png"));

    public final Identifier textureId;

    public AlbumCover(Identifier texture) {
        this.textureId = texture;
    }

    public void drawAlbumCover(MatrixStack matrices, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, this.textureId);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 20, 20, 20, 20);
    }

    public static AlbumCover parseAlbumCover(String cover, String soundtrack) {
        if (cover != null) return new AlbumCover(new Identifier(ClientMusic.MOD_ID, "textures/gui/sprites/toast/" + cover + ".png"));
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