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
      "customId": "Identifier",
      "title": "string"
    }  
}
```
- "key": the sound event identifier of the track
  - "author": The author of the track
  - "album": The album name of the track
  - "cover": (Optional) the name of the cover image found in `./assets/musicnotification/textures/gui/sprites/toast/` from the default texture to your custom resource pack
  - "customId": (Optional) Custom identifier for the track. e.g. when the music if from an ambient music sound event, see [sounds.json](./src/main/resources/assets/musicnotification/sounds.json) and the vanilla sounds.json that can be found [here](https://mcasset.cloud/)  
  - "title": the title of the track