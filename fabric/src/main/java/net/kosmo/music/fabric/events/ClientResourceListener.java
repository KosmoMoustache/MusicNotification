package net.kosmo.music.fabric.events;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.resource.MusicResourceReloadListener;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientResourceListener implements SimpleResourceReloadListener<Map<ResourceLocation, TrackData>> {
	@Override
	public ResourceLocation getFabricId() {
		return ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "client_resources");
	}

	@Override
	public CompletableFuture<Map<ResourceLocation, TrackData>> load(ResourceManager manager, Executor executor) {

		return CompletableFuture.supplyAsync(() -> MusicResourceReloadListener.INSTANCE.prepare(manager), executor);
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, TrackData> data, ResourceManager manager, Executor executor) {
		return CompletableFuture.runAsync(() -> MusicResourceReloadListener.INSTANCE.apply(data, manager), executor);
	}
}
