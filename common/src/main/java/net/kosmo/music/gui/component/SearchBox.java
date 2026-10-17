package net.kosmo.music.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class SearchBox {
	private static final Component SEARCH_HINT = Component.translatable("gui.musicnotification.jukebox.search_hint");
	private static final Identifier SEARCH_ICON = Identifier.fromNamespaceAndPath("minecraft", "icon/search");

	private final LinearLayout layout;
	private final EditBox editBox;

	public SearchBox(Minecraft minecraft, int width, int height, Consumer<String> responder) {
		this(minecraft, width, height, 12, 12, responder);
	}

	public SearchBox(Minecraft minecraft, int width, int height, int iconWidth, int iconHeight, Consumer<String> responder) {
		this.editBox = new EditBox(minecraft.font, width - iconWidth - 3 - 3, height, SEARCH_HINT);
		this.editBox.setHint(SEARCH_HINT);
		this.editBox.setResponder(responder);
		ImageWidget icon = ImageWidget.sprite(iconWidth, iconHeight, SEARCH_ICON);

		this.layout = LinearLayout.horizontal();
		layout.defaultCellSetting().alignVerticallyMiddle();
		layout.addChild(icon);
		layout.addChild(this.editBox, s -> s.paddingLeft(3).paddingRight(3));
		layout.arrangeElements();
	}

	public LinearLayout layout() {
		return this.layout;
	}

	public EditBox editBox() {
		return this.editBox;
	}

	public int contentHeight() {
		return this.layout.getHeight();
	} // = 20 + 3 + 3 = 26
}
