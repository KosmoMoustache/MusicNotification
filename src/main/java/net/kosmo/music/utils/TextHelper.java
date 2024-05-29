package net.kosmo.music.utils;

import net.kosmo.music.ClientMusic;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextHelper {
    public static Text getText(Text text) {
        return getText(text.copy());
    }
    public static MutableText getText(MutableText text) {
        if (ClientMusic.isDarkModeEnabled) {
            return text.formatted(Formatting.WHITE);
        }
        return text;
    }
}
