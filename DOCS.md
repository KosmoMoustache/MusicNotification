# JSON Schema
### musics.json
Handle what information is shown in the toast notification
```json
{
    "key": {
      "author": "string",
      "cover": "string",
      "album": "string",
      "title": "string"
    }  
}
```
- key: the sound event identifier of the track
- Author: the author of the track
- Cover: the identifier of the cover image found in ./assets/musicnotification/textures/gui/sprites/toast/)
- Album: the album name of the track
- Title: the title of the track

### random.json
Random sound event (related to GUI jukebox feature)
```json
{
    "key": {
      "identifier": "string",
      "title": "string",
      "cover": "string | null"
    }  
}
```
- key: identifer
- identifier: identifier of the sound event
- cover: the identifier of the cover image found in ./assets/musicnotification/textures/gui/sprites/toast/
- Title: the title of the track