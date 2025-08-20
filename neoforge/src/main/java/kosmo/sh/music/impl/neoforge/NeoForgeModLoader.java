package kosmo.sh.music.impl.neoforge;

import kosmo.sh.music.impl.IModLoader;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.LoadingModList;

public class NeoForgeModLoader implements IModLoader {
    @Override
    public String getModName(ResourceLocation location) {
        String namespace = location.getNamespace();
        namespace = LoadingModList.get().getModFileById(namespace).moduleName();
        return namespace;
    }
}
