package net.kosmo.music.toast;

import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class MusicToast implements Toast {
    private static final Identifier TEXTURE = new Identifier(ClientMusic.MOD_ID, "toast/background");
    private static final Identifier TEXTURE_EXTENDED = new Identifier(ClientMusic.MOD_ID, "toast/background_extended");
    private final Type type = Type.DEFAULT;

    private boolean justUpdated;
    private long startTime;
    private int rotation;
    private Text title;
    private Text author;
    private Text albumName;
    private AlbumCover albumCover;

    public MusicToast(MusicManager.Music music) {
        this.title = Text.literal(music.getTitle());
        this.author = Text.literal(music.getAuthor());
        this.albumName = Text.literal(music.getAlbumName());
        this.albumCover = music.albumCover;
        this.justUpdated = true;
    }

    public static void show(ToastManager manager, MusicManager.Music music) {
        if (!ClientMusic.isVolumeZero()) {
            return;
        }

        MusicToast musicToast = manager.getToast(MusicToast.class, Type.DEFAULT);
        if (musicToast == null) {
            ClientMusic.LOGGER.debug("Showing toast for {}", music.identifier);
            manager.add(new MusicToast(music));
        } else {
            ClientMusic.LOGGER.debug("Setting toast content: {} {}", music.identifier, musicToast);
            musicToast.setContent(music);
        }

        ClientMusic.musicHistory.addMusic(music);
    }

    public void setContent(MusicManager.Music music) {
        this.title = Text.literal(music.getTitle());
        this.author = Text.literal(music.getAuthor());
        this.albumName = Text.literal(music.getAlbumName());
        this.albumCover = music.albumCover;
        this.justUpdated = true;
    }

    @Override
    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        if (rotation >= 360) rotation = 0;
        rotation += 1;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        if (ClientMusic.config.TOAST_CONFIG.SHOW_ALBUM_NAME) {
            context.drawGuiTexture(TEXTURE_EXTENDED, 0, 0, this.getWidth(), this.getHeight());
        } else {
            context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        }

        // Make the icon rotate
        context.getMatrices().push();
        if (ClientMusic.config.TOAST_CONFIG.ROTATE_ALBUM_COVER) {
            MatrixStack matrices = context.getMatrices();
            matrices.translate(0, 0, 0);
            matrices.translate(16, 16, 0);
            matrices.multiply(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
            matrices.translate(-16, -16, 0);
            matrices.translate(-0, -0, 0);
        }
        this.albumCover.drawAlbumCover(context, 6, 6);
        context.getMatrices().pop();

        context.drawText(manager.getClient().textRenderer, this.title, 30, 7, -11534256, false);

        if (ClientMusic.config.TOAST_CONFIG.SHOW_AUTHOR) {
            context.drawText(manager.getClient().textRenderer, this.author, 30, 18, -16777216, false);
        }
        if (ClientMusic.config.TOAST_CONFIG.SHOW_ALBUM_NAME) {
            ClientMusic.drawScrollableText(context, manager.getClient().textRenderer, this.albumName, 30, 30, 29, this.getWidth() - 4, 29 + manager.getClient().textRenderer.fontHeight, Colors.BLACK, false, context.getScaledWindowWidth() - 160 + 30, 0, context.getScaledWindowWidth() - 4, 44);
        }

        return (double) (startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getHeight() {
        if (ClientMusic.config.TOAST_CONFIG.SHOW_ALBUM_NAME) {
            return 44;
        }
        return 32;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        DEFAULT
    }
}
