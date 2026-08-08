package net.kosmo.music.neoforge;

import net.kosmo.music.KeyBinding;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.PlatformHelper;
import net.kosmo.music.config.ClothScreenProvider;
import net.kosmo.music.neoforge.commands.JukeboxSongCommandNeoForge;
import net.kosmo.music.neoforge.notification.GuiCompactLayer;
import net.kosmo.music.resource.MusicResourceReloadListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.language.IModInfo;
//? if mixin_debug
//import org.spongepowered.asm.mixin.MixinEnvironment;

@Mod(value = MusicNotificationClient.MOD_ID, dist = Dist.CLIENT)
public class MusicNotificationClientNeoForge {
	public MusicNotificationClientNeoForge(IEventBus modEventBus) {
		//? if mixin_debug
		//MixinEnvironment.getCurrentEnvironment().audit();

		PlatformHelper<IModInfo> INSTANCE = new PlatformNeoForge();
		MusicNotificationClient.init(INSTANCE);

		// Register config screen
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (mc, parent) -> ClothScreenProvider.getConfigScreen(parent));

		modEventBus.addListener(this::registerBuiltinPacks);
		modEventBus.addListener(this::registerKeyMappings);
		modEventBus.addListener(this::addClientReloadListener);
		modEventBus.addListener(this::registerGuiCompactLayer);

		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, (event) -> {
			MusicNotificationClient.tick();
			GuiCompactLayer.getInstance().tick();
		});
	}

	void registerCommands(RegisterClientCommandsEvent event) {
		JukeboxSongCommandNeoForge.register(event.getDispatcher());
	}

	void registerBuiltinPacks(AddPackFindersEvent event) {
		event.addPackFinders(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "resourcepacks/dark_mode"), PackType.CLIENT_RESOURCES, Component.translatable("text.musicnotification.resourcepack.dark_mode.name"), PackSource.BUILT_IN, false, Pack.Position.TOP);
	}

	void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KeyBinding.getKeyMapping());
	}

	//? if >1.21.1 {
	void addClientReloadListener(AddClientReloadListenersEvent event) {
		event.addListener(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "json"), MusicResourceReloadListener.INSTANCE);
	}
	//? } else {
	/*void addClientReloadListener(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(MusicResourceReloadListener.INSTANCE);
	}
	*///? }

	void registerGuiCompactLayer(RegisterGuiLayersEvent event) {
		event.registerBelowAll(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "gui_notification_compact"), new GuiCompactLayer());
	}
}
