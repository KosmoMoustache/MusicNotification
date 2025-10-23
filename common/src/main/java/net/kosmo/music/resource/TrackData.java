package net.kosmo.music.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public record TrackData(
	ResourceLocation key,
	String title,
	String author,
	Optional<String> album,
	Optional<String> cover,
	Optional<ResourceLocation> customId
) {

	public static final Codec<TrackData> CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.optionalFieldOf("key").forGetter(t -> Optional.ofNullable(t.key)),
			Codec.STRING.fieldOf("title").forGetter(TrackData::title),
			Codec.STRING.fieldOf("author").forGetter(TrackData::author),
			Codec.STRING.optionalFieldOf("album").forGetter(TrackData::album),
			Codec.STRING.optionalFieldOf("cover").forGetter(TrackData::cover),
			ResourceLocation.CODEC.optionalFieldOf("customId").forGetter(TrackData::customId))
		.apply(i, (keyOpt, title, author, album, cover, customId) -> new TrackData(keyOpt.orElse(ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "generic")), title, author, album, cover, customId)));

	// A map codec that stores a map<ResourceLocation, TrackData> but injects the map key into each TrackData.key
	public static final Codec<Map<ResourceLocation, TrackData>> MAP_CODEC = ExtraCodecs.strictUnboundedMap(ResourceLocation.CODEC, CODEC)
		.xmap(TrackData::injectKeysFromMap, TrackData::ensureValuesHaveKeys);

	private static Map<ResourceLocation, TrackData> injectKeysFromMap(Map<ResourceLocation, TrackData> input) {
		Map<ResourceLocation, TrackData> out = new LinkedHashMap<>();
		for (Map.Entry<ResourceLocation, TrackData> e : input.entrySet()) {
			ResourceLocation mapKey = e.getKey();
			TrackData value = e.getValue();
			if (!value.key().equals(mapKey)) {
				value = new TrackData(mapKey, value.title(), value.author(), value.album(), value.cover(), value.customId());
			}
			out.put(mapKey, value);
		}
		return out;
	}

	private static Map<ResourceLocation, TrackData> ensureValuesHaveKeys(Map<ResourceLocation, TrackData> input) {
		return injectKeysFromMap(input);
	}

	private static final Map<ResourceLocation, AlbumCover> ALBUM_CACHE = new ConcurrentHashMap<>();

	public AlbumCover getAlbumInfo() {
		return ALBUM_CACHE.computeIfAbsent(this.key, k -> new AlbumCover(k.getNamespace(), this.cover));
	}

}