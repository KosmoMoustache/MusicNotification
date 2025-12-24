package net.kosmo.music;

import net.kosmo.music.mixin.MusicManagerAccessor;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public class Helper {
	/**
	 * Return true if either MASTER or MUSIC volume is set to 0
	 */
	public static boolean isVolumeZero() {
		if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) == 0f)
			return true;
		if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER) == 0f)
			return true;
		return false;
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

	public static @Nullable SoundEvent getSoundEvent(Minecraft client, ResourceLocation location) {
		return getSoundEvent(client.getSoundManager(), location);
	}

	public static @Nullable SoundEvent getSoundEvent(SoundManager soundManager, ResourceLocation location) {
		if (soundManager.getSoundEvent(location) != null) {
			return SoundEvent.createVariableRangeEvent(location);
		} else {
			return null;
		}
	}

	public static void playTrackData(Minecraft client, TrackData td) {
		SoundEvent soundEvent = getSoundEvent(client, td.getResolvedId());
		if (soundEvent == null) {
			MusicNotificationClient.LOGGER.warn("Unable to play unknown sound with id: {}", td.getResolvedId());
			return;
		}

		playTrackData(client, soundEvent);
	}

	public static void playTrackData(Minecraft client, SoundEvent soundEvent) {
		client.getSoundManager().stop(null, SoundSource.MUSIC);
		SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent/*? >=1.21.11 {*/ /*?} else {*/, 10 /*?}*/);
		MusicManagerAccessor musicManagerAccessor = (MusicManagerAccessor) client.getMusicManager();
		client.getSoundManager().play(soundInstance);
		musicManagerAccessor.setCurrentMusic(soundInstance);
		MusicNotificationClient.currentlyPlaying = soundInstance;
	}
}
