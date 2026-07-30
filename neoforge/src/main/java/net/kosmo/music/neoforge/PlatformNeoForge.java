package net.kosmo.music.neoforge;

import net.kosmo.music.PlatformHelper;
import net.kosmo.music.neoforge.notification.GuiCompactLayer;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
//? if >=1.21.10 {
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
//?} else {
/*import net.neoforged.fml.loading.LoadingModList;
 *///?}

public class PlatformNeoForge extends PlatformHelper<ModFileInfo> {
	@Override
	public Optional<ModFileInfo> getModInfo(String modId) {
		//? if >=1.21.10 {
		return Optional.of(FMLLoader.getCurrent().getLoadingModList().getModFileById(modId));
		//?} else {
		/*return Optional.of(LoadingModList.get().getModFileById(modId));
		 *///?}
	}

	@Override
	public String getModName(Identifier location) {
		return getModName(location.getNamespace());
	}

	@Override
	public @Nullable String getModName(String modId) {
		return getModInfo(modId).map(ModFileInfo::toString).orElse(null);
	}

	@Override
	public boolean isModLoaded(String modId) {
		return getModInfo(modId).isPresent();
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
