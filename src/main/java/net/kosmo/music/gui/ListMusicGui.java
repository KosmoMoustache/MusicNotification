package net.kosmo.music.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WTabPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.kosmo.music.ClientMusic;
import net.kosmo.music.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;


public class ListMusicGui extends LightweightGuiDescription {
    public MinecraftClient client;
    private final MusicManager musicManager;

    public ListMusicGui() {
        client = MinecraftClient.getInstance();
        musicManager = ClientMusic.musicManager;

        WTabPanel tabs = new WTabPanel();

//        Consumer<WTabPanel.Tab.Builder> historyConfigurator = (tab) -> tab.title(Text.translatable("gui.clientmusic.history")).icon(new TextureIcon(new Identifier(ClientMusic.MOD_ID, "textures/widget/note_1.png")));
        Consumer<WTabPanel.Tab.Builder> libraryConfigurator = (tab) -> tab.title(Text.translatable("gui.clientmusic.library")).icon(new ItemIcon(new ItemStack(Items.JUKEBOX)));

        // Library
        WBox box = new WBox(Axis.VERTICAL);
        WScrollPanel scrollPanel = new WScrollPanel(box);
        scrollPanel.setSize(200, 200);
        Library(box, scrollPanel/*, textField.getText()*/);


        // History
//        WBox box2 = new WBox(Axis.VERTICAL);
//        WScrollPanel scrollPanel2 = new WScrollPanel(box2);
//        scrollPanel2.setSize(200, 200);
//        History(box2, scrollPanel2, ClientMusic.musicHistory, ClientMusic.nowPlaying);


        tabs.add(scrollPanel, libraryConfigurator);
//        tabs.add(scrollPanel2, historyConfigurator);
        tabs.setSelectedIndex(0);

        tabs.setSize(7 * 18, 5 * 18);
        setRootPanel(tabs);
        getRootPanel().validate(this);
    }


    private void Library(WBox box, WScrollPanel scrollPanel/*, String search*/) {
//        box.add(new WLabel(Text.of(search)), 200, 20);
        for (SoundEvent sound : Registries.SOUND_EVENT) {
            if (sound.getId().getPath().contains("music")) {
                WButton button;
                Icon icon = new TextureIcon(new Identifier(ClientMusic.MOD_ID, "textures/widget/note_1.png"));
                if (sound.getId().getPath().contains("music_disc")) {
                    String discName = sound.getId().getPath().split("music_disc.")[1];
                    icon = getDiscIcon(discName);
                    try {
                        MusicManager.Entry entry = musicManager.getEntry(discName);
                        button = new WButton(icon, Text.of(entry.getTitle()));
                    } catch (Exception e) {
                        ClientMusic.LOGGER.warn("Failed to load music data for " + sound.getId().getPath());
                        button = new WButton(icon, Text.of(sound.getId().getPath()));
                    }
                } else {
                    button = new WButton(icon, Text.of(sound.getId().getPath()));
                }
                button.setOnClick(() -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.player.playSound(sound, 10.0f, 1.0f);
                });
                box.add(button, scrollPanel.getWidth() - 20, button.getHeight());
            }
        }
    }

/*
    private void History(WBox box, WScrollPanel scrollPanel, Collection<SoundInstance> musicHistory, SoundInstance nowPlaying) {
        ClientMusic.LOGGER.info("Music history {}", musicHistory);
        for (SoundInstance sound : musicHistory) {
            Identifier soundId = sound.getSound().getIdentifier();
            Icon icon = new TextureIcon(new Identifier(ClientMusic.MOD_ID, "textures/widget/note_1.png"));
            WButton button;
            if (soundId.getPath().contains("music_disc")) {
                String musicName = soundId.getPath().split("music_disc.")[1];
                try {
                    MusicManager.Entry entry = musicManager.getEntry(musicName);
                    button = new WButton(getDiscIcon(entry.getTitle()));
                    button.setLabel(Text.of(entry.getTitle()));
                    button.setOnClick(() -> {
                        ClientMusic.LOGGER.info("Clicked on {}", entry.getTitle());
                    });
                } catch (Exception e) {
                    ClientMusic.LOGGER.error("Music not found");
                    button = new WButton(icon, Text.of(sound.getId().getPath()));
                }

            } else {
                button = new WButton(icon, Text.of(sound.getId().getPath()));
            };

            button.setOnClick(() -> {
                ClientMusic.LOGGER.info("Clicked on {}", sound.getId().getPath());
            });
            box.add(button, scrollPanel.getWidth() - 20, button.getHeight());
        }
    }
    */

    private ItemIcon getDiscIcon(String discName) {
        return switch (discName) {
            case "5" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_5));
            case "11" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_11));
            case "13" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_13));
            case "blocks" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_BLOCKS));
            case "cat" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_CAT));
            case "chirp" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_CHIRP));
            case "far" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_FAR));
            case "mall" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_MALL));
            case "mellohi" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_MELLOHI));
            case "pigstep" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_PIGSTEP));
            case "stal" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_STAL));
            case "strad" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_STRAD));
            case "wait" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_WAIT));
            case "ward" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_WARD));
            case "otherside" -> new ItemIcon(new ItemStack(Items.MUSIC_DISC_OTHERSIDE));
            default -> new ItemIcon(new ItemStack(Items.AIR));
        };
    }
}