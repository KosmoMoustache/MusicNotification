package net.kosmo.music;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.kosmo.music.config.Config;
import net.kosmo.music.notification.toast.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MusicNotificationClient {
	public static final String MOD_ID = "musicnotification";
	public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");

	public static PlatformHelper PLATFORM_HELPER;
	public static @Nullable SoundInstance currentlyPlaying;

	public static void init(PlatformHelper platformHelper) {
		LOGGER.info("Music Notification initialized");
		Config.getAndSave();
		MusicNotificationClient.PLATFORM_HELPER = platformHelper;
//		PLATFORM_HELPER.isModLoaded("vanillabackport")); // TODO: Handle if VanillaBackport backport the music frequency options
	}

	public static void tick() {
		tick(Minecraft.getInstance());
	}

	public static void tick(Minecraft minecraft) {
		KeyBinding.tick();
	}

	public static void TManagerMixinPlaySoundMute(Toast.Visibility instance, SoundManager handler, Operation<Void> original, ToastManager.ToastInstance<? extends Toast> toast) {
		TManagerMixinPlaySoundMute(instance, handler, original, toast.getToast());
	}
	public static void TManagerMixinPlaySoundMute(Toast.Visibility instance, SoundManager handler, Operation<Void> original, Toast toast) {
		if (Config.options().DISABLE_TOAST_SOUND == Config.Options.DisableToastSound.MUTE_ALL) {
			return;
		}
		if (Config.options().DISABLE_TOAST_SOUND == Config.Options.DisableToastSound.MUTE_SELF && toast instanceof MusicToast) {
			return;
		}
		original.call(instance, handler);
	}
}
