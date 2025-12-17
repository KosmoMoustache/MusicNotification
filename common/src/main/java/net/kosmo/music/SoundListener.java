package net.kosmo.music;

import net.kosmo.music.config.Config;
import net.kosmo.music.notification.NotificationManager;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.kosmo.music.Helper.isMatchedInList;

public class SoundListener implements SoundEventListener {
	public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification/SoundListener");

	@Override
	public void onPlaySound(SoundInstance soundInstance, WeighedSoundEvents weighedSoundEvents, float f1) {
		if (soundInstance.getSource() != SoundSource.MUSIC && soundInstance.getSource() != SoundSource.RECORDS) {
			return;
		}

		ResourceLocation soundEventLocation = soundInstance.getLocation();
		ResourceLocation soundLocation = soundInstance.getSound().getLocation();

		// Ignore sound from the ignore list
		if (isMatchedInList(soundLocation.toString(), Config.options().IGNORE_SOUND_EVENT)) {
			LOGGER.info("Sound ignored: {}", soundLocation);
			return;
		}

		TrackData td = TrackDataManager.getInstance().getTrackData(soundLocation, soundEventLocation);
		NotificationManager.show(soundLocation, td);
		TrackHistory.getInstance().addTrack(td);
	}
}
