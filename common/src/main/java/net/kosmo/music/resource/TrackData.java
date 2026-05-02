package net.kosmo.music.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public record TrackData(
	Identifier key,
	Component title,
	Component author,
	Optional<Component> album,
	Optional<String> cover,
	Optional<Identifier> item,
	Optional<Identifier> customId
) {

	public static final Codec<Component> COMPONENT_CODEC = Codec.STRING.xmap(Component::literal, Component::getString);

	public static final Codec<TrackData> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("key").forGetter(t -> Optional.ofNullable(t.key)),
			COMPONENT_CODEC.fieldOf("title").forGetter(TrackData::title),
			COMPONENT_CODEC.fieldOf("author").forGetter(TrackData::author),
			COMPONENT_CODEC.optionalFieldOf("album").forGetter(TrackData::album),
			Codec.STRING.optionalFieldOf("cover").forGetter(TrackData::cover),
			Identifier.CODEC.optionalFieldOf("item").forGetter(TrackData::item),
			Identifier.CODEC.optionalFieldOf("customId").forGetter(TrackData::customId))
		.apply(i, (keyOpt, title, author, album, cover, item, customId) -> new TrackData(keyOpt.orElse(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "generic")), title, author, album, cover, item, customId)));

	// A map codec that stores a map<Identifier, TrackData> but injects the map key into each TrackData.key
	public static final Codec<Map<Identifier, TrackData>> MAP_CODEC = ExtraCodecs.strictUnboundedMap(Identifier.CODEC, CODEC)
		.xmap(TrackData::injectKeysFromMap, TrackData::ensureValuesHaveKeys);
	private static final Map<Identifier, AlbumCover> ALBUM_CACHE = new ConcurrentHashMap<>();

	private static Map<Identifier, TrackData> injectKeysFromMap(Map<Identifier, TrackData> input) {
		Map<Identifier, TrackData> out = new LinkedHashMap<>();
		for (Map.Entry<Identifier, TrackData> e : input.entrySet()) {
			Identifier mapKey = e.getKey();
			TrackData value = e.getValue();
			if (!value.key().equals(mapKey)) {
				value = new TrackData(mapKey, value.title(), value.author(), value.album(), value.cover(), value.item(), value.customId());
			}
			out.put(mapKey, value);
		}
		return out;
	}

	private static Map<Identifier, TrackData> ensureValuesHaveKeys(Map<Identifier, TrackData> input) {
		return injectKeysFromMap(input);
	}

	public static void clearAlbumCache() {
		MusicNotificationClient.LOGGER.info("Clearing album cover cache");
		ALBUM_CACHE.clear();
	}

	public Identifier getResolvedId() {
		return this.customId().isPresent() ? this.customId().get() : this.key();
	}

	public AlbumCover getAlbumCover() {
		return ALBUM_CACHE.computeIfAbsent(this.key, k -> {
				if (this.item.isPresent()) {
					return AlbumCover.fromItem(this.item.get());
				} else {
					return AlbumCover.fromSprite(k.getNamespace(), this.cover.orElse(null));
				}
			}
		);
	}
}