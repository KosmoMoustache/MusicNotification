package net.kosmo.music.neoforge.notification;

import net.kosmo.music.notification.CompactNotification;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
//? if >1.21.1 {
import net.neoforged.neoforge.client.gui.GuiLayer;
//? } else {
/*import net.minecraft.client.gui.LayeredDraw;
*///? }
import org.jetbrains.annotations.NotNull;

//~ if >1.21.1 'LayeredDraw.Layer' -> 'GuiLayer'
public class GuiCompactLayer implements GuiLayer {
	private static GuiCompactLayer INSTANCE;

	private Component message;
	private int time;

	public GuiCompactLayer() {
		INSTANCE = this;
	}

	public static GuiCompactLayer getInstance() {
		return INSTANCE;
	}

	public void setNotification(Component message, int time) {
		this.message = message;
		this.time = time;
	}

	public void tick() {
		if (this.time > 0) {
			this.time--;
			if (this.time == 0) {
				this.message = null;
			}
		}
	}

	@Override
	public void render(@NotNull GuiGraphicsExtractor guiGraphics, @NotNull DeltaTracker deltaTracker) {
		CompactNotification.render(guiGraphics, deltaTracker, this.message, this.time);
	}
}
