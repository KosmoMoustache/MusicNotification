package net.kosmo.music.mixin;

//? if music_frequency {
/*import net.kosmo.music.MusicManagerExtension;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.kosmo.music.config.Config;
import net.minecraft.sounds.Music;
import net.minecraft.util.RandomSource;
*///? }
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MixinMusicManager  /*? music_frequency {*//*implements MusicManagerExtension*//*?} else {*//*?}*/ {
	@Shadow
	private SoundInstance currentMusic;
	//? if music_frequency {
	/*@Shadow
	private int nextSongDelay;
	@Final
	@Shadow
	private Minecraft minecraft;
	@Final
	@Shadow
	private RandomSource random;
	@Unique
	private Config.Options.MusicFrequency gameMusicFrequency;
	*///? }

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo ci) {
		if (MusicNotificationClient.currentlyPlaying != null && Minecraft.getInstance().getSoundManager().isActive(MusicNotificationClient.currentlyPlaying)) {
			this.currentMusic = null;
			ci.cancel();
		} else {
			MusicNotificationClient.currentlyPlaying = null;
		}
	}

	//? if music_frequency {
	/*@Inject(method = "<init>", at=@At("RETURN"))
	private  void init(Minecraft minecraft, CallbackInfo ci) {
		this.gameMusicFrequency = Config.options().MUSIC_FREQUENCY;
	}

	public void setMinutesBetweenSongs(Config.Options.MusicFrequency musicFrequency) {
		this.gameMusicFrequency = musicFrequency;
		this.nextSongDelay = this.gameMusicFrequency.getNextSongDelay(this.minecraft.getSituationalMusic()/^? >=1.21.4 {^/.music()/^?} else {^//^?}^/, this.random);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 1))
	private int nextSongDelay(int original, @Local Music music) {
		this.gameMusicFrequency = Config.options().MUSIC_FREQUENCY;
		return this.gameMusicFrequency.getNextSongDelay(music, this.random);
	}
	*///? }
}
