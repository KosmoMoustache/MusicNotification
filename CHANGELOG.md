# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Fixed

### Deprecated

### Removed

### Changed

### Fixed

### Security

## [2.2.1] - 2024-10-23

- Support for 1.21.3 (compatible with 1.21.2)

## [2.2.0] - 2024-10-23

- Support for 1.21.2

## [2.1.0] - 2024-07-30

### Added

- Support for NeoForge

## [2.0.3] - 2024-07-05

### Added

- Add a new config option, "List of sounds events ignored", sound in this list will not display notification when
  played. (default to "minecraft:block.note_block.*", ignore every sound event for note blocks)

## [2.0.2] - 2024-06-13

### Added

- Back button in Jukebox Menu
- Dark Mode built-in resource pack thanks to [Knewest](https://github.com/Knewest)

## [2.0.1] - 2024-05-09

### Added

- New toast icon thanks to @carotteatomique #33

## [2.0.0] - 2024-05-06

### Added

- Simplified Chinese translation thanks to [Chiloven945](https://github.com/Chiloven945)
- New mod logo thanks to [akairoo](https://dribbble.com/akairoo)
- GUI to play musics on demands

### Changed

- Changed key 'soundtrack' to 'album' in musics.json (soundtrack key is still supported for compatibility)

## [1.6.1] - 2024-01-29

### Fixed

- Typo in path to Trails and tales album cover

## [1.6.0] - 2024-01-27

### Added

- Allow mods/resource pack to provide their own musics.json #21
- Album Cover are now retrieve from musics.json

### Changed

- Use the new sprite system
- Renamed settings MUTE_MOD to MUTE_SELF

## [1.5.2] - 2024-01-10

### Fixed

- Tracks "Comforting Memories", "Floating Dream", and "An Ordinary Day" were credited to Lena Raine instead of Kumi
  Tanioka (thanks @BeeTeeKay64)

## [1.5.1] - 2023-12-14

### Fixed

- Compatibility with mod that change toast sound (Better Recipe Book), if a similar mod is installed, you will not be
  able to configure the mute setting

## [1.5.0] - 2023-11-18

### Added

- Support to 1.20.2

### Changed

- DISABLE_TOAST_SOUND dropdown config options from `VANILLA, DISABLE_THIS, DISABLE_ALL` to `VANILLA, MUTE_MOD, MUTE_ALL`

### Fixed

- Update translations

## [1.4.0] - 2023-06-18

### Added

- Support to 1.20.1

## [1.3.0] - 2023-05-09

### Added

- Added new texture for the music discs (tanks to @YaCCBoy)
- Added support for option 'Notification Time'

### Removed

- Removed unused code

### Update

- Translation update

## [1.2.0] - 2023-05-08

- Change to how musics.json is loaded. Resource pack can now replace the default musics.json
- Bumped Cloth config and Mod Menu version

## [1.1.0] - 2023-01-23

### Added

- Support to 1.19.4
- Removed LibGui dependency and gui code