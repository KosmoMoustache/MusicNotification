//? if >=26.2 {
package net.kosmo.music.gui.nouveau.filter;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.gui.component.PillButton;
import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class NamespaceFilterWidget extends AbstractContainerWidget {
	private static final WidgetSprites LEFT_ARROW = new WidgetSprites(
		Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/left_arrow"),
		Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/left_arrow_disabled"),
		Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/left_arrow_focused")
	);
	private static final WidgetSprites RIGHT_ARROW = new WidgetSprites(
		Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/right_arrow"),
		Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/right_arrow_disabled"),
		Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/right_arrow_focused")
	);

	private static final int ARROW_WIDTH = 15;
	private static final int VISIBLE_NAMESPACES = 2;
	private static final int SLOT_SPACING = 2;

	private final Consumer<String> responder;
	private final ImageButton leftArrow;
	private final ImageButton rightArrow;
	private final PillButton[] pills = new PillButton[VISIBLE_NAMESPACES];
	private final List<AbstractWidget> children = new ArrayList<>();

	private List<String> namespaces = new ArrayList<>();
	private int selectedIndex = 0;
	private boolean filterActive = false;
	private boolean highlighted = false;
	private int windowStart = 0;

	public NamespaceFilterWidget(Minecraft minecraft, int width, int height, Consumer<String> responder) {
		super(0, 0, width, height, Component.empty());
		this.responder = responder;

		this.leftArrow = new ImageButton(0, 0, ARROW_WIDTH, height, LEFT_ARROW, button -> this.moveSelection(-1, 0), Component.translatable("gui.musicnotification.jukebox.filter.namespace_previous"));
		this.rightArrow = new ImageButton(0, 0, ARROW_WIDTH, height, RIGHT_ARROW, button -> this.moveSelection(1, 1), Component.translatable("gui.musicnotification.jukebox.filter.namespace_next"));

		for (int slot = 0; slot < VISIBLE_NAMESPACES; slot++) {
			this.pills[slot] = new PillButton(0, 0, 0, height, Component.empty(), this::onPillPressed);
			this.children.add(this.pills[slot]);
		}
		this.children.add(this.leftArrow);
		this.children.add(this.rightArrow);

		this.refresh(null);
	}

	@Override
	protected int contentHeight() {
		return this.height;
	}

	@Override
		//~ if >26 renderWidget -> extractWidgetRenderState
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		this.layoutSlots();
		for (AbstractWidget child : this.children) {
			//~ if >26 render -> extractRenderState
			child.extractRenderState(graphics, mouseX, mouseY, a);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return this.children;
	}

	private int wrap(int index) {
		int size = this.namespaces.size();
		return size == 0 ? 0 : Math.floorMod(index, size);
	}

	private void moveSelection(int delta, int slot) {
		this.selectedIndex = this.wrap(this.selectedIndex + delta);
		this.windowStart = this.wrap(this.selectedIndex - slot);
		this.highlighted = true;
		this.updateState();
	}

	private void onPillPressed(Button button) {
		for (int slot = 0; slot < VISIBLE_NAMESPACES; slot++) {
			if (this.pills[slot] == button) {
				if (slot >= this.namespaces.size()) {
					return;
				}
				int index = this.indexAt(slot);
				if (this.filterActive && this.selectedIndex == index) {
					this.filterActive = false;
					this.highlighted = false;
					this.responder.accept("");
				} else {
					this.selectedIndex = index;
					this.filterActive = true;
					this.highlighted = true;
					this.responder.accept(this.namespaces.get(index));
				}
				this.updateState();
				return;
			}
		}
	}

	private int indexAt(int slot) {
		return this.wrap(this.windowStart + slot);
	}

	private void updateState() {
		boolean cyclic = this.namespaces.size() > 1;
		this.leftArrow.active = cyclic;
		this.rightArrow.active = cyclic;

		for (int slot = 0; slot < VISIBLE_NAMESPACES; slot++) {
			PillButton pill = this.pills[slot];
			if (slot < this.namespaces.size()) {
				int index = this.indexAt(slot);
				pill.visible = true;
				pill.active = true;
				pill.setMessage(Component.literal(this.namespaces.get(index)));
				pill.setSelected(this.highlighted && index == this.selectedIndex);
			} else {
				pill.visible = false;
			}
		}
		this.layoutSlots();
	}

	private void layoutSlots() {
		int contentLeft = this.getX() + ARROW_WIDTH + 1;
		int contentRight = this.getRight() - ARROW_WIDTH - 1;
		int contentWidth = Math.max(0, contentRight - contentLeft);
		int slotWidth = contentWidth / VISIBLE_NAMESPACES;

		this.leftArrow.setPosition(this.getX(), this.getY());
		this.rightArrow.setPosition(this.getRight() - ARROW_WIDTH, this.getY());

		for (int slot = 0; slot < VISIBLE_NAMESPACES; slot++) {
			PillButton pill = this.pills[slot];
			pill.setWidth(Math.max(0, slotWidth - SLOT_SPACING));
			pill.setHeight(this.getHeight());
			pill.setPosition(contentLeft + slot * slotWidth, this.getY());
		}
	}

	/**
	 * Reloads the namespace list and syncs the highlighted pill with the active namespace filter.
	 *
	 * @param activeNamespace namespace currently applied to the TrackFilter, or null if none
	 */
	public void refresh(@Nullable String activeNamespace) {
		this.namespaces = new ArrayList<>(TrackDataManager.getInstance().getNamespaceList());
		Collections.sort(this.namespaces);

		int activeIndex = activeNamespace == null ? -1 : this.namespaces.indexOf(activeNamespace);
		if (activeIndex >= 0) {
			this.selectedIndex = activeIndex;
			this.windowStart = activeIndex;
			this.filterActive = true;
			this.highlighted = true;
		} else {
			this.selectedIndex = this.wrap(this.selectedIndex);
			this.windowStart = this.selectedIndex;
			this.filterActive = false;
			this.highlighted = false;
		}
		this.updateState();
	}

	public String getSelectedNamespace() {
		return this.filterActive ? this.namespaces.get(this.selectedIndex) : "";
	}
}
//? }
