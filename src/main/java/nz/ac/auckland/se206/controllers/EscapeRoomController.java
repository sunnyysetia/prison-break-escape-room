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
import java.util.function.UnaryOperator;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
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
import nz.ac.auckland.se206.utils.SoundUtils;

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
  private ImageView torchButtonCover;
  @FXML
  private ImageView volume;
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
  private Label leftButton;
  @FXML
  private Label rightButton;
  @FXML
  private Label phoneLabel;
  @FXML
  private Circle notifCircle;
  @FXML
  private ImageView torchButton;
  @FXML
  private SVGPath uvLightEffect;
  @FXML
  private SVGPath uvTorchEffect;
  @FXML
  private Label questionLabel;
  @FXML
  private TextArea answerTextArea;
  @FXML
  private Rectangle batteryPower1;
  @FXML
  private Rectangle batteryPower2;
  @FXML
  private Rectangle batteryPower3;
  @FXML
  private Rectangle batteryPower4;
  @FXML
  private Group calculatorKeysGroup;
  @FXML
  private Rectangle calculator0;
  @FXML
  private Rectangle calculator1;
  @FXML
  private Rectangle calculator2;
  @FXML
  private Rectangle calculator3;
  @FXML
  private Rectangle calculator4;
  @FXML
  private Rectangle calculator5;
  @FXML
  private Rectangle calculator6;
  @FXML
  private Rectangle calculator7;
  @FXML
  private Rectangle calculator8;
  @FXML
  private Rectangle calculator9;
  @FXML
  private Rectangle calculatorClear;
  @FXML
  private Rectangle calculatorSubmit;
  @FXML
  private Polygon redArrowPhone;
  @FXML
  private Polygon redArrowTorch;
  @FXML
  private Polygon redArrowComputer;

  private Rectangle lastCalculatorButtonHovered;

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
  @FXML
  private Group batteryGroup;
  @FXML
  private Label powerPercentLabel;
  @FXML
  private Rectangle batteryDimScreen;
  @FXML
  private Group batteriesGroup;
  @FXML
  private ImageView scrollPaperImage;
  @FXML
  private TextArea scrollPaperText;

  private HashMap<Rectangle, FadeTransition> kitchenHoverFadeMap = new HashMap<>() {
    {
      put(cuttingboard, new FadeTransition());
      put(oven, new FadeTransition());
      put(plates, new FadeTransition());
      put(extinguisher, new FadeTransition());
      put(kettle, new FadeTransition());
      put(clock, new FadeTransition());
      put(toaster, new FadeTransition());
    }
  };

  // Cell FXML
  @FXML
  private Text uvLightText;
  @FXML
  private Polygon toilet;

  // Guard's Room FXML
  @FXML
  private Polygon circuit;
  @FXML
  private Polygon computer;
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
  private Rectangle guardRoomDark2;

  private HashMap<Polygon, FadeTransition> miscHoverFadeMap = new HashMap<>() {
    {
      put(toilet, new FadeTransition());
      put(circuit, new FadeTransition());
      put(computer, new FadeTransition());
    }
  };

  @FXML
  private AnchorPane finishedGamePane;
  @FXML
  private Group endPhoneGroup;
  @FXML
  private TextArea endGameTextArea;
  @FXML
  private TextArea clickContinueTextArea;
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

  private SoundUtils soundUtils = new SoundUtils();

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
          "On %s %s, a shocking incident unfolded at The Prison as a"
              + " software malfunction inadvertently unlocked all prison cell doors."
              + " Pandemonium ensued as prisoners took advantage of this unexpected opportunity"
              + " to escape their confinement. Authorities are working diligently to regain"
              + " control and investigate the software glitch responsible for this"
              + " unprecedented breach of security.");
      put(
          "2",
          "On %s %s, chaos erupted at The Prison as a series of"
              + " explosions caused prison walls to crumble, granting inmates an unexpected"
              + " route to freedom. Inmates wasted no time seizing the opportunity, fleeing the"
              + " facility in a frantic rush. Authorities are now in pursuit of the escapees,"
              + " while an investigation is underway to determine the cause of the explosive"
              + " breach in the prison's perimeter.");
      put(
          "3",
          "On %s %s, a surprising incident occurred at The Prison. One"
              + " prisoner had disappeared, leaving an empty cell behind, and a prison guard"
              + " uniform was missing. It appeared that the inmate had managed to escape by"
              + " impersonating a guard. Authorities are now actively searching for the"
              + " escapee, while questions regarding the security lapse loom large.");
    }
  };
  // Create a new map to adjust dates that have inconsistent suffixes - ie: 1st
  // instead of 1th.
  private HashMap<String, String> dateGrammarMap = new HashMap<>() {
    {
      put("1", "st");
      put("2", "nd");
      put("3", "rd");
      put("21", "st");
      put("22", "nd");
      put("23", "rd");
      put("31", "st");
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
  private int sentMessageFlag = 0;
  private int timeout = 12;
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

  private int currentQuestionType;

  private Tooltip currentTooltip; // Maintain a reference to the current tooltip

  ///////////////
  // Shared
  ///////////////
  /**
   * Initializes the room view, it is called when the room loads.
   *
   * @throws ApiProxyException error if the API does not correctly work.
   */
  public void initialize() throws ApiProxyException {
    // Configure the timer length based on what the user selected.
    remainingSeconds = GameState.time;
    Platform.runLater(
        () -> {
          showGuideArrow(redArrowPhone);
        });
    generateQuestion();

    UnaryOperator<Change> modifyChange = change -> {
      if (change.isContentChange()) {
        int newLength = change.getControlNewText().length();
        if (newLength > 3) {
          String previousText = change.getControlNewText().substring(0, 3);
          change.setText(previousText);
          int previousLength = change.getControlText().length();
          change.setRange(0, previousLength);
        }
        if (!change.getControlNewText().matches("[0-9]*")) {
          change.setText(change.getControlText());
        }
        if (change.getControlNewText().contains("\n")) {
          System.out.println("return pressed");
          change.setText(change.getControlText());
        }
        if (change.getControlNewText().contains("=")) {
          change.setText(change.getControlText());
          System.out.println("= pressed");
          submitAnswer();
          answerTextArea.clear();
        }
        if (change.getControlNewText().contains("c")
            || change.getControlNewText().contains("C")) {
          change.setText(change.getControlText());
          System.out.println("c pressed");
          answerTextArea.clear();
        }
      }
      return change;
    };
    answerTextArea.setTextFormatter(new TextFormatter<>(modifyChange));

    volume
        .imageProperty()
        .set(
            new Image(
                App.class.getResourceAsStream(
                    "/images/volume" + ((GameState.muted.getValue()) ? "Off" : "On") + ".png")));

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

    GameState.uvPassword = (int) (Math.random() * 100000);
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
            soundUtils.playSound("outgoingText.mp3", 0.08);

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
              GameState.lastMessageFromGPT = false;
              try {
                // Send the user's message to the GPT for a response.
                runGpt(new ChatMessage("user", message));
              } catch (ApiProxyException e) {
                e.printStackTrace();
              }
            }
          }
        });

    volume.setOnMouseClicked(
        event -> {
          GameState.muted.setValue(!GameState.muted.getValue());
          System.out.println("volume button clicked");
          volume
              .imageProperty()
              .set(
                  new Image(
                      App.class.getResourceAsStream(
                          "/images/volume"
                              + ((GameState.muted.getValue()) ? "Off" : "On")
                              + ".png")));
          if (GameState.muted.getValue()) {
            soundUtils.stopSound();
            soundUtils.stopAudio();
          }
        });

    torchButton.setOnMouseClicked(
        event -> {
          if (GameState.batteryForeverClosed) {
            GameState.torchIsOn.setValue(!GameState.torchIsOn.getValue());
            soundUtils.playAudio("typing1.mp3", 1, 0.1);
          } else {
            clickBatteryScreen();
          }
        });
    uvLightText.visibleProperty().bind(GameState.torchIsOn);
    uvLightEffect.visibleProperty().bind(GameState.torchIsOn);
    uvTorchEffect.visibleProperty().bind(GameState.torchIsOn);

    // Configure settings for the riddle's chat completion request.
    chatCompletionRequest = new ChatCompletionRequest().setN(1).setTemperature(0.3).setTopP(0.5).setMaxTokens(200);

    // Run a GPT-based instruction for the introduction.
    runGpt(new ChatMessage("user", GptPromptEngineering.getIntroInstruction(GameState.difficulty)));
  }

  private void showGuideArrow(Polygon arrow) {
    // Establish a transition for the arrow appearing to the user.
    FadeTransition arrowIn = new FadeTransition();
    arrowIn.setNode(arrow);
    arrowIn.setDuration(Duration.millis(100));
    arrowIn.setFromValue(0);
    arrowIn.setToValue(1.0);

    // Establish a transition for the arrow disappearing from the user.
    FadeTransition arrowOut = new FadeTransition();
    arrowOut.setNode(arrow);
    arrowOut.setDuration(Duration.millis(100));
    arrowOut.setFromValue(1.0);
    arrowOut.setToValue(0);

    // Establish a transition for the arrow's upward trajectory.
    TranslateTransition arrowUp = new TranslateTransition();
    arrowUp.setNode(arrow);
    arrowUp.setDuration(Duration.millis(350));
    arrowUp.setByY(-50);

    // Establish a transition for the arrow's downward trajectory.
    TranslateTransition arrowDown = new TranslateTransition();
    arrowDown.setNode(arrow);
    arrowDown.setDuration(Duration.millis(350));
    arrowDown.setByY(50);

    wait(
        1000,
        () -> {
          arrowIn.play(); // Fade the arrow in.
        });

    wait(
        1050,
        () -> {
          arrowUp.play(); // Bob the arrow up.
        });

    wait(
        1400,
        () -> {
          arrowDown.play(); // Bob the arrow back down.
        });

    wait(
        1750,
        () -> {
          arrowUp.play(); // Bob the arrow up.
        });

    wait(
        2100,
        () -> {
          arrowDown.play(); // Bob the arrow back down.
        });

    wait(
        2450,
        () -> {
          arrowUp.play(); // Did I ever tell you the definition of insanity?
        });

    wait(
        2800,
        () -> {
          arrowDown.play(); // Bob the arrow back down.
        });

    wait(
        3150,
        () -> {
          arrowUp.play(); // Bob the arrow up.
        });

    wait(
        3500,
        () -> {
          arrowDown.play(); // Bob the arrow back down.
        });

    wait(
        3850,
        () -> {
          arrowUp.play();
          arrowOut.play(); // Kill the arrow.
        });
  }

  /** Generates a random math question and sets it in the user interface. */
  private void generateQuestion() {
    if (GameState.batteryGameSolved) {
      Platform.runLater(
          () -> {
            questionLabel.setText("full charge");
          });
      return;
    }

    // Reset the flag for a wrong math game answer
    GameState.mathGameWrongAns = false;

    // Clear the answer text area for the new question
    answerTextArea.clear();

    // Create a random number generator
    Random rand = new Random();
    int num1, num2;

    // Determine the current question type (addition, subtraction, or
    // multiplication)
    currentQuestionType = rand.nextInt(3) + 1;

    // Generate and set the math question based on the current question type
    switch (currentQuestionType) {
      case 1:
        // Addition: Generate two random numbers between 0 and 99
        num1 = rand.nextInt(100);
        num2 = rand.nextInt(100);
        questionLabel.setText(num1 + " + " + num2 + " =");
        break;
      case 2:
        // Subtraction: Generate two random numbers, where the second number is less
        // than the first
        num1 = rand.nextInt(100);
        num2 = rand.nextInt(num1);
        questionLabel.setText(num1 + " - " + num2 + " =");
        break;
      case 3:
        // Multiplication: Generate two random numbers between 0 and 12
        num1 = rand.nextInt(13);
        num2 = rand.nextInt(13);
        questionLabel.setText(num1 + " x " + num2 + " =");
        break;
    }
  }

  private void submitAnswer() {
    showKeyClicked("calculatorSubmit");

    if (answerTextArea.getText().isEmpty()) {
      return; // do nothing if answer area is empty
    }
    if (checkAnswer()) {
      generateQuestion();
    } else {
      soundUtils.playAudio("error.m4a", 1, 0.08);
      wait(
          1000,
          () -> {
            generateQuestion();
          });
    }
  }

  /**
   * Provide a responsive experience for the user by lighting up keys that they
   * click on during the calculator screen and playing a sound effect.
   *
   * @param fxid the FXID of the key that has been clicked.
   */
  public void showKeyClicked(String fxid) {
    // Find the rectangle by fxid
    Node node = calculatorKeysGroup.lookup("#" + fxid);

    if (node instanceof Rectangle) {
      Rectangle rectangle = (Rectangle) node;

      // Set opacity to 1 and stroke width to 3
      rectangle.setOpacity(1);
      rectangle.setStrokeWidth(3);

      // Create a timeline for the animation
      Timeline timeline = new Timeline();
      timeline.setCycleCount(1);

      // Define keyframes for opacity and stroke width changes
      KeyFrame startKeyFrame = new KeyFrame(
          Duration.ZERO,
          new KeyValue(rectangle.opacityProperty(), 1),
          new KeyValue(rectangle.strokeWidthProperty(), 3));

      KeyFrame endKeyFrame = new KeyFrame(
          Duration.seconds(1),
          new KeyValue(rectangle.opacityProperty(), 0),
          new KeyValue(rectangle.strokeWidthProperty(), 0));

      timeline.getKeyFrames().addAll(startKeyFrame, endKeyFrame);
      timeline.play();
      int k = (int) (Math.random() * 5 + 1);
      soundUtils.playAudio("typing" + k + ".mp3", 1, 0.08);
    }
  }

  /**
   * Handles user clicking on a key of the calculator by playing a responsive
   * animation+sfx and updating the status of the minigame accordingly.
   *
   * @param event event handler used to get the key that was clicked.
   */
  @FXML
  public void keypadPressed(MouseEvent event) {
    if (GameState.mathGameWrongAns) {
      return;
    }
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    String rectangleId = clickedRectangle.getId();
    System.out.println("KeyPad clicked: " + rectangleId);
    //
    //
    //
    // WHEN THE KEYPAD IS PRESSED, MAKE IT BLINK TO INDICATE USER PRESSED
    showKeyClicked(rectangleId);
    //
    //
    //

    if (rectangleId.equals("calculatorSubmit")) {
      submitAnswer();
    } else if (rectangleId.equals("calculatorClear")) {
      answerTextArea.clear();
    } else {
      answerTextArea.appendText(clickedRectangle.getId().split("calculator")[1]);
    }
  }

  /**
   * Checks if the user's answer to a math question is correct.
   *
   * @return true if the user's answer is correct, false otherwise.
   */
  private boolean checkAnswer() {
    // Parse the user's answer from the text area
    int userAnswer = Integer.parseInt(answerTextArea.getText());

    // Initialize the correct answer variable
    int correctAnswer = 0;

    // Clear the answer text area for the next question
    answerTextArea.clear();

    // Determine the correct answer based on the current question type
    switch (currentQuestionType) {
      case 1:
        // Addition: Parse and calculate the correct answer
        correctAnswer = Integer.parseInt(questionLabel.getText().split(" ")[0])
            + Integer.parseInt(questionLabel.getText().split(" ")[2]);
        break;
      case 2:
        // Subtraction: Parse and calculate the correct answer
        correctAnswer = Integer.parseInt(questionLabel.getText().split(" ")[0])
            - Integer.parseInt(questionLabel.getText().split(" ")[2]);
        break;
      case 3:
        // Multiplication: Parse and calculate the correct answer
        correctAnswer = Integer.parseInt(questionLabel.getText().split(" ")[0])
            * Integer.parseInt(questionLabel.getText().split(" ")[2]);
        break;
    }

    // Check if the user's answer matches the correct answer
    if (userAnswer == correctAnswer) {
      // If the battery game is not solved, charge the battery
      if (!GameState.batteryGameSolved) {
        chargeBattery();
      }
      return true;
    } else {
      // Set a flag to indicate that the user's answer was wrong
      GameState.mathGameWrongAns = true;

      // Update the question label to indicate a wrong answer
      Platform.runLater(
          () -> {
            questionLabel.setText("wrong Ans");
          });

      return false;
    }
  }

  @FXML
  public void exitGame(MouseEvent event) throws IOException {
    System.exit(0);
  }

  @FXML
  public void restartGame(MouseEvent event) throws IOException {
    returnToWaitingLobby();
  }

  /**
   * Handles the user ending the game by playing a sound indicating their
   * victory/loss and taking them to a screen where they can choose to continue or
   * quit. Also displays time taken if they have won.
   *
   * @param event unused event handler.
   */
  @FXML
  public void endContinue(MouseEvent event) {
    // goes to the final final screen to show time used and buttons to restart game
    // or exit game
    if (!GameState.continueEnabled) {
      return;
    }
    GameState.continueEnabled = false; // prevent spam
    Thread audioThread = new Thread(
        () -> {
          SoundUtils gameEndSoundUtils = new SoundUtils();
          if (GameState.gameWon) {
            gameEndSoundUtils.playAudio("win.m4a", 1, 0.08);
          } else {
            gameEndSoundUtils.playAudio("lose.m4a", 1, 0.08);
          }
        });

    TranslateTransition backgroundTransition = new TranslateTransition();
    backgroundTransition.setNode(finishedGamePane);
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

    audioThread.setDaemon(true);
    phoneAnimThread.setDaemon(true);
    backgroundAnimThread.setDaemon(true);
    audioThread.start();
    phoneAnimThread.start();
    backgroundAnimThread.start();
  }

  private void endGame(String ending) {
    // Stop the timer when the game ends.
    timer.stop();

    // Get the current date and time.
    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();

    String dayAdjusted = day + ((dateGrammarMap.keySet().contains(day + "")) ? dateGrammarMap.get(day + "") : "th");

    // Disable mouse interactions with certain UI elements.
    endGameTextArea.setMouseTransparent(true);
    clickContinueTextArea.setMouseTransparent(true);

    // Configure the message displayed on the phone UI.
    endPhoneTitle.setText("Good Luck Next Time");
    endPhoneMessage.setText("Unfortunately you failed to escape the prison within the time limit");

    // Initialize the default ending message.
    String endMessage = "As the tension in the air thickens and your heart races, you push your luck to the limit"
        + " in a daring attempt to break free from your prison confines. But alas, as the"
        + " clock's relentless ticking echoes in your ears, your every move becomes more"
        + " desperate. Time slips through your fingers like sand, and despite your best"
        + " efforts, the guards' footsteps draw nearer. With a heavy heart, you realize that"
        + " your window of opportunity has closed. You failed to escape, and the prison's"
        + " unforgiving walls will continue to confine you. Try again, for the path to freedom"
        + " is as elusive as ever.";

    // Check if the game ending is not the default ending ("0").
    if (!ending.equals("0")) {
      GameState.gameWon = true;
      int minutes = (int) ((GameState.time - remainingSeconds) / 60);
      int seconds = (GameState.time - remainingSeconds) % 60;
      // Format and set the ending message based on the selected ending.
      endMessage = String.format(
          endingMap.get(ending), new DateFormatSymbols().getMonths()[month - 1], dayAdjusted);
      // Update the phone UI to display a congratulatory message.
      endPhoneTitle.setText("Congratulations!");
      endPhoneMessage.setText(
          "You escaped the prison within "
              + minutes
              + " minute"
              + ((minutes == 1) ? "" : "s")
              + " and "
              + seconds
              + " second"
              + ((seconds == 1) ? "" : "s")
              + "!");
    }

    // Store the final ending message.
    String finalEndMessage = endMessage;

    // Play a sound based on the selected ending.
    Thread soundThread = new Thread(
        () -> {
          SoundUtils endingSoundUtils = new SoundUtils();
          endingSoundUtils.playAudio("ending" + ending + ".mp3", 1, 0.1);
        });

    soundThread.setDaemon(true);
    soundThread.start();
    // Set the game ending image.
    endGameImage
        .imageProperty()
        .set(new Image(App.class.getResourceAsStream("/images/ending" + ending + ".png")));

    // Create a thread to handle the animation of the game ending UI.
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

    // Set the animation thread as a daemon thread and start it.
    animationThread.setDaemon(true);
    animationThread.start();

    // Create a thread to handle the text typing animation.
    Thread textThread = new Thread(
        () -> {
          try {
            Thread.sleep(1250); // Wait for 1.25 seconds before typing the ending message.
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          typeWrite(endGameTextArea, finalEndMessage, 10); // Type the final ending message.
          System.out.println("finished typing ending message");
          try {
            Thread.sleep(2000); // Wait for 2 seconds after typing the ending message.
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println("Finished waiting for 3 seconds");
          typeWrite(
              clickContinueTextArea,
              "Click anywhere to continue",
              15); // Display a click-to-continue message.
          GameState.continueEnabled = true; // Enable the click-to-continue functionality.
        });

    // Set the text animation thread as a daemon thread and start it.
    textThread.setDaemon(true);
    textThread.start();
  }

  private void typeWrite(TextArea sceneTextArea, String message, int interval) {
    int i = 0;
    SoundUtils typingSoundUtils = new SoundUtils();
    while (i < message.length()) {
      int j = i;
      int k = (int) (Math.random() * 5 + 1);
      typingSoundUtils.playSound("typing" + k + ".mp3", 0.04);
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

    // Play incoming sound effect
    soundUtils.playSound("incomingText.mp3", 0.08);

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
      if (GameState.torchFound) {
        torchButtonCover.setVisible(false);
      }
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
      if (GameState.torchFound) {
        torchButtonCover.setVisible(true);
      }
    }

    // Play the phone switch animation.
    phoneSwitch.play();
  }

  /**
   * Finishes the battery game by animating various elements and transitioning to
   * the next screen.
   */
  private void finishBatteryGame() {
    // Make the torch button cover visible
    torchButtonCover.setVisible(true);

    // Create fade transitions for paper image
    FadeTransition paperImageFade = new FadeTransition();
    paperImageFade.setNode(scrollPaperImage);
    paperImageFade.setDuration(Duration.millis(400));
    paperImageFade.setFromValue(1.0);
    paperImageFade.setToValue(0);

    // Create fade transitions for paper text
    FadeTransition paperTextFade = new FadeTransition();
    paperTextFade.setNode(scrollPaperText);
    paperTextFade.setDuration(Duration.millis(400));
    paperTextFade.setFromValue(1.0);
    paperTextFade.setToValue(0);

    // Create fade transitions for calculator keys
    FadeTransition calculatorFade = new FadeTransition();
    calculatorFade.setNode(calculatorKeysGroup);
    calculatorFade.setDuration(Duration.millis(400));
    calculatorFade.setFromValue(1.0);
    calculatorFade.setToValue(0);

    // Create translate transitions for paper image
    TranslateTransition paperImageMove = new TranslateTransition();
    paperImageMove.setNode(scrollPaperImage);
    paperImageMove.setDuration(Duration.millis(500));
    paperImageMove.setByY(200);

    // Create translate transitions for paper text
    TranslateTransition paperTextMove = new TranslateTransition();
    paperTextMove.setNode(scrollPaperText);
    paperTextMove.setDuration(Duration.millis(500));
    paperTextMove.setByY(200);

    // Create translate transitions for calculator keys
    TranslateTransition calculatorMove = new TranslateTransition();
    calculatorMove.setNode(calculatorKeysGroup);
    calculatorMove.setDuration(Duration.millis(500));
    calculatorMove.setByY(200);

    // Create translate transitions for battery
    TranslateTransition batteryMove = new TranslateTransition();
    batteryMove.setNode(batteriesGroup);
    batteryMove.setDuration(Duration.millis(180));
    batteryMove.setByX(-400);

    TranslateTransition batteryMoveBack = new TranslateTransition();
    batteryMoveBack.setNode(batteriesGroup);
    batteryMoveBack.setDuration(Duration.millis(90));
    batteryMoveBack.setByX(49);

    TranslateTransition batteryMoveUp = new TranslateTransition();
    batteryMoveUp.setNode(batteriesGroup);
    batteryMoveUp.setDuration(Duration.millis(90));
    batteryMoveUp.setByY(-50);

    TranslateTransition batteryMoveDown = new TranslateTransition();
    batteryMoveDown.setNode(batteriesGroup);
    batteryMoveDown.setDuration(Duration.millis(90));
    batteryMoveDown.setByY(50);

    // Wait for specified durations and play animations
    wait(
        1000,
        () -> {
          // Play fade transitions for paper image, paper text, and calculator keys
          paperImageFade.play();
          paperTextFade.play();
          calculatorFade.play();

          // Play translate transitions for paper image, paper text, and calculator keys
          paperImageMove.play();
          paperTextMove.play();
          calculatorMove.play();
        });

    wait(
        1200,
        () -> {
          // Play the translate transition for the battery
          batteryMove.play();
        });

    wait(
        1400,
        () -> {
          // Play translate transitions for paper image, paper text, and calculator keys
          batteryMoveBack.play();
          scrollPaperImage.setDisable(true);
          scrollPaperImage.setVisible(false);
          scrollPaperText.setDisable(true);
          scrollPaperText.setVisible(false);
          calculatorKeysGroup.setDisable(true);
          calculatorKeysGroup.setVisible(false);
        });

    wait(
        1500,
        () -> {
          wait(
              100,
              () -> {
                batteryMoveUp.play();
              });
          wait(
              200,
              () -> {
                batteryMoveDown.play();
              });
          wait(
              800,
              () -> {
                batteryMoveUp.play();
              });
          wait(
              900,
              () -> {
                batteryMoveDown.play();
              });
        });

    wait(
        1500,
        () -> {
          wait(
              200,
              () -> {
                batteryPower1.setEffect(new Glow(1.0));
              });
          wait(
              400,
              () -> {
                batteryPower2.setEffect(new Glow(1.0));
              });
          wait(
              600,
              () -> {
                batteryPower3.setEffect(new Glow(1.0));
              });
          wait(
              800,
              () -> {
                batteryPower4.setEffect(new Glow(1.0));
              });
          soundUtils.playAudio("electric1.m4a", 2, 0.1);
        });

    wait(
        3000,
        () -> {
          // Toggle the battery game screen and hide the torch button cover
          toggleBatteryScreen();
          torchButtonCover.setVisible(false);
        });
  }

  /**
   * Increases the battery percentage, plays a charging animation, and updates the
   * UI.
   */
  private void chargeBattery() {
    // Increase the battery percentage by 25%
    GameState.batteryPercent += 25;

    // Check if the battery is fully charged
    if (GameState.batteryPercent >= 100) {
      GameState.batteryGameSolved = true;
      finishBatteryGame();
    }

    // Create a new thread for the fade animation and sound effects
    Thread fadeThread = new Thread(
        () -> {
          // Create a fade transition for the battery icon
          FadeTransition batteryFade = new FadeTransition();

          // Determine which battery power icon to target based on battery percentage
          switch (GameState.batteryPercent) {
            case 25:
              batteryFade.setNode(batteryPower1);
              break;
            case 50:
              batteryFade.setNode(batteryPower2);
              break;
            case 75:
              batteryFade.setNode(batteryPower3);
              break;
            case 100:
              batteryFade.setNode(batteryPower4);
              break;
          }

          // Configure the fade transition
          batteryFade.setDuration(Duration.millis(400));
          batteryFade.setFromValue(0);
          batteryFade.setToValue(1);

          // Play the battery charging animation
          batteryFade.play();

          // Play the electric charging sound
          soundUtils.playAudio("electric2.m4a", 1, 0.08);
        });

    // Set the fadeThread as a daemon thread and start it
    fadeThread.setDaemon(true);
    fadeThread.start();

    // Update the UI on the JavaFX application thread
    Platform.runLater(
        () -> {
          // Update the battery percentage label text
          powerPercentLabel.setText(GameState.batteryPercent + "%");

          // Set the text color to green to indicate charging
          powerPercentLabel.setTextFill(Color.rgb(0, 255, 0));

          // Apply a glow effect to the battery percentage label
          powerPercentLabel.setEffect(new Glow(0.5));
        });
  }

  private void clickBatteryScreen() {
    System.out.println("Torch clicked, trying to toggle battery screen");
    if (GameState.togglingBattery) {
      return;
    } else {
      toggleBatteryScreen();
    }
  }

  /**
   * Toggles the battery screen's visibility and updates the game state
   * accordingly.
   */
  private void toggleBatteryScreen() {
    // Print a message to indicate that the battery screen is being toggled
    System.out.println("Toggling battery screen");

    // Set the flag to indicate that the battery screen is in the process of being
    // toggled
    GameState.togglingBattery = true;

    // Wait for a short duration and then set the flag to indicate that the toggle
    // is complete
    wait(
        500,
        () -> {
          GameState.togglingBattery = false;
        });

    // Print the current battery screen status
    System.out.println("Current battery screen status: " + GameState.batteryIsOpen);

    // Create a translate transition for the battery screen
    final TranslateTransition batterySwitch = new TranslateTransition();
    batterySwitch.setNode(batteryGroup);
    batterySwitch.setDuration(Duration.millis(500));

    if (GameState.batteryIsOpen) {
      // Close the battery screen by moving it upwards
      batterySwitch.setByY(-760);
      GameState.batteryIsOpen = false;

      // Disable and hide the dimmed screen behind the battery
      batteryDimScreen.setDisable(true);
      batteryDimScreen.setVisible(false);
    } else {
      // Open the battery screen by moving it downwards
      batterySwitch.setByY(760);
      GameState.batteryIsOpen = true;

      // Set focus to the battery group (for keyboard input, if needed)
      batteryGroup.requestFocus();

      // Enable and show the dimmed screen behind the battery
      batteryDimScreen.setDisable(false);
      batteryDimScreen.setVisible(true);
    }

    // Check if the battery game has been solved and set the corresponding flag
    if (GameState.batteryGameSolved) {
      GameState.batteryForeverClosed = true;
    }

    // Play the battery screen transition
    batterySwitch.play();

    // Print the new battery screen status
    System.out.println("New battery screen status: " + GameState.batteryIsOpen);
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

  /**
   * Handles the user clicking on the phone icon.
   *
   * @param event unused event handler.
   */
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

  /**
   * Handle the user clicking the button to go to the left room.
   *
   * @param event unused event handler
   */
  @FXML
  public void leftPane(MouseEvent event) {
    System.out.println("Left switch clicked");
    if (GameState.currentRoom == 0 || GameState.switchingRoom) {
      return;
    } else {
      switchRoom(GameState.currentRoom - 1);
    }
  }

  /**
   * Handle the user clicking the button to go to the right room.
   *
   * @param event unused event handler
   */
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

                // If a certain amount of seconds have elapsed, assume that the API is not
                // working.
                if (remainingSeconds <= (sentMessageFlag - timeout)) {
                  handleError();
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
    // System.out.println("key pressed: " + event.getCode());
    if (event.getCode() == KeyCode.ENTER) {
      // Prevent the Enter key event from propagating further
      if (GameState.phoneIsOpen) {
        sendButton.fire();
        soundUtils.playSound("outgoingText.mp3", 0.08);
      }
      if (GameState.computerIsOpen) {
        computerLoginButton.fire();
      }
      if (GameState.batteryIsOpen) {
        submitAnswer();
      }
    }
    if (GameState.batteryIsOpen) {
      int k = (int) (Math.random() * 5 + 1);
      soundUtils.playAudio("typing" + k + ".mp3", 1, 0.1);
      if (GameState.mathGameWrongAns) {
        return;
      }
      if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
        if (answerTextArea.getText().isEmpty()) {
          return;
        }
        answerTextArea.setText(
            answerTextArea.getText().substring(0, answerTextArea.getText().length() - 1));
      } else {
        try {
          if (event.getText().equals("C") || event.getText().equals("c")) {
            showKeyClicked("calculatorClear");
            answerTextArea.clear();
          }
          if (event.getText().equals("=")) {
            submitAnswer();
            return;
          }
          System.out.println("user pressed: " + event.getText());
          showKeyClicked("calculator" + event.getText());
          answerTextArea.appendText(event.getText());
        } catch (Exception e) {
          // do nothing, only happens when user presses some key like del or insert
          System.out.println(e);
        }
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
      Rectangle rectangleObject = (Rectangle) source;
      String rectangleName = (rectangleObject).getId();
      Tooltip tooltip = new Tooltip(rectangleName);

      // Make the object light up and cancel any existing fade animation
      rectangleObject.setOpacity(1);
      if (kitchenHoverFadeMap.get(rectangleObject) != null) {
        kitchenHoverFadeMap.get(rectangleObject).stop();
      }

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
    Shape clickedRectangle = (Shape) event.getSource();
    String rectangleId = clickedRectangle.getId();
    System.out.println("Object clicked: " + rectangleId);
    soundUtils.playAudio("typing4.mp3", 1, 0.1);
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
      Thread audioThread = new Thread(
          () -> {
            soundUtils.playAudio("torchGot.mp3", 1, 0.03);
          });

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
            Platform.runLater(
                () -> {
                  showGuideArrow(redArrowTorch);
                });
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
      audioThread.setDaemon(true);
      animationThread.start();
      disappearThread.start();
      audioThread.start();
    }
  }

  @FXML
  private void onKitchenMouseExit(MouseEvent event) {
    resetCursor(event);
    Node source = (Node) event.getSource();
    if (source instanceof Rectangle) {
      // Find the object that the mouse just exited.
      Rectangle rectangleObject = (Rectangle) source;
      // Establish an animation that causes the highlight of the object to slowly fade
      // out.
      FadeTransition disappearFade = new FadeTransition();
      disappearFade.setNode(rectangleObject);
      disappearFade.setDuration(Duration.millis(500));
      disappearFade.setFromValue(1);
      disappearFade.setToValue(0);

      // Store the animation so that it can be cancelled if need arises and play it.
      kitchenHoverFadeMap.put(rectangleObject, disappearFade);
      kitchenHoverFadeMap.get(rectangleObject).play();
    }
  }

  @FXML
  private void onPolygonEnter(MouseEvent event) {
    // Get the source node from the event
    Node source = (Node) event.getSource();

    // Check if the source is a Polygon
    if (source instanceof Polygon) {
      // Cast the source to a Polygon object
      Polygon polygonObject = (Polygon) source;

      // Set the opacity of the polygon to fully visible, and
      // stop the fade animation if it is playing.
      if (miscHoverFadeMap.get(polygonObject) != null) {
        miscHoverFadeMap.get(polygonObject).stop();
      }
      polygonObject.setOpacity(1);
    }

    // Change the cursor to a hand icon to indicate interactivity
    changeCursorToHand(event);
  }

  @FXML
  private void onPolygonExit(MouseEvent event) {
    // Get the source node from the event
    Node source = (Node) event.getSource();

    // Check if the source is a Polygon
    if (source instanceof Polygon) {
      // Cast the source to a Polygon object
      Polygon polygonObject = (Polygon) source;

      // Create a fade transition for the polygon to make it disappear
      FadeTransition disappearFade = new FadeTransition();
      disappearFade.setNode(polygonObject);
      disappearFade.setDuration(Duration.millis(500));
      disappearFade.setFromValue(1);
      disappearFade.setToValue(0);

      // Play the fade-out animation
      miscHoverFadeMap.put(polygonObject, disappearFade);
      miscHoverFadeMap.get(polygonObject).play();
    }

    // Reset the cursor to its default state
    resetCursor(event);
  }

  @FXML
  private void onToiletClick(MouseEvent event) {
    // Prevent spamming of the toilet sound effect.
    if (GameState.toiletPressed) {
      return;
    }
    GameState.toiletPressed = true;
    System.out.println("Toilet clicked");
    // Play a random toilet sound effect from 3 possible options.
    String toiletString = "toilet" + Math.round(Math.random() * 2 + 1) + ".m4a";
    System.out.println("Playing: " + toiletString);
    soundUtils.playAudio(toiletString, 1, 0.1);
    // The sound effect must fully play out before the toilet is clickable again.
    wait(
        4000,
        () -> {
          GameState.toiletPressed = false;
        });
  }

  @FXML
  private void addGlowToCalculatorKey(MouseEvent event) {
    Node source = (Node) event.getSource();
    source.getScene().setCursor(Cursor.HAND);
    if (source instanceof Rectangle) {
      Rectangle rectangleKey = (Rectangle) source;
      // String rectangleName = (rectangleKey).getId();
      rectangleKey.setOpacity(1);
      lastCalculatorButtonHovered = rectangleKey;
    }
  }

  @FXML
  private void removeGlowFromCalculatorKey(MouseEvent event) {
    Node source = (Node) event.getSource();
    source.getScene().setCursor(null);
    lastCalculatorButtonHovered.setOpacity(0);
  }

  ///////////////
  // Guard's Room
  ///////////////
  @FXML
  private void openCircuit(MouseEvent event) {
    System.out.println("Circuit clicked");
    // Show and enable the circuit buttons to be clicked and disable the torch.
    circuitGroup.setDisable(false);
    circuitGroup.setVisible(true);
    GameState.torchIsOn.setValue(false);
    // Make a transition for the circuit appearing.
    Thread animationThread = new Thread(
        () -> {
          FadeTransition endFade = new FadeTransition();
          endFade.setNode(circuitGroup);
          endFade.setDuration(Duration.millis(1000));
          endFade.setFromValue(0);
          endFade.setToValue(1);
          endFade.play();
        });
    // Start the circuit minigame.
    wait(
        250,
        () -> {
          startMemoryRecallGame(); // add a pause before starting the game
        });
    soundUtils.playAudio("typing1.mp3", 1, 0.1);
    animationThread.setDaemon(true);
    animationThread.start();
  }

  /**
   * Handles the closing of the electrical circuit.
   *
   * @param event The MouseEvent that triggered the circuit closing.
   */
  @FXML
  private void closeCircuit(MouseEvent event) {
    // Disable all switches to prevent further interactions
    disableAllSwitches(true);

    // Print a message indicating that the circuit has been closed
    System.out.println("Circuit closed");

    // Play a typing sound effect if the memory game is not solved
    if (!GameState.memoryGameSolved) {
      soundUtils.playAudio("typing4.mp3", 1, 0.1);
    }

    // Create a new thread for the closing animation
    Thread animationThread = new Thread(
        () -> {
          // Create a fade transition for the circuit group
          FadeTransition endFade = new FadeTransition();
          endFade.setNode(circuitGroup);
          endFade.setDuration(Duration.millis(500));
          endFade.setFromValue(1);
          endFade.setToValue(0);
          endFade.play();
        });

    // Set the animationThread as a daemon thread and start it
    animationThread.setDaemon(true);
    animationThread.start();

    // Wait for a short duration and then hide the circuit group
    wait(
        500,
        () -> {
          circuitGroup.setVisible(false);
        });

    // Disable the circuit group to prevent further interactions
    circuitGroup.setDisable(true);

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
    soundUtils.playAudio("typing3.mp3", 1, 0.1);
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
      GameState.memoryGameSolved = true;
      closeCircuit(null);
      circuit.setDisable(true);
      guardRoomDarkness.setVisible(false);
      guardRoomDark2.setVisible(false);
      soundUtils.playAudio("spotlight.m4a", 1, 0.3);
      issueInstruction(GptPromptEngineering.getLightsOnInstruction());
      Platform.runLater(
          () -> {
            showGuideArrow(redArrowComputer);
          });
    } else {
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
    sentMessageFlag = remainingSeconds;
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

            for (String paragraph : resultMessage.getContent().split("\n\n")) {
              if (resultMessage.getRole().equals("assistant")
                  && paragraph.startsWith("Hint")
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
            }

            // Append the GPT response message to the chat.
            if (!messageSent) {
              appendTexts(resultMessage.getContent());
            }

            // Alert the user to check the phone.
            if (!GameState.phoneIsOpen) {
              notifCircle.setVisible(true);
              heartbeatAnimation.play();
            }
            sentMessageFlag = 0;
            GameState.gptThinking.setValue(false);

            // Check if the response indicates a correct riddle answer.
            if (resultMessage.getRole().equals("assistant")
                && resultMessage.getContent().startsWith("Correct")
                && !GameState.riddleSolved) {
              GameState.riddleSolved = true;
            }
          } else {
            // If the message is null, it is likely that an api proxy error has occurred.
            handleError();
          }
        });

    // Create a new thread for running the GPT task.
    Thread gptThread = new Thread(gptTask, "Gpt Thread");
    gptThread.setDaemon(true);
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
    // Check if GPT is currently generating a response.
    if (GameState.gptThinking.getValue()) {
      // If GPT is still thinking, wait for a short duration and then retry issuing
      // the instruction.
      wait(
          200,
          () -> {
            issueInstruction(message);
          });
    } else {
      // If GPT is not currently thinking, send the user's instruction message to GPT.
      try {
        runGpt(new ChatMessage("user", message));
      } catch (ApiProxyException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Appends a message to the user interface, formatting it appropriately.
   *
   * @param message The message to be appended, possibly containing paragraphs
   *                separated by "\n\n".
   */
  private void appendTexts(String message) {
    // Check if the last message came from the GPT model
    if (GameState.lastMessageFromGPT) {
      // Run the following code on the JavaFX application thread
      Platform.runLater(
          () -> {
            // Create a horizontal box for layout
            HBox horiBox = new HBox();
            horiBox.setAlignment(Pos.CENTER);

            // Create a Text element with the timer label text
            Text text = new Text(timerLabel.getText());

            // Create a TextFlow to display the text
            TextFlow textFlow = new TextFlow(text);

            // Style the TextFlow with background color and text color
            textFlow.setStyle(
                "-fx-background-color: rgb(143,143,143); " + "-fx-color: rgb(255,255,255);");

            // Set padding and size constraints for the TextFlow
            textFlow.setPadding(new Insets(2, 5, 2, 5));
            textFlow.setMaxWidth(90);
            textFlow.setMaxHeight(18);

            // Add the TextFlow to the horizontal box
            horiBox.getChildren().add(textFlow);

            // Add the horizontal box to the vertical message container
            messagesVertBox.getChildren().add(horiBox);
          });
    }

    // Split the message into paragraphs and add them to the user interface
    for (String paragraph : message.split("\n\n")) {
      addLabel(paragraph, messagesVertBox);
    }

    // Update the flag to indicate that the last message came from the GPT model
    GameState.lastMessageFromGPT = true;
  }

  /**
   * Handles the case that GPT stops working correctly by informing the user of
   * what they can do.
   */
  private void handleError() {
    // When an error occurs, print a message suggesting fixes to the user.
    String apology = "Sorry, it seems like you cannot receive messages at this time. \n\n"
        + "Maybe try to check your internet connection or your apiproxy.config file in order"
        + " to see what is causing this problem, and then restart the application when you are"
        + " ready to retry. \n\n"
        + "You cannot escape from this facility without assistance.";
    wait(
        3500,
        () -> {
          appendTexts(apology);
          if (!GameState.phoneIsOpen) {
            notifCircle.setVisible(true);
            heartbeatAnimation.play();
          }
          sentMessageFlag = 0;
          // gptThinking is kept as true to prevent future messages from processing, so
          // the texter label and gif are unbound.
          phoneNameLabel.textProperty().unbind();
          phoneNameLabel.setText("Prison Guard");
          typingImage.setVisible(false);
        });
  }
}
