# Documentation

## JSON schema for `musics.json`

Default file: [musics.json](./common/src/main/resources/assets/musicnotification/musics.json)

The information shown in the toast notifications is taken from this file; jukebox entries are also pulled from it.

```json
{
	"key": {
		"album": "string",
		"author": "string",
		"cover": "string",
		"item": "identifier",
		"customId": "identifier",
		"title": "string"
	}
}
```

- `key`: The sound event identifier of the track
- `album`: The album name of the track
- `author`: The author of the track
- `cover`: (Optional) the filename of the cover image located in `musicnotification/textures/gui/sprites/toast/` of your
  resource pack
- `item`: (Optional) The item identifier whose sprite will be shown in the toast or Jukebox
- `customId`: (Optional) Custom identifier for the track. For the jukebox to work, you must provide a sound event for
  every track
- `title`: The title of the track

The `item` and `cover` fields correspond to the image shown in a toast notification and jukebox. If both are present,
`item` takes
precedence over `cover`.

If the provided item identifier is invalid, the texture for `minecraft:music_disc_13` will be used instead. If `cover`
is missing or invalid, a default image will be used.

### Examples & Test

#### dp_musicnotification_test

Register new jukebox songs (datapack)

#### rp_custom_jukebox_song

Resource pack to use with the datapack `dp_musicnotification_test`; adds the sounds for the jukebox songs

#### rp_custom_musics.json

Adds the information entries for the jukebox songs added by the datapack `dp_musicnotification_test`

#### rp_custom_musics.json_morediscs

Adds information for discs from the mod [More Music Discs](https://modrinth.com/mod/more-music-discs)
