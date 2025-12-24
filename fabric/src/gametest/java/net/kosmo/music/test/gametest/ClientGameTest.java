package net.kosmo.music.test.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.kosmo.music.Helper;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("UnstableApiUsage")
public class ClientGameTest implements FabricClientGameTest {
	public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification/GameTest");
	public final Test test = new Test(LOGGER);

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			singleplayer.getClientWorld().waitForChunksRender();


			context.runOnClient(this::testSoundEventExistence);
		}
	}

	public void testSoundEventExistence(Minecraft minecraft) {
		for (TrackData entry : TrackDataManager.getInstance().tracks.values()) {
//			if (!entry.key().toString().contains("records")) {
//				continue;
//			}

			LOGGER.info("testing: {}", entry.key());
			AtomicReference<Boolean> result = new AtomicReference<>(false);
			AtomicReference<String> returnResult = new AtomicReference<>("");
			// test customId
			if (entry.customId().isPresent()) {
				result.set(test.booleanTest(
					isSoundEventValid(minecraft, entry.customId().get()),
					returnResult,
					String.format("(customId) valid sound event for %s", entry.customId().get()), String.format("(customId) Missing sound event for %s", entry.customId().get())
				));
			}
			// test key
			if (!result.get()) {
				result.set(test.booleanTest(
					isSoundEventValid(minecraft, entry.key()),
					returnResult,
					String.format("(key) valid sound event for %s", entry.key()), String.format("(key) Missing sound event for %s", entry.key())
				));
			}
			// resolvedId
			if (!result.get()) {
				test.booleanTest(
					isSoundEventValid(minecraft, entry.getResolvedId()),
					returnResult,
					String.format("(resolvedId) valid sound event for %s", entry.getResolvedId()), String.format("(resolvedId) Missing sound event for %s", entry.getResolvedId())
				);
			}
			this.test.RESULT.put(entry.key(), new Test.TestResult(result.get(), returnResult.get()));
			LOGGER.info("----");
		}
		this.test.printAllResult();
	}

	public boolean isSoundEventValid(Minecraft minecraft, Identifier location) {
		SoundManager soundManager = minecraft.getSoundManager();

		SoundEvent soundEvent = Helper.getSoundEvent(soundManager, location);
		if (soundEvent == null) {
			LOGGER.warn("unknow sound event: {}", location);
			return false;
		}

		SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent/*? >=1.21.11 {*/ /*?} else {*//*, 1 *//*?}*/);
		WeighedSoundEvents weighedSoundEvents = soundInstance.resolve(soundManager);
		if (weighedSoundEvents == null) {
			return false;
		}
		soundManager.stop(null, SoundSource.MUSIC);
		soundManager.stop(null, SoundSource.RECORDS);
		// Stop working after a while because of ChannelHandle idk
//		SoundEngine.PlayResult playResult = soundManager.play(soundInstance);
//		return playResult == SoundEngine.PlayResult.STARTED || playResult == SoundEngine.PlayResult.STARTED_SILENTLY;
		return true;
	}
}
