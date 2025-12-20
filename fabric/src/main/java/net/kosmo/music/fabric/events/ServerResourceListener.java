package net.kosmo.music.fabric.events;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ServerResourceListener implements SimpleResourceReloadListener<Object> {
	@Override
	public Identifier getFabricId() {
		return Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "server_resources");
	}

	@Override
	public CompletableFuture<Object> load(ResourceManager manager, Executor executor) {
		MusicNotificationClient.LOGGER.info("Server resource reload listener. load");
		return CompletableFuture.supplyAsync(Object::new, executor);

	}

	@Override
	public CompletableFuture<Void> apply(Object data, ResourceManager manager, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			MusicNotificationClient.LOGGER.info("Server resource reload listener. apply");
		}, executor);
	}
}