package net.kosmo.music.impl.neoforge;

import net.kosmo.music.PlatformHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.LoadingModList;

public class PlatformNeoForge extends PlatformHelper {
	@Override
	public String getModName(ResourceLocation location) {
		String namespace = location.getNamespace();
		namespace = LoadingModList.get().getModFileById(namespace).moduleName();
		return namespace;
	}
}
