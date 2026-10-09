package net.kosmo.music.gui.v3;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.kosmo.music.gui.component.AlbumCoverWidget;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJukeboxEntryContainerWidget extends AbstractContainerWidget {
	protected final Minecraft minecraft;
	protected final JukeboxScreenV3 screen;
	protected final TrackData trackData;
	private final List<AbstractWidget> children = new ArrayList<>();

	protected AlbumCoverWidget albumCoverWidget;
	protected StringWidget line1Widget;
	protected StringWidget line2Widget;

	public AbstractJukeboxEntryContainerWidget(Minecraft minecraft, JukeboxScreenV3 screen, int x, int y, int width, int height, TrackData trackData, boolean animated) {
		super(x, y, width, height, Component.empty());
		this.minecraft = minecraft;
		this.screen = screen;
		this.trackData = trackData;
		this.initWidgets(width, animated);
	}

	@Override
	protected int contentHeight() {
		return this.height;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, this.line1Widget.getMessage());
		if (this.getFocused() instanceof AbstractWidget focusedWidget) {
			focusedWidget.updateNarration(output.nest());
		} else {
			output.add(NarratedElementType.USAGE, this.getEntryNarration());
		}
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.fill(this.getX(), this.getY() + this.getHeight() - 2, this.getRight(), this.getY() + this.getHeight(), CommonColors.DARK_GRAY);

		if (this.isHoveredOrFocused()) {
			graphics.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight() - 1, CommonColors.GRAY);
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}

		this.renderWidget(graphics, mouseX, mouseY, a);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		this.play();
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.isConfirmation()) {
			this.play();
		}
		return super.keyPressed(event);
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return this.children;
	}

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
	}

	protected void renderWidget(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		this.albumCoverWidget.setPosition(this.getX() + 4, this.getY() + (this.getHeight() - this.albumCoverWidget.getHeight()) / 2 - 1);
		this.albumCoverWidget.extractRenderState(graphics, mouseX, mouseY, a);

		LinearLayout layout = LinearLayout.vertical();
		layout.addChild(line1Widget, s -> s.paddingBottom(3));
		layout.addChild(line2Widget);

		layout.arrangeElements();
		layout.setPosition(this.albumCoverWidget.getX() + this.albumCoverWidget.getWidth() + 4, this.getY() + (this.getHeight() - layout.getHeight()) / 2);
		layout.visitWidgets(child -> child.extractRenderState(graphics, mouseX, mouseY, a));

//		ScreenRectangle rec = layout.getRectangle();
//		graphics.fill(rec.left(), rec.top(), rec.right(), rec.bottom(), CommonColors.RED);
	}

	// ? TODO: Narrator things
	protected abstract Component getEntryNarration();

	protected final void addChild(AbstractWidget child) {
		this.children.add(child);
	}

	protected final void removeChild(AbstractWidget child) {
		if (this.children.remove(child) && this.getFocused() == child) {
			this.setFocused(null);
		}
	}

	abstract boolean play();
}
