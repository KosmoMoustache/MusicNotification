package net.kosmo.music.impl.neoforge;

import net.kosmo.music.KeyBinding;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.PlatformHelper;
import net.kosmo.music.config.ClothScreenProvider;
import net.kosmo.music.resource.MusicResourceReloadListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(MusicNotificationClient.MOD_ID)
public class ClientMusicNeoForge {

	public ClientMusicNeoForge(IEventBus modEventBus, ModContainer modContainer) {
		PlatformHelper INSTANCE = new PlatformNeoForge();
		MusicNotificationClient.init(INSTANCE);

		// Register config screen
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (mc, parent) -> ClothScreenProvider.getConfigScreen(parent));

		modEventBus.addListener(this::registerBuiltinPacks);
		modEventBus.addListener(this::registerKeyMappings);
		modEventBus.addListener(this::addClientReloadListener);

		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, (event) -> {
			MusicNotificationClient.tick();
		});
	}

	void registerBuiltinPacks(AddPackFindersEvent event) {
		event.addPackFinders(ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "resourcepacks/dark_mode"), PackType.CLIENT_RESOURCES, Component.translatable("text.musicnotification.resourcepack.dark_mode.name"), PackSource.BUILT_IN, false, Pack.Position.TOP);
	}

	void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KeyBinding.getKeyMapping());
	}

	void addClientReloadListener(AddClientReloadListenersEvent event) {
		event.addListener(ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "json"), MusicResourceReloadListener.INSTANCE);
//		event.addListener(ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "client_reload_listener"), ReloadListener);
	}

//	static SimplePreparableReloadListener<Object> ReloadListener = new SimplePreparableReloadListener<>() {
//		@Override
//		protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
//			MusicNotificationClient.LOGGER.info("PREPARE");
//			return null;
//		}
//
//		@Override
//		protected void apply(Object o, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
//			MusicNotificationClient.LOGGER.info("APPLY");
//		}
//	};

}
