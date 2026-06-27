//~ extract_contents
package net.kosmo.music.gui.component;

import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
//? >=1.21.2 && <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
 *///? } elif >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

public class PillButton extends Button {
	private static final WidgetSprites SPRITES = new WidgetSprites(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/namespace"), Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/namespace_focused"));

	public PillButton(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
		this.setMessage(message);
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		//? if >=1.21.4 {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
			SPRITES.get(this.isActive(), this.isHoveredOrFocused()),
			this.getX(),
			this.getY(),
			this.getWidth(),
			this.getHeight(),
			ARGB.white(this.alpha)
		);
		//? } else {
		/*graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
			SPRITES.get(this.isActive(), this.isHoveredOrFocused()),
			this.getX(),
			this.getY(),
			0,
			this.getWidth(),
			this.getHeight()
		);
		*///? }
		//? if >=1.21.11 {
		//~ if >=26 renderDefaultLabel -> extractDefaultLabel
		this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
		//? } else {
		/*this.renderString(graphics, Minecraft.getInstance().font, -1);
	    *///? }
	}
}
