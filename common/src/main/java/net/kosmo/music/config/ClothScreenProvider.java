package net.kosmo.music.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ClothScreenProvider {

	public static Screen getConfigScreen(Screen parent) {
		Config.Options options = Config.options();

		ConfigBuilder builder = ConfigBuilder.create()
			.setTitle(Component.translatable("config.musicnotification.title"))
			.setParentScreen(parent)
			.setSavingRunnable(Config::save);

		ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.musicnotification.title"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		SubCategoryBuilder notification = entryBuilder.startSubCategory(Component.translatable("config.musicnotification.notification.title")).setExpanded(true);
		SubCategoryBuilder style = entryBuilder.startSubCategory(Component.translatable("config.musicnotification.style.title")).setExpanded(true);
		SubCategoryBuilder jukebox = entryBuilder.startSubCategory(Component.translatable("config.musicnotification.jukebox.title")).setExpanded(true);
		SubCategoryBuilder general = entryBuilder.startSubCategory(Component.translatable("config.musicnotification.general.title")).setExpanded(true);

		notification.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.show_author"), options.SHOW_AUTHOR)
			.setDefaultValue(Config.Options.SHOW_AUTHOR_DEFAULT)
			.setSaveConsumer(val -> options.SHOW_AUTHOR = val)
			.build());
		notification.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.show_album_name"), options.SHOW_ALBUM_NAME)
			.setDefaultValue(Config.Options.SHOW_ALBUM_NAME_DEFAULT)
			.setSaveConsumer(val -> options.SHOW_ALBUM_NAME = val)
			.build());

		@NotNull EnumListEntry<Config.Options.NotificationStyle> notificationStyleEnumListEntry = entryBuilder.startEnumSelector(Component.translatable("config.musicnotification.notification.notification_style"), Config.Options.NotificationStyle.class, options.NOTIFICATION_STYLE)
			.setDefaultValue(Config.Options.NOTIFICATION_STYLE_DEFAULT)
			.setEnumNameProvider(Config.Options.NotificationStyle::name)
			.setTooltipSupplier(Config.Options.NotificationStyle::tooltipSupplier)
			.setSaveConsumer(val -> options.NOTIFICATION_STYLE = val)
			.build();
		notification.add(notificationStyleEnumListEntry);

		@NotNull EnumListEntry<Config.Options.NotificationStyle> notificationStyleEnumListEntryFallback = entryBuilder.startEnumSelector(Component.translatable("config.musicnotification.notification.notification_style_fallback"), Config.Options.NotificationStyle.class, options.NOTIFICATION_STYLE_FALLBACK.getFirst())
			.setDefaultValue(Config.Options.NOTIFICATION_STYLE_FALLBACK_DEFAULT.getFirst())
			.setEnumNameProvider(Config.Options.NotificationStyle::name)
			.setTooltipSupplier(Config.Options.NotificationStyle::tooltipSupplier)
			.setSaveConsumer(val -> options.NOTIFICATION_STYLE_FALLBACK.set(0, val))
			.build();
		notification.add(notificationStyleEnumListEntryFallback);

		// Only show when Notification Style is Legacy
		style.add(entryBuilder.startEnumSelector(Component.translatable("config.musicnotification.notification.disable_toast_sound"), Config.Options.DisableToastSound.class, options.DISABLE_TOAST_SOUND)
			.setDisplayRequirement(requirementAnyOfNotificationStyles(Config.Options.NotificationStyle.LEGACY_TOAST, notificationStyleEnumListEntry, notificationStyleEnumListEntryFallback))
			.setDefaultValue(Config.Options.DISABLE_TOAST_SOUND_DEFAULT)
			.setEnumNameProvider(Config.Options.DisableToastSound::name)
			.setTooltipSupplier(Config.Options.DisableToastSound::tooltipSupplier)
			.setSaveConsumer(val -> options.DISABLE_TOAST_SOUND = val)
			.build()
		);
		style.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.animate_cover"), options.ROTATE_ALBUM_COVER)
			.setDisplayRequirement(requirementAnyOfNotificationStyles(Config.Options.NotificationStyle.LEGACY_TOAST, notificationStyleEnumListEntry, notificationStyleEnumListEntryFallback))
			.setDefaultValue(Config.Options.ROTATE_ALBUM_COVER_DEFAULT)
			.setSaveConsumer(val -> options.ROTATE_ALBUM_COVER = val)
			.build());

		style.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.style.legacy_toast.scale"), options.STYLE_LEGACY_TOAST_SCALE)
			.setDisplayRequirement(requirementAnyOfNotificationStyles(Config.Options.NotificationStyle.LEGACY_TOAST, notificationStyleEnumListEntry, notificationStyleEnumListEntryFallback))
			.setDefaultValue(Config.Options.STYLE_LEGACY_TOAST_SCALE_DEFAULT)
			.setSaveConsumer(val -> options.STYLE_LEGACY_TOAST_SCALE = val)
			.build()
		);

		// Only show when Notification Style is Compact
		style.add(entryBuilder.startIntSlider(Component.translatable("config.musicnotification.notification.style.compact.alpha"), (int) Math.round(options.STYLE_COMPACT_ALPHA / 2.55), 0, 100)
			.setDisplayRequirement(requirementAnyOfNotificationStyles(Config.Options.NotificationStyle.COMPACT, notificationStyleEnumListEntry, notificationStyleEnumListEntryFallback))
			.setDefaultValue((int) Math.round(Config.Options.STYLE_COMPACT_ALPHA_DEFAULT / 2.55))
			.setSaveConsumer(val -> options.STYLE_COMPACT_ALPHA = (int) Math.round(val * 2.55))
			.build()
		);

		style.add(entryBuilder.startIntField(Component.translatable("config.musicnotification.notification.style.compact.time"), options.STYLE_COMPACT_TIME / 20)
			.setDisplayRequirement(requirementAnyOfNotificationStyles(Config.Options.NotificationStyle.COMPACT, notificationStyleEnumListEntry, notificationStyleEnumListEntryFallback))
			.setDefaultValue(Config.Options.STYLE_COMPACT_TIME_DEFAULT / 20)
			.setSaveConsumer(val -> options.STYLE_COMPACT_TIME = val * 20)
			.build()
		);

		// Only show when Notification Style is Action Bar
		style.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.style.action_bar.animate_color"), options.STYLE_ACTION_BAR_ANIMATE_COLOR)
			.setDisplayRequirement(requirementAnyOfNotificationStyles(Config.Options.NotificationStyle.ACTION_BAR, notificationStyleEnumListEntry, notificationStyleEnumListEntryFallback))
			.setDefaultValue(Config.Options.STYLE_ACTION_BAR_ANIMATE_COLOR_DEFAULT)
			.setSaveConsumer(val -> options.STYLE_ACTION_BAR_ANIMATE_COLOR = val)
			.build()
		);

		jukebox.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.jukebox.show_on_title_screen"), options.SHOW_TITLE_SCREEN_BUTTON)
			.setDefaultValue(Config.Options.SHOW_TITLE_SCREEN_BUTTON_DEFAULT)
			.setSaveConsumer(val -> options.SHOW_TITLE_SCREEN_BUTTON = val)
			.build());

		jukebox.add(entryBuilder.startIntField(Component.translatable("config.musicnotification.jukebox.max_count_history"), options.MAX_COUNT_HISTORY)
			.setDefaultValue(Config.Options.MAX_COUNT_HISTORY_DEFAULT)
			.setSaveConsumer(val -> options.MAX_COUNT_HISTORY = val)
			.build());

		jukebox.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.jukebox.debug_mod"), options.DEBUG_MOD)
			.setDefaultValue(Config.Options.DEBUG_MOD_DEFAULT)
			.setSaveConsumer(val -> options.DEBUG_MOD = val)
			.build());
		jukebox.add(entryBuilder.startBooleanToggle(Component.translatable("(Work in progress) enable filters"), options.WIP_FILTER_IN_JUKEBOX)
			.setDefaultValue(Config.Options.WIP_FILTER_IN_JUKEBOX_DEFAULT)
			.setSaveConsumer(val -> options.WIP_FILTER_IN_JUKEBOX = val)
			.build());

		general.add(entryBuilder.startStrList(Component.translatable("config.musicnotification.general.ignore_sound_event"), options.IGNORE_SOUND_EVENT)
			.setDefaultValue(Config.Options.IGNORE_SOUND_EVENT_DEFAULT)
			.setTooltip(Component.translatable("config.musicnotification.general.ignore_sound_event.tooltip"))
			.setSaveConsumer(val -> options.IGNORE_SOUND_EVENT = val)
			.build());

		//? if music_frequency {
		/*general.add(entryBuilder.startEnumSelector(Component.translatable("config.musicnotification.general.music_frequency"), Config.Options.MusicFrequency.class, options.MUSIC_FREQUENCY)
			.setDefaultValue(Config.Options.MUSIC_FREQUENCY_DEFAULT)
			.setEnumNameProvider(Config.Options.MusicFrequency::name)
			.setTooltipSupplier(Config.Options.MusicFrequency::tooltipSupplier)
			.setSaveConsumer(val -> {
				options.MUSIC_FREQUENCY = val;
				Config.Options.MusicFrequency.onChange(val);
			})
			.build()
		);
		*///? }

		category.addEntry(notification.build());
		category.addEntry(style.build());
		category.addEntry(jukebox.build());
		category.addEntry(general.build());

		return builder.build();
	}

	private static Requirement requirementAnyOfNotificationStyles(Config.Options.NotificationStyle notificationStyle, EnumListEntry<Config.Options.NotificationStyle> style1, EnumListEntry<Config.Options.NotificationStyle> style2) {
		return Requirement.any(Requirement.isValue(style1, notificationStyle), Requirement.isValue(style2, notificationStyle));
	}
}
