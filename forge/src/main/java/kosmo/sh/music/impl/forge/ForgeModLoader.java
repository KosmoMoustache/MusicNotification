package kosmo.sh.music.impl.forge;

import kosmo.sh.music.impl.IModLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.LoadingModList;

public class ForgeModLoader implements IModLoader {
    @Override
    public String getModName(ResourceLocation location) {
        String namespace = location.getNamespace();
        namespace = LoadingModList.get().getModFileById(namespace).moduleName();
        return namespace;
    }
}
