package net.kosmo.music;

import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;

import java.util.LinkedList;

public class TrackHistory {
	static TrackHistory instance;
	private final LinkedList<TrackData> history;

	public TrackHistory() {
		this.history = new LinkedList<>();
		instance = this;
	}

	public static TrackHistory getInstance() {
		if (instance == null) {
			instance = new TrackHistory();
		}
		return instance;
	}

	public void addTrack(TrackData track) {
		if (history.size() >= Config.options().MAX_COUNT_HISTORY) {
			history.removeFirst();
		}
		history.addFirst(track);
	}

	public void clear() {
		history.clear();
	}

	public LinkedList<TrackData> getHistory() {
		return history;
	}
}
