package kosmo.sh.music.impl.toast;

import kosmo.sh.music.impl.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

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
    private Toast.Visibility visibility;

    public MusicToast(MusicManager.Music music) {
        this.setContent(music);
    }

    public void setContent(MusicManager.Music music) {
        this.visibility = Visibility.SHOW;
        this.title = Component.literal(music.getTitle());
        this.author = Component.literal(music.getAuthor());
        this.albumName = Component.literal(music.getAlbumName());
        this.albumCover = music.albumCover;
        this.justUpdated = true;
    }

    @Override
    public @NotNull Visibility getWantedVisibility() {
        return this.visibility;
    }

    public void update(ToastManager toastManager, long l) {
        if (!Helper.isVolumeZero()) {
            this.visibility = Visibility.HIDE;
            return;
        }

        this.visibility = (double) (l - this.startTime) >= 5000.0 * toastManager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }


    @Override
    public void render(GuiGraphics guiGraphics, Font font, long startTime) {
        if (rotation >= 360) rotation = 0;
        rotation += 1;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        if (ClientMusic.config.SHOW_ALBUM_NAME) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE_EXTENDED, 0, 0, this.width(), this.height());
        } else {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.width(), this.height());
        }

        // Make the icon rotate
        guiGraphics.pose().pushMatrix();
        if (ClientMusic.config.ROTATE_ALBUM_COVER) {
            Matrix3x2fStack matrices = guiGraphics.pose();
            matrices.translate(0, 0);
            matrices.translate(16, 16);
            matrices.rotate((float) Math.toRadians(rotation));
            matrices.translate(-16, -16);
            matrices.translate(-0, -0);
        }
        this.albumCover.drawAlbumCover(guiGraphics, 6, 6);
        guiGraphics.pose().popMatrix();

        RenderHelper.drawScrollableText(guiGraphics, font, this.title, 30, 30, 7, this.width() - 4, 7 + font.lineHeight, ClientMusic.isDarkModeEnabled ? CommonColors.COSMOS_PINK : -11534256, false, 30, 0, this.width() + 4, this.height());

        if (ClientMusic.config.SHOW_AUTHOR) {
            RenderHelper.drawScrollableText(guiGraphics, font, this.author, 30, 30, 18, this.width() - 4, 18 + font.lineHeight, ClientMusic.isDarkModeEnabled ? -3355444 : CommonColors.BLACK, false, 30, 0, this.width() + 4, this.height());
        }
        if (ClientMusic.config.SHOW_ALBUM_NAME) {
            RenderHelper.drawScrollableText(guiGraphics, font, this.albumName, 30, 30, 29, this.width() - 4, 29 + font.lineHeight, ClientMusic.isDarkModeEnabled ? -3355444 : CommonColors.BLACK, false, 30, 0, this.width() + 4, this.height());
        }
    }

    @Override
    public int height() {
        if (ClientMusic.config.SHOW_ALBUM_NAME) {
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