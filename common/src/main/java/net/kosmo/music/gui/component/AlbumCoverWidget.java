package net.kosmo.music.gui.component;

import net.kosmo.music.resource.AlbumCover;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
//? if >=1.21.6 {
//?} else {
/*import org.joml.Quaternionf;
*///? }

public class AlbumCoverWidget extends AbstractWidget {
	private final AlbumCover cover;
	private int rotation;
	private boolean animated;

	public AlbumCoverWidget(int width, int height, AlbumCover cover, boolean animated) {
		this(0, 0, width, height, cover, animated);
	}

	public AlbumCoverWidget(int x, int y, int width, int height, AlbumCover cover, boolean animated) {
		super(x, y, width, height, Component.empty());
		this.cover = cover;
		this.rotation = 0;
		this.animated = animated;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
	}

	@Override
	//~ if >26 renderWidget -> extractWidgetRenderState
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		if (!this.animated) {
			this.cover.drawCover(graphics, this.getX(), this.getY(), this.width, this.height);
		} else {
			if (rotation >= 360) rotation = 0;
			rotation += 1;
			int coverX = this.getX() + this.cover.getWidth() / 2;
			int coverY = this.getY() + this.cover.getHeight() / 2;
			graphics.pose().pushMatrix();
			//? if >=1.21.6 {
			graphics.pose().translate(coverX, coverY);
			graphics.pose().rotate((float) Math.toRadians(rotation));
			graphics.pose().translate(-this.cover.getWidth() / 2.0f, -this.cover.getHeight() / 2.0f);
			this.cover.drawCover(graphics, 0, 0, this.width, this.height);
			//?} else {
			/*graphics.pose().translate(coverX, coverY, 0);
			graphics.pose().mulPose(new Quaternionf().rotateLocalZ((float) Math.toRadians(rotation)));
			graphics.pose().translate(-this.cover.getWidth() / 2.0f, -this.cover.getHeight() / 2.0f, 0);
			this.cover.drawCover(graphics, coverX, coverY);
			*///?}
			graphics.pose().popMatrix();
		}
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}
}
