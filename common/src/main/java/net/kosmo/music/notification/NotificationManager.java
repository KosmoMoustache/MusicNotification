package net.kosmo.music.notification;

import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.Identifier;

import java.util.List;

public class NotificationManager {
	public static void show(Identifier soundId, TrackData td) {
		Config.Options.NotificationStyle style = Config.options().NOTIFICATION_STYLE;
		show(style.canBeShown() ? style : getFallback(), soundId, td);
	}

	public static void show(Config.Options.NotificationStyle style, Identifier resourceLocation, TrackData trackData) {
		switch (style) {
			case Config.Options.NotificationStyle.LOG -> {
				new Notification().show(resourceLocation, trackData);
			}
//			case Config.Options.NotificationStyle.VANILLA -> {
//				new VanillaNotification().show(resourceLocation, trackData);
//			}
//			case Config.Options.NotificationStyle.VANILLA_ENHANCED -> {
//				new VanillaNotification.Enhanced().show(resourceLocation, trackData);
//			}
			case Config.Options.NotificationStyle.COMPACT -> {
				new CompactNotification().show(resourceLocation, trackData);
			}
			case Config.Options.NotificationStyle.LEGACY_TOAST -> {
				new LegacyToastNotification().show(resourceLocation, trackData);
			}
			case Config.Options.NotificationStyle.ACTION_BAR -> {
				new ActionBarNotification().show(resourceLocation, trackData);
			}
		}
	}

	public static Config.Options.NotificationStyle getFallback() {
		List<Config.Options.NotificationStyle> styles = Config.options().NOTIFICATION_STYLE_FALLBACK;
		for (Config.Options.NotificationStyle style : styles) {
			if (style.canBeShown()) {
				return style;
			}
		}
		Notification.LOGGER.warn("No valid notification style found in fallback list, using LOG style");
		return Config.Options.NotificationStyle.LOG;
	}
}
