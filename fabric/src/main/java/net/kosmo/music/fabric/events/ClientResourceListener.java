package net.kosmo.music.fabric.events;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.resource.MusicResourceReloadListener;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientResourceListener implements SimpleResourceReloadListener<Map<Identifier, TrackData>> {
	@Override
	public Identifier getFabricId() {
		return Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "client_resources");
	}

	@Override
	public CompletableFuture<Map<Identifier, TrackData>> load(ResourceManager manager, Executor executor) {

		return CompletableFuture.supplyAsync(() -> MusicResourceReloadListener.INSTANCE.prepare(manager), executor);
	}

	@Override
	public CompletableFuture<Void> apply(Map<Identifier, TrackData> data, ResourceManager manager, Executor executor) {
		return CompletableFuture.runAsync(() -> MusicResourceReloadListener.INSTANCE.apply(data, manager), executor);
	}
}
