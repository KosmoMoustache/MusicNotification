package net.kosmo.music.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class MusicResourceReloadListener {
	public static final MusicResourceReloadListener INSTANCE = new MusicResourceReloadListener();
	private final String MUSIC_PATH = "musics.json";

	public Map<ResourceLocation, TrackData> prepare(ResourceManager resourceManager, Executor executor) {
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

	public void apply(Map<ResourceLocation, TrackData> sounds, ResourceManager resourceManager, Executor executor) {
		TrackDataManager.getInstance().setTracks(sounds);
	}
}
