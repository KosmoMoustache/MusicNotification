package net.kosmo.music.notification.toast;

import net.kosmo.music.Helper;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.util.TextRender;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class MusicToast implements Toast {
	private static final Identifier BACKGROUND_SPRITE = Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/background");
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
		if (Helper.isVolumeZero()) {
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

		if (this.justUpdated) {
			this.startTime = visibilityTime;
			this.justUpdated = false;
		}

		int x = 0;
		if (Config.options().STYLE_LEGACY_TOAST_SCALE) {
			int a = this.getMaxWidth(font);
			int padding = 32 + (4 * 2) + 5;
			x = (this.width() - a) - padding;
		}

		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, x, 0, this.width() - x, this.height());

		if (Config.options().ROTATE_ALBUM_COVER && content.getAlbumCover().canBeAnimated()) {
			renderAnimatedAlbumCover(guiGraphics, x, rotation);
		} else {
			content.getAlbumCover().drawCover(guiGraphics, x + 6, 6);
		}

		// TODO: Fix: When album name is long and STYLE_LEGACY_TOAST_SCALE is true, title is not aligned properly (O's Piano; Lilypad)
		int x1 = x + 6 + /*AlbumCover.getWidth() */ 20 + 6;
		TextRender.drawScrollableText(guiGraphics, font, content.title(), 30, x1, 7, this.width() - 4, 7 + font.lineHeight, Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? CommonColors.COSMOS_PINK : -11534256, false);
		TextRender.drawScrollableText(guiGraphics, font, content.author(), 30, x1, 18, this.width() - 4, 18 + font.lineHeight, Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? -3355444 : CommonColors.BLACK, false);

		if (shouldRenderExtended()) {
			TextRender.drawScrollableText(guiGraphics, font, content.album().get(), 30, x1, 29, this.width() - 4, 29 + font.lineHeight, Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? -3355444 : CommonColors.BLACK, false);
		}
	}

	private void renderAnimatedAlbumCover(GuiGraphics guiGraphics, int x, int rotation) {
		int cx = x + 16;
		guiGraphics.pose().pushMatrix();
		Matrix3x2fStack matrices = guiGraphics.pose();
		// 16 = 6 + AlbumCover.get{Width/Height}() / 2
		matrices.translate(cx, 16);
		matrices.rotate((float) Math.toRadians(rotation));
		matrices.translate(-16, -16);
		content.getAlbumCover().drawCover(guiGraphics, 6, 6);

		matrices.translate(x, 0);

		guiGraphics.pose().popMatrix();
	}

	@Override
	public Object getToken() {
		return Toast.super.getToken();
	}

	public boolean shouldRenderExtended() {
		return Config.options().SHOW_ALBUM_NAME && content.album().isPresent();
	}

	public int width(Font font) {
		return getMaxWidth(font);
	}

	@Override
	public int width() {
		return Toast.super.width();
	}

	@Override
	public int height() {
		if (shouldRenderExtended()) {
			return 44;
		}
		return 32;
	}

	private int getMaxWidth(Font font) {
		int w = 0;
		if (Config.options().SHOW_ALBUM_NAME && content.album().isPresent()) {
			w = font.width(content.album().get());
		}
		return Math.max(font.width(content.title()), Math.max(font.width(content.author()), w));
	}
}
