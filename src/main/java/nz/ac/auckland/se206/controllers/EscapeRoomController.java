package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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

  // This pane contains all the three panes below, we move this pane left and right
  @FXML private Pane roomCollectionPane;

  // These three panes below are the individual rooms
  @FXML private Pane prisonCellPane;
  @FXML private Pane cafeteriaPane;
  @FXML private Pane guardRoomPane;

  // Shared FXML
  @FXML private Group chatGroup;
  @FXML private Label timerLabel;
  @FXML private Label hintLabel;
  @FXML private Rectangle dimScreen;
  @FXML private ImageView leftButton;
  @FXML private ImageView rightButton;
  @FXML private Circle notifCircle;

  // Chat fxml
  @FXML private Button send_button;
  @FXML private TextField messagesTextField;
  @FXML private VBox messagesVBox;
  @FXML private ScrollPane chatScrollPane;

  // Shared
  private int remainingSeconds = 120;
  private Timeline timer;
  private ChatCompletionRequest instructionCompletionRequest;

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

    // Set the state of the game.
    GameState.state = GameState.State.INTRO;

    // Start a timer for the game.
    startTimer();

    // Update the UI label to display the timer.
    updateTimerLabel();

    // Binds send button so that it is disabled while gpt is writing a message.
    send_button.disableProperty().bind(GameState.gptThinking);

    // Set the hint label to display 0 hints.
    hintLabel.setText("0");

    chatScrollPane.setFitToWidth(true);
    messagesVBox
        .heightProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                chatScrollPane.setVvalue((Double) newValue);
              }
            });

    // On send message
    send_button.setOnAction(
        (EventHandler<ActionEvent>)
            new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent event) {
                System.out.println("Send Button clicked");
                String message = messagesTextField.getText();
                if (!message.isEmpty() && !GameState.gptThinking.getValue()) {
                  HBox hBox = new HBox();
                  hBox.setAlignment(Pos.CENTER_RIGHT);
                  hBox.setPadding(new Insets(5, 5, 5, 10));
                  Text text = new Text(message);
                  TextFlow textFlow = new TextFlow(text);
                  textFlow.setStyle(
                      "-fx-color: rgb(239,242,255); "
                          + "-fx-background-color: rgb(15,125,242); "
                          + "-fx-background-radius: 10px; ");
                  textFlow.setPadding(new Insets(5, 10, 5, 10));
                  text.setFill(Color.color(0.934, 0.945, 0.996));
                  hBox.getChildren().add(textFlow);
                  messagesVBox.getChildren().add(hBox);
                  messagesTextField.clear();

                  try {
                    runGptRiddle(new ChatMessage("user", message));
                  } catch (ApiProxyException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }
              }
            });

    // Configure settings for a chat completion request.
    chatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(100);

    // Run a GPT-based riddle using a chat message.
    runGptRiddle(
        new ChatMessage(
            "user",
            GptPromptEngineering.getRiddleWithGivenWord(
                GameState.wordToGuess, GameState.difficulty)));
  }

  // on recieve message, run in different thread
  private static void addLabel(String messageFromGPT, VBox vbox) {
    System.out.println("GPT sent user a message");
    HBox hBox = new HBox();
    hBox.setAlignment(Pos.CENTER_LEFT);
    hBox.setPadding(new Insets(5, 5, 5, 10));
    Text text = new Text(messageFromGPT);
    TextFlow textFlow = new TextFlow(text);
    textFlow.setStyle("-fx-background-color: rgb(233,233,235); " + "-fx-background-radius: 10px; ");
    textFlow.setPadding(new Insets(5, 10, 5, 10));
    hBox.getChildren().add(textFlow);
    // vbox.getChildren().add(hBox);
    Platform.runLater(
        new Runnable() {
          @Override
          public void run() {
            vbox.getChildren().add(hBox);
          }
        });
  }

  private void togglePhone() {
    System.out.println("toggling phone");
    GameState.togglingPhone = true;
    Thread waitThread =
        new Thread(
            () -> {
              try {
                Thread.sleep(700);
                GameState.togglingPhone = false;
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
    waitThread.setDaemon(true);
    waitThread.start();
    final TranslateTransition phoneSwitch = new TranslateTransition();
    phoneSwitch.setNode(chatGroup);
    phoneSwitch.setDuration(javafx.util.Duration.millis(500));
    if (GameState.phoneIsOpen) {
      phoneSwitch.setByY(-550);
      GameState.phoneIsOpen = false;
      dimScreen.setDisable(true);
      dimScreen.setVisible(false);
    } else {
      phoneSwitch.setByY(550);
      GameState.phoneIsOpen = true;
      dimScreen.setDisable(false);
      dimScreen.setVisible(true);
      notifCircle.setVisible(false);
    }
    phoneSwitch.play();
  }

  @FXML
  public void openPhone(MouseEvent event) {
    System.out.println("Phone clicked");
    if (GameState.togglingPhone) {
      return;
    } else {
      togglePhone();
    }
  }

  private void switchRoom(int nextRoom) {
    GameState.switchingRoom = true;
    // use a new method to switch between rooms to prevent spamming and causing visual glitches
    Thread waitThread =
        new Thread(
            () -> {
              try {
                Thread.sleep(700);
                GameState.switchingRoom = false;
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
    waitThread.setDaemon(true);
    waitThread.start();
    final TranslateTransition roomSwitch = new TranslateTransition();
    roomSwitch.setNode(roomCollectionPane);
    roomSwitch.setDuration(javafx.util.Duration.millis(500));
    if (nextRoom > GameState.currentRoom) {
      roomSwitch.setByX(-1022);
    } else {
      roomSwitch.setByX(1022);
    }
    roomSwitch.play();
    GameState.currentRoom = nextRoom;

    if (GameState.currentRoom == 0) {
      leftButton.setVisible(false);
    } else if (GameState.currentRoom == 2) {
      rightButton.setVisible(false);
    } else {
      leftButton.setVisible(true);
      rightButton.setVisible(true);
    }
  }

  // plays the animation for moving left
  @FXML
  public void leftPane(MouseEvent event) {
    System.out.println("Left switch clicked");
    if (GameState.currentRoom == 0 || GameState.switchingRoom) {
      return;
    } else {
      switchRoom(GameState.currentRoom - 1);
    }
  }

  // plays the animation for moving right
  @FXML
  public void rightPane(MouseEvent event) {
    System.out.println("Right switch clicked");
    if (GameState.currentRoom == 2 || GameState.switchingRoom) {
      return;
    } else {
      switchRoom(GameState.currentRoom + 1);
    }
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
          // showDialog("Time's Up", "Game Over", "You ran out of time!");
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
      // event.consume();
      if (GameState.phoneIsOpen) {
        send_button.fire();
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

  private void returnToWaitingLobby() {
    timer.stop();

    App.setUi(AppUi.WAITING_LOBBY);
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
    GameState.gptThinking.setValue(true);

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
          addLabel(resultContent, messagesVBox);

          GameState.gptThinking.setValue(false);
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
  // Chat
  ///////////////

  // /**
  //  * Appends a chat message to the chat text area.
  //  *
  //  * @param msg the chat message to append
  //  */
  // private void appendChatMessage(ChatMessage msg) {
  //   chatTextArea.appendText(msg.getRole() + ": " + msg.getContent() + "\n\n");
  // }

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
    GameState.gptThinking.setValue(true);

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
            addLabel(resultMessage.getContent(), messagesVBox);
            // Check if the response indicates a correct riddle answer.
            if (!GameState.phoneIsOpen) {
              notifCircle.setVisible(true);
            }
            if (resultMessage.getRole().equals("assistant")
                && resultMessage.getContent().startsWith("Correct")) {
              // GameState.state = GameState.State.FIND_ID;
            }
            if (resultMessage.getContent().contains("hint")) {
              try {
                int hint = Integer.parseInt(hintLabel.getText());
                hint++;
                hintLabel.setText("" + hint);
              } catch (NumberFormatException ex) {
                hintLabel.setText("Error");
              }
            }
          }
          GameState.gptThinking.setValue(false);
        });

    // Create a new thread for running the GPT task.
    Thread gptThread = new Thread(gptTask, "Gpt Thread");
    gptThread.start();

    // Return null for now (the actual return value is not used).
    return null;
  }

  // /**
  //  * Sends a message to the GPT model.
  //  *
  //  * @param event the action event triggered by the send button
  //  * @throws ApiProxyException if there is an error communicating with the API proxy
  //  * @throws IOException if there is an I/O error
  //  */
  // @FXML
  // private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
  //   String message = inputText.getText();
  //   if (message.trim().isEmpty()) {
  //     return;
  //   }
  //   inputText.clear();
  //   ChatMessage msg = new ChatMessage("user", message);
  //   appendChatMessage(msg);
  //   runGptRiddle(msg);
  // }
}
