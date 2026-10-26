//? if <=26.1 {
/*//package net.kosmo.music.mixin.compat;

import net.kosmo.music.compat.ILayoutCompat;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LinearLayout.class)
public class MixinLinearLayout implements ILayoutCompat {
	@Final
	@Shadow
	private GridLayout wrapped;

	@Shadow
	private int nextChildIndex;

	@Unique
	public void musicnotification$removeChildren() {
		((ILayoutCompat) this.wrapped).musicnotification$removeChildren();
		this.nextChildIndex = 0;
	}
}
*///? }