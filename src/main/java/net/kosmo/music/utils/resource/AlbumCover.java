package net.kosmo.music.utils.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kosmo.music.ClientMusic;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AlbumCover {
    public static final AlbumCover GENERIC = new AlbumCover(new ResourceLocation(ClientMusic.MOD_ID, "toast/generic"));
    public static final AlbumCover MODDED = new AlbumCover(new ResourceLocation(ClientMusic.MOD_ID, "toast/modded"));

    public final ResourceLocation textureId;

    public AlbumCover(ResourceLocation texture) {
        this.textureId = texture;
    }

    public static int getWidth() {
        return 20;
    }

    public static int getHeight() {
        return 20;
    }

    public void drawAlbumCover(GuiGraphics context, int x, int y) {
        RenderSystem.enableBlend();
        context.blitSprite(this.textureId, x, y, getWidth(), getHeight());
    }

    public static AlbumCover parseAlbumCover(@Nullable String cover) {
        if (cover != null) return new AlbumCover(new ResourceLocation(ClientMusic.MOD_ID, "toast/" + cover));
        return GENERIC;
    }
}