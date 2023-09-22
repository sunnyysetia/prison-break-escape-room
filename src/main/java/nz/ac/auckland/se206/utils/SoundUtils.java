package nz.ac.auckland.se206.utils;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundUtils {
  private static MediaPlayer currentMediaPlayer;

  private static AudioClip audioClip;

  public void playAudio(String fileName, int cycleCount) {
    try {
      String resourcePath = "/sounds/" + fileName;
      audioClip = new AudioClip(SoundUtils.class.getResource(resourcePath).toString());
      audioClip.setVolume(0.1);
      audioClip.setCycleCount(cycleCount);
      audioClip.play();
    } catch (Exception e) {
      // Handle any exceptions that may occur (e.g., file not found)
      e.printStackTrace();
    }
  }

  public void stopAudio() {
    audioClip.stop();
  }

  public void playSound(String fileName) {
    try {
      // Build the resource path to the sound file
      String resourcePath = "/sounds/" + fileName;

      // Create a Media object with the resource path
      Media sound = new Media(SoundUtils.class.getResource(resourcePath).toString());

      // Create a new MediaPlayer with the Media object
      MediaPlayer newMediaPlayer = new MediaPlayer(sound);
      newMediaPlayer.setVolume(0.1);

      // Check if there is a currently playing sound
      if (currentMediaPlayer != null
          && currentMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
        // A sound is already playing, stop it
        currentMediaPlayer.stop();
      }

      // Play the sound effect
      newMediaPlayer.play();

      // Update the currentMediaPlayer
      currentMediaPlayer = newMediaPlayer;

      // Set an event handler to clear the currentMediaPlayer when playback ends
      currentMediaPlayer.setOnEndOfMedia(() -> currentMediaPlayer = null);
    } catch (Exception e) {
      // Handle any exceptions that may occur (e.g., file not found)
      e.printStackTrace();
    }
  }
}
