package net.kosmo.music.utils;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.MusicManager;

import java.util.LinkedList;

public class MusicHistory {
    private final LinkedList<MusicManager.Music> history;

    public MusicHistory() {
        this.history = new LinkedList<>();
    }

    public void addMusic(MusicManager.Music music) {
        if (history.size() >= ClientMusic.config.JUKEBOX_CONFIG.MAX_COUNT_HISTORY) {
            history.removeFirst();
        }
        history.addFirst(music);
    }

    public void clear() {
        history.clear();
    }

    public LinkedList<MusicManager.Music> getHistory() {
        return history;
    }
}
