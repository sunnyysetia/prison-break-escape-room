package nz.ac.auckland.se206.utils;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.GameState;

/** Handles sound effects and music that is played throughout the game. */
public class SoundUtils {
  private static MediaPlayer currentMediaPlayer;

  private static AudioClip audioClip;

  /**
   * Plays a provided audio clip to the user.
   *
   * @param fileName   the file that is to be played.
   * @param cycleCount the amount of times that it should be played.
   * @param volume     the volume level that it is to be played at.
   */
  public void playAudio(String fileName, int cycleCount, double volume) {
    if (GameState.muted.get()) {
      return;
    }
    try {
      String resourcePath = "/sounds/" + fileName;
      audioClip = new AudioClip(SoundUtils.class.getResource(resourcePath).toString());
      audioClip.setVolume(volume);
      audioClip.setCycleCount(cycleCount);
      audioClip.play();
    } catch (Exception e) {
      // Handle any exceptions that may occur (e.g., file not found)
      e.printStackTrace();
    }
  }

  /** Cuts the current audio clip that is playing. */
  public void stopAudio() {
    audioClip.stop();
  }

  /** Cut the current media file that is playing. */
  public void stopSound() {
    if (currentMediaPlayer != null) {
      currentMediaPlayer.stop();
    }
  }

  /**
   * Plays a provided media to the user.
   *
   * @param fileName the file that is to be played.
   * @param volume   the volume level that it is to be played at.
   */
  public void playSound(String fileName, double volume) {
    if (GameState.muted.get()) {
      return;
    }
    try {
      // Build the resource path to the sound file
      String resourcePath = "/sounds/" + fileName;

      // Create a Media object with the resource path
      Media sound = new Media(SoundUtils.class.getResource(resourcePath).toString());

      // Create a new MediaPlayer with the Media object
      MediaPlayer newMediaPlayer = new MediaPlayer(sound);
      newMediaPlayer.setVolume(volume);

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
