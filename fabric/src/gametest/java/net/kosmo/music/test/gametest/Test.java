package net.kosmo.music.test.gametest;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Test {
	public final Map<Identifier, TestResult> RESULT = new LinkedHashMap<>();
	private final Logger LOGGER;

	Test(Logger logger) {
		this.LOGGER = logger;
	}

	public Boolean booleanTest(boolean b1, AtomicReference<String> atomicReference, String good, String bad) {
		boolean b = booleanTest(b1, good, bad);
		if (!b) {
			atomicReference.set(bad);
		} else {
			atomicReference.set(good);
		}
		return b;
	}

	public Boolean booleanTest(boolean b1, String good, String bad) {
		if (b1) {
			LOGGER.info("{}", good);
			return true;
		} else {
			LOGGER.error("{}", bad);
			return false;
		}
	}

	public void printAllResult() {
		LOGGER.info("━━━━ Test summary ━━━━");
		int i = 0;
		int j = 0;
		for (Map.Entry<Identifier, TestResult> entry : RESULT.entrySet()) {
			if (entry.getValue().result) {
				i++;
				LOGGER.info("  ✔ {}", entry.getKey());
				LOGGER.info("  ↳ {}", entry.getValue().message);

			} else {
				j++;
				LOGGER.error(" ✖ {}", entry.getKey());
				LOGGER.error(" ↳ {}", entry.getValue().message);
			}
		};
		LOGGER.info("Valid {}/{}", i, i + j);
		LOGGER.info("Invalid {}/{}", j, i + j);
	}

	public record TestResult(boolean result, String message) {
	}
}
