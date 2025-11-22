package net.kosmo.music.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MusicResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, TrackData>> {
	public static final MusicResourceReloadListener INSTANCE = new MusicResourceReloadListener();
	private final String MUSIC_PATH = "musics.json";

	public Map<ResourceLocation, TrackData> prepare(ResourceManager resourceManager) {
		Map<ResourceLocation, TrackData> map = new HashMap<>();
		for (Resource resource : resourceManager.getResourceStack(ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, MUSIC_PATH))) {
			try (BufferedReader bufferedReader = resource.openAsReader()) {
				JsonElement jsonElement = StrictJsonParser.parse(bufferedReader);
				map.putAll(TrackData.MAP_CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new));
			} catch (Exception exception) {
				MusicNotificationClient.LOGGER.error("Failed to parse song information {} in resource pack '{}'", MUSIC_PATH, resource.sourcePackId(), exception);
			}
		}
		return map;
	}

	public void apply(Map<ResourceLocation, TrackData> sounds, ResourceManager resourceManager) {
		TrackData.clearAlbumCache();
		TrackDataManager.getInstance().setTracks(sounds);
	}

	@Override
	protected Map<ResourceLocation, TrackData> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		return this.prepare(resourceManager);
	}

	@Override
	protected void apply(Map<ResourceLocation, TrackData> sounds, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.apply(sounds, resourceManager);
	}
}
