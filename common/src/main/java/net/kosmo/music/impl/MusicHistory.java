package net.kosmo.music.impl;

import java.util.LinkedList;

public class MusicHistory {
    private final LinkedList<MusicManager.Music> history;

    public MusicHistory() {
        this.history = new LinkedList<>();
    }

    public void addMusic(MusicManager.Music music) {
        if (history.size() >= ClientMusic.config.MAX_COUNT_HISTORY) {
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
