package net.kosmo.music.resource;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TrackDataManager {
	static TrackDataManager instance;
	public Map<ResourceLocation, TrackData> tracks = new HashMap<>();

	public static TrackDataManager getInstance() {
		if (instance == null) {
			instance = new TrackDataManager();
		}
		return instance;
	}

	public void setTracks(Map<ResourceLocation, TrackData> sounds) {
		this.tracks = sounds;
	}

	public TrackData getTrackData(ResourceLocation soundId) {
		@Nullable TrackData td = tracks.get(soundId);
		// TODO: Maybe if not found, try to find by converting record id to music disc id
		//            m = musics.get(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath().replace("records/", "music_disc.")));

		// TODO: Create method to generate unknown TrackData with soundId only
		if (td == null) {
			MusicNotificationClient.LOGGER.warn("TrackData not found for id: {}.", soundId);
			return new TrackData(
				soundId,
				"Unknown Title",
				"Unknown Artist",
				Optional.empty(),
				Optional.empty(),
				Optional.of(soundId));
		}

		return td;
	}
}