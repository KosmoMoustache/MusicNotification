package net.kosmo.nowplaying.toast;

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
    public static final Identifier TEXTURE = new Identifier(NowPlaying.MOD_ID, "textures/gui/toasts.png");
    private static final Type DEFAULT = Type.DEFAULT;
    private final Type type;
    private AlbumCover albumCover;
    private long startTime;
    private boolean justUpdated;
    private Text title;
    private Text author;
    private Text soundtrack;
    private int rotation;

    public NowPlayingToast(Type type, AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        LOGGER.info("Now playing: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.type = type;
        this.albumCover = albumCover;
    }

    public static void show(SoundInstance soundInstance, Type type) {
        String key = NowPlaying.getLastSegmentOfPath(soundInstance.getSound().getIdentifier());
        Optional<MusicEntry> entry = NowPlaying.musicManager.getByKey(key);

        if (entry.isPresent()) {
            show(MinecraftClient.getInstance().getToastManager(), entry.get(), type);
        } else {
            show(MinecraftClient.getInstance().getToastManager(), Text.literal(key), Text.literal(soundInstance.getSound().getIdentifier().getNamespace()), Text.literal(""), AlbumCover.CD, type);
        }
    }

    public static void show(ToastManager manager, MusicEntry entry, Type type) {
        show(manager, Text.literal(entry.title), Text.literal(entry.author), Text.literal(entry.soundtrack), entry.albumCover, type);
    }

    public static void show(ToastManager manager, Text title, Text author, Text soundtrack, AlbumCover albumCover, Type type) {
        NowPlayingToast musicToast = manager.getToast(NowPlayingToast.class, DEFAULT);

        if (musicToast == null) {
            add(manager, albumCover, title, author, soundtrack);
        } else {
            musicToast.setContent(title, author, soundtrack, albumCover);
        }
    }

    public static void add(ToastManager manager, AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        manager.add(new NowPlayingToast(DEFAULT, albumCover, title, author, soundtrack));
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), NowPlaying.config.SHOW_SOUNDTRACK_NAME ? 44 : 32);
    }

    /**
     * When {@link NowPlayingConfig#SHOW_SOUNDTRACK_NAME} is false, when another toast is shown, the soundtrack
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
        context.drawTexture(TEXTURE, 0, 0, 0, NowPlaying.config.SHOW_SOUNDTRACK_NAME ? 32 : 0, this.getWidth(), this.getHeight());
        context.getMatrices().push();

        // Make the icon rotate
        if (NowPlaying.config.ROTATE_ALBUM_COVER) {
            if (rotation >= 360) rotation = 0;
            rotation += 1;

            matrices.translate(0, 0, 0);
            matrices.translate((float) AlbumCover.getWidth() / 2, (float) AlbumCover.getHeight() / 2, 0);
            matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
            matrices.translate(-(float) AlbumCover.getWidth() / 2, -(float) AlbumCover.getHeight() / 2, 0);
            matrices.translate(-0, -0, 0);
        }

        this.albumCover.drawIcon(context, 6, 6);
        context.getMatrices().pop();


        context.drawText(textRenderer, this.title, 30, 7, -11534256, false);

        if (!NowPlaying.config.HIDE_AUTHOR) {
            context.drawText(textRenderer, this.author, 30, 18, -16777216, false);
        }

        if (NowPlaying.config.SHOW_SOUNDTRACK_NAME) {
            // max length 23 chars
            context.drawText(textRenderer, this.soundtrack, 30, 29, -16777216, false);
        }

        return (double) (startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    public void setContent(Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        LOGGER.info("setContent: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = albumCover;
        this.justUpdated = true;
    }

    @Override
    public int getHeight() {
        if (NowPlaying.config.SHOW_SOUNDTRACK_NAME) {
            return 44;
        }
        return 32;
    }


    public enum Type {
        DEFAULT,
        DISC,
    }

    public enum AlbumCover {
        CD(0, 0),
        MODDED_CD(1, 0),
        // Cover art
        ALPHA(0, 1),
        BETA(1, 1),
        AXOLOTL(2, 1),
        DRAGON_FISH(3, 1),
        SHUNIJI(0, 2),
        NETHER(1, 2),
        CAVES(2, 2),
        WILD(3, 2),
        TRAILSANDTALES(0, 3);

        private final int textureSlotY;
        private final int textureSlotX;

        AlbumCover(int textureSlotX, int textureSlotY) {
            this.textureSlotX = textureSlotX;
            this.textureSlotY = textureSlotY;
        }

        static public int getWidth() {
            return 32;
        }

        static public int getHeight() {
            return 32;
        }

        public void drawIcon(DrawContext context, int x, int y) {
            context.drawTexture(TEXTURE, x, y, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
        }
    }
}
