package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class WaitingLobbyController {

  @FXML private TextField nameField;
  @FXML private Button onBeginGameButton;
  @FXML private ToggleGroup tgDifficulty;
  @FXML private ToggleGroup tgTime;

  private ArrayList<String> kitchenItems =
      new ArrayList<>(
          Arrays.asList(
              "cuttingboard", "oven", "plates", "extinguisher", "kettle", "clock", "toaster"));

  @FXML
  public void initialize() {
    // Check if name already exists in GameState and populate nameField
    if (GameState.playerName != null && !GameState.playerName.isEmpty()) {
      nameField.setText(GameState.playerName);
      // Prevent the text field from receiving focus
      nameField.setFocusTraversable(false);
    }

    // Bind the disable property of the "Begin Game" button to the empty property of the nameField
    onBeginGameButton.disableProperty().bind(Bindings.isEmpty(nameField.textProperty()));
  }

  @FXML
  private void onBeginGame(ActionEvent event) throws IOException {
    String playerName = nameField.getText().trim();
    if (!playerName.isEmpty()) {
      // Set all game state variables to their default values
      GameState.state = GameState.State.INTRO;
      GameState.playerName = null;
      GameState.difficulty = "easy";
      GameState.wordToGuess = "kettle";
      GameState.phoneIsOpen = false;
      GameState.togglingPhone = false;
      GameState.switchingRoom = false;
      GameState.currentRoom = 1;

      // Generate random 4-digit pincode
      Random randomCode = new Random();
      GameState.pincode = String.format("%04d", randomCode.nextInt(10000));

      // Ask GPT for a funny comment about the person's name
      String funnyComment = generateFunnyComment(playerName);

      // Log the name and funny comment to the console
      System.out.println("Player's Name: " + playerName);
      System.out.println("Funny Comment: " + funnyComment);
      // Save the entered name
      GameState.playerName = playerName;

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
        SceneManager.addUi(SceneManager.AppUi.ROOM, App.loadFxml("escape_room"));
        App.setUi(AppUi.ROOM);
      }
    }
  }

  private String generateFunnyComment(String name) {
    return "GPT-generated funny comment about " + name;
  }
}
