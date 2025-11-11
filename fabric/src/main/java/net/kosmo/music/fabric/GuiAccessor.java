package net.kosmo.music.fabric;

import net.minecraft.network.chat.Component;

public interface GuiAccessor {

	void musicNotification$setCompactNotificationMessage(Component message);

	void musicNotification$setCompactNotificationTime(int time);

	Component musicNotification$getCompactNotificationMessage();

	int musicNotification$getCompactNotificationTime();
}
