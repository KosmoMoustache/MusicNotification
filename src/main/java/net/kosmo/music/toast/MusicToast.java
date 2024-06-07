package net.kosmo.music.toast;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.RenderHelper;
import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class MusicToast implements Toast {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "toast/background");
    private static final ResourceLocation TEXTURE_EXTENDED = ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "toast/background_extended");
    private final Type type = Type.DEFAULT;

    private boolean justUpdated;
    private long startTime;
    private int rotation;
    private Component title;
    private Component author;
    private Component albumName;
    private AlbumCover albumCover;

    public MusicToast(MusicManager.Music music) {
        this.title = Component.literal(music.getTitle());
        this.author = Component.literal(music.getAuthor());
        this.albumName = Component.literal(music.getAlbumName());
        this.albumCover = music.albumCover;
        this.justUpdated = true;
    }

    public static void show(ToastComponent manager, MusicManager.Music music) {
        if (!ClientMusic.isVolumeZero()) {
            return;
        }

        MusicToast musicToast = manager.getToast(MusicToast.class, Type.DEFAULT);
        if (musicToast == null) {
            ClientMusic.LOGGER.debug("Showing toast for {}", music.identifier);
            manager.addToast(new MusicToast(music));
        } else {
            ClientMusic.LOGGER.debug("Setting toast content: {} {}", music.identifier, musicToast);
            musicToast.setContent(music);
        }

        ClientMusic.musicHistory.addMusic(music);
    }

    public void setContent(MusicManager.Music music) {
        this.title = Component.literal(music.getTitle());
        this.author = Component.literal(music.getAuthor());
        this.albumName = Component.literal(music.getAlbumName());
        this.albumCover = music.albumCover;
        this.justUpdated = true;
    }

    @Override
    public Toast.@NotNull Visibility render(GuiGraphics context, ToastComponent manager, long startTime) {
        if (rotation >= 360) rotation = 0;
        rotation += 1;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        if (ClientMusic.config.TOAST_CONFIG.SHOW_ALBUM_NAME) {
            context.blitSprite(TEXTURE_EXTENDED, 0, 0, this.width(), this.height());
        } else {
            context.blitSprite(TEXTURE, 0, 0, this.width(), this.height());
        }

        // Make the icon rotate
        context.pose().pushPose();
        if (ClientMusic.config.TOAST_CONFIG.ROTATE_ALBUM_COVER) {
            PoseStack matrices = context.pose();
            matrices.translate(0, 0, 0);
            matrices.translate(16, 16, 0);
            matrices.mulPose(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
            matrices.translate(-16, -16, 0);
            matrices.translate(-0, -0, 0);
        }
        this.albumCover.drawAlbumCover(context, 6, 6);
        context.pose().popPose();

        context.drawString(manager.getMinecraft().font, this.title, 30, 7, ClientMusic.isDarkModeEnabled ? 0xff75ff : -11534256, false);

        if (ClientMusic.config.TOAST_CONFIG.SHOW_AUTHOR) {
            context.drawString(manager.getMinecraft().font, this.author, 30, 18, ClientMusic.isDarkModeEnabled ? -3355444 : Colors.BLACK, false);
        }
        if (ClientMusic.config.TOAST_CONFIG.SHOW_ALBUM_NAME) {
            RenderHelper.drawScrollableText(context, manager.getMinecraft().font, this.albumName, 30, 30, 29, this.width() - 4, 29 + manager.getMinecraft().font.lineHeight, ClientMusic.isDarkModeEnabled ? -3355444 : CommonColors.BLACK, false, context.guiWidth() - 160 + 30, 0, context.guiWidth() - 4, 44);
        }

        return (double) (startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int height() {
        if (ClientMusic.config.TOAST_CONFIG.SHOW_ALBUM_NAME) {
            return 44;
        }
        return 32;
    }

    public @NotNull Type getToken() {
        return this.type;
    }

    public enum Type {
        DEFAULT
    }
}