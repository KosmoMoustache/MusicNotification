package net.kosmo.music.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.kosmo.music.PlatformHelper;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlatformFabric extends PlatformHelper<ModContainer> {
	@Override
	public Optional<ModContainer> getModInfo(String modId) {
		return FabricLoader.getInstance().getModContainer(modId);
	}

	@Override
	public @Nullable String getModName(Identifier location) {
		return getModName(location.getNamespace());
	}

	@Override
	public @Nullable String getModName(String modId) {
		return getModInfo(modId).map(container -> container.getMetadata().getName()).orElse(null);
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public void setCompactNotification(TrackData trackData, int time) {
		GuiAccessor gui = (GuiAccessor) Minecraft.getInstance().gui;
		gui.musicNotification$setCompactNotificationMessage(this.getCompactNotificationMessage(trackData));
		gui.musicNotification$setCompactNotificationTime(time);
	}

	@Override
	public String getDarkModeResourcePackId() {
		return "musicnotification:dark_mode";
	}
}
