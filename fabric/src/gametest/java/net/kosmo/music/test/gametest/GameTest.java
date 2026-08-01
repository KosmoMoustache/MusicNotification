package net.kosmo.music.test.gametest;

//~ if >1.21.4 'FabricGameTest' -> 'CustomTestMethodInvoker'
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.minecraft.gametest.framework.GameTestHelper;

import java.lang.reflect.Method;

//~ if >1.21.4 'FabricGameTest' -> 'CustomTestMethodInvoker'
public class GameTest implements CustomTestMethodInvoker {

	@Override
	public void invokeTestMethod(GameTestHelper context, Method method) {
	}
}