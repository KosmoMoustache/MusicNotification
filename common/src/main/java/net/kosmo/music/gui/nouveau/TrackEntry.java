//? if >=26.2 {
package net.kosmo.music.gui.nouveau;

import net.kosmo.music.Helper;
import net.kosmo.music.gui.component.AlbumCoverWidget;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;

public class TrackEntry extends AbstractJukeboxEntryContainerWidget {
	protected AlbumCoverWidget albumCoverWidget;

	public TrackEntry(Minecraft minecraft, JukeboxScreenNouveau screen, ScrollableLayout scrollableLayout, TrackData trackData) {
		this(minecraft, screen, scrollableLayout, trackData, false);
	}

	public TrackEntry(Minecraft minecraft, JukeboxScreenNouveau screen, ScrollableLayout scrollableLayout, TrackData trackData, boolean animated) {
		super(minecraft, screen, scrollableLayout, 0, 0, screen.getContentWidth(), 36, trackData, animated);
	}

	@Override
	boolean primaryAction() {
		Helper.playTrackData(this.minecraft, this.trackData);
		return true;
	}

	@Override
	protected void initWidgets(int width, boolean animated) {
		this.albumCoverWidget = new AlbumCoverWidget(20, 20, this.trackData.getAlbumCover(), animated);

		this.line1Widget = new StringWidget(
			this.trackData.title().copy()
				.append(" - ").withColor(CommonColors.WHITE)
				.append(this.trackData.author().copy()
					.withColor(CommonColors.LIGHT_GRAY)),
			this.minecraft.font
		);
		this.line1Widget.setMaxWidth(width, StringWidget.TextOverflow.SCROLLING);

		MutableComponent trackNumber = this.trackData.track()
			.filter(num -> num != 0)
			.map(num -> Component.literal("#" + num + " "))
			.orElse(Component.empty());

		this.line2Widget = new StringWidget(
			trackNumber.append(this.trackData.album().orElse(Component.empty()))
				.withStyle(Style.EMPTY.withColor(CommonColors.LIGHT_GRAY).withoutShadow()),
			this.minecraft.font
		);
		this.line2Widget.setMaxWidth(width, StringWidget.TextOverflow.SCROLLING);

		this.addChild(this.albumCoverWidget);
		this.addChild(this.line1Widget);
		this.addChild(this.line2Widget);

		this.layout.addChild(this.line1Widget, s -> s.paddingBottom(3));
		this.layout.addChild(this.line2Widget);
		this.layout.arrangeElements();
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

		this.albumCoverWidget.setPosition(this.getX() + 4, this.getY() + (this.getHeight() - this.albumCoverWidget.getHeight()) / 2 - 1);
		this.albumCoverWidget.extractRenderState(graphics, mouseX, mouseY, a);

		this.layout.setPosition(this.albumCoverWidget.getX() + this.albumCoverWidget.getWidth() + 4, this.getY() + (this.getHeight() - this.layout.getHeight()) / 2);
		this.layout.visitWidgets(child -> child.extractRenderState(graphics, mouseX, mouseY, a));
	}

	@Override
	protected Component getEntryNarration() {
		return Component.translatable("gui.musicnotification.jukebox.entry.track", this.trackData.title().getString());
	}
}
//? }
