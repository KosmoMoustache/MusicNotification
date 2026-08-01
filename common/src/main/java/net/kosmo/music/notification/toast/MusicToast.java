package net.kosmo.music.notification.toast;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.util.TextRender;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;
//? >=1.21.2 {
import net.kosmo.music.Helper;
//?}
//? <1.21.6 {
/*import org.joml.Quaternionf;
*///?}
//? >=1.21.2 && <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
*///? } elif >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?}
//? if >=1.21.5 {
import org.jetbrains.annotations.Nullable;
import net.minecraft.sounds.SoundEvent;
//?}

public class MusicToast implements Toast {
	private static final Identifier BACKGROUND_SPRITE = Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/background");
	private Visibility visibility;

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

	private Visibility computeVisibility(long visibilityTime, ToastManager toastManager) {
		return (double) (visibilityTime - this.startTime) >= 5000.0 * toastManager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
	}

	//? if >=1.21.2 {
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

		this.visibility = computeVisibility(visibilityTime, toastManager);
	}
	//? }

	//? if <=1.21.1 {
	/*@Override
	public @NotNull Visibility render(GuiGraphicsExtractor guiGraphics, ToastManager toastComponent, long timeSinceLastVisible) {
		render(guiGraphics, toastComponent.getMinecraft().font, timeSinceLastVisible);
		this.visibility = computeVisibility(timeSinceLastVisible, toastComponent);
		return this.visibility;
	}
	*///? }

	//? if >1.21.6
	@Override
	//~ if >26 render -> extractRenderState
	public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long visibilityTime) {
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

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED,BACKGROUND_SPRITE, x, 0, this.width() - x, this.height());

		if (Config.options().ROTATE_ALBUM_COVER && content.getAlbumCover().canBeAnimated()) {
			renderAnimatedAlbumCover(graphics, x, rotation);
		} else {
			content.getAlbumCover().drawCover(graphics, x + 6, 6);
		}

		// TODO: Fix: When album name is long and STYLE_LEGACY_TOAST_SCALE is true, title is not aligned properly (O's Piano; Lilypad)
		int x1 = x + 6 + /*AlbumCover.getWidth() */ 20 + 6;
		//~ if >=1.21.6 '-13108' -> 'CommonColors.COSMOS_PINK'
		TextRender.drawScrollableText(graphics, font, content.title(), 30, x1, 7, this.width() - 4, 7 + font.lineHeight, Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? CommonColors.COSMOS_PINK : -11534256, false);
		TextRender.drawScrollableText(graphics, font, content.author(), 30, x1, 18, this.width() - 4, 18 + font.lineHeight, Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? -3355444 : CommonColors.BLACK, false);

		if (shouldRenderExtended()) {
			TextRender.drawScrollableText(graphics, font, content.album().get(), 30, x1, 29, this.width() - 4, 29 + font.lineHeight, Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? -3355444 : CommonColors.BLACK, false);
		}
	}

	private void renderAnimatedAlbumCover(GuiGraphicsExtractor guiGraphics, int x, int rotation) {
		int cx = x + 16;
		guiGraphics.pose().pushMatrix();
		// 16 = 6 + AlbumCover.get{Width/Height}() / 2
		//? if >=1.21.6 {
		guiGraphics.pose().translate(cx, 16);
		guiGraphics.pose().rotate((float) Math.toRadians(rotation));
		guiGraphics.pose().translate(-16, -16);
		content.getAlbumCover().drawCover(guiGraphics, 6, 6);
		guiGraphics.pose().translate(x, 0);
		//? } else {
		/*guiGraphics.pose().translate(cx, 16, 0);
		guiGraphics.pose().mulPose(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
		guiGraphics.pose().translate(-16, -16, 0);
		content.getAlbumCover().drawCover(guiGraphics, 6, 6);
		guiGraphics.pose().translate(x, 0, 0);
		*///? }

		guiGraphics.pose().popMatrix();
	}

	@Override
	public @NotNull Object getToken() {
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
