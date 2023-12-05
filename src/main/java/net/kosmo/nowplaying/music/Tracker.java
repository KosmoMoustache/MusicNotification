package net.kosmo.nowplaying.music;

import com.google.common.collect.Lists;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;

import java.util.List;

public class Tracker {
    private final CurrentPlaying now_playing = new CurrentPlaying();
//    private final History history = new History();

    public CurrentPlaying getNowPlaying() {
        return this.now_playing;
    }

//    public History getHistory() {
//        return this.history;
//    }

    public static class CurrentPlaying {
        SoundInstance sound;

        public boolean isPlaying() {
            return this.sound != null;
        }

        public SoundInstance getSound() {
            return this.sound;
        }

        public void setSound(SoundInstance soundInstance) {
            this.sound = soundInstance;
        }
    }

//    public static class History {
//        private final List<Identifier> list = Lists.newArrayList();
//        private final int LIMIT = 50;
//
//        public List<Identifier> getEntries() {
//            return this.list;
//        }
//
//        public void add(SoundInstance sound) {
//            if (this.isAboveLimit()) this.list.remove(0);
//            this.list.add(sound.getId());
//        }
//
//        public boolean isAboveLimit() {
//            return this.list.size() > LIMIT;
//        }
//
//    }
}
