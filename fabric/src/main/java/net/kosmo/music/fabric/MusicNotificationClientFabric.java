package net.kosmo.music.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.kosmo.music.KeyBinding;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.PlatformHelper;
import net.kosmo.music.fabric.command.JukeboxSongCommandFabric;
import net.kosmo.music.fabric.events.ClientResourceListener;
import net.kosmo.music.fabric.events.ServerResourceListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ClientResourceListener());
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ServerResourceListener());
	}

	public void registerBuiltinPacks() {
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
			ResourceManagerHelper.registerBuiltinResourcePack(
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "dark_mode"),
				modContainer,
				Component.translatable("text.musicnotification.resourcepack.dark_mode.name"),
				ResourcePackActivationType.NORMAL);
		});
	}

	public void registerKeyMappings() {
		KeyBindingHelper.registerKeyBinding(KeyBinding.getKeyMapping());
	}
}
