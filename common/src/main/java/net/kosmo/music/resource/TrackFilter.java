package net.kosmo.music.resource;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;

public final class TrackFilter implements Predicate<TrackData> {
	private final @Nullable String namespace;
	private final @Nullable String album;
	private final @Nullable Integer trackNumber;
	private final @Nullable String title;
	private final @Nullable String author;

	private TrackFilter(@Nullable String namespace, @Nullable String album, @Nullable Integer trackNumber, @Nullable String title, @Nullable String author) {
		this.namespace = namespace;
		this.album = album;
		this.trackNumber = trackNumber;
		this.title = title;
		this.author = author;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Predicate<TrackData> fromQuery(String query) {
		if (query == null || query.isBlank()) {
			return track -> true;
		}
		String[] terms = query.toLowerCase(Locale.ROOT).trim().split("\\s+");
		return track -> {
			String[] haystack = searchStrings(track);
			return Arrays.stream(terms).allMatch(term -> Arrays.stream(haystack).anyMatch(field -> field.contains(term)));
		};
	}

	@Override
	public boolean test(TrackData track) {
		if (this.namespace != null && !this.namespace.equalsIgnoreCase(track.key().getNamespace())) {
			return false;
		}
		if (this.album != null && !contains(track.album().map(Component::getString).orElse(""), this.album)) {
			return false;
		}
		if (this.trackNumber != null && !this.trackNumber.equals(track.track().orElse(null))) {
			return false;
		}
		if (this.title != null && !contains(track.title().getString(), this.title)) {
			return false;
		}
		if (this.author != null && !contains(track.author().getString(), this.author)) {
			return false;
		}
		return true;
	}

	private static String[] searchStrings(TrackData track) {
		return new String[] {
			track.key().getNamespace().toLowerCase(Locale.ROOT),
			track.title().getString().toLowerCase(Locale.ROOT),
			track.author().getString().toLowerCase(Locale.ROOT),
			track.album().map(component -> component.getString().toLowerCase(Locale.ROOT)).orElse(""),
			track.track().map(String::valueOf).orElse("")
		};
	}

	private static boolean contains(String value, String query) {
		return value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
	}

	public static final class Builder {
		private @Nullable String namespace;
		private @Nullable String album;
		private @Nullable Integer trackNumber;
		private @Nullable String title;
		private @Nullable String author;
		private @Nullable String plainString;

		public Builder namespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder album(String album) {
			this.album = album;
			return this;
		}

		public Builder trackNumber(int trackNumber) {
			this.trackNumber = trackNumber;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder author(String author) {
			this.author = author;
			return this;
		}

		public Builder string(String string) {
			this.plainString = string;
			return this;
		}

		public TrackFilter build() {
			return new TrackFilter(this.namespace, this.album, this.trackNumber, this.title, this.author);
		}
	}
}
