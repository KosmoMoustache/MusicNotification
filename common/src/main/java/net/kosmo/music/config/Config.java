package net.kosmo.music.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.notification.*;
import net.minecraft.network.chat.Component;
//? if music_frequency {
/*import net.kosmo.music.MusicManagerExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
*///? }

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path DIR_PATH = Path.of("config");
	private static final String FILE_NAME = MusicNotificationClient.MOD_ID + ".json";
	private static Config instance = null;
	private final Options options = new Options();

	public static Options options() {
		return Config.get().options;
	}

	private static Config get() {
		if (instance == null) {
			instance = Config.load();
		}
		return instance;
	}

	public static Config getAndSave() {
		get();
		save();
		return instance;
	}

	public static Config load() {
		Path path = DIR_PATH.resolve(FILE_NAME);
		Config config = null;
		if (Files.exists(path)) {
			config = load(path, GSON);
			if (config == null) {
				MusicNotificationClient.LOGGER.warn("Failed to load config, creating a new one.");
			}
		}
		return config != null ? config : new Config();
	}

	public static Config load(Path path, Gson gson) {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8)) {
			return gson.fromJson(reader, Config.class);
		} catch (Exception e) {
			MusicNotificationClient.LOGGER.error("Unable to load config file", e);
			return null;
		}
	}

	public static void save() {
		if (instance == null) return;
		try {
			if (!Files.isDirectory(DIR_PATH)) Files.createDirectories(DIR_PATH);
			Path file = DIR_PATH.resolve(FILE_NAME);
			Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");
			try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile.toFile()), StandardCharsets.UTF_8)) {
				writer.write(GSON.toJson(instance));
			} catch (IOException e) {
				throw new IOException(e);
			}
			Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			MusicNotificationClient.LOGGER.error("Unable to save config file", e);
		}
	}

	public static class Options {
		public static final boolean COMPUTED_IS_DARK_MODE_ENABLED_DEFAULT = false;

		public static final boolean SHOW_TITLE_SCREEN_BUTTON_DEFAULT = true;
		public static final boolean SHOW_AUTHOR_DEFAULT = true;
		public static final boolean SHOW_ALBUM_NAME_DEFAULT = false;
		public static final boolean ROTATE_ALBUM_COVER_DEFAULT = false;
		public static final DisableToastSound DISABLE_TOAST_SOUND_DEFAULT = DisableToastSound.MUTE_SELF;
		public static final List<String> IGNORE_SOUND_EVENT_DEFAULT = List.of("minecraft:note/*");
		public static final int MAX_COUNT_HISTORY_DEFAULT = 20;
		public static final boolean DEBUG_MOD_DEFAULT = false;
		public static final NotificationStyle NOTIFICATION_STYLE_DEFAULT = NotificationStyle.LEGACY_TOAST;
		public static final List<NotificationStyle> NOTIFICATION_STYLE_FALLBACK_DEFAULT = List.of(NotificationStyle.COMPACT, NotificationStyle.LOG);
		public static final boolean STYLE_ACTION_BAR_ANIMATE_COLOR_DEFAULT = false;
		public static final boolean STYLE_LEGACY_TOAST_SCALE_DEFAULT = false;
		public static final int STYLE_COMPACT_ALPHA_DEFAULT = 179; // 70%
		public static final int STYLE_COMPACT_TIME_DEFAULT = 60; // 3*20 ticks = 3 seconds
		public static final boolean WIP_FILTER_IN_JUKEBOX_DEFAULT = false;
		//? music_frequency
		//public static final MusicFrequency MUSIC_FREQUENCY_DEFAULT = MusicFrequency.DEFAULT;
		// -
		public boolean COMPUTED_IS_DARK_MODE_ENABLED = COMPUTED_IS_DARK_MODE_ENABLED_DEFAULT;
		public boolean SHOW_TITLE_SCREEN_BUTTON = SHOW_TITLE_SCREEN_BUTTON_DEFAULT;
		public boolean SHOW_AUTHOR = SHOW_AUTHOR_DEFAULT;
		public boolean SHOW_ALBUM_NAME = SHOW_ALBUM_NAME_DEFAULT;
		public boolean ROTATE_ALBUM_COVER = ROTATE_ALBUM_COVER_DEFAULT;
		public DisableToastSound DISABLE_TOAST_SOUND = DISABLE_TOAST_SOUND_DEFAULT;
		public List<String> IGNORE_SOUND_EVENT = IGNORE_SOUND_EVENT_DEFAULT;
		public int MAX_COUNT_HISTORY = MAX_COUNT_HISTORY_DEFAULT;
		public boolean DEBUG_MOD = DEBUG_MOD_DEFAULT;
		public NotificationStyle NOTIFICATION_STYLE = NOTIFICATION_STYLE_DEFAULT;
		public List<NotificationStyle> NOTIFICATION_STYLE_FALLBACK = NOTIFICATION_STYLE_FALLBACK_DEFAULT;
		public boolean STYLE_ACTION_BAR_ANIMATE_COLOR = STYLE_ACTION_BAR_ANIMATE_COLOR_DEFAULT;
		public boolean STYLE_LEGACY_TOAST_SCALE = STYLE_LEGACY_TOAST_SCALE_DEFAULT;
		public int STYLE_COMPACT_ALPHA = STYLE_COMPACT_ALPHA_DEFAULT;
		public int STYLE_COMPACT_TIME = STYLE_COMPACT_TIME_DEFAULT;
		public boolean WIP_FILTER_IN_JUKEBOX = WIP_FILTER_IN_JUKEBOX_DEFAULT;
		//? music_frequency
		//public MusicFrequency MUSIC_FREQUENCY = MUSIC_FREQUENCY_DEFAULT;

		public enum DisableToastSound {
			VANILLA, MUTE_SELF, MUTE_ALL;

			public static Component name(Enum<DisableToastSound> disableToastSoundEnum) {
				return Component.translatable("config.musicnotification.notification.disable_toast_sound." + disableToastSoundEnum.name().toLowerCase());
			}

			public Optional<Component[]> tooltipSupplier() {
				return Optional.of(new Component[]{Component.translatable("config.musicnotification.notification.disable_toast_sound." + this.name().toLowerCase() + ".tooltip")});
			}
		}

		public enum NotificationStyle {
			LOG() {
				@Override
				public boolean canBeShown() {
					return Notification.canBeShown();
				}
			},
			COMPACT() {
				@Override
				public boolean canBeShown() {
					return CompactNotification.canBeShown();
				}
			},
			ACTION_BAR() {
				@Override
				public boolean canBeShown() {
					return ActionBarNotification.canBeShown();
				}
			},
			//			VANILLA() {
//				@Override
//				public boolean canBeShown() {
//					return VanillaNotification.canBeShown();
//				}
//			},
//			VANILLA_ENHANCED() {
//				@Override
//				public boolean canBeShown() {
//					return VanillaNotification.Enhanced.canBeShown();
//				}
//			},
			LEGACY_TOAST() {
				@Override
				public boolean canBeShown() {
					return LegacyToastNotification.canBeShown();
				}
			};

			public boolean canBeShown() {
				return false;
			}

			public static Component name(Enum<DisableToastSound> disableToastSoundEnum) {
				return Component.translatable("config.musicnotification.notification.style." + disableToastSoundEnum.name().toLowerCase());
			}

			public Optional<Component[]> tooltipSupplier() {
				return Optional.of(new Component[]{Component.translatable("config.musicnotification.notification.style." + this.name().toLowerCase() + ".tooltip")});
			}
		}

		//? music_frequency {
		/*public enum MusicFrequency {
			DEFAULT("DEFAULT", 20),
			FREQUENT("FREQUENT", 10),
			CONSTANT("CONSTANT", 0);

			private final String name;
			private final int maxFrequency;

			MusicFrequency(String name, int maxFrequencyMinutes) {
				this.name = name;
				this.maxFrequency = maxFrequencyMinutes * 1200;
			}

			public int getNextSongDelay(final @Nullable Music music, final RandomSource random) {
				if (music == null) {
					return this.maxFrequency;
				} else if (this == CONSTANT) {
					return 100;
				} else {
					int minFrequency = Math.min(music.getMinDelay(), this.maxFrequency);
					int maxFrequency = Math.min(music.getMaxDelay(), this.maxFrequency);
					return Mth.nextInt(random, minFrequency, maxFrequency);
				}
			}

			public static void onChange(Config.Options.MusicFrequency newVal) {
				((MusicManagerExtension) Minecraft.getInstance().getMusicManager()).setMinutesBetweenSongs(newVal);
			}

			public static Component name(Enum<DisableToastSound> disableToastSoundEnum) {
				return Component.translatable("config.musicnotification.general.music_frequency." + disableToastSoundEnum.name().toLowerCase());
			}

			public Optional<Component[]> tooltipSupplier() {
				return Optional.of(new Component[]{Component.translatable("config.musicnotification.general.music_frequency." + this.name().toLowerCase() + ".tooltip")});
			}
		}
		*///? }
	}
}
