package nz.ac.auckland.se206;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isCodeFound = false;

  /** Player's name */
  public static String playerName = null;

  /** Difficulty level (easy/hard) */
  public static String difficulty = "easy"; // Default to easy

  /** Word to guess */
  public static String wordToGuess = "couch";

  /** Pincode to find */
  public static String pincode = "1234";
}
