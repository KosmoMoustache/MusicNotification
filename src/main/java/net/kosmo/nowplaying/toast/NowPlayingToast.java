package net.kosmo.nowplaying.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.NowPlayingConfig;
import net.kosmo.nowplaying.music.MusicEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import java.util.Optional;

import static net.kosmo.nowplaying.NowPlaying.LOGGER;

public class NowPlayingToast implements Toast {
    public static final Identifier TEXTURE_NORMAL = new Identifier(NowPlaying.MOD_ID, "toast/normal");
    public static final Identifier TEXTURE_EXTENDED = new Identifier(NowPlaying.MOD_ID, "toast/extended");
    private AlbumCover albumCover;
    private final Type type;
    private long startTime;
    private boolean justUpdated;
    private Text title;
    private Text author;
    private Text soundtrack;
    private int rotation;

    public NowPlayingToast(AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        LOGGER.info("Now playing: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = albumCover;
        this.type = Type.DEFAULT;
    }

    public static void show(SoundInstance soundInstance) {
        String key = NowPlaying.getLastSegmentOfPath(soundInstance.getSound().getIdentifier());
        Optional<MusicEntry> entry = NowPlaying.musicManager.getByKey(key);

        if (entry.isPresent()) {
            show(MinecraftClient.getInstance().getToastManager(), entry.get());
        } else {
            show(MinecraftClient.getInstance().getToastManager(), Text.literal(key), Text.literal(soundInstance.getSound().getIdentifier().getNamespace()), Text.literal(""), AlbumCover.VANILLA);
        }
    }

    public static void show(ToastManager manager, MusicEntry entry) {
        show(manager, Text.literal(entry.title), Text.literal(entry.author), Text.literal(entry.soundtrack), entry.albumCover);
    }

    public static void show(ToastManager manager, Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        NowPlayingToast musicToast = manager.getToast(NowPlayingToast.class, Type.DEFAULT);

        if (musicToast == null) {
            add(manager, albumCover, title, author, soundtrack);
        } else {
            musicToast.setContent(title, author, soundtrack, albumCover);
        }
    }

    public static void add(ToastManager manager, AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        manager.add(new NowPlayingToast(albumCover, title, author, soundtrack));
    }

    /**
     * When {@link NowPlayingConfig#SHOW_SOUNDTRACK} is false, when another toast is shown, the soundtrack
     * is hided by the new toast
     */
    @Override
    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        MatrixStack matrices = context.getMatrices();
        TextRenderer textRenderer = manager.getClient().textRenderer;

        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        if (NowPlaying.config.SHOW_SOUNDTRACK) {
            context.drawGuiTexture(TEXTURE_EXTENDED, 0, 0, this.getWidth(), this.getHeight());
        } else {
            context.drawGuiTexture(TEXTURE_NORMAL, 0, 0, this.getWidth(), this.getHeight());
        }
        context.getMatrices().push();

        // rotate disc icon
        if (NowPlaying.config.ROTATE_ALBUM_COVER) {
            if (rotation >= 360) rotation = 0;
            rotation += 1;

            matrices.translate(0, 0, 0);
            matrices.translate((float) 32 / 2, (float) 32 / 2, 0);
            matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
            matrices.translate(-(float) 32 / 2, -(float) 32 / 2, 0);
            matrices.translate(-0, -0, 0);
        }
        this.albumCover.drawIcon(context, 6, 6);
        context.getMatrices().pop();

        context.drawText(textRenderer, this.title, 30, 7, -11534256, false);

        if (NowPlaying.config.SHOW_AUTHOR) {
            context.drawText(textRenderer, this.author, 30, 18, -16777216, false);
        }
        if (NowPlaying.config.SHOW_SOUNDTRACK) {
            context.drawText(textRenderer, this.soundtrack, 30, 29, -16777216, false);
        }

        return (double) (startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    public void setContent(Text title, Text author, Text soundtrack, AlbumCover type) {
        LOGGER.debug("setContent: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = type;
        this.justUpdated = true;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), NowPlaying.config.SHOW_SOUNDTRACK ? 44 : 32);
    }

    @Override
    public int getHeight() {
        if (NowPlaying.config.SHOW_SOUNDTRACK) {
            return 44;
        }
        return 32;
    }

    public enum Type {
        DEFAULT;

        Type() {
        }
    }

    public enum AlbumCover {
        VANILLA(new Identifier(NowPlaying.MOD_ID, "toast/vanilla")),
        MODDED(new Identifier(NowPlaying.MOD_ID, "toast/modded")),
        ALPHA(new Identifier(NowPlaying.MOD_ID, "toast/alpha")),
        BETA(new Identifier(NowPlaying.MOD_ID, "toast/beta")),
        AXOLOTL(new Identifier(NowPlaying.MOD_ID, "toast/axolotl")),
        DRAGON_FISH(new Identifier(NowPlaying.MOD_ID, "toast/dragon_fish")),
        SHUNIJI(new Identifier(NowPlaying.MOD_ID, "toast/shuniji")),
        NETHER(new Identifier(NowPlaying.MOD_ID, "toast/nether")),
        CAVES(new Identifier(NowPlaying.MOD_ID, "toast/caves")),
        WILD(new Identifier(NowPlaying.MOD_ID, "toast/wild")),
        TRAILSANDTALES(new Identifier(NowPlaying.MOD_ID, "toast/trailsandtales"));

        private final Identifier texture;

        AlbumCover(Identifier texture) {
            this.texture = texture;
        }

        public void drawIcon(DrawContext context, int x, int y) {
            RenderSystem.enableBlend();
            context.drawGuiTexture(this.texture, x, y, 20, 20);
        }
    }
}
