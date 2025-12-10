package net.kosmo.music.resource;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AlbumCover {
	private final AlbumCoverType cover;

	private AlbumCover(AlbumCoverType cover) {
		this.cover = cover;
	}

	public static AlbumCover fromSprite(String namespace, @Nullable String spriteId) {
		return new AlbumCover(new SpriteCover(namespace, spriteId));
	}

	public static AlbumCover fromItem(ResourceLocation itemId) {
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

	public void drawCover(GuiGraphics guiGraphics, int x, int y) {
		this.cover.drawCover(guiGraphics, x, y);
	}

	interface AlbumCoverType {
		void drawCover(GuiGraphics guiGraphics, int x, int y);

		int getHeight();

		int getWidth();
	}

	private static class SpriteCover implements AlbumCoverType {
		private final ResourceLocation sprite;

		SpriteCover(String namespace, @Nullable String spriteId) {
			if (spriteId == null || spriteId.isEmpty()) {
				spriteId = getDefaultCoverId(namespace);
			}
			this.sprite = fromCoverId(spriteId);
		}

		private static String getDefaultCoverId(String namespace) {
			return namespace.equalsIgnoreCase("minecraft") ? "generic" : "modded";
		}

		private ResourceLocation fromCoverId(String coverId) {
			try {
				return ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/" + coverId);
			} catch (Exception e) {
				MusicNotificationClient.LOGGER.warn("Failed to load album cover '{}', falling back to default. Error: {}", coverId, e.getMessage());
				return ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/" + getDefaultCoverId("minecraft"));
			}
		}

		public int getWidth() {
			return 20;
		}

		public int getHeight() {
			return 20;
		}

		public void drawCover(GuiGraphics guiGraphics, int x, int y) {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, getWidth(), getHeight());
		}
	}

	private static class ItemCover implements AlbumCoverType {
		private final ItemStack stack;

		private ItemCover(ResourceLocation stack) {
			this.stack = getItemStack(stack);
		}

		public int getWidth() {
			return 16;
		}

		public int getHeight() {
			return 16;
		}

		private ItemStack getItemStack(ResourceLocation itemId) {
			if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
				MusicNotificationClient.LOGGER.error("Failed to find album cover item '{}', falling back to dirt.", itemId);
				itemId = ResourceLocation.tryParse("minecraft:music_disc_13");
			}
			ItemStack itemStack = BuiltInRegistries.ITEM.get(itemId).get().value().getDefaultInstance();
			if (itemStack.isEmpty()) {
				itemStack = new ItemStack(Items.MUSIC_DISC_13);
			}
			return itemStack;
		}

		public void drawCover(GuiGraphics guiGraphics, int x, int y) {
			guiGraphics.renderFakeItem(this.stack, x, y);
		}
	}
}