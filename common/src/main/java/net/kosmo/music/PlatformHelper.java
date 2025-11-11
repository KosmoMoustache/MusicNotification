package net.kosmo.music;

import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.ResourceLocation;

public abstract class PlatformHelper {

	public abstract String getModName(ResourceLocation location);

	public abstract void setCompactNotification(TrackData trackData, int time);
}

