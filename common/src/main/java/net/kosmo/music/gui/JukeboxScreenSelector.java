package net.kosmo.music.gui;

import net.kosmo.music.gui.nouveau.JukeboxScreenNouveau;
import net.minecraft.client.gui.screens.Screen;

public class JukeboxScreenSelector {
	public static Screen create(Screen parent) {
		//? if >=26.2 {
		return new JukeboxScreenNouveau(parent);
		//? } else {
		/*return new JukeboxScreen(parent);
		*///? }

	}
}
