package net.kosmo.music.test.gametest;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class ExampleModClientGameTest implements FabricClientGameTest {
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			singleplayer.getClientWorld().waitForChunksRender();
			context.runOnClient(minecraft -> {
				LOGGER.info("Testing if sound events exist and are valid...");
				for (Map.Entry<ResourceLocation, TrackData> entry : TrackDataManager.getInstance().tracks.entrySet()) {
					soundEventTest(minecraft, entry.getKey());
				}
				LOGGER.info("Testing if custom sound events exist and are valid...");
				for (Map.Entry<ResourceLocation, TrackData> entry : TrackDataManager.getInstance().tracks.entrySet()) {
					if (entry.getValue().customId().isPresent())
						soundEventTest(minecraft, entry.getValue().customId().get());
				}
			});
		}
	}

	public void soundEventTest(Minecraft minecraft, ResourceLocation id) throws AssertionError {
		if (minecraft.getSoundManager().getSoundEvent(id) != null) {
			if (minecraft.getSoundManager().getSoundEvent(id).getSound(RandomSource.create()).getLocation() == id) {
				LOGGER.info("{}: VALID", id);
			} else {
				LOGGER.error("{}: INVALID", id);
			}
		} else {
			LOGGER.error("MISSING sound event for id: {}", id);
		}
	}
}
