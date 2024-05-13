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
    public static final AlbumCover AQUATIC_UPDATE = new AlbumCover(0, 1);
    public static final AlbumCover NETHER_UPDATE = new AlbumCover(1, 1);
    public static final AlbumCover CAVE_AND_CLIFFS_UPDATE = new AlbumCover(2, 1);
    public static final AlbumCover WILD_UPDATE = new AlbumCover(3, 1);
    public static final AlbumCover TRAILS_AND_TALES_UPDATE = new AlbumCover(0, 2);
    public static final AlbumCover TRICK_TRIALS_UPDATE = new AlbumCover(1, 2);
    public static final AlbumCover FIVE_RECORD = new AlbumCover(0, 3);
    public static final AlbumCover ELEVEN_RECORD = new AlbumCover(1, 3);
    public static final AlbumCover THIRTEEN_RECORD = new AlbumCover(2, 3);
    public static final AlbumCover BLOCK_RECORD = new AlbumCover(3, 3);
    public static final AlbumCover CAT_RECORD = new AlbumCover(0, 4);
    public static final AlbumCover CHIRP_RECORD = new AlbumCover(1, 4);
    public static final AlbumCover FAR_RECORD = new AlbumCover(2, 4);
    public static final AlbumCover MALL_RECORD = new AlbumCover(3, 4);
    public static final AlbumCover MELLOHI_RECORD = new AlbumCover(0, 5);
    public static final AlbumCover OTHERSIDE_RECORD = new AlbumCover(1, 5);
    public static final AlbumCover PIGSTEP_RECORD = new AlbumCover(2, 5);
    public static final AlbumCover RELIC_RECORD = new AlbumCover(3, 5);
    public static final AlbumCover STAL_RECORD = new AlbumCover(0, 6);
    public static final AlbumCover STRAD_RECORD = new AlbumCover(1, 6);
    public static final AlbumCover WAIT_RECORD = new AlbumCover(2, 6);
    public static final AlbumCover WARD_RECORD = new AlbumCover(3, 6);

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
            case "aquatic_update_ost" -> AQUATIC_UPDATE;
            case "nether_update_ost" -> NETHER_UPDATE;
            case "cave_and_cliffs_update_ost" -> CAVE_AND_CLIFFS_UPDATE;
            case "wild_update_ost" -> WILD_UPDATE;
            case "trails_and_tales_ost" -> TRAILS_AND_TALES_UPDATE;
            case "tricky_trials_ost" -> TRICK_TRIALS_UPDATE;
            case "5" -> FIVE_RECORD;
            case "13" -> THIRTEEN_RECORD;
            case "11" -> ELEVEN_RECORD;
            case "blocks" -> BLOCK_RECORD;
            case "cat" -> CAT_RECORD;
            case "chirp" -> CHIRP_RECORD;
            case "far" -> FAR_RECORD;
            case "mall" -> MALL_RECORD;
            case "mellohi" -> MELLOHI_RECORD;
            case "otherside" -> OTHERSIDE_RECORD;
            case "pigstep" -> PIGSTEP_RECORD;
            case "relic" -> RELIC_RECORD;
            case "stal" -> STAL_RECORD;
            case "strad" -> STRAD_RECORD;
            case "wait" -> WAIT_RECORD;
            case "ward" -> WARD_RECORD;
            default -> GENERIC;
        };
    }

    public void drawAlbumCover(DrawContext context, int x, int y) {
        RenderSystem.enableBlend();
        context.drawTexture(new Identifier(ClientMusic.MOD_ID, "textures/gui/toasts.png"), x, y, 176 + this.textureSlotX * 20, this.textureSlotY * 20, getWidth(), getHeight());
    }
}