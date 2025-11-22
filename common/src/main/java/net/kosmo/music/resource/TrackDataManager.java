package net.kosmo.music.resource;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

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

	public TrackData getTrackData(ResourceLocation soundId, ResourceLocation soundEventId) {
		@Nullable TrackData td = tracks.get(soundId);

		if (td != null) {
			return td;
		}
		
		if (Minecraft.getInstance().level != null) {
			MusicNotificationClient.LOGGER.warn("no TrackData found for id: {} sound event: {}), trying with JukeboxSong registry", soundId, soundEventId);
			try {
				Registry<JukeboxSong> registryLookup = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG);

				Optional<JukeboxSong> songOpt = registryLookup.stream()
					.filter(e -> e.soundEvent().value().location().equals(soundEventId))
					.findFirst();
				if (songOpt.isPresent()) {
					return getTrackDataFromJukeboxSong(soundId, songOpt.get());
				}
			} catch (IllegalStateException ignored) {
			}
		} else {
			MusicNotificationClient.LOGGER.warn("no TrackData found for id: {} sound event: {}", soundId, soundEventId);
		}

		return new TrackData(
			soundId,
			Component.literal(soundId.toString()),
			Component.literal("Unknown Artist"),
			Optional.empty(),
			Optional.empty(),
			Optional.of(soundId)
		);
	}

	public TrackData getTrackDataFromJukeboxSong(ResourceLocation soundId, JukeboxSong song) {
		String title;
		String author;
		// string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep;
		String[] split = song.description().getString().split(" - ");
		try {
			title = split[0];
			author = split[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			title = song.description().getString();
			author = "Unknown";
		}
		return new TrackData(
			soundId,
			Component.literal(title),
			Component.literal(author),
			Optional.empty(),
			Optional.empty(),
			Optional.of(soundId)
		);
	}
}