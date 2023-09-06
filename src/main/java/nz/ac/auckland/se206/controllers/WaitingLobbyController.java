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

  private ArrayList<String> easyList = new ArrayList<>(Arrays.asList("curtains", "couch", "plant"));
  private ArrayList<String> hardList =
      new ArrayList<>(Arrays.asList("painting", "lampshade", "airvent"));

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
      GameState.isRiddleResolved = false;
      GameState.isCodeFound = false;
      GameState.playerName = null;
      GameState.difficulty = "easy";
      GameState.wordToGuess = "couch";

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
      RadioButton selectedRadioButton = (RadioButton) tgDifficulty.getSelectedToggle();

      if (selectedRadioButton != null) {

        String selectedDifficulty = selectedRadioButton.getText().toLowerCase();
        System.out.println("Difficulty: " + selectedDifficulty);
        GameState.difficulty = selectedDifficulty;

        Random randomWord = new Random();

        // Set word to guess
        if (selectedDifficulty.equals("hard")) {
          GameState.wordToGuess = hardList.get(randomWord.nextInt(hardList.size()));
        } else {
          GameState.wordToGuess = easyList.get(randomWord.nextInt(easyList.size()));
        }
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
