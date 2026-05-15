package game.view;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Main extends Application {

    public static MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {

        playMusic();

        // Open start screen properly
        StartScreen startScreen = new StartScreen();
        startScreen.start(stage);
    }

    private void playMusic() {

        try {

            String musicFile = getClass()
                    .getResource("/assets/Monsters Inc theme full.mp3")
                    .toExternalForm();

            Media media = new Media(musicFile);

            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            mediaPlayer.setVolume(0.5);

            mediaPlayer.play();

        } catch (Exception e) {

            System.out.println("Music Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}