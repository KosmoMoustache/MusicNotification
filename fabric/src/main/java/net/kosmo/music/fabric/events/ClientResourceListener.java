package net.kosmo.music.fabric.events;

//? if >26 {
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener;
import org.jspecify.annotations.NonNull;
//? } else {
/*import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
*///? }
import net.kosmo.music.resource.MusicResourceReloadListener;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.Identifier;

import java.util.Map;

//? if >26 {
public class ClientResourceListener extends SimpleReloadListener<Map<Identifier, TrackData>> {
	@Override
	protected @NonNull Map<Identifier, TrackData> prepare(SharedState state) {
		return MusicResourceReloadListener.INSTANCE.prepare(state.resourceManager());
	}

	@Override
	protected void apply(@NonNull Map<Identifier, TrackData> prepared, SharedState state) {
		MusicResourceReloadListener.INSTANCE.apply(prepared, state.resourceManager());
	}
}
//? } else {
	/*public class ClientResourceListener implements SimpleResourceReloadListener<Map<Identifier, TrackData>> {
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
*///? }