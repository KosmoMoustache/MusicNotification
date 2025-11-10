package net.kosmo.music.test.gametest;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.minecraft.gametest.framework.GameTestHelper;

import java.lang.reflect.Method;

public class GameTest implements CustomTestMethodInvoker {
	@net.fabricmc.fabric.api.gametest.v1.GameTest
	public void test(GameTestHelper context) {
	}

	@Override
	public void invokeTestMethod(GameTestHelper context, Method method) throws ReflectiveOperationException {
	}
}