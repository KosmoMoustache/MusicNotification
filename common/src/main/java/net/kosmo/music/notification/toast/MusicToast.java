package net.kosmo.music.notification.toast;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.AlbumCover;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.util.TextRender;
import net.minecraft.client.Minecraft;
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
	//?}

	//? if <=1.21.1 {
	/*@Override
	public @NotNull Visibility render(GuiGraphicsExtractor graphics, ToastManager toastComponent, long timeSinceLastVisible) {
		generalRender(graphics, Minecraft.getInstance().font, timeSinceLastVisible);
//		render(graphics, toastComponent.getMinecraft().font, timeSinceLastVisible);
		this.visibility = computeVisibility(timeSinceLastVisible, toastComponent);
		return this.visibility;
	}
	*///?}

	//? if >1.21.6
	@Override
	//~ if >26 render -> extractRenderState
	public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long visibilityTime) {
		generalRender(graphics, font, visibilityTime);
	}

	private void generalRender(GuiGraphicsExtractor graphics, Font font, long visibilityTime) {
		if (rotation >= 360) rotation = 0;
		rotation += 1;

		if (this.justUpdated) {
			this.startTime = visibilityTime;
			this.justUpdated = false;
		}

		int fullWidthScale = 0;
		if (Config.options().STYLE_LEGACY_TOAST_SCALE) {
			int a = this.getMaxWidth(font);
			int padding = 32 + (4 * 2) + 5;
			fullWidthScale = (this.width() - a) - padding;
		}

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED,BACKGROUND_SPRITE, fullWidthScale, 0, this.width() - fullWidthScale, this.height());

		if (Config.options().ROTATE_ALBUM_COVER && content.getAlbumCover().canBeAnimated()) {
			renderAnimatedAlbumCover(graphics, fullWidthScale, rotation);
		} else {
			content.getAlbumCover().drawCover(graphics, fullWidthScale + 6, 6);
		}

		int textStartX = fullWidthScale + 32;
		int textStopX = this.width() - 3;
		int textStartY = 3 + 2;
		int textStopY = textStartY + font.lineHeight;

//		graphics.fill(textStartX, textStartY, textStopX, textStopY, CommonColors.RED);

		//~ if >=1.21.6 '-13108' -> 'CommonColors.COSMOS_PINK'
		TextRender.drawScrollableText(graphics, font,
			content.title().copy().withColor(Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? -13108 : -11534256),
			textStartX,
			textStopX,
			textStartY,
			textStopY,
			false
		);

//		textStartX += 30;
//		textStopX = this.width();
		textStartY += 4 + font.lineHeight;
		textStopY = textStartY + font.lineHeight;

//		graphics.fill(textStartX, textStartY, textStopX, textStopY, CommonColors.GREEN);

		TextRender.drawScrollableText(graphics, font,
			content.author().copy().withColor(Config.options().COMPUTED_IS_DARK_MODE_ENABLED ? -3355444 : CommonColors.BLACK),
			textStartX,
			textStopX,
			textStartY,
			textStopY,
			false
		);

		if (shouldRenderExtended()) {
			textStartY += 4 + font.lineHeight;
			textStopY = textStartY + font.lineHeight;

//			graphics.fill(textStartX, textStartY, textStopX, textStopY, CommonColors.BLUE);

			TextRender.drawScrollableText(graphics, font,
				content.album().get().copy().withColor(CommonColors.BLACK),
				textStartX,
				textStopX,
				textStartY,
				textStopY,
				false
			);
		}
	}

	private void renderAnimatedAlbumCover(GuiGraphicsExtractor guiGraphics, int x, int rotation) {
		AlbumCover albumCover = content.getAlbumCover();
		float coverCenterX = x + 6 + albumCover.getWidth() / 2.0f;
		float coverCenterY = 6 + albumCover.getHeight() / 2.0f;
		guiGraphics.pose().pushMatrix();
		//? if >=1.21.6 {
		guiGraphics.pose().translate(coverCenterX, coverCenterY);
		guiGraphics.pose().rotate((float) Math.toRadians(rotation));
		guiGraphics.pose().translate(-albumCover.getWidth() / 2.0f, -albumCover.getHeight() / 2.0f);
		albumCover.drawCover(guiGraphics, 0, 0);
		//?} else {
		/*guiGraphics.pose().translate(coverCenterX, coverCenterY, 0);
		guiGraphics.pose().mulPose(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
		guiGraphics.pose().translate(-albumCover.getWidth() / 2.0f, -albumCover.getHeight() / 2.0f, 0);
		albumCover.drawCover(guiGraphics, 0, 0);
		*///?}

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
