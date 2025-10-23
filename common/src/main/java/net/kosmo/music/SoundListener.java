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

//		if (soundInstance.getSource() == SoundSource.RECORDS) {
//			RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
//			Optional<Registry<JukeboxSong>> jukeboxSongRegistry = registryAccess.lookup(Registries.JUKEBOX_SONG);
//			MusicNotificationClient.LOGGER.info("{}", jukeboxSongRegistry);
//			Set<Map.Entry<ResourceKey<JukeboxSong>, JukeboxSong>> a = jukeboxSongRegistry.get().;
//			//                for (Map.Entry<ResourceKey<JukeboxSong>, JukeboxSong> jukeboxSong : jukeboxSongRegistry.get().entrySet()) {
////                    Music m = Music.parseJukeboxSongRegistry(jukeboxSong.getValue());
////                    musics.put(m.customId, m);
////                }
//			TrackDataManager.getInstance().setRecords();
//		}

		ResourceLocation soundEventLocation = soundInstance.getLocation();
		ResourceLocation soundLocation = soundInstance.getSound().getLocation();

		// Ignore sound from the ignore list
		if (isMatchedInList(soundLocation.toString(), Config.options().IGNORE_SOUND_EVENT)) {
			LOGGER.info("Sound ignored: {}", soundLocation);
			return;
		}

		TrackData td = TrackDataManager.getInstance().getTrackData(soundLocation);
		NotificationManager.show(soundLocation, td);
		TrackHistory.getInstance().addTrack(td);

		// TODO: Legacy ! REMOVE IT
//        ResourceLocation location = soundInstance.getSound().getLocation();
//        Helper.ResourceLocationParser parsed = new Helper.ResourceLocationParser(location);
//        String namespace = MusicNotificationClient.PLATFORM_HELPER.getModName(location);
//

	}

//    @Deprecated
//    public static class Listeners {
//        @Deprecated
//        public static class SoundManagerSoundEventListener implements SoundEventListener {
//            public void onPlaySound(SoundInstance soundInstance, WeighedSoundEvents weighedSoundEvents, float f) {
//                MusicNotificationClient.LOGGER.error("DEPRECATED SoundManagerSoundEventListener called");
//
//
//                ResourceLocation identifier1 = soundInstance.getSound().getLocation();
//                ResourceLocation identifier2 = soundInstance.getLocation();
//
//                // Try 1 with identifier1
//                MusicManager.Music managerMusic = MusicNotificationClient.musicManager.get(identifier1);
//
//                // Try 2 with identifier2
//                if (managerMusic == null) {
//                    MusicNotificationClient.LOGGER.info("Music not found with customId: {} trying with {}", identifier1, identifier2);
//                    managerMusic = MusicNotificationClient.musicManager.get(identifier2);
//                }
//
//                // Try 3 create a dummy music entry
//                if (managerMusic == null) {
//                    MusicNotificationClient.LOGGER.info("Unknown music {}", identifier1);
//
//                    String namespace = MusicNotificationClient.PLATFORM_HELPER.getModName(identifier1);
//
//                    Helper.ResourceLocationParser parsed = new Helper.ResourceLocationParser(identifier1);
//                    managerMusic = new MusicManager.Music(
//                            identifier1,
//                            null,
//                            parsed.title,
//                            namespace,
//                            identifier1.toString(),
//                            AlbumInfo.dummy(), //AlbumCover.getDefaultCover(identifier1),
//                            false
//                    );
//                }
//
//                MusicToast toast = Minecraft.getInstance().getToastManager().getToast(MusicToast.class, MusicToast.Type.DEFAULT);
//                if (toast == null) {
//                    Minecraft.getInstance().getToastManager().addToast(new MusicToast(managerMusic));
//                } else {
//                    toast.setContent(managerMusic);
//                }
//
//                // Add to history
//                MusicNotificationClient.musicHistory.addMusic(managerMusic);
//            }
//        }
//    }
}
