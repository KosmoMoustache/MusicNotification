# Music Notification

Add toast notification when music is played in game with the name, author and soundtrack of it.

# 📷 In game screenshots
<div>
    <table>
        <tr>
            <td align="middle">
                <img alt="" src="https://i.imgur.com/JEg89Cg.png"/>
                <figcaption align="middle">From a music disc with "Show soundtrack name": true</figcaption>
            </td>
        </tr>
        <tr>
            <td align="middle">
                <img alt="" src="https://i.imgur.com/GXg4KcP.png"/>
                <figcaption align="middle">Gameplay music with "Show soundtrack name": false</figcaption>
            </td>
        </tr>
        <tr>
            <td align="middle">
                <img alt="" src="https://i.imgur.com/OzVgmXq.png"/>
            </td>
        </tr>
    </table>
</div>

# ⚙ Settings
<img alt="" src="https://i.imgur.com/6HwTxNk.png">


# Video Preview
https://youtu.be/HK2swFtPanA

# Customization
You can customize the music name, author and soundtrack by creating a file called `musics.json` under `./assets/musicnotification/musics.json` of your ressource pack.

See [musics.json](https://github.com/KosmoMoustache/MusicNotification/blob/main/src/main/resources/assets/musicnotification/musics.json) for the default file.
To add a new music, add a new object in the json file with the following format:
```json
{
  "sound identifier": {
    "name": "Music name",
    "author": "Music author",
    "soundtrack": "Soundtrack name",
    "cover": "File name without extension",
    "isRandom": "true/false", // Not currently used
    "identifier": "Identifier to play this sound" // Not currently used
  }
}
```
The cover image need to be placed under the `./assets/musicnotification/textures/gui/sprites/toast/` folder of your ressource pack.
if you have any question feel free to fill an issue.

# Compatibility issues
- Better Recipe Mod
  - You will not be able to configure the toast sound mute setting

# Contributor
Discs icon are made by [YaCCBoy](https://github.com/YaCCBoy)