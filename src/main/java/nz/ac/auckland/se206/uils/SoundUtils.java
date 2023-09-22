package nz.ac.auckland.se206.uils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundUtils {
    public static void playSound(String fileName) {
        try {
            // Build the resource path to the sound file
            String resourcePath = "/sounds/" + fileName;

            // Create a Media object with the resource path
            Media sound = new Media(SoundUtils.class.getResource(resourcePath).toString());

            // Create a MediaPlayer with the Media object
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(0.1);

            // Play the sound effect
            mediaPlayer.play();
        } catch (Exception e) {
            // Handle any exceptions that may occur (e.g., file not found)
            e.printStackTrace();
        }
    }
}
