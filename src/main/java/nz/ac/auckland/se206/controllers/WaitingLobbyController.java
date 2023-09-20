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
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class WaitingLobbyController {

  @FXML private Group phoneGroup;
  @FXML private Button onBeginGameButton;
  @FXML private ToggleGroup tgDifficulty;
  @FXML private ToggleGroup tgTime;
  @FXML private Rectangle lightDim;

  private BooleanProperty lightsOn = new SimpleBooleanProperty();
  // Create a completely random timeline to simulate lights flickering
  private Timeline timeline = new Timeline();

  private ArrayList<String> kitchenItems =
      new ArrayList<>(
          Arrays.asList(
              "cuttingboard", "oven", "plates", "extinguisher", "kettle", "clock", "toaster"));

  @FXML
  public void initialize() {
    for (int i = 0; i < 20; i++) {
      boolean onCheck = (i % 2 == 0) ? true : false;
      timeline
          .getKeyFrames()
          .add(
              new KeyFrame(
                  javafx.util.Duration.millis(Math.random() * 1000),
                  new KeyValue(lightsOn, onCheck)));
    }

    try {
      SceneManager.delUi(AppUi.ROOM); // Delete the room UI if it exists
    } catch (Exception e) {
      e.printStackTrace();
    }
    lightDim.visibleProperty().bind(Bindings.when(lightsOn).then(true).otherwise(false));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
  }

  @FXML
  private void onBeginGame(ActionEvent event) throws IOException {
    TranslateTransition phoneTransition = new TranslateTransition();
    phoneTransition.setNode(phoneGroup);
    phoneTransition.setDuration(javafx.util.Duration.millis(500));
    phoneTransition.setByY(-550);
    Thread animationThread =
        new Thread(
            () -> {
              phoneTransition.play();
            });
    Thread changeSceneThread =
        new Thread(
            () -> {
              try {
                Thread.sleep(500);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              timeline.stop();
              Platform.runLater(
                  () -> {
                    try {
                      SceneManager.addUi(SceneManager.AppUi.ROOM, App.loadFxml("escape_room"));
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    App.setUi(AppUi.ROOM);
                  });
            });
    animationThread.setDaemon(true);
    animationThread.start();
    changeSceneThread.start();

    // Set all game state variables to their default values
    GameState.state = GameState.State.INTRO;
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

    // Get the selected radio button (difficulty)
    RadioButton selectedDifficultyButton = (RadioButton) tgDifficulty.getSelectedToggle();
    RadioButton selectedTimeButton = (RadioButton) tgTime.getSelectedToggle();

    if (selectedDifficultyButton != null && selectedTimeButton != null) {

      String selectedDifficulty = selectedDifficultyButton.getText().toLowerCase();
      System.out.println("Difficulty: " + selectedDifficulty);
      GameState.difficulty = selectedDifficulty;

      String selectedTime = selectedTimeButton.getText().toLowerCase();
      System.out.println("Time: " + selectedTime);
      if (selectedTime.equals("2:00")) {
        GameState.time = 120;
      } else if (selectedTime.equals("4:00")) {
        GameState.time = 240;
      } else if (selectedTime.equals("6:00")) {
        GameState.time = 360;
      }
      Random randomWord = new Random();

      // Set word to guess

      GameState.wordToGuess = kitchenItems.get(randomWord.nextInt(kitchenItems.size()));

      System.out.println("Word to guess: " + GameState.wordToGuess);
    }
  }
}
