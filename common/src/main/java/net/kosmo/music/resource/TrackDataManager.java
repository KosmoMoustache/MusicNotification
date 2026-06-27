package net.kosmo.music.resource;

//? if <=1.21.1 {
/*import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
*///? }
import org.jetbrains.annotations.Nullable;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.JukeboxSong;

import java.util.*;

public class TrackDataManager {
	static TrackDataManager instance;
	public Map<Identifier, TrackData> tracks = new HashMap<>();

	public static TrackDataManager getInstance() {
		if (instance == null) {
			instance = new TrackDataManager();
		}
		return instance;
	}

	public void setTracks(Map<Identifier, TrackData> sounds) {
		this.tracks = sounds;
	}

	public TrackData getTrackData(Identifier soundId, Identifier soundEventId) {
		@Nullable TrackData td = tracks.get(soundId);

		if (td != null) {
			return td;
		}

		if (Minecraft.getInstance().level != null) {
			MusicNotificationClient.LOGGER.warn("no TrackData found for id: {} sound event: {}), trying with JukeboxSong registry", soundId, soundEventId);
			try {
				//? if >1.21.1 {
				Registry<JukeboxSong> registryLookup = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG);
				Optional<JukeboxSong> songOpt = registryLookup.stream()
					.filter(e -> e.soundEvent().value().location().equals(soundEventId))
					.findFirst();
				if (songOpt.isPresent()) {
					return getTrackDataFromJukeboxSong(soundId, songOpt.get());
				}
				//? } else {
				/*Optional<Holder.Reference<JukeboxSong>> registryLookup = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.JUKEBOX_SONG).getHolder(soundEventId);
				if (registryLookup.isPresent()) {
					return getTrackDataFromJukeboxSong(soundId, registryLookup.get().value());
				}
				*///? }
			} catch (IllegalStateException ignored) {
			}
		} else {
			MusicNotificationClient.LOGGER.warn("no TrackData found for id: {} sound event: {}", soundId, soundEventId);
		}

		String end_path = soundId.toString().split("/")[soundId.toString().split("/").length - 1];
		return getEmpty(soundId, end_path, soundId.toString());
	}

	public TrackData getTrackDataFromJukeboxSong(Identifier soundId, JukeboxSong song) {
		String author;
		String title;
		// string[0] = author / string[1] = title | Now playing: Lena Raine - Pigstep;
		String[] split = song.description().getString().split(" - ");
		try {
			author = split[0];
			title = split[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			title = song.description().getString();
			author = "Unknown";
		}
		return getEmpty(soundId, title, author);
	}

	public Set<String> getNamespaceList() {
		Set<String> namespaces = new HashSet<>();
		tracks.forEach((i, t) -> {
			namespaces.add(i.getNamespace());
		});
		return namespaces;
	}

	private TrackData getEmpty(Identifier key, String title, String author) {
		return new TrackData(
			key,
			Component.literal(title),
			Component.literal(author),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of(key)
		);
	}
}