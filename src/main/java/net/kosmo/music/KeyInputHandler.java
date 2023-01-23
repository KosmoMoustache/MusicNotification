package net.kosmo.music;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.kosmo.music.gui.ListMusicGui;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import static net.kosmo.music.ClientMusic.config;

public class KeyInputHandler {

    public static final String KEY_CATEGORY = "key.musicnotification.category";
    public static final String KEY_GUI = "key.musicnotification.openGui";
    public static KeyBinding keyGui;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyGui.isPressed() && config.enableBetaGui) {
                client.setScreen(new CottonClientScreen(new ListMusicGui()));
            }
        });
    }

    public static void register() {
        /* Show to keybinding in controls menu
        keyGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_GUI,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                KEY_CATEGORY
        );
        */
        keyGui = new KeyBinding(
                KEY_GUI,
                GLFW.GLFW_KEY_M,
                KEY_CATEGORY
        );

        registerKeyInputs();
    }
}
