package net.kosmo.music.gui;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.gui.component.PillButton;
import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.function.Consumer;

public class ExtendedFilter extends AbstractContainerWidget {
	private static final WidgetSprites LEFT_ARROW = new WidgetSprites(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/left_arrow"), Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/left_arrow_disabled"), Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/left_arrow_focused"));
	private static final WidgetSprites RIGHT_ARROW = new WidgetSprites(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/right_arrow"), Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/right_arrow_disabled"), Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/right_arrow_focused"));

	private final Font font;
	private final JukeboxScreen parent;
	private final Consumer<String> responder;

	private final ImageButton leftArrow;
	private final ImageButton rightArrow;
	private final List<AbstractWidget> children = new ArrayList<>();
	private final List<PillButton> pills = new ArrayList<>();

	private int pillIndex = -1;

	public ExtendedFilter(JukeboxScreen parent, Font font, int x, int y, int width, int height, Component narration, Consumer<String> responder) {
		super(x, y, width, height, narration/*? >=26.1 {*/,AbstractScrollArea.defaultSettings(1)/*?} else {*//*?}*/);
		this.parent = parent;
		this.font = font;
		this.responder = responder;

		this.leftArrow = new ImageButton(this.getX(), this.getY(), 15, 20, LEFT_ARROW, button -> {
			this.moveSelectedPillLeft();
		}, Component.translatable("gui.musicnotification.jukebox.filter.<left_arrow"));
		this.rightArrow = new ImageButton(this.getX() + (this.width - 15), this.getY(), 15, 20, RIGHT_ARROW, button -> {
			this.moveSelectedPillRight();
		}, Component.translatable("gui.musicnotification.jukebox.filter.right_arrow"));

		this.addChild(this.leftArrow);
		this.addChild(this.rightArrow);

		Set<String> set = TrackDataManager.getInstance().getNamespaceList();
		int n = set.size() - 1;
		String[] arr = new String[n];
		arr = set.toArray(arr);
		int x2 = this.getX() + 16;
		for (int i = 0; i <= n; i++) {
			String namespace = arr[i];
			if (set.size() == 1) {
				int curWidth = this.getWidth() /2-this.font.width(namespace);
				x2 += curWidth;
				this.leftArrow.active = false;
				this.rightArrow.active = false;
			} else {
				int curWidth = i > 0 ? this.font.width(arr[i - 1]) + 1 + (5 * 2) : 0;
				x2 += curWidth;
			}
			int finalI = i;
			PillButton pill = new PillButton(x2, this.getY(), this.font.width(namespace) + 5 * 2, 20, Component.literal(namespace), button -> {
				if (button.isFocused()) {
					responder.accept("");
					this.pills.forEach(p -> p.setFocused(false));
					this.pillIndex = -1;
				} else {
					this.pills.forEach(p -> p.setFocused(false));
					button.setFocused(true);
					this.pillIndex = finalI;

					if (this.parent.getFilter() != null || !(this.parent.getFilter() != null && this.parent.getFilter().equals(namespace))) {
						responder.accept(namespace);
					} else {
						responder.accept("");
					}
				}
			});
			this.addChild(pill);
			this.pills.add(pill);
		}
	}

	protected final void addChild(AbstractWidget child) {
		this.children.add(child);
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return this.children;
	}

	public void moveSelectedPillRight() {
		setSelectedPillIndex(this.pillIndex >= this.pills.size() - 1 ? 0 : this.pillIndex + 1);
	}

	public void moveSelectedPillLeft() {
		setSelectedPillIndex(this.pillIndex <= 0 ? this.pills.size() - 1 : this.pillIndex - 1);
	}

	public void setSelectedPillIndex(int index) {
		this.pillIndex = index;
		this.pills.forEach(pill -> pill.setFocused(false));
		this.pills.get(this.pillIndex).onPress(/*? >=1.21.10 {*/null/*?} else {*//*?}*/);
	}

	@Override
	//~ if >26 renderWidget -> extractWidgetRenderState
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		//~ if >=26 render -> extractRenderState
		this.children.forEach(widget -> widget.extractRenderState(graphics, mouseX, mouseY, delta));
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, this.createNarrationMessage());
	}

	//? if >=1.21.4 {
	@Override
	protected int contentHeight() {
		return 32;
	}

	@Override
	protected double scrollRate() {
		return 0;
	}
	//? }
}
