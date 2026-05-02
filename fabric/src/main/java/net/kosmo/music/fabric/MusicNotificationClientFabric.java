package net.kosmo.music.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//? if >26 {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
//? } else {
/*import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
*///? }
import net.fabricmc.loader.api.FabricLoader;
import net.kosmo.music.KeyBinding;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.PlatformHelper;
import net.kosmo.music.fabric.command.JukeboxSongCommandFabric;
import net.kosmo.music.fabric.events.ClientResourceListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import static net.kosmo.music.MusicNotificationClient.MOD_ID;

@Environment(EnvType.CLIENT)
public class MusicNotificationClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PlatformHelper INSTANCE = new PlatformFabric();
		MusicNotificationClient.init(INSTANCE);

		this.registerBuiltinPacks();
		this.registerKeyMappings();

		ClientTickEvents.END_CLIENT_TICK.register(MusicNotificationClient::tick);
		ClientCommandRegistrationCallback.EVENT.register(JukeboxSongCommandFabric::register);
		//? if >26 {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
			Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "client_resources"),
			new ClientResourceListener()
		);
		//? } else {
		/*ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ClientResourceListener());
		*///? }
	}

	public void registerBuiltinPacks() {
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
			//~ if >26 'ResourceManagerHelper.registerBuiltinResourcePack' -> 'ResourceLoader.registerBuiltinPack'
			ResourceLoader.registerBuiltinPack(
				Identifier.fromNamespaceAndPath(MOD_ID, "dark_mode"),
				container,
				Component.translatable("text.musicnotification" + ".resourcepack.dark_mode.name"),
				//~ if >26 'ResourcePackActivationType.NORMAL' -> 'PackActivationType.NORMAL'
				PackActivationType.NORMAL
			);
		});
	}

	public void registerKeyMappings() {
		KeyMappingHelper.registerKeyMapping(KeyBinding.getKeyMapping());
	}
}
