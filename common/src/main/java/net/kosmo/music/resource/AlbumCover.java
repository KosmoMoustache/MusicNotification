package net.kosmo.music.resource;

import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class AlbumCover {
	private final ResourceLocation sprite;

	public AlbumCover(String namespace, Optional<String> coverId) {
		this.sprite = fromCoverId(coverId.orElseGet(() -> getDefaultCoverId(namespace)));
	}

	public ResourceLocation fromCoverId(String coverId) {
		try {
			return ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/" + coverId);
		} catch (Exception e) {
			MusicNotificationClient.LOGGER.warn("Failed to load album cover '{}', falling back to default. Error: {}", coverId, e.getMessage());
			return ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "toast/" + getDefaultCoverId("minecraft"));
		}
	}

	private static String getDefaultCoverId(String namespace) {
		return namespace.equalsIgnoreCase("minecraft") ? "generic" : "modded";
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