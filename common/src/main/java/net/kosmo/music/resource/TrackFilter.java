package net.kosmo.music.resource;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Immutable track filter. Criteria are combined with AND:
 * namespace, then albums (OR between selected albums), then the search query.
 * Every "with..." / "add..." / "remove..." / "clear..." method returns a new instance.
 */
public final class TrackFilter implements Predicate<TrackData> {
	private static final TrackFilter EMPTY = new TrackFilter("", null, Set.of());

	private final String query;
	private final List<String> terms;
	private final @Nullable String namespace;
	private final Set<String> albums;

	private TrackFilter(String query, @Nullable String namespace, Set<String> albums) {
		this.query = query;
		this.terms = query.isBlank() ? List.of() : Arrays.asList(query.toLowerCase(Locale.ROOT).trim().split("\\s+"));
		this.namespace = namespace;
		this.albums = albums;
	}

	public static TrackFilter empty() {
		return EMPTY;
	}

	public String query() {
		return this.query;
	}

	public @Nullable String namespace() {
		return this.namespace;
	}

	public Set<String> albums() {
		return this.albums;
	}

	public boolean isAlbumSelected(String album) {
		return this.albums.contains(album);
	}

	public TrackFilter withQuery(@Nullable String query) {
		return new TrackFilter(query == null ? "" : query, this.namespace, this.albums);
	}

	public TrackFilter withNamespace(@Nullable String namespace) {
		String normalized = namespace == null || namespace.isEmpty() ? null : namespace;
		return new TrackFilter(this.query, normalized, this.albums);
	}

	public TrackFilter addAlbum(String album) {
		if (this.albums.contains(album)) return this;
		Set<String> next = new HashSet<>(this.albums);
		next.add(album);
		return new TrackFilter(this.query, this.namespace, Set.copyOf(next));
	}

	public TrackFilter removeAlbum(String album) {
		if (!this.albums.contains(album)) return this;
		Set<String> next = new HashSet<>(this.albums);
		next.remove(album);
		return new TrackFilter(this.query, this.namespace, Set.copyOf(next));
	}

	public TrackFilter clearAlbums() {
		return this.albums.isEmpty() ? this : new TrackFilter(this.query, this.namespace, Set.of());
	}

	@Override
	public boolean test(TrackData track) {
		if (this.namespace != null && !this.namespace.equalsIgnoreCase(track.key().getNamespace())) {
			return false;
		}
		if (!this.albums.isEmpty()) {
			String trackAlbum = track.album().map(Component::getString).orElse("");
			if (this.albums.stream().noneMatch(album -> contains(trackAlbum, album))) {
				return false;
			}
		}
		if (this.terms.isEmpty()) {
			return true;
		}
		String[] haystack = searchStrings(track);
		return this.terms.stream().allMatch(term -> Arrays.stream(haystack).anyMatch(field -> field.contains(term)));
	}

	public boolean test(Identifier id) {
		String[] haystack = searchStrings(TrackDataManager.getEmpty(id, id.toString(), id.getNamespace()));
		return this.terms.stream().allMatch(term -> Arrays.stream(haystack).anyMatch(field -> field.contains(term)));
	}

	private static String[] searchStrings(TrackData track) {
		return new String[]{
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
}
