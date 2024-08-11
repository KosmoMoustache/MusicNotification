package net.kosmo.music.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.kosmo.music.impl.mixin.IMixinMusicTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.JukeboxSong;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class Helper {
    /**
     * Parse a JSON Resource
     */
    public static JsonObject parseJSONResource(Resource resource) throws IOException, JsonParseException {
        BufferedReader reader = resource.openAsReader();
        return GsonHelper.parse(reader);
    }

    /**
     * Return false if either MASTER or MUSIC volume is set to 0
     */
    public static boolean isVolumeZero() {
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) == 0f) return false;
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER) == 0f) return false;
        return true;
    }

    public static void playAndResetTracker(Minecraft client, MusicManager.Music music) {
        SoundEvent soundEvent = music.getSoundEvent(ClientMusic.soundManager);
        if (soundEvent == null) {
            ClientMusic.LOGGER.warn("Unable to play unknown sound with id: {}", music.customId == null ? music.identifier : music.customId);
            return;
        }

        client.getSoundManager().stop(null, SoundSource.MUSIC);

        SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent);
        IMixinMusicTracker musicTracker = (IMixinMusicTracker) client.getMusicManager();

        musicTracker.setCurrentMusic(soundInstance);
        client.getSoundManager().play(soundInstance);
        ClientMusic.currentlyPlaying = soundInstance;
    }

    public static boolean isMatchedInList(String input, List<String> patterns) {
        for (String pattern : patterns) {
            // Replace * with .* to create a regex pattern
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            if (Pattern.matches(regex, input)) {
                return true;
            }
        }
        return false;
    }

    public static class ResourceLocationParser {
        public String title;

        public ResourceLocationParser(ResourceLocation location) {
            // get the last part of the path
            String[] split = location.getPath().split("/");
            this.title = split[split.length - 1];
        }
    }

    public static class JukeboxSongParser {
        public String title;
        public String author;

        public JukeboxSongParser(JukeboxSong jukeboxSong) {
            // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep;
            String[] split = jukeboxSong.description().getString().split(" - ");
            try {
                title = split[0];
                author = split[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                title = jukeboxSong.description().getString();
                author = "Unknown";
            }
        }
    }
}
