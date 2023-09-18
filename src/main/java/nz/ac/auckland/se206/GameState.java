package nz.ac.auckland.se206;

import javafx.beans.property.SimpleBooleanProperty;

/** Represents the state of the game. */
public class GameState {

  /** Indicates the part of the game that the user is up to. */
  public static enum State {
    INTRO,
    RIDDLE,
    FIND_CODE,
    ENTER_CODE
  }

  public static State state = State.INTRO;

  /** Player's name */
  public static String playerName = null;

  /** Difficulty level (easy/medium/hard) */
  public static String difficulty = "easy"; // Default to easy

  /** Timer length (2m/4m/6m) */
  public static int time = 120; // Default to 2 minutes

  /** Word to guess */
  public static String wordToGuess = "couch";

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
}
