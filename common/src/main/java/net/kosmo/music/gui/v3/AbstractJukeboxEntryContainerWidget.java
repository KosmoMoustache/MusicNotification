package net.kosmo.music.gui.v3;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJukeboxEntryContainerWidget extends AbstractContainerWidget {
	protected final Minecraft minecraft;
	protected final JukeboxScreenV3 screen;
	protected final ScrollableLayout scrollableLayout;
	protected final TrackData trackData;
	private final List<AbstractWidget> children = new ArrayList<>();

	protected StringWidget line1Widget;
	protected StringWidget line2Widget;
	protected LinearLayout layout;

	public AbstractJukeboxEntryContainerWidget(Minecraft minecraft, JukeboxScreenV3 screen, ScrollableLayout scrollableLayout, int x, int y, int width, int height, TrackData trackData, boolean animated) {
		super(x, y, width, height, Component.empty());
		this.minecraft = minecraft;
		this.screen = screen;
		this.scrollableLayout = scrollableLayout;
		this.trackData = trackData;

		this.layout = LinearLayout.vertical();
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
		if (this.getY() + this.getHeight() < this.scrollableLayout.getY()
			|| this.getY() > this.scrollableLayout.getY() + this.scrollableLayout.getHeight()) {
			return;
		}

		graphics.fill(this.getX(), this.getY() + this.getHeight() - 2, this.getRight(), this.getY() + this.getHeight(), CommonColors.DARK_GRAY);

		if (this.isHoveredOrFocused()) {
			graphics.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight() - 1, CommonColors.GRAY);
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}

//		ScreenRectangle rec = layout.getRectangle();
//		graphics.fill(rec.left(), rec.top(), rec.right(), rec.bottom(), CommonColors.RED);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		this.primaryAction();
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.isConfirmation()) {
			this.primaryAction();
		}
		return super.keyPressed(event);
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return this.children;
	}

	protected void initWidgets(int width, boolean animated) {
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

	abstract boolean primaryAction();
}
