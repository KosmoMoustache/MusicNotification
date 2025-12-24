package net.kosmo.music.test.gametest;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Test {
	public final Map<ResourceLocation, TestResult> RESULT = new LinkedHashMap<>();
	private final Logger LOGGER;

	Test(Logger logger) {
		this.LOGGER = logger;
	}

	public Boolean booleanTest(boolean soundEventValid, AtomicReference<String> atomicReference, String good, String bad) {
		boolean b = booleanTest(soundEventValid, good, bad);
		if (!b) {
			atomicReference.set(bad);
		} else {
			atomicReference.set(good);
		}
		return b;
	}

	public Boolean booleanTest(boolean soundEventValid, String good, String bad) {
		if (soundEventValid) {
			LOGGER.info("{}", good);
			return true;
		} else {
			LOGGER.error("{}", bad);
			return false;
		}
	}

	public void printAllResult() {
		LOGGER.info("━━━━ Test summary ━━━━");
		for (Map.Entry<ResourceLocation, TestResult> entry : RESULT.entrySet()) {
			if (entry.getValue().result) {
				LOGGER.info("  ✔ {}", entry.getKey());
				LOGGER.info("  ↳ {}", entry.getValue().message);

			} else {
				LOGGER.error(" ✖ {}", entry.getKey());
				LOGGER.error(" ↳ {}", entry.getValue().message);
			}
		}
	}

	public record TestResult(boolean result, String message) {
	}
}
