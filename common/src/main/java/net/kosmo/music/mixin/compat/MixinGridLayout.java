//? if <=26.1 {
/*package net.kosmo.music.mixin.compat;

import net.kosmo.music.compat.ILayoutCompat;
import net.minecraft.client.gui.layouts.GridLayout;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(GridLayout.class)
public class MixinGridLayout implements ILayoutCompat {
	@Final
	@Shadow
	private List<GridLayout.ChildContainer> children;

	@Override
	public void musicnotification$removeChildren() {
		this.children.clear();
	}
}
*///? }