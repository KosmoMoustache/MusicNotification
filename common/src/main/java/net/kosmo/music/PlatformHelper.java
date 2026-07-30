package net.kosmo.music;

import net.kosmo.music.resource.TrackData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class PlatformHelper<T> {

	public Component getCompactNotificationMessage(TrackData trackData) {
		return Component.literal(trackData.title().getString() + " - " + trackData.author().getString());
	}

	public abstract Optional<T> getModInfo(String modId);

	public abstract @Nullable String getModName(Identifier location);
	public abstract @Nullable String getModName(String modId);

	public abstract boolean isModLoaded(String modId);

	public abstract void setCompactNotification(TrackData trackData, int time);

	public abstract String getDarkModeResourcePackId();
}

