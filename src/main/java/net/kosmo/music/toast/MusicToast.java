package net.kosmo.music.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kosmo.music.AlbumCover;
import net.kosmo.music.ClientMusic;
import net.kosmo.music.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static net.kosmo.music.ClientMusic.LOGGER;

public class MusicToast implements Toast {
    private final Type type = Type.DEFAULT;
    private long startTime;
    private boolean justUpdated;
    private Text title;
    private Text author;
    private Text soundtrack;
    private AlbumCover albumCover;

    public MusicToast(AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = albumCover;
    }

    public static void show(Sound sound) {
        Identifier identifier = sound.getIdentifier();
        MusicManager.Entry entry = ClientMusic.musicManager.get(identifier);

        if (entry != null) {
            show(MinecraftClient.getInstance().getToastManager(), entry);
        } else {
            String[] lastPartId = identifier.getPath().split("/");
            show(MinecraftClient.getInstance().getToastManager(), Text.literal(lastPartId[lastPartId.length - 1]), Text.literal(identifier.getNamespace()), Text.literal("Unknown"), AlbumCover.MODDED);
        }
    }

    public static void show(ToastManager manager, MusicManager.Entry entry) {
        show(manager, Text.literal(entry.getTitle()), Text.literal(entry.getAuthor()), Text.literal(entry.getSoundtrack()), new AlbumCover(entry.getAlbumCover()));
    }

    public static void show(ToastManager manager, Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        if (!ClientMusic.canShowToast()) {
            return;
        }

        LOGGER.info("Showing toast: {} by {} ({}) {}", title.getString(), author.getString(), soundtrack.getString(), albumCover.textureId);

        MusicToast musicToast = manager.getToast(MusicToast.class, Type.DEFAULT);

        if (musicToast == null) {
            manager.add(new MusicToast(albumCover, title, author, soundtrack));
        } else {
            musicToast.setContent(title, author, soundtrack, albumCover);
        }
    }

    public void setContent(Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        LOGGER.info("Setting content: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = albumCover;
        this.justUpdated = true;
    }

    /**
     * When {@link net.kosmo.music.ModConfig#SHOW_SOUNDTRACK_NAME} is false, when another toast is shown, the soundtrack
     * is hided by the new toast
     */
    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        RenderSystem.setShaderTexture(0,  TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 96, getWidth(), getHeight());

        this.albumCover.drawAlbumCover(matrices, 6, 6);

        manager.getClient().textRenderer.draw(matrices, this.title, 30, 7, -11534256);

        if (!ClientMusic.config.HIDE_AUTHOR) {
            manager.getClient().textRenderer.draw(matrices, this.author, 30, 18, -16777216);
        }

        if (ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
            // max length 23 chars
            manager.getClient().textRenderer.draw(matrices, this.soundtrack, 30, 29, -16777216);
        }

        return (double) (startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getHeight() {
        if (ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
            return 44;
        }
        return 32;
    }

    @Override
    public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), ClientMusic.config.SHOW_SOUNDTRACK_NAME ? 44 : 32);
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        DEFAULT
    }
}
