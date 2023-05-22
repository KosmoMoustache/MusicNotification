package net.kosmo.music.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kosmo.music.ClientMusic;
import net.kosmo.music.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class MusicToast implements Toast {
    public static final Identifier TEXTURE = new Identifier(ClientMusic.MOD_ID, "textures/gui/toasts.png");
    private static final Type DEFAULT = Type.DEFAULT;
    private AlbumCover albumCover;
    private final Type type;
    private long startTime;
    private boolean justUpdated;
    private Text title;
    private Text author;
    private Text soundtrack;

    public MusicToast(Type type, AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        ClientMusic.LOGGER.info("Now playing: {} by {} ({})", title.getString(), author.getString(), soundtrack.getString());
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.type = type;
        this.albumCover = albumCover;
    }

    public static void show(SoundInstance soundInstance) {
        String soundName = ClientMusic.getLastSegmentOfPath(soundInstance.getSound().getIdentifier());
        MusicManager.Entry entry = ClientMusic.musicManager.getEntry(soundName.toLowerCase());

        if (entry != null) {
            show(MinecraftClient.getInstance().getToastManager(), entry);
        } else {
            show(MinecraftClient.getInstance().getToastManager(), Text.literal(soundName), Text.literal(soundInstance.getSound().getIdentifier().getNamespace()), Text.literal(""), AlbumCover.CD);
        }
    }

    public static void show(ToastManager manager, MusicManager.Entry entry) {
        show(manager, Text.literal(entry.getTitle()), Text.literal(entry.getAuthor()), Text.literal(entry.getSoundtrack()), entry.getAlbumCover());
    }

    public static void show(ToastManager manager, Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        MusicToast musicToast = manager.getToast(MusicToast.class, DEFAULT);

        if (musicToast == null) {
            add(manager, albumCover, title, author, soundtrack);
        } else {
            musicToast.setContent(title, author, soundtrack, albumCover);
        }
    }

    public static void add(ToastManager manager, AlbumCover albumCover, Text title, Text author, Text soundtrack) {
        manager.add(new MusicToast(DEFAULT, albumCover, title, author, soundtrack));
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int getRequiredSpaceCount() {
        return MathHelper.ceilDiv(this.getHeight(), ClientMusic.config.showSoundtrackName ? 44 : 32);
    }

    /**
     * When {@link net.kosmo.music.ModConfig#showSoundtrackName} is false, when another toast is shown, the soundtrack
     * is hided by the new toast
     */
    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, ClientMusic.config.showSoundtrackName ? 32 : 0, this.getWidth(), this.getHeight());
        this.albumCover.drawIcon(matrices, 6, 6);

        manager.getClient().textRenderer.draw(matrices, this.title, 30.0f, 7.0f, -11534256);
        if (!ClientMusic.config.hideAuthor) {
            manager.getClient().textRenderer.draw(matrices, this.author, 30.0f, 18.0f, -16777216);
        }
        if (ClientMusic.config.showSoundtrackName) {
            // max length 23 chars
            manager.getClient().textRenderer.draw(matrices, this.soundtrack, 30.0f, 29.0f, -16777216);
        }

        return (double)(startTime - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    public void setContent(Text title, Text author, Text soundtrack, AlbumCover albumCover) {
        this.title = title;
        this.author = author;
        this.soundtrack = soundtrack;
        this.albumCover = albumCover;
        this.justUpdated = true;
    }

    @Override
    public int getHeight() {
        if (ClientMusic.config.showSoundtrackName) {
            return 44;
        }
        return 32;
    }


    public enum Type {
        DEFAULT
    }

    public enum AlbumCover {
        CD(0, 0),
        MODDED_CD(1, 0),
        // Cover art
        ALPHA(0, 1),
        BETA(1, 1),
        AXOLOTL(2, 1),
        DRAGON_FISH(3, 1),
        SHUNIJI(0, 2),
        NETHER(1, 2),
        CAVES(2, 2),
        WILD(3, 2);

        private final int textureSlotY;
        private final int textureSlotX;

        AlbumCover(int textureSlotX, int textureSlotY) {
            this.textureSlotX = textureSlotX;
            this.textureSlotY = textureSlotY;
        }

        public void drawIcon(MatrixStack matrices, int x, int y) {
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
            RenderSystem.enableBlend();
        }
    }
}
