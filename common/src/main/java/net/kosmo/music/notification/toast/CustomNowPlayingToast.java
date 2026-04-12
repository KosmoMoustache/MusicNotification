//? if >= 1.21.6 {
package net.kosmo.music.notification.toast;

import net.kosmo.music.resource.TrackData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;

public class CustomNowPlayingToast extends NowPlayingToast {
	private static TrackData content;
	private static String currentSong;

	public CustomNowPlayingToast(TrackData td) {
		super();
		content = td;
	}

	public static void renderToast(GuiGraphicsExtractor guiGraphics, Font font) {
		//~ if >26 renderToast -> extractToast
		NowPlayingToast.extractToast(guiGraphics, font);
	}

	public static void tickMusicNotes() {
		currentSong = content.author() + " - " + content.title();
		long l = System.currentTimeMillis();
//		if (l > lastMusicNoteColorChange + 25L) {
//			musicNoteColorTick++;
//			lastMusicNoteColorChange = l;
//			musicNoteColor = ColorLerper.getLerpedColor(ColorLerper.Type.MUSIC_NOTE, musicNoteColorTick);
//		}
	}
}
//? }