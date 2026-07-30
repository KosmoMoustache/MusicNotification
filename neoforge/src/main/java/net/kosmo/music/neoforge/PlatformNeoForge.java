package net.kosmo.music.neoforge;

import net.kosmo.music.PlatformHelper;
import net.kosmo.music.neoforge.notification.GuiCompactLayer;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.Identifier;
//~ if >=1.21.10 'LoadingModList' -> 'FMLLoader'
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class PlatformNeoForge extends PlatformHelper<IModInfo> {
	@Override
	public Optional<IModInfo> getModInfo(String modId) {
		try {
			//~ if >=1.21.10 'LoadingModList.get()' -> 'FMLLoader.getCurrent().getLoadingModList()'
			return Optional.of(FMLLoader.getCurrent().getLoadingModList().getModFileById(modId).getMods().getFirst());
		} catch (NullPointerException e) {
			return Optional.empty();
		}
	}

	@Override
	public String getModName(Identifier location) {
		return getModName(location.getNamespace());
	}

	@Override
	public @Nullable String getModName(String modId) {
		return getModInfo(modId).map(IModInfo::getDisplayName).orElse(null);
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
