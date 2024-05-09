package net.kosmo.music.utils.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kosmo.music.ClientMusic;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AlbumCover {
    public static final AlbumCover GENERIC = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/generic"));
    public static final AlbumCover MODDED = new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/modded"));

    public final Identifier textureId;

    public AlbumCover(Identifier texture) {
        this.textureId = texture;
    }

    public static int getWidth() {
        return 20;
    }

    public static int getHeight() {
        return 20;
    }

    public void drawAlbumCover(DrawContext context, int x, int y) {
        RenderSystem.enableBlend();
        context.drawGuiTexture(this.textureId, x, y, getWidth(), getHeight());
    }

    public static AlbumCover parseAlbumCover(@Nullable String cover) {
        if (cover != null) return new AlbumCover(new Identifier(ClientMusic.MOD_ID, "toast/" + cover));
        return GENERIC;
    }
}