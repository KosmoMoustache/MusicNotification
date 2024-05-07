package net.kosmo.music.utils.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kosmo.music.ClientMusic;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AlbumCover {
    public static final AlbumCover GENERIC = new AlbumCover(0, 0);
    public static final AlbumCover MODDED = new AlbumCover(1, 0);
    public static final AlbumCover ALPHA = new AlbumCover(2, 0);
    public static final AlbumCover BETA = new AlbumCover(3, 0);
    public static final AlbumCover AXOLOTL = new AlbumCover(1, 1);
    public static final AlbumCover DRAGON_FISH = new AlbumCover(0, 1);
    public static final AlbumCover SHUNIJI = new AlbumCover(1, 1);
    public static final AlbumCover NETHER = new AlbumCover(2, 1);
    public static final AlbumCover WILD = new AlbumCover(0, 2);
    public static final AlbumCover CAVES = new AlbumCover(1, 2);
    public static final AlbumCover TRAILS_AND_TALES = new AlbumCover(3, 1);

    public final int textureSlotX;
    public final int textureSlotY;

    public AlbumCover(int textureSlotX, int textureSlotY) {
        this.textureSlotX = textureSlotX;
        this.textureSlotY = textureSlotY;
    }

    public static int getWidth() {
        return 20;
    }

    public static int getHeight() {
        return 20;
    }

    public static AlbumCover parseAlbumCover(@Nullable String cover, int textureSlotX, int textureSlotY) {
        if (cover == null) {
            return new AlbumCover(textureSlotX, textureSlotY);
        }
        return switch (cover) {
            case "generic" -> GENERIC;
            case "modded" -> MODDED;
            case "alpha" -> ALPHA;
            case "beta" -> BETA;
            case "axolotl" -> AXOLOTL;
            case "dragon_fish" -> DRAGON_FISH;
            case "shuniji" -> SHUNIJI;
            case "nether" -> NETHER;
            case "wild" -> WILD;
            case "caves" -> CAVES;
            case "trails_and_tales" -> TRAILS_AND_TALES;
            default -> GENERIC;
        };
    }

    public void drawAlbumCover(DrawContext context, int x, int y) {
        RenderSystem.enableBlend();
        context.drawTexture(new Identifier(ClientMusic.MOD_ID, "textures/gui/toasts.png"), x, y, 176 + this.textureSlotX * 20, this.textureSlotY * 20, getWidth(), getHeight());
    }
}