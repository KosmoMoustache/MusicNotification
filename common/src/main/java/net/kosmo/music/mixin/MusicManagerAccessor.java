package net.kosmo.music.mixin;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicManager.class)
public interface MusicManagerAccessor {

	@Accessor
	void setCurrentMusic(SoundInstance soundInstance);

	@Accessor
	@Nullable
	SoundInstance getCurrentMusic();
}
