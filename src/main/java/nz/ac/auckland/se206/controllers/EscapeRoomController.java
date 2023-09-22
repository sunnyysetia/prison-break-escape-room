package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
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

  // This pane contains all the three panes below, we move this pane left and
  // right
  @FXML
  private Pane roomCollectionPane;

  // These three panes below are the individual rooms
  @FXML
  private Pane prisonCellPane;
  @FXML
  private Pane kitchenPane;
  @FXML
  private Pane guardRoomPane;

  // Shared FXML
  @FXML
  private Group chatGroup;
  @FXML
  private Group computerGroup;
  @FXML
  private Label timerLabel;
  @FXML
  private Label hintLabel;
  @FXML
  private Rectangle dimScreen;
  @FXML
  private ImageView leftButton;
  @FXML
  private ImageView rightButton;
  @FXML
  private Label phoneLabel;
  @FXML
  private Circle notifCircle;
  @FXML
  private ImageView torchButton;
  @FXML
  private SVGPath uvLightEffect;

  // Kitchen FXML
  @FXML
  private Rectangle cuttingboard;
  @FXML
  private Rectangle oven;
  @FXML
  private Rectangle plates;
  @FXML
  private Rectangle extinguisher;
  @FXML
  private Rectangle kettle;
  @FXML
  private Rectangle clock;
  @FXML
  private Rectangle toaster;
  @FXML
  private Group torchGetGroup;

  // Cell FXML
  @FXML
  private Text uvLightText;

  // Guard's Room FXML
  @FXML
  private Rectangle circuit;
  @FXML
  private Rectangle computer;
  @FXML
  private Button computerCloseButton;
  @FXML
  private Rectangle computerDimScreen;
  @FXML
  private TextField computerPasswordField;
  @FXML
  private TextArea computerConsoleTextArea;
  @FXML
  private Button computerLoginButton;
  @FXML
  private AnchorPane endingControlAnchorPane;
  @FXML
  private AnchorPane computerConsoleAnchorPane;

  @FXML
  private AnchorPane finsihedGamePane;
  @FXML
  private Group endPhoneGroup;
  @FXML
  private TextArea endGameTA;
  @FXML
  private TextArea clickContinueTA;
  @FXML
  private Label endPhoneTitle;
  @FXML
  private Label endPhoneMessage;
  @FXML
  private ImageView endGameImage;
  @FXML
  private AnchorPane gameEndPane;
  @FXML
  private ImageView guardRoomDarkness;
  @FXML
  private Group circuitGroup;
  @FXML
  private Label memoryCountdownLabel;
  @FXML
  private Button goBackMemory;
  @FXML
  private Button checkGuessMemory;
  @FXML
  private Rectangle a1;
  @FXML
  private Rectangle b1;
  @FXML
  private Rectangle c1;
  @FXML
  private Rectangle a2;
  @FXML
  private Rectangle b2;
  @FXML
  private Rectangle c2;
  @FXML
  private Rectangle a3;
  @FXML
  private Rectangle b3;
  @FXML
  private Rectangle c3;
  @FXML
  private Rectangle a4;
  @FXML
  private Rectangle b4;
  @FXML
  private Rectangle c4;
  @FXML
  private Rectangle a5;
  @FXML
  private Rectangle b5;
  @FXML
  private Rectangle c5;

  private List<String> allSwitches = new ArrayList<>(
      List.of(
          "a1", "b1", "c1", "a2", "b2", "c2", "a3", "b3", "c3", "a4", "b4", "c4", "a5", "b5",
          "c5"));

  private List<String> switchesToRecall = new ArrayList<>();
  private List<String> playerChoices = new ArrayList<>();
  private Thread countdownThread;
  // " 1. Unlock all prison cell doors"
  // " 2. Explode all walls"
  // " 3. Retrieve new uniform"
  private HashMap<String, String> endingMap = new HashMap<>() {
    {
      put(
          "1",
          "On %s %sth, a shocking incident unfolded at The Prison as a"
              + " software malfunction inadvertently unlocked all prison cell doors."
              + " Pandemonium ensued as prisoners took advantage of this unexpected opportunity"
              + " to escape their confinement. Authorities are working diligently to regain"
              + " control and investigate the software glitch responsible for this"
              + " unprecedented breach of security.");
      put(
          "2",
          "On %s %sth, chaos erupted at The Prison as a series of"
              + " explosions caused prison walls to crumble, granting inmates an unexpected"
              + " route to freedom. Inmates wasted no time seizing the opportunity, fleeing the"
              + " facility in a frantic rush. Authorities are now in pursuit of the escapees,"
              + " while an investigation is underway to determine the cause of the explosive"
              + " breach in the prison's perimeter.");
      put(
          "3",
          "On %s %sth, a surprising incident occurred at The Prison. One"
              + " prisoner had disappeared, leaving an empty cell behind, and a prison guard"
              + " uniform was missing. It appeared that the inmate had managed to escape by"
              + " impersonating a guard. Authorities are now actively searching for the"
              + " escapee, while questions regarding the security lapse loom large.");
    }
  };

  // Chat fxml
  @FXML
  private Button sendButton;
  @FXML
  private TextField messagesTextField;
  @FXML
  private VBox messagesVertBox;
  @FXML
  private ScrollPane chatScrollPane;
  @FXML
  private Label phoneNameLabel;
  @FXML
  private ImageView typingImage;

  // Shared
  private int remainingSeconds = 120;
  private Timeline timer;
  private ScaleTransition heartbeatAnimation;
  private int hintsRemaining = 5;

  // Chat
  private ChatCompletionRequest chatCompletionRequest;

  // UV code
  private HashMap<Integer, int[]> uvCodeLocations = new HashMap<Integer, int[]>() {
    {
      put(-45, new int[] { 733, 300 });
      put(-30, new int[] { 734, 187 });
      put(30, new int[] { 233, 623 });
      put(31, new int[] { 33, 221 });
      put(66, new int[] { 558, 374 });
      put(19, new int[] { 361, 623 });
      put(32, new int[] { 362, 148 });
      put(-23, new int[] { 809, 136 });
    }
  };

  private Tooltip currentTooltip; // Maintain a reference to the current tooltip

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

    // Binds send button so that it is disabled while gpt is writing a message.
    sendButton.disableProperty().bind(GameState.gptThinking);

    // Set the hint label to display hints remaining.
    if (GameState.difficulty.equals("hard")) {
      hintLabel.setText("0");
    } else if (GameState.difficulty.equals("medium")) {
      hintLabel.setText("5/5");
    } else {
      hintLabel.setText("∞");
    }

    // Initialise notification heartbeat animation
    heartbeatAnimation = new ScaleTransition(Duration.seconds(1), notifCircle);
    heartbeatAnimation.setFromX(1.0);
    heartbeatAnimation.setToX(1.5);
    heartbeatAnimation.setFromY(1.0);
    heartbeatAnimation.setToY(1.5);
    heartbeatAnimation.setAutoReverse(true);
    heartbeatAnimation.setCycleCount(ScaleTransition.INDEFINITE);

    // Generate a different passcode everytime
    Object[] uvRotateAngles = uvCodeLocations.keySet().toArray();
    int randomAngle = (int) uvRotateAngles[(int) (Math.random() * uvRotateAngles.length)];
    int[] uvCodeLocation = uvCodeLocations.get(randomAngle);
    uvLightText.setRotate(randomAngle);
    uvLightText.setManaged(false);
    uvLightText.xProperty().setValue(uvCodeLocation[0]);
    uvLightText.yProperty().setValue(uvCodeLocation[1]);

    GameState.uvPassword = (int) (Math.random() * 100000000);
    uvLightText.setText(Integer.toString(GameState.uvPassword));
    System.out.println("uvPassword: " + GameState.uvPassword);

    chatScrollPane.setFitToWidth(true);
    messagesVertBox
        .heightProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                chatScrollPane.setVvalue((Double) newValue);
              }
            });

    computerLoginButton.setOnAction(
        (EventHandler<ActionEvent>) event -> {
          if (computerPasswordField.getText().isEmpty()) {
            return;
          }
          String userInput = computerPasswordField.getText();
          computerPasswordField.clear();
          computerConsoleTextArea.setText(
              computerConsoleTextArea.getText() + "\nC:\\PrisonPC\\>" + userInput);

          if (!GameState.computerLoggedIn) {
            if (userInput.equals(GameState.uvPassword + "")) {
              GameState.computerLoggedIn = true;
              Thread writerThread = new Thread(
                  () -> {
                    typeWrite(
                        computerConsoleTextArea,
                        "\nSystem:>Login Successful!\n"
                            + "System:>Welcome back Prison Guard!\n"
                            + "System:>Here is a list of admin functions you have access"
                            + " to:\n\n",
                        10);
                    typeWrite(
                        computerConsoleTextArea,
                        "  1. Unlock all prison cell doors\n"
                            + "  2. Explode all walls\n"
                            + "  3. Retrieve new uniform\n"
                            + "\n Choose a function by typing the corresponding number\n"
                            + "System:>Choose an Option:",
                        6);
                  });
              writerThread.setDaemon(true);
              writerThread.start();
            } else {
              Thread writerThread = new Thread(
                  () -> {
                    typeWrite(
                        computerConsoleTextArea,
                        "\n" + "System:>Incorrect Password!\n" + "System:>Enter Password:",
                        15);
                  });
              writerThread.setDaemon(true);
              writerThread.start();
            }
          } else {
            // Runs after the user logins correctly
            if (userInput.trim().equals("1")
                || userInput.trim().equals("2")
                || userInput.trim().equals("3")) {
              // call the end game method
              System.out.println("User chose option: " + userInput.trim());
              endGame(userInput.trim());
            } else {
              Thread writerThread = new Thread(
                  () -> {
                    typeWrite(
                        computerConsoleTextArea,
                        "\n" + "System:>Invalid Option!\n" + "System:>Choose an Option:",
                        15);
                  });
              writerThread.setDaemon(true);
              writerThread.start();
            }
          }
          // Use to auto scroll the pane to the bottom
          Thread scrollThread = new Thread(
              () -> {
                try {
                  Thread.sleep(100);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                Platform.runLater(
                    () -> {
                      computerConsoleTextArea.setScrollTop(Double.MAX_VALUE);
                    });
              });
          scrollThread.setDaemon(true);
          scrollThread.start();
        });

    phoneNameLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  String string;
                  if (GameState.gptThinking.get()) {
                    string = "Typing. . .";
                    typingImage.setVisible(true);
                  } else {
                    string = "Prison Guard";
                    typingImage.setVisible(false);
                  }
                  return string;
                },
                GameState.gptThinking));

    computer.setOnMouseClicked(
        (EventHandler<MouseEvent>) event -> {
          if (GameState.togglingComputer) {
            return;
          } else {
            toggleComputer();
          }
        });

    computerCloseButton.setOnAction(
        (EventHandler<ActionEvent>) event -> {
          if (GameState.togglingComputer) {
            return;
          } else {
            toggleComputer();
          }
        });

    // On send message
    sendButton.setOnAction(
        (EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            // Print a message to indicate that the Send Button was clicked.
            System.out.println("Send Button clicked");
            playSound("outgoingText.mp3");

            // Get the message from the messagesTextField.
            String message = messagesTextField.getText();

            // Check if the message is not empty and GPT is not currently generating a
            // response.
            if (!message.isEmpty() && !GameState.gptThinking.getValue()) {
              // Create an HBox for the message display and configure its properties.
              HBox horiBox = new HBox();
              horiBox.setAlignment(Pos.CENTER_RIGHT);
              horiBox.setPadding(new Insets(5, 5, 5, 10));

              // Create a Text element to display the message.
              Text text = new Text(message);

              // Create a TextFlow to display the text message with styling.
              TextFlow textFlow = new TextFlow(text);
              textFlow.setStyle(
                  "-fx-color: rgb(239,242,255); "
                      + "-fx-background-color: rgb(15,125,242); "
                      + "-fx-background-radius: 10px; ");
              textFlow.setPadding(new Insets(5, 10, 5, 10));
              text.setFill(Color.color(0.934, 0.945, 0.996));

              // Add the TextFlow to the HBox.
              horiBox.getChildren().add(textFlow);

              // Add the HBox to the messagesVertBox to display the message.
              messagesVertBox.getChildren().add(horiBox);

              // Clear the messagesTextField to prepare for the next message.
              messagesTextField.clear();

              try {
                // Send the user's message to the GPT for a response.
                runGpt(new ChatMessage("user", message));
              } catch (ApiProxyException e) {
                e.printStackTrace();
              }
            }
          }
        });

    torchButton.setOnMouseClicked(
        event -> {
          GameState.torchIsOn.setValue(!GameState.torchIsOn.getValue());
        });
    uvLightText.visibleProperty().bind(GameState.torchIsOn);

    uvLightEffect.visibleProperty().bind(GameState.torchIsOn);

    // Configure settings for the riddle's chat completion request.
    chatCompletionRequest = new ChatCompletionRequest().setN(1).setTemperature(0.3).setTopP(0.5).setMaxTokens(200);

    // Run a GPT-based instruction for the introduction.
    runGpt(new ChatMessage("user", GptPromptEngineering.getIntroInstruction(GameState.difficulty)));
  }

  @FXML
  public void exitGame(MouseEvent event) throws IOException {
    System.exit(0);
  }

  @FXML
  public void restartGame(MouseEvent event) throws IOException {
    returnToWaitingLobby();
  }

  @FXML
  public void endContinue(MouseEvent event) {
    // goes to the final final screen to show time used and buttons to restart game
    // or exit game
    if (!GameState.continueEnabled) {
      return;
    }
    GameState.continueEnabled = false; // prevent spam
    TranslateTransition backgroundTransition = new TranslateTransition();
    backgroundTransition.setNode(finsihedGamePane);
    backgroundTransition.setByY(720);
    backgroundTransition.setDuration(Duration.millis(500));
    TranslateTransition phoneTransition = new TranslateTransition();
    phoneTransition.setNode(endPhoneGroup);
    phoneTransition.setByY(550);
    phoneTransition.setDuration(Duration.millis(500));
    Thread backgroundAnimThread = new Thread(
        () -> {
          backgroundTransition.play();
        });
    Thread phoneAnimThread = new Thread(
        () -> {
          try {
            Thread.sleep(750);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          phoneTransition.play();
        });
    phoneAnimThread.setDaemon(true);
    backgroundAnimThread.setDaemon(true);
    phoneAnimThread.start();
    backgroundAnimThread.start();
  }

  private void endGame(String ending) {
    timer.stop();
    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();
    endGameTA.setMouseTransparent(true);
    clickContinueTA.setMouseTransparent(true);
    endPhoneTitle.setText("Good Luck Next Time");
    endPhoneMessage.setText("Unfortunately you failed to escape the prison within the time limit");
    String endMessage = "As the tension in the air thickens and your heart races, you push your luck to the limit"
        + " in a daring attempt to break free from your prison confines. But alas, as the"
        + " clock's relentless ticking echoes in your ears, your every move becomes more"
        + " desperate. Time slips through your fingers like sand, and despite your best"
        + " efforts, the guards' footsteps draw nearer. With a heavy heart, you realize that"
        + " your window of opportunity has closed. You failed to escape, and the prison's"
        + " unforgiving walls will continue to confine you. Try again, for the path to freedom"
        + " is as elusive as ever.";
    if (!ending.equals("0")) {
      int minutes = (int) ((GameState.time - remainingSeconds) / 60);
      int seconds = (GameState.time - remainingSeconds) % 60;
      endMessage = String.format(endingMap.get(ending), new DateFormatSymbols().getMonths()[month - 1], day);
      endPhoneTitle.setText("Congratulations!");
      endPhoneMessage.setText(
          "You escaped the prison within "
              + minutes
              + " minute"
              + ((minutes == 1) ? ""
                  : "s")
              + " and "
              + seconds
              + " second"
              + ((seconds == 1) ? "" : "s")
              + "!");
    }
    String finalEndMessage = endMessage;
    endGameImage
        .imageProperty()
        .set(new Image(App.class.getResourceAsStream("/images/ending" + ending + ".png")));
    Thread animationThread = new Thread(
        () -> {
          TranslateTransition endSlide = new TranslateTransition();
          endSlide.setNode(gameEndPane);
          endSlide.setDuration(Duration.millis(500));
          endSlide.setByY(720);
          FadeTransition endFade = new FadeTransition();
          endFade.setNode(gameEndPane);
          endFade.setDuration(Duration.millis(1000));
          endFade.setFromValue(0);
          endFade.setToValue(1);
          endFade.play();
          endSlide.play();
        });
    animationThread.setDaemon(true);
    animationThread.start();
    Thread textThread = new Thread(
        () -> {
          try {
            Thread.sleep(1250);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          typeWrite(endGameTA, finalEndMessage, 15);
          System.out.println("finished typing ending message");
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println("Finished waiting for 3 seconds");
          typeWrite(clickContinueTA, "Click anywhere to continue", 15);
          GameState.continueEnabled = true;
        });
    textThread.setDaemon(true);
    textThread.start();
  }

  private void typeWrite(TextArea sceneTextArea, String message, int interval) {
    int i = 0;
    while (i < message.length()) {
      int j = i;
      Platform.runLater(
          () -> {
            // Append the character at position j from the message to the sceneTextArea.
            sceneTextArea.setText(sceneTextArea.getText() + message.charAt(j));

            // Append an empty string to ensure the text area is updated.
            sceneTextArea.appendText("");

            // Scroll the text area to the bottom to display the new text.
            sceneTextArea.setScrollTop(Double.MAX_VALUE);
          });

      i++;

      try {
        // Pause the thread to simulate typing at the specified interval.
        Thread.sleep(interval);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // on recieve message, run in different thread
  private void addLabel(String messageFromGpt, VBox vbox) {
    // Print a message to indicate that GPT sent a message to the user.
    System.out.println("GPT sent user a message");

    // Create an HBox for displaying the GPT message and configure its properties.
    HBox horiBox = new HBox();
    horiBox.setAlignment(Pos.CENTER_LEFT);
    horiBox.setPadding(new Insets(5, 5, 5, 10));

    // Create a Text element to display the GPT message.
    Text text = new Text(messageFromGpt);

    // Create a TextFlow to display the text message with styling.
    TextFlow textFlow = new TextFlow(text);
    textFlow.setStyle("-fx-background-color: rgb(233,233,235); " + "-fx-background-radius: 10px; ");
    textFlow.setPadding(new Insets(5, 10, 5, 10));
    textFlow.setMaxWidth(450);

    // Add the TextFlow to the HBox.
    horiBox.getChildren().add(textFlow);

    // Use Platform.runLater to update the VBox with the new label on the JavaFX
    // application thread.
    Platform.runLater(
        new Runnable() {
          @Override
          public void run() {
            vbox.getChildren().add(horiBox);
          }
        });
  }

  // Navigation
  private void togglePhone() {
    // Print a message to indicate that the phone is being toggled.
    System.out.println("toggling phone");

    // Set a flag to indicate that the phone is in the process of being toggled.
    GameState.togglingPhone = true;

    // Wait for 500 milliseconds (0.5 seconds) before continuing.
    wait(
        500,
        () -> {
          // Once the wait is over, set the flag to indicate that the phone toggle is
          // complete.
          GameState.togglingPhone = false;
        });

    // Create a translation animation for the phone switch.
    final TranslateTransition phoneSwitch = new TranslateTransition();
    phoneSwitch.setNode(chatGroup);

    // Set the duration of the animation to 500 milliseconds (0.5 seconds).
    phoneSwitch.setDuration(Duration.millis(500));

    // Check if the phone is currently open.
    if (GameState.phoneIsOpen) {
      // Update phone label
      phoneLabel.setLayoutX(-6);
      // phoneLabel.setLayoutY(58);
      phoneLabel.setText("Phone");
      // If the phone is open, move it upwards by 550 units.
      phoneSwitch.setByY(-550);
      // Update the phone state to indicate it's closed.
      GameState.phoneIsOpen = false;
      // Disable and hide the dim screen overlay.
      dimScreen.setDisable(true);
      dimScreen.setVisible(false);
    } else {
      // Update phone label
      phoneLabel.setLayoutX(-3);
      phoneLabel.setText("Close");
      // If the phone is closed, move it downwards by 550 units.
      phoneSwitch.setByY(550);
      // Update the phone state to indicate it's open.
      GameState.phoneIsOpen = true;
      // Turn off the torch (assuming GameState.torchIsOn represents torch state).
      GameState.torchIsOn.setValue(false);
      // Enable and show the dim screen overlay.
      dimScreen.setDisable(false);
      dimScreen.setVisible(true);
      // Hide the notification circle.
      notifCircle.setVisible(false);
      heartbeatAnimation.stop();
    }

    // Play the phone switch animation.
    phoneSwitch.play();
  }

  private void toggleComputer() {
    // Print a message to indicate that the computer is being toggled.
    System.out.println("toggling computer");

    // Set a flag to indicate that the computer is in the process of being toggled.
    GameState.togglingComputer = true;

    // Wait for 500 milliseconds (0.5 seconds) before continuing.
    wait(
        500,
        () -> {
          // Once the wait is over, set the flag to indicate that the computer toggle is
          // complete.
          GameState.togglingComputer = false;
        });

    // Create a translation animation for the computer switch.
    final TranslateTransition computerSwitch = new TranslateTransition();
    computerSwitch.setNode(computerGroup);

    // Set the duration of the animation to 500 milliseconds (0.5 seconds).
    computerSwitch.setDuration(Duration.millis(500));

    // Check if the computer is currently open.
    if (GameState.computerIsOpen) {
      // If the computer is open, move it upwards by 650 units.
      computerSwitch.setByY(-650);
      // Update the computer state to indicate it's closed.
      GameState.computerIsOpen = false;
      // Disable and hide the computer dim screen overlay.
      computerDimScreen.setDisable(true);
      computerDimScreen.setVisible(false);
    } else {
      // If the computer is closed, move it downwards by 650 units.
      computerSwitch.setByY(650);
      // Update the computer state to indicate it's open.
      GameState.computerIsOpen = true;
      // Request focus on the computer password field.
      computerPasswordField.requestFocus();
      // Turn off the torch.
      GameState.torchIsOn.setValue(false);
      // Enable and show the computer dim screen overlay.
      computerDimScreen.setDisable(false);
      computerDimScreen.setVisible(true);
    }

    // Play the computer switch animation.
    computerSwitch.play();
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
    // Set a flag to indicate that room switching is in progress.
    GameState.switchingRoom = true;

    // Use a wait method to delay and prevent spamming of room switches, then clear
    // the
    // switchingRoom flag.
    wait(
        700,
        () -> {
          GameState.switchingRoom = false;
        });

    // Create a translation animation to switch between rooms.
    final TranslateTransition roomSwitch = new TranslateTransition();
    roomSwitch.setNode(roomCollectionPane);
    roomSwitch.setDuration(Duration.millis(500));

    // Determine the direction of the room switch based on the nextRoom value.
    if (nextRoom > GameState.currentRoom) {
      roomSwitch.setByX(-1022);
    } else {
      roomSwitch.setByX(1022);
    }

    // Play the room switch animation.
    roomSwitch.play();

    // Update the currentRoom value to the next room.
    GameState.currentRoom = nextRoom;

    // Handle specific actions based on the current room.
    if (GameState.currentRoom == 0) {
      // Check if the riddle has not been provided yet.
      if (!GameState.riddleProvided) {
        // Set the riddleProvided flag to true and request a riddle instruction from
        // GPT.
        GameState.riddleProvided = true;
        issueInstruction(
            GptPromptEngineering.getRiddleInstruction(GameState.wordToGuess, GameState.difficulty));
      }
      leftButton.setVisible(false); // Hide the left button in this room.
    } else if (GameState.currentRoom == 2) {
      // Check if the lightTip has not been provided yet.
      if (!GameState.lightTipProvided) {
        // Set the lightTipProvided flag to true and request a lights-off instruction
        // from GPT.
        GameState.lightTipProvided = true;
        issueInstruction(GptPromptEngineering.getLightsOffInstruction(GameState.difficulty));
      }
      rightButton.setVisible(false); // Hide the right button in this room.
    } else {
      leftButton.setVisible(true); // Show the left button for other rooms.
      rightButton.setVisible(true); // Show the right button for other rooms.
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

  private void returnToWaitingLobby() throws IOException {
    SceneManager.delUi(SceneManager.AppUi.WAITING_LOBBY);
    SceneManager.addUi(SceneManager.AppUi.WAITING_LOBBY, App.loadFxml("waitinglobby"));
    App.setUi(AppUi.WAITING_LOBBY);
  }

  // Timer
  private void startTimer() {
    // Create a Timeline for the timer with a 1-second interval.
    timer = new Timeline(
        new KeyFrame(
            Duration.seconds(1),
            event -> {
              // Check if there are remaining seconds.
              if (remainingSeconds > 0) {
                remainingSeconds--;
                updateTimerLabel(); // Update the timer label to display the remaining time.

                // Notify the user when a minute has passed.
                if (remainingSeconds % 60 == 0 && remainingSeconds != 0) {
                  String plural = (remainingSeconds == 60) ? "" : "s";
                  textToSpeech(
                      "You have "
                          + (remainingSeconds / 60)
                          + " minute"
                          + plural
                          + " remaining");
                }
              } else {
                // If no remaining seconds, stop the timer and handle the timer expiration.
                timer.stop();
                // handleTimerExpired();
                endGame("0");
              }
            }));

    // Set the cycle count to INDEFINITE, meaning the timer will run continuously.
    timer.setCycleCount(Animation.INDEFINITE);

    // Start the timer.
    timer.play();
  }

  private void updateTimerLabel() {
    int minutes = remainingSeconds / 60;
    int seconds = remainingSeconds % 60;
    String timeText = String.format("%02d:%02d", minutes, seconds);

    // Change timer color from black to red as time runs out
    double progress = 1.0 - (double) remainingSeconds / (GameState.time);
    Color textColor = Color.WHITE.interpolate(Color.RED, progress);

    timerLabel.setTextFill(textColor);
    timerLabel.setText(timeText);
  }

  // Key Presses
  /**
   * Handles the key pressed event in the input text field. If the Enter key is
   * pressed and the
   * input is not blank, triggers the onSendMessage function.
   *
   * @param event the key event
   * @throws ApiProxyException if there is an error communicating with the API
   *                           proxy
   * @throws IOException       if there is an I/O error
   */
  @FXML
  private void onKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      // Prevent the Enter key event from propagating further
      if (GameState.phoneIsOpen) {
        sendButton.fire();
        playSound("outgoingText.mp3");
      }
      if (GameState.computerIsOpen) {
        computerLoginButton.fire();
      }
    }
  }

  // TTS
  private void textToSpeech(String message) {
    // Create a task to handle text-to-speech processing in a background thread.
    Task<Void> speechTask = new Task<Void>() {
      @Override
      public Void call() throws Exception {
        // Create a TextToSpeech object to convert text to speech.
        TextToSpeech textToSpeech = new TextToSpeech();

        // Use the TextToSpeech object to speak the provided message.
        textToSpeech.speak(message);

        return null;
      }
    };

    // Create a new thread and start the text-to-speech task in the background.
    new Thread(speechTask).start();
  }

  ////////////////////////
  // Objects Interaction
  ///////////////////////

  @FXML
  private void showObjectName(MouseEvent event) {
    Node source = (Node) event.getSource();
    source.getScene().setCursor(Cursor.HAND);
    if (source instanceof Rectangle) {
      String rectangleName = ((Rectangle) source).getId();
      Tooltip tooltip = new Tooltip(rectangleName);

      // Set a shorter tooltip delay (in milliseconds)
      tooltip.setShowDelay(Duration.millis(100));

      Tooltip.install(source, tooltip);

      // Add mouse move listener to update tooltip position
      source.setOnMouseMoved(
          mouseEvent -> {
            double horiOffset = 10; // X-offset from the cursor
            double vertOffset = 10; // Y-offset from the cursor
            tooltip.show(
                source, mouseEvent.getScreenX() + horiOffset, mouseEvent.getScreenY() + vertOffset);
          });

      // Set the current tooltip
      currentTooltip = tooltip;
    }
  }

  @FXML
  private void changeCursorToHand(MouseEvent event) {
    Node source = (Node) event.getSource();
    if (!source.isDisabled()) {
      source.getScene().setCursor(Cursor.HAND);
    }
  }

  @FXML
  private void resetCursor(MouseEvent event) {
    Node source = (Node) event.getSource();
    if (currentTooltip != null) {
      currentTooltip.hide(); // Hide the current tooltip
      currentTooltip = null; // Remove the reference
    }
    if (!source.isDisabled()) {
      source.getScene().setCursor(null);
    }
  }

  @FXML
  private void clickObject(MouseEvent event) {
    // Find which object was clicked.
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    String rectangleId = clickedRectangle.getId();
    System.out.println("Object clicked: " + rectangleId);
    System.out.println("Riddle solved: " + GameState.riddleSolved);

    // Check if the riddle is solved, the clicked object matches the riddle answer,
    // and the torch is
    // not found yet.
    if (GameState.riddleSolved
        && rectangleId.equals(GameState.wordToGuess)
        && !GameState.torchFound) {
      // Mark that the torch is found and make the torchButton visible.
      GameState.torchFound = true;
      torchButton.setVisible(true);

      issueInstruction(GptPromptEngineering.getRiddleSolvedInstruction(GameState.difficulty));

      // Insert an animation to show the torch being retrieved.
      Thread animationThread = new Thread(
          () -> {
            TranslateTransition torchGet = new TranslateTransition();
            torchGet.setNode(torchGetGroup);
            torchGet.setDuration(Duration.millis(500));
            torchGet.setByY(600);

            FadeTransition torchFade = new FadeTransition();
            torchFade.setNode(torchGetGroup);
            torchFade.setDuration(Duration.millis(1000));
            torchFade.setFromValue(0);
            torchFade.setToValue(1);

            torchFade.play();
            torchGet.play();
          });

      // Create a thread to make the torch disappear after a certain time period.
      Thread disappearThread = new Thread(
          () -> {
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

            TranslateTransition torchGet = new TranslateTransition();
            torchGet.setNode(torchGetGroup);
            torchGet.setDuration(Duration.millis(500));
            torchGet.setByY(600);

            FadeTransition torchFade = new FadeTransition();
            torchFade.setNode(torchGetGroup);
            torchFade.setDuration(Duration.millis(500));
            torchFade.setFromValue(1);
            torchFade.setToValue(0);

            torchFade.play();
            torchGet.play();
          });

      // Set both animation threads as daemon threads and start them.
      animationThread.setDaemon(true);
      disappearThread.setDaemon(true);
      animationThread.start();
      disappearThread.start();
    }
  }

  ///////////////
  // Guard's Room
  ///////////////
  @FXML
  private void openCircuit(MouseEvent event) {
    System.out.println("Circuit clicked");
    circuitGroup.setDisable(false);
    circuitGroup.setVisible(true);
    GameState.torchIsOn.setValue(false);
    startMemoryRecallGame();
  }

  @FXML
  private void closeCircuit(MouseEvent event) {
    System.out.println("Circuit clicked");
    circuitGroup.setDisable(true);
    circuitGroup.setVisible(false);

    // Check if the countdown thread is running and interrupt it
    if (countdownThread != null && countdownThread.isAlive()) {
      countdownThread.interrupt();
      countdownThread = null;
    }
  }

  @FXML
  private void clickSwitch(MouseEvent event) {
    // Find which object was clicked
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    String rectangleId = clickedRectangle.getId();
    System.out.println("Object clicked: " + rectangleId);

    // Toggle the switch
    toggleSwitch(rectangleId);
  }

  private void initialiseMemoryGame() {
    System.out.println("Initialising memory game");
    // Clear the switchesToRecall array
    switchesToRecall.clear();
    playerChoices.clear();

    // Set all switches to red
    for (String fxid : allSwitches) {
      setSwitchToRed(fxid);
    }

    // Create a copy of allSwitches to avoid modifying the original list
    List<String> availableSwitches = new ArrayList<>(allSwitches);

    // Initialize a random number generator
    Random random = new Random();

    // Choose and toggle 7 random switches
    for (int i = 0; i < 7 && !availableSwitches.isEmpty(); i++) {
      // Generate a random index within the availableSwitches list
      int randomIndex = random.nextInt(availableSwitches.size());

      // Get the fx:id at the random index
      String randomSwitch = availableSwitches.get(randomIndex);

      // Call setSwitchToGreen on the selected switch
      setSwitchToGreen(randomSwitch);

      // Add it to the switchesToRecall list
      switchesToRecall.add(randomSwitch);

      // Remove the selected switch from the availableSwitches list
      availableSwitches.remove(randomIndex);
    }
  }

  private void toggleSwitch(String fxid) {
    Node node = circuitGroup.lookup("#" + fxid);
    if (node instanceof Rectangle) {
      Rectangle switchRect = (Rectangle) node;

      // Check the current fill color and toggle it
      if (switchRect.getFill().equals(Color.rgb(255, 0, 0))) {
        // Changing from red to green
        switchRect.setFill(Color.rgb(0, 255, 0));

        // Add the id to playerChoices if it doesn't exist
        if (!playerChoices.contains(fxid)) {
          playerChoices.add(fxid);
        }
      } else {
        // Changing from green to red
        switchRect.setFill(Color.rgb(255, 0, 0));

        // Remove the id from playerChoices if it exists
        playerChoices.remove(fxid);
      }
    }
  }

  private void setSwitchToGreen(String fxid) {
    Node node = circuitGroup.lookup("#" + fxid);
    if (node instanceof Rectangle) {
      Rectangle switchRect = (Rectangle) node;
      switchRect.setFill(Color.rgb(0, 255, 0));
    }
  }

  private void setSwitchToRed(String fxid) {
    Node node = circuitGroup.lookup("#" + fxid);
    if (node instanceof Rectangle) {
      Rectangle switchRect = (Rectangle) node;
      switchRect.setFill(Color.rgb(255, 0, 0));
    }
  }

  private void disableAllSwitches(boolean disable) {
    for (String fxid : allSwitches) {
      Node node = circuitGroup.lookup("#" + fxid);
      if (node instanceof Rectangle) {
        Rectangle switchRect = (Rectangle) node;
        switchRect.setDisable(disable);
      }
    }
  }

  @FXML
  private void checkIfUserInputCorrect() {
    // Convert both lists to HashSet for comparison
    HashSet<String> switchesToRecallSet = new HashSet<>(switchesToRecall);
    HashSet<String> playerChoicesSet = new HashSet<>(playerChoices);

    // Check if the sets are equal (order doesn't matter)
    boolean areEqual = switchesToRecallSet.equals(playerChoicesSet);

    // Now, 'areEqual' will be true if both sets have the same elements, regardless
    // of order.
    System.out.println("Are equal: " + areEqual);

    if (areEqual) {
      closeCircuit(null);
      circuit.setDisable(true);
      guardRoomDarkness.setVisible(false);
      issueInstruction(GptPromptEngineering.getLightsOnInstruction());
    } else {
      // initialiseMemoryGame();
      startMemoryRecallGame();
    }
  }

  private void startMemoryRecallGame() {
    initialiseMemoryGame();
    goBackMemory.setLayoutX(469);
    goBackMemory.setLayoutY(528);
    checkGuessMemory.setVisible(false);
    disableAllSwitches(true);
    memoryCountdownLabel.setVisible(true);
    // Countdown Label
    int countdownSeconds = 5;

    countdownThread = new Thread(
        () -> {
          for (int i = countdownSeconds; i >= 0; i--) {
            final int remainingTime = i;
            Platform.runLater(
                () -> {
                  if (remainingTime != 1) {
                    memoryCountdownLabel.setText(
                        "You have " + remainingTime + " seconds to remember");
                  } else {
                    memoryCountdownLabel.setText(
                        "You have " + remainingTime + " second to remember");
                  }
                });

            try {
              // Check for interruption and break out of the loop
              if (Thread.currentThread().isInterrupted()) {
                return;
              }
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              // Handle the InterruptedException here if needed
              // In this case, just return without rethrowing or printing the exception
              return;
            }
          }

          // Execute code after countdown
          Platform.runLater(
              () -> {
                memoryCountdownLabel.setVisible(false);
                for (String fxid : allSwitches) {
                  setSwitchToRed(fxid);
                }
                goBackMemory.setLayoutX(403);
                goBackMemory.setLayoutY(528);
                checkGuessMemory.setVisible(true);
                disableAllSwitches(false);
              });
        });

    countdownThread.start(); // Start the thread after creating it
  }

  ///////////////
  // Chat
  ///////////////

  /**
   * Runs a GPT-based riddle using the provided chat message.
   *
   * @param msg The input chat message for the riddle.
   * @return The response chat message from GPT.
   * @throws ApiProxyException If there's an issue with the API proxy.
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    // Add the input message to the chat completion request.
    GameState.gptThinking.setValue(true);
    chatCompletionRequest.addMessage(msg);

    // Create a task for GPT processing.
    Task<ChatMessage> gptTask = new Task<ChatMessage>() {
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
            // This field is used to skip the GPT message if it must be intercepted by a
            // hard coded message.
            boolean messageSent = false;

            if (resultMessage.getRole().equals("assistant")
                && resultMessage.getContent().startsWith("Hint")
                && GameState.difficulty.equals("medium")) {
              // If the AI attempts to give a hint while they are supposed to be out of hints,
              // intercept with a hard coded message.
              if (hintsRemaining == 0) {
                addLabel(
                    "Inmate, you do not have any more hint allowances. "
                        + "You must find out how to proceed on your own.",
                    messagesVertBox);
                messageSent = true;
              } else {
                hintsRemaining--;
                hintLabel.setText(hintsRemaining + "/5");
              }
            }

            // Append the GPT response message to the chat.
            if (!messageSent) {
              for (String paragraph : resultMessage.getContent().split("\n\n")) {
                addLabel(paragraph, messagesVertBox);
              }
            }

            if (!GameState.phoneIsOpen) {
              playSound("incomingText.mp3");

              notifCircle.setVisible(true);
              heartbeatAnimation.play();
            }
            GameState.gptThinking.setValue(false);

            // Check if the response indicates a correct riddle answer.
            if (resultMessage.getRole().equals("assistant")
                && resultMessage.getContent().startsWith("Correct")
                && !GameState.riddleSolved) {
              GameState.riddleSolved = true;
            }
          } else {
            // When an error occurs, print a message suggesting fixes to the user.
            String apology = "Sorry, it seems like you cannot receive messages at this time. Maybe try to check"
                + " your internet connection or your apiproxy.config file in order to see what"
                + " is causing this problem, and then restart the application when you are"
                + " ready to retry. You cannot escape from this facility without assistance.";
            wait(
                3500,
                () -> {
                  addLabel(apology, messagesVertBox);
                  if (!GameState.phoneIsOpen) {
                    playSound("incomingText.mp3");
                    notifCircle.setVisible(true);
                    heartbeatAnimation.play();
                  }
                  // gptThinking is kept as true to prevent future messages, so the texter label
                  // is unbound from "Typing..."
                  phoneNameLabel.textProperty().unbind();
                  phoneNameLabel.setText("Prison Guard");
                });
          }
        });

    // Create a new thread for running the GPT task.
    Thread gptThread = new Thread(gptTask, "Gpt Thread");
    gptThread.start();

    // Return null for now (the actual return value is not used).
    return null;
  }

  ///////////////
  // Helper
  ///////////////

  /**
   * Waits for the specified amount of time before executing a task.
   *
   * @param time    The amount of time in milliseconds to wait for.
   * @param process The process to be completed afterwards.
   */
  private void wait(int time, Runnable process) {
    // Create a new thread for waiting and processing.
    Thread waitThread = new Thread(
        () -> {
          try {
            // Pause the thread for the specified time (in milliseconds).
            Thread.sleep(time);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          // Use Platform.runLater to execute the provided process on the JavaFX
          // application
          // thread.
          Platform.runLater(process);
        });

    // Set the waitThread as a daemon thread, which won't prevent the application
    // from exiting.
    waitThread.setDaemon(true);

    // Start the waitThread.
    waitThread.start();
  }

  /**
   * Issues an instruction to GPT, waiting for the previous instruction to
   * complete if it has not
   * completed yet.
   *
   * @param message the message to be used.
   */
  private void issueInstruction(String message) {
    if (GameState.gptThinking.getValue()) {
      wait(
          200,
          () -> {
            issueInstruction(message);
          });
    } else {
      try {
        runGpt(new ChatMessage("user", message));
      } catch (ApiProxyException e) {
        e.printStackTrace();
      }
    }
  }

  private void playSound(String fileName) {
    try {
      // Build the resource path to the sound file
      String resourcePath = "/sounds/" + fileName;

      // Create a Media object with the resource path
      Media sound = new Media(getClass().getResource(resourcePath).toString());

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
