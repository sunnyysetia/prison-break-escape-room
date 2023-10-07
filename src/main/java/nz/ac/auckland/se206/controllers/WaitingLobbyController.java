package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.utils.SoundUtils;

public class WaitingLobbyController {

  @FXML
  private Group phoneGroup;
  @FXML
  private Button onBeginGameButton;
  @FXML
  private ToggleGroup tgDifficulty;
  @FXML
  private ToggleGroup tgTime;
  @FXML
  private Rectangle lightDim;
  @FXML
  private Rectangle lights;

  private SoundUtils soundUtils = new SoundUtils();
  private SoundUtils flickerSound = new SoundUtils();

  private BooleanProperty lightsOn = new SimpleBooleanProperty();
  // Create a completely random timeline to simulate lights flickering
  private Timeline timeline = new Timeline();
  private int keyFrames = 15;

  private ArrayList<String> kitchenItems = new ArrayList<>(
      Arrays.asList(
          "cuttingboard", "oven", "plates", "extinguisher", "kettle", "clock", "toaster"));

  @FXML
  public void initialize() {
    flickerSound.playAudio("lightFlicker.m4a", AudioClip.INDEFINITE, 0.1);
    for (int i = 0; i < keyFrames; i++) {
      boolean onCheck = (i % 2 == 0);

      // Add the flicker key frame
      timeline
          .getKeyFrames()
          .add(
              new KeyFrame(
                  javafx.util.Duration.millis(Math.random() * 1000),
                  new KeyValue(lightsOn, onCheck)));

      // Add a pause key frame after each flicker
      timeline
          .getKeyFrames()
          .add(new KeyFrame(javafx.util.Duration.millis(2750), new KeyValue(lightsOn, onCheck)));
    }

    try {
      SceneManager.delUi(AppUi.ROOM); // Delete the room UI if it exists
    } catch (Exception e) {
      e.printStackTrace();
    }
    lightDim.visibleProperty().bind(Bindings.when(lightsOn).then(true).otherwise(false));
    lights.visibleProperty().bind(Bindings.when(lightsOn).then(true).otherwise(false));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
  }

  @FXML
  private void onBeginGame(ActionEvent event) throws IOException {
    // Create a translation animation to move the phone off the screen.
    TranslateTransition phoneTransition = new TranslateTransition();
    phoneTransition.setNode(phoneGroup);
    phoneTransition.setDuration(javafx.util.Duration.millis(500));
    phoneTransition.setByY(-550);

    Thread soundThread = new Thread(
        () -> {
          // Play bang on metal door sound effect.
          soundUtils.playSound("bangOnMetalDoor.mp3", 0.1);
        });

    // Create a thread to play the phone transition animation.
    Thread animationThread = new Thread(
        () -> {
          phoneTransition.play();
        });

    // Create a thread to change the scene after a delay.
    Thread changeSceneThread = new Thread(
        () -> {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          // Stop the timeline.
          timeline.stop();

          // Use Platform.runLater to safely change the scene UI elements.
          Platform.runLater(
              () -> {
                try {
                  // Load and set the ROOM scene UI using SceneManager and App.
                  SceneManager.addUi(SceneManager.AppUi.ROOM, App.loadFxml("escape_room"));
                } catch (IOException e) {
                  e.printStackTrace();
                }
                flickerSound.stopAudio();
                // Set the UI to the ROOM scene.
                App.setUi(AppUi.ROOM);
              });
        });

    // Set both animation threads as daemon threads and start them.
    animationThread.setDaemon(true);
    animationThread.start();
    soundThread.setDaemon(true);
    soundThread.start();
    changeSceneThread.start();

    // Reset all game state variables to their default values.
    GameState.riddleProvided = false;
    GameState.lightTipProvided = false;
    GameState.difficulty = "easy";
    GameState.wordToGuess = "kettle";
    GameState.phoneIsOpen = false;
    GameState.togglingPhone = false;
    GameState.switchingRoom = false;
    GameState.riddleSolved = false;
    GameState.currentRoom = 1;
    GameState.togglingComputer = false;
    GameState.computerIsOpen = false;
    GameState.torchIsOn.set(false);
    GameState.gptThinking.set(false);
    GameState.uvPassword = 00000000;
    GameState.computerLoggedIn = false;
    GameState.torchFound = false;
    GameState.continueEnabled = false;
    GameState.gameWon = false;
    // GameState.muted.set(false);
    GameState.togglingBattery = false;
    GameState.batteryIsOpen = false;
    GameState.batteryGameSolved = false;
    GameState.batteryPercent = 0;
    GameState.batteryForeverClosed = false;

    // Get the selected radio buttons (difficulty and time).
    RadioButton selectedDifficultyButton = (RadioButton) tgDifficulty.getSelectedToggle();
    RadioButton selectedTimeButton = (RadioButton) tgTime.getSelectedToggle();

    if (selectedDifficultyButton != null && selectedTimeButton != null) {

      // Extract the selected difficulty and set it in GameState.
      String selectedDifficulty = selectedDifficultyButton.getText().toLowerCase();
      System.out.println("Difficulty: " + selectedDifficulty);
      GameState.difficulty = selectedDifficulty;

      // Extract the selected time and set it in GameState.
      String selectedTime = selectedTimeButton.getText().toLowerCase();
      System.out.println("Time: " + selectedTime);
      if (selectedTime.equals("2:00")) {
        GameState.time = 120;
      } else if (selectedTime.equals("4:00")) {
        GameState.time = 240;
      } else if (selectedTime.equals("6:00")) {
        GameState.time = 360;
      }

      // Generate a random word from the kitchenItems list and set it in GameState.
      Random randomWord = new Random();
      GameState.wordToGuess = kitchenItems.get(randomWord.nextInt(kitchenItems.size()));
      System.out.println("Word to guess: " + GameState.wordToGuess);
    }
  }
}
