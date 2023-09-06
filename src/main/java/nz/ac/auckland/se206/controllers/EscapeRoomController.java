package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** Controller class for the room view. */
public class EscapeRoomController {

  // Shared FXML
  @FXML private Group chatboxGroup;
  @FXML private Group roomGroup;
  @FXML private Group memoryGroup;
  @FXML private Group chatGroup;
  @FXML private Group pincodeGroup;
  @FXML private Button giveUpButton;
  @FXML private Label timerLabel;
  @FXML private Label speechBubble;

  // Room FXML
  @FXML private Group lightsOffGroup;
  @FXML private ImageView lightBulb;
  @FXML private Rectangle darkness;
  @FXML private Rectangle curtains;
  @FXML private Rectangle door;
  @FXML private Rectangle painting;
  @FXML private Rectangle lampshade;
  @FXML private Rectangle couch;
  @FXML private Rectangle airvent;
  @FXML private Rectangle plant;

  // Memory recall fxml
  @FXML private Pane emojisPane;
  @FXML private Button checkGuessMemory;
  @FXML private Button goBackMemory;
  @FXML private Label memoryCountdownLabel;
  @FXML private ImageView pineapple;
  @FXML private ImageView pizza;
  @FXML private ImageView burrito;
  @FXML private ImageView hotdog;
  @FXML private ImageView fries;
  @FXML private ImageView popcorn;
  @FXML private ImageView strawberry;
  @FXML private ImageView burger;
  @FXML private ImageView icecream;
  @FXML private ImageView lollipop;
  @FXML private ImageView beverage;

  // Chat fxml
  @FXML private TextArea chatTextArea;
  @FXML private TextField inputText;
  @FXML private Button sendButton;

  // Pincode fxml
  @FXML private TextField pincodeInput;
  @FXML private Button pincodeUnlock;

  // Shared
  private int remainingSeconds = 120;
  private Timeline timer;
  private ChatCompletionRequest instructionCompletionRequest;

  // Room
  private Timeline flickerAnimation;

  // Memory Recall game
  private List<String> allEmojis =
      new ArrayList<>(
          List.of(
              "pineapple",
              "pizza",
              "burrito",
              "hotdog",
              "fries",
              "popcorn",
              "strawberry",
              "burger",
              "chocolate",
              "icecream",
              "lollipop",
              "beverage"));
  private List<String> emojisToGuess = new ArrayList<>();
  private List<String> playerGuess = new ArrayList<>();

  // Chat
  private ChatCompletionRequest chatCompletionRequest;

  ///////////////
  // Shared
  ///////////////
  /**
   * Initializes the room view, it is called when the room loads.
   *
   * @throws ApiProxyException
   */
  public void initialize() throws ApiProxyException {

    // Configure the timer length based on what the user selected.
    remainingSeconds = GameState.time;

    // Start a timer for the game.
    startTimer();

    // Update the UI label to display the timer.
    updateTimerLabel();

    // Use text-to-speech to welcome the player.
    textToSpeech("Welcome to my house " + GameState.playerName + "!");

    // Send a welcome message to the user through the UI thread.
    Platform.runLater(
        () -> {
          sendMessageToUser("Welcome to my house " + GameState.playerName + "!");
        });

    // Set up a delayed execution after 4 seconds.
    CompletableFuture.delayedExecutor(4, TimeUnit.SECONDS)
        .execute(
            () -> {
              // Make darkness UI element visible.
              darkness.setVisible(true);

              // Enable liught bulb UI element
              lightBulb.setDisable(false);

              // Start an animation for flickering light.
              startFlickerAnimation();

              // Generate and display an instruction to the user about a broken light.
              Platform.runLater(
                  () -> {
                    try {
                      generateInstruction("Oh no. Looks like the light broke.");
                    } catch (ApiProxyException e) {
                      e.printStackTrace();
                    }
                  });

              // Set up another delayed execution after 1 second.
              CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
                  .execute(
                      () -> {
                        // Use text-to-speech to inform the user about the broken light.
                        textToSpeech("oh no! Looks like the light broke.");
                      });
            });

    // Configure settings for a chat completion request.
    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(100);

    // Run a GPT-based riddle using a chat message.
    runGptRiddle(
        new ChatMessage(
            "user", GptPromptEngineering.getRiddleWithGivenWord(GameState.wordToGuess)));
  }

  // Timer
  private void startTimer() {
    timer =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                event -> {
                  if (remainingSeconds > 0) {
                    remainingSeconds--;
                    updateTimerLabel();
                    if (remainingSeconds == 60) {
                      textToSpeech("You have 1 minute remaining");
                    }
                  } else {
                    timer.stop();
                    handleTimerExpired();
                  }
                }));
    timer.setCycleCount(Animation.INDEFINITE);
    timer.play();
  }

  private void updateTimerLabel() {
    int minutes = remainingSeconds / 60;
    int seconds = remainingSeconds % 60;
    String timeText = String.format("%02d:%02d", minutes, seconds);

    // Change timer color from black to red as time runs out
    double progress = 1.0 - (double) remainingSeconds / (GameState.time);
    Color textColor = Color.BLACK.interpolate(Color.RED, progress);

    timerLabel.setTextFill(textColor);
    timerLabel.setText(timeText);
  }

  private void handleTimerExpired() {
    Platform.runLater(
        () -> {
          textToSpeech("Time's up! You ran out of time!");
          showDialog("Time's Up", "Game Over", "You ran out of time!");
          returnToWaitingLobby();
        });
  }

  // Key Presses

  /**
   * Handles the key pressed event in the input text field. If the Enter key is pressed and the
   * input is not blank, triggers the onSendMessage function.
   *
   * @param event the key event
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onKeyPressed(KeyEvent event) {
    System.out.println("key " + event.getCode() + " pressed");
    if (event.getCode() == KeyCode.ENTER) {
      // Prevent the Enter key event from propagating further
      event.consume();

      // Call onSendMessage if input is not blank
      String message = inputText.getText().trim();
      if (!message.isEmpty()) {
        try {
          onSendMessage(null);
        } catch (ApiProxyException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  private void onKeyReleased(KeyEvent event) {
    System.out.println("key " + event.getCode() + " released");
  }

  // Give up button
  @FXML
  private void onGiveUp(ActionEvent event) {
    System.out.println("Give up button clicked");
    timer.stop();
    showDialog("Give Up", "Game Over", "You gave up!");
    returnToWaitingLobby();
  }

  private void returnToWaitingLobby() {
    timer.stop();

    App.setUi(AppUi.WAITING_LOBBY);
  }

  // Navigation

  @FXML
  private void navigateToMemory() throws ApiProxyException {
    generateInstruction("I will fix the light if you can remember my favourite emojis");
    roomGroup.setVisible(false);
    memoryGroup.setVisible(true);
    chatGroup.setVisible(false);
    pincodeGroup.setVisible(false);
    startMemoryRecallGame();
  }

  @FXML
  private void navigateToPincode() {
    roomGroup.setVisible(false);
    memoryGroup.setVisible(false);
    chatGroup.setVisible(false);
    pincodeGroup.setVisible(true);
  }

  private void navigateToChat() {
    roomGroup.setVisible(false);
    memoryGroup.setVisible(false);
    chatGroup.setVisible(true);
    pincodeGroup.setVisible(false);
  }

  @FXML
  private void onNavigateToRoom() {
    roomGroup.setVisible(true);
    memoryGroup.setVisible(false);
    chatGroup.setVisible(false);
    pincodeGroup.setVisible(false);
  }

  // Dialog
  /**
   * Displays a dialog box with the given title, header text, and message.
   *
   * @param title the title of the dialog box
   * @param headerText the header text of the dialog box
   * @param message the message content of the dialog box
   */
  private void showDialog(String title, String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void sendMessageToUser(String message) {
    speechBubble.setText(message);
  }

  private void textToSpeech(String message) {

    Task<Void> speechTask =
        new Task<Void>() {
          @Override
          public Void call() throws Exception {
            TextToSpeech textToSpeech = new TextToSpeech();
            textToSpeech.speak(message);
            return null;
          }
        };

    new Thread(speechTask).start();
  }

  /**
   * Runs a GPT-based instruction generation using the provided chat message.
   *
   * @param msg The input chat message for the instruction.
   * @return Always returns null (result not used).
   * @throws ApiProxyException If there's an issue with the API proxy.
   */
  private Void runGptInstruction(ChatMessage msg) throws ApiProxyException {
    // Add the input message to the instruction completion request.
    instructionCompletionRequest.addMessage(msg);

    // Create a task for GPT instruction processing.
    Task<String> gptTask =
        new Task<String>() {
          @Override
          protected String call() throws Exception {
            try {
              // Execute the instruction completion request.
              ChatCompletionResult chatCompletionResult = instructionCompletionRequest.execute();

              // Get the first choice from the result.
              Choice result = chatCompletionResult.getChoices().iterator().next();

              // Add the result message to the instruction completion request.
              instructionCompletionRequest.addMessage(result.getChatMessage());

              // Return the content of the GPT-generated instruction.
              return result.getChatMessage().getContent();
            } catch (ApiProxyException e) {
              e.printStackTrace();
              return null;
            }
          }
        };

    // Define actions to be performed when the task succeeds.
    gptTask.setOnSucceeded(
        e -> {
          String resultContent = gptTask.getValue();

          // Print the GPT-generated result content to the console.
          System.out.println("GPT result: " + resultContent);

          // Send the GPT-generated instruction to the user.
          sendMessageToUser(resultContent);
        });

    // Create a new thread for running the GPT task.
    Thread gptThread = new Thread(gptTask, "Gpt Thread");
    gptThread.start();

    // Always returns null (result not used).
    return null;
  }

  private void generateInstruction(String instruction) throws ApiProxyException {
    instructionCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.3).setTopP(0.5).setMaxTokens(100);

    runGptInstruction(new ChatMessage("user", GptPromptEngineering.createInstruction(instruction)));
  }

  ///////////////
  // Room
  ///////////////

  // Stage 1: Fix light
  // Method to start the flicker animation
  private void startFlickerAnimation() {
    flickerAnimation =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.3),
                event -> {
                  double randomOpacity = 0.8 + Math.random() * 0.05;
                  lightBulb.setOpacity(randomOpacity - 0.2);
                  darkness.setOpacity(randomOpacity);
                }));
    flickerAnimation.setCycleCount(Animation.INDEFINITE);
    flickerAnimation.play();
  }

  @FXML
  private void changeCursorToHand(MouseEvent event) {
    Node source = (Node) event.getSource();
    if (!source.isDisabled()) {
      source.getScene().setCursor(Cursor.HAND);
    }
  }

  @FXML
  private void restoreCursorFromHand(MouseEvent event) {
    Node source = (Node) event.getSource();
    if (!source.isDisabled()) {
      source.getScene().setCursor(null);
    }
  }

  @FXML
  private void fixLight() {
    // Stop the flicker animation
    flickerAnimation.stop();
    lightsOffGroup.setVisible(false);

    door.setDisable(false);
    onNavigateToRoom();
    CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS)
        .execute(
            () -> {
              Platform.runLater(
                  () -> {
                    try {
                      generateInstruction("You fixed the light! Now locate the door.");
                    } catch (ApiProxyException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                  });
            });
  }

  // Stage 2: Solve Riddle
  @FXML
  private void changeCursor(MouseEvent event) {
    Node source = (Node) event.getSource();

    if (source instanceof Rectangle) {
      String rectangleName = ((Rectangle) source).getId();
      Tooltip tooltip = new Tooltip(rectangleName);

      // Set a shorter tooltip delay (in milliseconds)
      tooltip.setShowDelay(Duration.millis(100));

      Tooltip.install(source, tooltip);
    }
  }

  @FXML
  private void restoreCursor(MouseEvent event) {
    ((Node) event.getSource()).setCursor(Cursor.DEFAULT);
  }

  /**
   * Handles the click event on the door.
   *
   * @param event the mouse event
   * @throws IOException if there is an error loading the chat view
   * @throws ApiProxyException
   */
  @FXML
  private void clickDoor(MouseEvent event) throws IOException, ApiProxyException {
    System.out.println("door clicked");
    if (!GameState.isRiddleResolved) {
      textToSpeech("You must solve a riddle to escape!");
      generateInstruction("You must solve a riddle to find the door code!");
      navigateToChat();
      return;
    }

    if (!GameState.isCodeFound) {
      showDialog(
          "Info",
          "Find the 4-digit code!",
          "You solved the riddle, now you know where the code is.");
    } else {
      navigateToPincode();
    }
  }

  /**
   * Handles the click event on Object.
   *
   * @param event the mouse event
   */
  @FXML
  private void clickObject(MouseEvent event) {
    // Find which object was clicked
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    String rectangleId = clickedRectangle.getId();
    System.out.println("Object clicked: " + rectangleId);

    // Handle click
    if (GameState.isRiddleResolved) {
      if (GameState.wordToGuess.equals(rectangleId)) {
        showDialog(
            "Info",
            "You found a note under the " + rectangleId,
            "Note to self: \n"
                + "Stewie's birthday is on 24th August. \n"
                + "The door code is: "
                + GameState.pincode
                + ". \n"
                + "Lois will be back in town on Friday. \n");
        GameState.isCodeFound = true;
      } else {
        showDialog("Info", "Keep looking!", "You found nothing under the " + rectangleId + "!");
      }
    }
  }

  ////////////////////////
  // MemoryRecall Game
  ////////////////////////

  private void startMemoryRecallGame() {
    // Start with a clean slate
    emojisToGuess.clear();
    playerGuess.clear();
    checkGuessMemory.setVisible(false);
    memoryCountdownLabel.setVisible(true);

    for (String emoji : allEmojis) {
      setImageView(false, emoji, true);
    }

    // Generate random number of emojis between min and max
    int minEmojis = 3;
    int maxEmojis = 5;

    Random random = new Random();
    int numberOfEmojisToGuess = random.nextInt(maxEmojis - minEmojis + 1) + minEmojis;

    // Select a random subset of emojis to guess
    while (emojisToGuess.size() < numberOfEmojisToGuess) {
      int randomIndex = random.nextInt(allEmojis.size());
      String selectedEmoji = allEmojis.get(randomIndex);

      // Avoid duplicate selections
      if (!emojisToGuess.contains(selectedEmoji)) {
        emojisToGuess.add(selectedEmoji);
      }
    }

    // Show the emojis to guess
    for (String emoji : emojisToGuess) {
      setImageView(true, emoji, true);
    }

    // Countdown Label
    int countdownSeconds = 5;

    new Thread(
            () -> {
              for (int i = countdownSeconds; i >= 0; i--) {
                final int remainingTime = i;
                Platform.runLater(
                    () -> {
                      memoryCountdownLabel.setText(
                          "You have " + remainingTime + " seconds to remember");
                    });

                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            })
        .start();

    // Execute code after countdown
    CompletableFuture.delayedExecutor(countdownSeconds, TimeUnit.SECONDS)
        .execute(
            () -> {
              Platform.runLater(
                  () -> {
                    memoryCountdownLabel.setVisible(false);
                    for (String emoji : allEmojis) {
                      setImageView(true, emoji, false);
                    }
                    checkGuessMemory.setVisible(true);
                  });
            });
  }

  @FXML
  private void clickEmoji(MouseEvent event) {
    // Find which object was clicked
    ImageView clickedEmoji = (ImageView) event.getSource();
    String emojiId = clickedEmoji.getId();
    System.out.println("Object clicked: " + emojiId);

    if (playerGuess.contains(emojiId)) {
      playerGuess.remove(emojiId);
      clickedEmoji.setEffect(null);
      return;
    } else {
      DropShadow shadow = new DropShadow(25, Color.CYAN);
      clickedEmoji.setEffect(shadow);
      playerGuess.add(emojiId);
    }
  }

  private void setImageView(Boolean show, String fxid, Boolean disable) {
    Node node = emojisPane.lookup("#" + fxid);
    if (node instanceof ImageView) {
      ImageView imageView = (ImageView) node;
      imageView.setVisible(show);
      imageView.setDisable(disable);
      imageView.setEffect(null);
    }
  }

  private void handleWrongGuess() throws ApiProxyException {
    textToSpeech("You guessed incorrectly!");
    sendMessageToUser("You failed me! I thought we were friends " + GameState.playerName + "!");
    CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS)
        .execute(
            () -> {
              Platform.runLater(
                  () -> {
                    sendMessageToUser("Let's try again!");
                  });

              startMemoryRecallGame();
            });
  }

  @FXML
  private void onSubmitGuessMemory(ActionEvent event) throws ApiProxyException {
    System.out.println("playerGuess: " + playerGuess);
    System.out.println("emojisToGuess: " + emojisToGuess);
    if (playerGuess.size() != emojisToGuess.size()) {
      handleWrongGuess();
      return;
    }

    for (String emoji : emojisToGuess) {
      if (!playerGuess.contains(emoji)) {
        handleWrongGuess();
        return;
      }
    }
    sendMessageToUser(
        "You guessed correctly. It is like we are best friends " + GameState.playerName + "!");
    textToSpeech("You guessed correctly!");
    fixLight();
  }

  ///////////////
  // Chat
  ///////////////

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    chatTextArea.appendText(msg.getRole() + ": " + msg.getContent() + "\n\n");
  }

  /**
   * Runs a GPT-based riddle using the provided chat message.
   *
   * @param msg The input chat message for the riddle.
   * @return The response chat message from GPT.
   * @throws ApiProxyException If there's an issue with the API proxy.
   */
  private ChatMessage runGptRiddle(ChatMessage msg) throws ApiProxyException {
    // Add the input message to the chat completion request.
    chatCompletionRequest.addMessage(msg);

    // Create a task for GPT processing.
    Task<ChatMessage> gptTask =
        new Task<ChatMessage>() {
          @Override
          protected ChatMessage call() throws Exception {
            try {
              // Execute the chat completion request.
              ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();

              // Get the first choice from the result.
              Choice result = chatCompletionResult.getChoices().iterator().next();

              // Add the result message to the chat completion request.
              chatCompletionRequest.addMessage(result.getChatMessage());

              // Return the GPT-generated response message.
              return result.getChatMessage();
            } catch (ApiProxyException e) {
              e.printStackTrace();
              return null;
            }
          }
        };

    // Define actions to be performed when the task succeeds.
    gptTask.setOnSucceeded(
        e -> {
          ChatMessage resultMessage = gptTask.getValue();

          if (resultMessage != null) {
            // Append the GPT response message to the chat.
            appendChatMessage(resultMessage);

            // Check if the response indicates a correct riddle answer.
            if (resultMessage.getRole().equals("assistant")
                && resultMessage.getContent().startsWith("Correct")) {
              GameState.isRiddleResolved = true;
            }
          }
        });

    // Create a new thread for running the GPT task.
    Thread gptThread = new Thread(gptTask, "Gpt Thread");
    gptThread.start();

    // Return null for now (the actual return value is not used).
    return null;
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    String message = inputText.getText();
    if (message.trim().isEmpty()) {
      return;
    }
    inputText.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    runGptRiddle(msg);
  }

  ///////////////
  // Pincode
  ///////////////

  @FXML
  private void onPincodeAttempt(ActionEvent event) {
    String pincode = pincodeInput.getText();
    if (pincode.equals(GameState.pincode)) {
      timer.stop();
      textToSpeech("You Won! Good Job!");
      showDialog("Info", "You Won!", "Good Job!");
      returnToWaitingLobby();
    } else {
      showDialog("Info", "Incorrect", "You guessed incorrectly!");
    }
  }
}
