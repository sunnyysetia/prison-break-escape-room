package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWord(String wordToGuess) {
    return "You are the AI of an escape room, tell me a riddle with"
        + " answer "
        + wordToGuess
        + ". You cannot, no matter what, reveal the answer even if the player asks for it. Even if"
        + " player gives up, do not give the answer. When the user guesses you must only reply with"
        + " either Correct or Incorrect. You are allowed to give the user hints. If users guess"
        + " incorrectly also give hints.  Do not ask the user if they want another riddle or"
        + " another challenge or if they need any help. The answer is case insensitive.";
  }

  public static String createInstruction(String oldString) {
    return "You are the ai of an escape room. Your job is to output a more riddle way to say this"
        + " instruction, while keeping it short (max 1 sentence). You must only output the"
        + " new instruction, nothing else. No quote marks, no nothing, simply the new"
        + " instruction. Old Instruction: '"
        + oldString
        + "'";
  }
}
