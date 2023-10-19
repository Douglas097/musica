package com.music;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImprovedMusicPlayer {
    private ArrayList<File> playlist = new ArrayList<>();
    private int currentTrackIndex = 0;
    private boolean playingStatus = false;
    private Player player;

    // Método para adicionar uma música à playlist
    public void addToPlaylist(File file) {
        playlist.add(file);
    }

    // Método para iniciar a reprodução
    public void play() {
        if (!playlist.isEmpty()) {
            playingStatus = true;
            File currentTrack = playlist.get(currentTrackIndex);

            try {
                if (currentTrack.getName().toLowerCase().endsWith(".mp3")) {
                    FileInputStream fileInputStream = new FileInputStream(currentTrack);
                    player = new Player(fileInputStream);

                    new Thread(() -> {
                        try {
                            player.play();
                        } catch (JavaLayerException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(currentTrack);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();

                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            if (playingStatus) {
                                playNext();
                            }
                        }
                    });
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | JavaLayerException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Playlist vazia. Adicione músicas para reproduzir.");
        }
    }

    // Método para pausar a reprodução
    public void pause() {
        playingStatus = false;
        if (player != null) {
            player.close();
        }
    }

    // Método para parar a reprodução
    public void stop() {
        playingStatus = false;
        if (player != null) {
            player.close();
            player = null; // Define o player como nulo após fechar
        }
    }

    // Método para tocar a próxima música na playlist
    public void playNext() {
        if (currentTrackIndex < playlist.size() - 1) {
            currentTrackIndex++;
            play();
        } else {
            System.out.println("Fim da playlist.");
            stop();
        }
    }

    // Método principal para iniciar o programa
    public static void main(String[] args) {
        ImprovedMusicPlayer player = new ImprovedMusicPlayer();
        JFrame frame = new JFrame("Music Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton stopButton = new JButton("Stop");
        JButton nextButton = new JButton("Próxima música");
        JButton addButton = new JButton("Adicionar música");

        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.play();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.pause();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.stop();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.playNext();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    player.addToPlaylist(selectedFile);
                    System.out.println("Música adicionada à playlist: " + selectedFile.getName());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(stopButton);
        panel.add(nextButton);
        panel.add(addButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
