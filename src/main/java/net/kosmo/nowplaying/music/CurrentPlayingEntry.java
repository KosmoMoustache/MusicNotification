package net.kosmo.nowplaying.music;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CurrentPlayingEntry {


    @Nullable private SoundInstance soundInstance;
    @Nullable private MusicManager.Entry entry;

    public CurrentPlayingEntry(@Nullable SoundInstance soundInstance, @Nullable MusicManager.Entry entry) {
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


    public MusicManager.@Nullable Entry getEntry() {
        return entry;
    }

}