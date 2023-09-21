package nz.ac.auckland.se206;

import javafx.beans.property.SimpleBooleanProperty;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle needs to be given. */
  public static boolean riddleProvided = false;

  /** Indicates whether the riddle has been solved. */
  public static boolean riddleSolved = false;

  /** Indicates whether the light instructions need to be given. */
  public static boolean lightTipProvided = false;

  /** Difficulty level (easy/medium/hard) */
  public static String difficulty = "easy"; // Default to easy

  /** Timer length (2m/4m/6m) */
  public static int time = 120; // Default to 2 minutes

  /** Word to guess */
  public static String wordToGuess = "kettle";

  /** Pincode to find */
  public static String pincode = "1234";

  /** current room */
  public static int currentRoom = 1;

  /** switching room (state of whether room switching animation is playing) */
  public static boolean switchingRoom = false;

  /** opening phone to show chat screen, similar to switchingRoom boolean */
  public static boolean togglingPhone = false;

  /** boolean value to check when the phone is open */
  public static boolean phoneIsOpen = false;

  public static boolean togglingComputer = false;

  public static boolean computerIsOpen = false;

  /** boolean value to determine if torch is on or off */
  public static SimpleBooleanProperty torchIsOn = new SimpleBooleanProperty(false);

  /** disables input functionality when gpt is currently processing a response */
  public static SimpleBooleanProperty gptThinking = new SimpleBooleanProperty(false);

  public static int uvPassword;

  public static boolean computerLoggedIn = false;

  public static boolean torchFound = false;

  public static boolean continueEnabled = false;
}
