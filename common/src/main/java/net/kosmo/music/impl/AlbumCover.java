package net.kosmo.music.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AlbumCover {
    public static final AlbumCover GENERIC = new AlbumCover(ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "toast/generic"));
    public static final AlbumCover MODDED = new AlbumCover(ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "toast/modded"));

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

    public static AlbumCover parseAlbumCover(@Nullable String cover) {
        if (cover != null)
            return new AlbumCover(ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "toast/" + cover));
        return GENERIC;
    }

    public static AlbumCover getDefaultCover(ResourceLocation location) {
        return Objects.equals(location.getNamespace().toLowerCase(), "minecraft") ? AlbumCover.GENERIC : AlbumCover.MODDED;
    }

    public void drawAlbumCover(GuiGraphics guiGraphics, int x, int y) {
        RenderSystem.enableBlend();
        guiGraphics.blitSprite(RenderType::guiTextured, this.textureId, x, y, getWidth(), getHeight());
    }
}