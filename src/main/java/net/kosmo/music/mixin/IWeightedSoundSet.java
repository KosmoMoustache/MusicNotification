package net.kosmo.music.mixin;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.WeightedSoundSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(WeightedSoundSet.class)
public interface IWeightedSoundSet {

    @Accessor("sounds")
    List<SoundContainer<Sound>> getSounds();
}
