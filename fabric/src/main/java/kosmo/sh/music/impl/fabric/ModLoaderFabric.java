package kosmo.sh.music.impl.fabric;

import net.fabricmc.loader.api.FabricLoader;
import kosmo.sh.music.impl.IModLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicReference;

public class ModLoaderFabric implements IModLoader {
    @Override
    public String getModName(ResourceLocation location) {
        AtomicReference<String> namespace = new AtomicReference<>(location.getNamespace());
        FabricLoader.getInstance().getModContainer(namespace.get()).ifPresent(modContainer -> namespace.set(modContainer.getMetadata().getName()));
        return namespace.get();
    }
}
