package net.kosmo.music.toast;

import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.ModConfig;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import static net.kosmo.music.ClientMusic.LOGGER;

public class MusicToast implements Toast {
    public static final Identifier TEXTURE = new Identifier(ClientMusic.MOD_ID, "toast/background");
    public static final Identifier TEXTURE_EXTENDED = new Identifier(ClientMusic.MOD_ID, "toast/background_extended");

    private final Type type = Type.DEFAULT;
    private long startTime;
    private boolean justUpdated;
    private int rotation;
    private Text title;
    private Text author;
    private Text soundtrack;
    private AlbumCover albumCover;

    public MusicToast(AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = albumCover;
    }

    public static void show(Sound sound) {
        Identifier identifier = sound.getIdentifier();
        MusicManager.Entry entry = ClientMusic.musicManager.get(identifier);

        if (music != null) {
            show(MinecraftClient.getInstance().getToastManager(), music);
        } else {
            String[] lastPartId = identifier.getPath().split("/");
            show(MinecraftClient.getInstance().getToastManager(), Text.literal(lastPartId[lastPartId.length - 1]), Text.literal(identifier.getNamespace()), Text.literal("Unknown"), AlbumCover.MODDED);
        }
    }

    public static void show(ToastManager manager, MusicManager.Music music) {
        show(manager, Text.literal(music.getTitle()), Text.literal(music.getAuthor()), Text.literal(music.getAlbumName()), new AlbumCover(music.getAlbumCover()));
    }

    public static void show(ToastManager manager, Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        if (!ClientMusic.canShowToast()) {
            return;
        }

        LOGGER.info("Showing toast: {} by {} ({}) {}", title.getString(), author.getString(), soundtrack.getString(), albumCover.textureId);

        MusicToast musicToast = manager.getToast(MusicToast.class, Type.DEFAULT);

        if (musicToast == null) {
            manager.add(new MusicToast(albumCover, title, author, soundtrack));
        } else {
            musicToast.setContent(title, author, soundtrack, albumCover);
        }
    }

    public void setContent(Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        LOGGER.info("Setting content: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = albumCover;
        this.justUpdated = true;
    }

    /**
     * When {@link ModConfig#SHOW_SOUNDTRACK_NAME} is false, when another toast is shown, the soundtrack
     * is hided by the new toast
     */
    @Override
    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        // Draw background
        if (ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
            context.drawGuiTexture(TEXTURE_EXTENDED, 0, 0, this.getWidth(), this.getHeight());
        } else {
            context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        }

        if (rotation >= 360) rotation = 0;
        rotation += 1;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        // Make the icon rotate
        context.getMatrices().push();
        // TODO: Use sprites animation instead of matrix transformations
        if (ClientMusic.config.ROTATE_ALBUM_COVER) {
            MatrixStack matrices = context.getMatrices();
            matrices.translate(0, 0, 0);
            matrices.translate((float) AlbumCover.getWidth() / 2, (float) AlbumCover.getHeight() / 2, 0);
            matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
            matrices.translate(-(float) AlbumCover.getWidth() / 2, -(float) AlbumCover.getHeight() / 2, 0);
            matrices.translate(-0, -0, 0);
        }

        this.albumCover.drawAlbumCover(context, 6, 6);
        context.getMatrices().pop();

        context.drawText(manager.getClient().textRenderer, this.title, 30, 7, -11534256, false);

        if (!ClientMusic.config.HIDE_AUTHOR) {
            context.drawText(manager.getClient().textRenderer, this.author, 30, 18, -16777216, false);
        }

        if (ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
            // max length 23 chars
            context.drawText(manager.getClient().textRenderer, this.soundtrack, 30, 29, -16777216, false);
        }

        return (double) (startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getHeight() {
        if (ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
            return 44;
        }
        return 32;
    }

    @Override
    public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), ClientMusic.config.SHOW_SOUNDTRACK_NAME ? 44 : 32);
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        DEFAULT
    }
}
