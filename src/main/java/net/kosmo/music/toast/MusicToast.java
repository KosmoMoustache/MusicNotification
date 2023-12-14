package net.kosmo.music.toast;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.MusicManager;
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

import static net.kosmo.music.ClientMusic.LOGGER;

public class MusicToast implements Toast {
    public static final Identifier TEXTURE = new Identifier(ClientMusic.MOD_ID, "textures/gui/toasts.png");
    private static final Type DEFAULT = Type.DEFAULT;
    private final Type type;
    private AlbumCover albumCover;
    private long startTime;
    private boolean justUpdated;
    private Text title;
    private Text author;
    private Text soundtrack;
    private int rotation;

    public MusicToast(Type type, AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        LOGGER.info("Now playing: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.type = type;
        this.albumCover = albumCover;
    }

    public static void show(SoundInstance soundInstance) {
        String soundName = ClientMusic.getLastSegmentOfPath(soundInstance.getSound().getIdentifier());
        MusicManager.Entry entry = ClientMusic.musicManager.getEntry(soundName.toLowerCase());

        if (entry != null) {
            show(MinecraftClient.getInstance().getToastManager(), entry);
        } else {
            show(MinecraftClient.getInstance().getToastManager(), Text.literal(soundName), Text.literal(soundInstance.getSound().getIdentifier().getNamespace()), Text.literal(""), AlbumCover.CD);
        }
    }

    public static void show(ToastManager manager, MusicManager.Entry entry) {
        show(manager, Text.literal(entry.getTitle()), Text.literal(entry.getAuthor()), Text.literal(entry.getSoundtrack()), entry.getAlbumCover());
    }

    public static void show(ToastManager manager, Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        MusicToast musicToast = manager.getToast(MusicToast.class, DEFAULT);

        if (musicToast == null) {
            add(manager, albumCover, title, author, soundtrack);
        } else {
            musicToast.setContent(title, author, soundtrack, albumCover);
        }
    }

    public static void add(ToastManager manager, AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        manager.add(new MusicToast(DEFAULT, albumCover, title, author, soundtrack));
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), ClientMusic.config.SHOW_SOUNDTRACK_NAME ? 44 : 32);
    }

    /**
     * When {@link net.kosmo.music.ModConfig#SHOW_SOUNDTRACK_NAME} is false, when another toast is shown, the soundtrack
     * is hided by the new toast
     */
    @Override
    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        MatrixStack matrices = context.getMatrices();


        if (rotation >= 360) rotation = 0;
        rotation += 1;
        TextRenderer textRenderer = manager.getClient().textRenderer;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        context.drawTexture(TEXTURE, 0, 0, 0, ClientMusic.config.SHOW_SOUNDTRACK_NAME ? 32 : 0, this.getWidth(), this.getHeight());

        context.getMatrices().push();
        // Make the icon rotate
        if (ClientMusic.config.ROTATE_ALBUM_COVER) {
            matrices.translate(0, 0, 0);
            matrices.translate((float) AlbumCover.getWidth() / 2, (float) AlbumCover.getHeight() / 2, 0);
            matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
            matrices.translate(-(float) AlbumCover.getWidth() / 2, -(float) AlbumCover.getHeight() / 2, 0);
            matrices.translate(-0, -0, 0);
        }

        this.albumCover.drawIcon(context, 6, 6);
        context.getMatrices().pop();


        context.drawText(textRenderer, this.title, 30, 7, -11534256, false);

        if (!ClientMusic.config.HIDE_AUTHOR) {
            context.drawText(textRenderer, this.author, 30, 18, -16777216, false);
        }

        if (ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
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
        if (ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
            return 44;
        }
        return 32;
    }


    public enum Type {
        DEFAULT
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

        static int getWidth() {
            return 32;
        }

        static int getHeight() {
            return 32;
        }

        public void drawIcon(DrawContext context, int x, int y) {
            context.drawTexture(TEXTURE, x, y, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
        }
    }
}