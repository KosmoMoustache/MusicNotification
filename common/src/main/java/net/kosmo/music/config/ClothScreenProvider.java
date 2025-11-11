package net.kosmo.music.config;

import io.netty.util.internal.UnstableApi;
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
		SubCategoryBuilder jukebox = entryBuilder.startSubCategory(Component.translatable("config.musicnotification.jukebox.title")).setExpanded(true);
		SubCategoryBuilder general = entryBuilder.startSubCategory(Component.translatable("config.musicnotification.general.title")).setExpanded(false);

		notification.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.show_author"), options.SHOW_AUTHOR)
			.setDefaultValue(Config.Options.SHOW_AUTHOR_DEFAULT)
			.setSaveConsumer(val -> options.SHOW_AUTHOR = val)
			.build());
		notification.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.show_album_name"), options.SHOW_ALBUM_NAME)
			.setDefaultValue(Config.Options.SHOW_ALBUM_NAME_DEFAULT)
			.setSaveConsumer(val -> options.SHOW_ALBUM_NAME = val)
			.build());
		notification.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.animate_cover"), options.ROTATE_ALBUM_COVER)
			.setDefaultValue(Config.Options.ROTATE_ALBUM_COVER_DEFAULT)
			.setSaveConsumer(val -> options.ROTATE_ALBUM_COVER = val)
			.build());
		notification.add(entryBuilder.startEnumSelector(Component.translatable("config.musicnotification.notification.disable_toast_sound"), Config.Options.DisableToastSound.class, options.DISABLE_TOAST_SOUND)
			.setDefaultValue(Config.Options.DISABLE_TOAST_SOUND_DEFAULT)
			.setEnumNameProvider(Config.Options.DisableToastSound::name)
			.setTooltipSupplier(Config.Options.DisableToastSound::tooltipSupplier)
			.setSaveConsumer(val -> options.DISABLE_TOAST_SOUND = val)
			.build());

		@NotNull EnumListEntry<Config.Options.NotificationStyle> notificationStyleEnumListEntry = entryBuilder.startEnumSelector(Component.translatable("config.musicnotification.notification.notification_style"), Config.Options.NotificationStyle.class, options.NOTIFICATION_STYLE)
			.setDefaultValue(Config.Options.NOTIFICATION_STYLE_DEFAULT)
			.setEnumNameProvider(Config.Options.NotificationStyle::name)
			.setTooltipSupplier(Config.Options.NotificationStyle::tooltipSupplier)
			.setSaveConsumer(val -> options.NOTIFICATION_STYLE = val)
			.build();
		notification.add(notificationStyleEnumListEntry);

		// Only show when Notification Style is Legacy
		notification.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.style.legacy.scale"), options.STYLE_LEGACY_TOAST_SCALE)
			.setDefaultValue(Config.Options.STYLE_LEGACY_TOAST_SCALE_DEFAULT)
			.setDisplayRequirement(Requirement.isValue(notificationStyleEnumListEntry, Config.Options.NotificationStyle.LEGACY))
			.setSaveConsumer(val -> options.STYLE_LEGACY_TOAST_SCALE = val)
			.build()
		);

		// Only show when Notification Style is Compact
		notification.add(entryBuilder.startIntSlider(Component.translatable("config.musicnotification.notification.style.compact.alpha"), (int) Math.round(options.STYLE_COMPACT_ALPHA / 2.55), 0, 100)
			.setDefaultValue((int) Math.round(Config.Options.STYLE_COMPACT_ALPHA_DEFAULT / 2.55))
			.setDisplayRequirement(Requirement.isValue(notificationStyleEnumListEntry, Config.Options.NotificationStyle.COMPACT))
			.setSaveConsumer(val -> options.STYLE_COMPACT_ALPHA = (int) Math.round(val * 2.55))
			.build()
		);

		// Only show when Notification Style is Action Bar
		notification.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.notification.style.action_bar.animate_color"), options.STYLE_ACTION_BAR_ANIMATE_COLOR)
			.setDefaultValue(Config.Options.STYLE_ACTION_BAR_ANIMATE_COLOR_DEFAULT)
			.setDisplayRequirement(Requirement.isValue(notificationStyleEnumListEntry, Config.Options.NotificationStyle.ACTION_BAR))
			.setSaveConsumer(val -> options.STYLE_ACTION_BAR_ANIMATE_COLOR = val)
			.build()
		);

		jukebox.add(entryBuilder.startIntField(Component.translatable("config.musicnotification.jukebox.max_count_history"), options.MAX_COUNT_HISTORY)
			.setDefaultValue(Config.Options.MAX_COUNT_HISTORY_DEFAULT)
			.setSaveConsumer(val -> options.MAX_COUNT_HISTORY = val)
			.build());
		jukebox.add(entryBuilder.startBooleanToggle(Component.translatable("config.musicnotification.jukebox.debug_mod"), options.DEBUG_MOD)
			.setDefaultValue(Config.Options.DEBUG_MOD_DEFAULT)
			.setSaveConsumer(val -> options.DEBUG_MOD = val)
			.build());

		general.add(entryBuilder.startStrList(Component.translatable("config.musicnotification.general.ignore_sound_event"), options.IGNORE_SOUND_EVENT)
			.setDefaultValue(Config.Options.IGNORE_SOUND_EVENT_DEFAULT)
			.setTooltip(Component.translatable("config.musicnotification.general.ignore_sound_event.tooltip"))
			.setSaveConsumer(val -> options.IGNORE_SOUND_EVENT = val)
			.build());

		category.addEntry(notification.build());
		category.addEntry(jukebox.build());
		category.addEntry(general.build());

		return builder.build();
	}
}
