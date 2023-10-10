package nz.ac.auckland.se206;

import javafx.beans.property.SimpleBooleanProperty;

/** Represents the state of the game. */
public class GameState {

  /**
   * Variables corresponding to different prompts that should be sent.
   */
  public static boolean riddleProvided = false;
  public static boolean lightTipProvided = false;

  /**
   * Variables tracking the user's completion progress in the game.
   */
  public static boolean riddleSolved = false;
  public static boolean torchFound = false;
  public static boolean batteryGameSolved = false;
  public static boolean batteryForeverClosed = false;
  public static boolean memoryGameSolved = false;
  public static boolean computerLoggedIn = false;

  /**
   * Variables corresponding to the difficulty options that the user selects at
   * the beginning of the game.
   */
  public static String difficulty = "easy";
  public static int time = 120;

  /**
   * Variables corresponding to the events at the end of the game.
   */
  public static boolean continueEnabled = false;
  public static boolean gameWon = false;

  /**
   * Variables corresponding to obstacles that the user must surpass.
   */
  public static String wordToGuess = "kettle";
  public static int uvPassword;
  public static int batteryPercent = 0;

  /**
   * Variables corresponding to the scene.
   */
  public static int currentRoom = 1;
  public static boolean phoneIsOpen = false;
  public static boolean computerIsOpen = false;
  public static boolean batteryIsOpen = false;
  public static SimpleBooleanProperty torchIsOn = new SimpleBooleanProperty(false);
  public static SimpleBooleanProperty muted = new SimpleBooleanProperty(false);

  /**
   * Variables corresponding to GPT.
   */
  public static SimpleBooleanProperty gptThinking = new SimpleBooleanProperty(false);
  public static boolean lastMessageFromGPT = true;

  /**
   * Variables corresponding to animations or processes to prevent unexpected
   * functionality.
   */
  public static boolean switchingRoom = false;
  public static boolean togglingPhone = false;
  public static boolean togglingComputer = false;
  public static boolean togglingBattery = false;
  public static boolean mathGameWrongAns = false;
}
