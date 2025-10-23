package net.kosmo.music;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

import java.util.List;
import java.util.regex.Pattern;

public class Helper {
	/**
	 * Return false if either MASTER or MUSIC volume is set to 0
	 */
	public static boolean isVolumeZero() {
		if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) == 0f)
			return false;
		if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER) == 0f)
			return false;
		return true;
	}

	public static boolean isMatchedInList(String input, List<String> patterns) {
		for (String pattern : patterns) {
			// Replace * with .* to create a regex pattern
			String regex = pattern.replace(".", "\\.").replace("*", ".*");
			if (Pattern.matches(regex, input)) {
				return true;
			}
		}
		return false;
	}
}
