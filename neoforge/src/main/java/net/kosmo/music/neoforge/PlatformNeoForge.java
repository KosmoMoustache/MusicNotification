package net.kosmo.music.neoforge;

import net.kosmo.music.Helper;
import net.kosmo.music.PlatformHelper;
import net.kosmo.music.neoforge.notification.GuiCompactLayer;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.LoadingModList;

public class PlatformNeoForge extends PlatformHelper {
	@Override
	public String getModName(ResourceLocation location) {
		String namespace = location.getNamespace();
		namespace = LoadingModList.get().getModFileById(namespace).moduleName();
		return namespace;
	}

	@Override
	public void setCompactNotification(TrackData trackData, int time) {
		GuiCompactLayer layer = GuiCompactLayer.getInstance();
		if (layer != null && trackData != null) {
			layer.setNotification(Helper.getCompactNotificationMessage(trackData), time);
		}
	}

}
