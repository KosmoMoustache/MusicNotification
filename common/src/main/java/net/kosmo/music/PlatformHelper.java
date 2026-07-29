package net.kosmo.music;

import net.kosmo.music.resource.TrackData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class PlatformHelper {

	public Component getCompactNotificationMessage(TrackData trackData) {
		return Component.literal(trackData.title().getString() + " - " + trackData.author().getString());
	}

	public abstract String getModName(Identifier location);

	public abstract boolean isModLoaded(String modId);

	public abstract void setCompactNotification(TrackData trackData, int time);

	public abstract String getDarkModeResourcePackId();
}

