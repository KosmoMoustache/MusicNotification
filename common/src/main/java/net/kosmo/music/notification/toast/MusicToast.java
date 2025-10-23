package net.kosmo.music.notification.toast;

import net.kosmo.music.Helper;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class MusicToast implements Toast {
	private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/background");
	private static final ResourceLocation EXTENDED_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/background_extended");
	private Toast.Visibility visibility;

	private TrackData content;
	private boolean justUpdated;
	private int rotation;
	private long startTime;

	public MusicToast(TrackData td) {
		this.setContent(td);
	}

	public void setContent(TrackData td) {
		this.visibility = Visibility.SHOW;
		this.content = td;
		this.justUpdated = true;
	}

	@Override
	public @NotNull Visibility getWantedVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager toastManager, long visibilityTime) {
		if (!Helper.isVolumeZero()) {
			this.visibility = Visibility.HIDE;
			return;
		}

		this.visibility = (double) (visibilityTime - this.startTime) >= 5000.0 * toastManager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
	}

	@Override
	public @Nullable SoundEvent getSoundEvent() {
		return Config.options().DISABLE_TOAST_SOUND == Config.Options.DisableToastSound.MUTE_SELF ? null : Toast.super.getSoundEvent();
	}

	@Override
	public void render(GuiGraphics guiGraphics, Font font, long visibilityTime) {
		if (rotation >= 360) rotation = 0;
		rotation += 1;

//		if (content == null) {
//			return;
//		}

		if (this.justUpdated) {
			this.startTime = visibilityTime;
			this.justUpdated = false;
		}

		boolean scaleWidthWithContent = true;

		int x = 0;
		if (scaleWidthWithContent) {
			int a = this.getMaxWidth(font);
			int padding = 32 + (4 * 2) + 5;
			x = (this.width() - a) - padding;
		}

		if (Config.options().SHOW_ALBUM_NAME) {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXTENDED_BACKGROUND_SPRITE, x, 0, this.width() - x, this.height());
		} else {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, x, 0, this.width() - x, this.height());
		}

		guiGraphics.pose().pushMatrix();
		if (Config.options().ROTATE_ALBUM_COVER) {
			Matrix3x2fStack matrices = guiGraphics.pose();
			matrices.translate(0, 0);
			matrices.translate(16, 16);
			matrices.rotate((float) Math.toRadians(rotation));
			matrices.translate(-16, -16);
			matrices.translate(-0, -0);
		}

		content.getAlbumInfo().drawCover(guiGraphics, 6, 6);

//		content.albumInfo().get().cover().drawCover(guiGraphics, 6, 6);
		guiGraphics.pose().popMatrix();


//			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, x, 0, this.width() - x, 30);

//            int i = 7;
//            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, MUSIC_NOTES_SPRITE, 7, 7, 16, 16, musicNoteColor);
//            Component var10002 = getNowPlayingString(currentSong);
//            Objects.requireNonNull(font);
		guiGraphics.drawString(font, content.title(), x + 5, 15 - 9 / 2, -11534256);
		guiGraphics.drawString(font, content.author(), x + 5, 15 - 9 / 2 + font.lineHeight, -11534256);
		guiGraphics.drawString(font, content.album().get(), x + 5, 15 - 9 / 2 + font.lineHeight * 2, -11534256);
	}

	@Override
	public Object getToken() {
		return Toast.super.getToken();
	}

	@Override
	public int width() {
		return Toast.super.width();
	}

	@Override
	public int height() {
		if (Config.options().SHOW_ALBUM_NAME) {
			return 44;
		}
		return 32;
	}

	private int getMaxWidth(Font font) {
		int w = 0;
		if (content.album().isPresent()) {
			w = font.width(content.album().get());
		}
		return Math.max(font.width(content.title()), Math.max(font.width(content.author()), w));
	}

//	public static void tick() {
//		currentSong = Minecraft.getInstance().getMusicManager().getCurrentMusicTranslationKey();
//	}


//	@Override
//	public void update(ToastManager toastManager, long visibilityTime) {
//        if (!Helper.isVolumeZero()) {
//            this.visibility = Visibility.HIDE;
//            return;
//        }

//		this.visibility = (double) visibilityTime < 5000.0F * toastManager.getNotificationDisplayTimeMultiplier() ? Visibility.SHOW : Visibility.HIDE;
//		tick();

//            musicNoteColor = ColorLerper.getLerpedColor(ColorLerper.Type.MUSIC_NOTE, (float)musicNoteColorTick);
//	}


//	private static int getWidth(@Nullable String text, Font font) {
//		return 30 + font.width(getNowPlayingString(text)) + 7;
//	}
}
