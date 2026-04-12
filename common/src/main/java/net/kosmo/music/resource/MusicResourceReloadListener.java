package net.kosmo.music.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
//? if >1.21.1 {
import net.minecraft.util.StrictJsonParser;
//? } else {
/*import com.google.gson.JsonParser;
*///? }
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MusicResourceReloadListener extends SimplePreparableReloadListener<Map<Identifier, TrackData>> {
	public static final MusicResourceReloadListener INSTANCE = new MusicResourceReloadListener();
	private final String MUSIC_PATH = "musics.json";

	public Map<Identifier, TrackData> prepare(ResourceManager resourceManager) {
		Map<Identifier, TrackData> map = new HashMap<>();
		for (Resource resource : resourceManager.getResourceStack(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, MUSIC_PATH))) {
			try (BufferedReader bufferedReader = resource.openAsReader()) {
				//? if >1.21.1 {
				JsonElement jsonElement = StrictJsonParser.parse(bufferedReader);
				//? } else {
				/*JsonElement jsonElement = JsonParser.parseReader(bufferedReader);
				*///? }
				map.putAll(TrackData.MAP_CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new));
			} catch (Exception exception) {
				MusicNotificationClient.LOGGER.error("Failed to parse song information {} in resource pack '{}'", MUSIC_PATH, resource.sourcePackId(), exception);
			}
		}
		return map;
	}

	public void apply(Map<Identifier, TrackData> sounds, ResourceManager resourceManager) {
		TrackData.clearAlbumCache();
		TrackDataManager.getInstance().setTracks(sounds);
		Config.options().COMPUTED_IS_DARK_MODE_ENABLED = resourceManager.listPacks().anyMatch(resourcePack -> resourcePack.packId().equals(MusicNotificationClient.PLATFORM_HELPER.getDarkModeResourcePackId()));
	}

	@Override
	protected Map<Identifier, TrackData> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		return this.prepare(resourceManager);
	}

	@Override
	protected void apply(Map<Identifier, TrackData> sounds, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.apply(sounds, resourceManager);
	}
}
