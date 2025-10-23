package net.kosmo.music;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

import static net.kosmo.music.MusicNotificationClient.id;

@Deprecated
public class AlbumInfo {
	public String name;
	public Cover cover;

	AlbumInfo(String name, String coverId) {
		this.name = "Unknown Album";
		this.cover = new Cover("minecraft", coverId);
	}

	public static AlbumInfo Dummy() {
		return new AlbumInfo("Dummy", "toast/generic");
	}

	public static AlbumInfo fromSoundInstance(SoundInstance soundInstance) {
		return new AlbumInfo("Unknown Album", soundInstance.getLocation().toString());
	}

	@Deprecated
	public static AlbumInfo dummy() {
		return new AlbumInfo("IDK", "toast/generic");
	}

	public boolean isVanilla(SoundInstance soundInstance) {
		return Objects.equals(soundInstance.getLocation().getNamespace().toLowerCase(), "minecraft");
	}

	public static class Cover {
		public static final String GENERIC = "toast/generic";
		public static final String MODDED = "toast/modded";
		public static final ResourceLocation GENERIC1 = id("toast/generic");
		public static final ResourceLocation MODDED1 = id("toast/modded");


		ResourceLocation sprite;

		public Cover(String namespace, String coverId) {
			try {
				this.sprite = ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/" + coverId);
			} catch (Exception e) {
				MusicNotificationClient.LOGGER.warn("Invalid cover ID: {}, using generic cover instead.", coverId);
				this.sprite = ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/generic"); // getDefaultCover(ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, cover));
			}
		}

		public static ResourceLocation getDefaultCover(ResourceLocation location) {
			return ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, Objects.equals(location.getNamespace().toLowerCase(), "minecraft") ? GENERIC : MODDED);
		}

		public static int getWidth() {
			return 20;
		}

		public static int getHeight() {
			return 20;
		}

		public void drawCover(GuiGraphics guiGraphics, int x, int y) {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, x, y, getWidth(), getHeight());

		}
	}
}
