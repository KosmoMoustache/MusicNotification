# JSON Schema
### musics.json
Default file: [musics.json](https://github.com/KosmoMoustache/MusicNotification/blob/next/src/main/resources/assets/musicnotification/musics.json)

The information that will be shown in the toast notifications, the Jukebox's entries are also pulled from this file
```json
{
    "key": {
      "album": "string",
      "author": "string",
      "cover": "string",
      "coverTextureSlotX": "int",
      "coverTextureSlotY": "int",
      "customId": "Identifier",
      "title": "string"
    }  
}
```
- "key": the sound event identifier of the track
  - "author": The author of the track
  - "album": The album name of the track
  - "cover": (Optional) the coordinate of the texture atlas found in `./assets/musicnotification/textures/gui/sprites/toast/` or a key from the list below
  - "coverTextureSlotX" (Optional) the x coordinate in the texture atlas
  - "coverTextureSlotY" (Optional) the y coordinate in the texture atlas
  - "customId": (Optional) Custom identifier for the track. e.g. when the music if from an ambient music sound event, see [sounds.json](./src/main/resources/assets/musicnotification/sounds.json) and the vanilla sounds.json that can be found [here](https://mcasset.cloud/)  
  - "title": the title of the track


Cover id:
- generic
- modded
- alpha
- beta
- aquatic_update_ost
- nether_update_ost
- cave_and_cliffs_update_ost
- wild_update_ost
- trails_and_tales_ost
- tricky_trials_ost
- 5
- 13
- 11
- blocks
- cat
- chirp
- far
- mall
- mellohi
- otherside
- pigstep
- relic
- stal
- strad
- wait
- ward