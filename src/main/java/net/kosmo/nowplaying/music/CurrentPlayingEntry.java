package net.kosmo.nowplaying.music;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CurrentPlayingEntry {


    @Nullable private SoundInstance soundInstance;
    @Nullable private Old_MusicManager.Entry entry;

    public CurrentPlayingEntry(@Nullable SoundInstance soundInstance, @Nullable Old_MusicManager.Entry entry) {
        this.soundInstance = soundInstance;
        this.entry = entry;
    }

   public boolean isNull() {
        return soundInstance == null && entry == null;
    }

    public Identifier getIdentifier() {
        return entry == null ? null : entry.getIdentifier();
    }


    public @Nullable SoundInstance getSoundInstance() {
        return soundInstance;
    }
    public void setSoundInstance(SoundInstance soundInstance) {
        this.soundInstance = soundInstance;
    }


    public Old_MusicManager.@Nullable Entry getEntry() {
        return entry;
    }

}