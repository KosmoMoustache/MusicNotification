package net.kosmo.music.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

public class Parser {
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
