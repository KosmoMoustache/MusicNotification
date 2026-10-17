package net.kosmo.music.gui.v3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;


public class JukeboxTabButton extends TabButton {
	private final WidgetSprites sprites = new WidgetSprites(
		Identifier.withDefaultNamespace("friends/button"),
		Identifier.withDefaultNamespace("friends/button_disabled"),
		Identifier.withDefaultNamespace("friends/button_highlighted"),
		Identifier.withDefaultNamespace("friends/button_highlighted")
	);

	public JukeboxTabButton(TabManager tabManager, Tab tab, int width, int height) {
		super(tabManager, tab, width, height);
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED, this.sprites.get(this.isSelected(), this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height
		);
		Font font = Minecraft.getInstance().font;
		int underlineColor = this.active ? -1 : -6250336;
		if (this.isSelected()) {
			this.renderFocusUnderline(graphics, font, underlineColor);
		}

		this.renderLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
		this.handleCursor(graphics);
	}

	private void renderLabel(ActiveTextCollector output) {
		int left = this.getX() + 1;
		int top = this.getY() + (this.isSelected() ? 0 : 1);
		int right = this.getX() + this.getWidth() - 1;
		int bottom = this.getY() + this.getHeight();
		output.acceptScrollingWithDefaultCenter(this.getMessage(), left, right, top, bottom);
	}

	private void renderFocusUnderline(GuiGraphicsExtractor graphics, Font font, int color) {
		int width = Math.min(font.width(this.getMessage()), this.getWidth() - 4);
		int left = this.getX() + (this.getWidth() - width) / 2;
		int top = this.getY() + this.getHeight() - 2;
		graphics.fill(left, top, left + width, top + 1, color);
	}
}
