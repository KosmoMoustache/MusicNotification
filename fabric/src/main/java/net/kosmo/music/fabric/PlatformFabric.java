package net.kosmo.music.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.kosmo.music.PlatformHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicReference;

public class PlatformFabric extends PlatformHelper {
	@Override
	public String getModName(ResourceLocation location) {
		AtomicReference<String> namespace = new AtomicReference<>(location.getNamespace());
		FabricLoader.getInstance().getModContainer(namespace.get()).ifPresent(modContainer -> namespace.set(modContainer.getMetadata().getName()));
		return namespace.get();
	}
}
