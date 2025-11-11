package net.kosmo.music.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.kosmo.music.Helper;
import net.kosmo.music.PlatformHelper;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicReference;

public class PlatformFabric extends PlatformHelper {
	@Override
	public String getModName(ResourceLocation location) {
		AtomicReference<String> namespace = new AtomicReference<>(location.getNamespace());
		FabricLoader.getInstance().getModContainer(namespace.get()).ifPresent(modContainer -> namespace.set(modContainer.getMetadata().getName()));
		return namespace.get();
	}

	public void setCompactNotification(TrackData trackData, int time) {
		GuiAccessor gui = (GuiAccessor) Minecraft.getInstance().gui;
		gui.musicNotification$setCompactNotificationMessage(Helper.getCompactNotificationMessage(trackData));
		gui.musicNotification$setCompactNotificationTime(60);
	}
}
