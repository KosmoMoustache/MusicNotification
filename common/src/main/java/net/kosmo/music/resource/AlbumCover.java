package net.kosmo.music.resource;

import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? >1.21.1
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class AlbumCover {
	private final AlbumCoverType cover;

	private AlbumCover(AlbumCoverType cover) {
		this.cover = cover;
	}

	public static AlbumCover fromSprite(String namespace, @Nullable String spriteId) {
		return new AlbumCover(new SpriteCover(namespace, spriteId));
	}

	public static AlbumCover fromItem(Identifier itemId) {
		return new AlbumCover(new ItemCover(itemId));
	}

	public int getWidth() {
		return this.cover.getWidth();
	}

	public int getHeight() {
		return this.cover.getHeight();
	}

	public boolean canBeAnimated() {
		return this.cover instanceof SpriteCover;
	}

	public void drawCover(GuiGraphicsExtractor guiGraphics, int x, int y) {
		this.cover.drawCover(guiGraphics, x, y);
	}

	interface AlbumCoverType {
		void drawCover(GuiGraphicsExtractor guiGraphics, int x, int y);

		int getHeight();

		int getWidth();
	}

	private static class SpriteCover implements AlbumCoverType {
		private final Identifier sprite;

		SpriteCover(String namespace, @Nullable String spriteId) {
			if (spriteId == null || spriteId.isEmpty()) {
				spriteId = getDefaultCoverId(namespace);
			}
			this.sprite = fromCoverId(spriteId);
		}

		private static String getDefaultCoverId(String namespace) {
			return namespace.equalsIgnoreCase("minecraft") ? "generic" : "modded";
		}

		private Identifier fromCoverId(String coverId) {
			try {
				return Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/" + coverId);
			} catch (Exception e) {
				MusicNotificationClient.LOGGER.warn("Failed to load album cover '{}', falling back to default. Error: {}", coverId, e.getMessage());
				return Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/" + getDefaultCoverId("minecraft"));
			}
		}

		public int getWidth() {
			return 20;
		}

		public int getHeight() {
			return 20;
		}

		public void drawCover(GuiGraphicsExtractor guiGraphics, int x, int y) {
			guiGraphics.blitSprite(/*? >1.21.1 {*/RenderPipelines.GUI_TEXTURED,/*?}*/sprite, x, y, getWidth(), getHeight());
		}
	}

	private static class ItemCover implements AlbumCoverType {
		private final ItemStack stack;

		private ItemCover(Identifier stack) {
			this.stack = getItemStack(stack);
		}

		public int getWidth() {
			return 16;
		}

		public int getHeight() {
			return 16;
		}

		private ItemStack getItemStack(Identifier itemId) {
			if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
				MusicNotificationClient.LOGGER.error("Failed to find album cover item '{}', falling back to minecraft:music_disc_13.", itemId);
				itemId = Identifier.tryParse("minecraft:music_disc_13");
			}
			//~ if >1.21.1 '.getDefaultInstance()' -> '.get().value().getDefaultInstance()'
			ItemStack itemStack = BuiltInRegistries.ITEM.get(itemId).get().value().getDefaultInstance();
			if (itemStack.isEmpty()) {
				itemStack = new ItemStack(Items.MUSIC_DISC_13);
			}
			return itemStack;
		}

		public void drawCover(GuiGraphicsExtractor guiGraphics, int x, int y) {
			//~ if >26 renderFakeItem -> fakeItem
			guiGraphics.fakeItem(this.stack, x, y);
		}
	}
}