package net.kosmo.music.neoforge;

import net.kosmo.music.PlatformHelper;
import net.kosmo.music.neoforge.notification.GuiCompactLayer;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
//?} else {
/*import net.neoforged.fml.loading.LoadingModList;
 *///? }

public class PlatformNeoForge extends PlatformHelper {
	@Override
	public String getModName(ResourceLocation location) {
		String namespace = location.getNamespace();
		//? if >=1.21.9 {
		namespace = FMLLoader.getCurrent().getLoadingModList().getModFileById(namespace).toString();
		//?} else {
		/*namespace = LoadingModList.get().getModFileById(namespace).moduleName();
		 *///?}
		return namespace;
	}

	@Override
	public void setCompactNotification(TrackData trackData, int time) {
		GuiCompactLayer layer = GuiCompactLayer.getInstance();
		if (layer != null && trackData != null) {
			layer.setNotification(this.getCompactNotificationMessage(trackData), time);
		}
	}

	@Override
	public String getDarkModeResourcePackId() {
		return "mod/musicnotification:resourcepacks/dark_mode";
	}
}
