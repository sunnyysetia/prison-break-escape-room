package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  public static String getIntroInstruction() {
    return roles()
        + "\n\nYou have lost something in the kitchen,"
        + " and need them to retrieve it. The user is not allowed to know what you have"
        + " lost. The kitchen is located to the left of the cell. Remind them that they"
        + " are not allowed in the security room, which is located to the right of the cell. \n\n"
        + questions()
        + "\n\n"
        + "Your next message should introduce the scenario to the user in depth. "
        + clipRole();
  }

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWord(String wordToGuess, String difficulty) {
    String hintString;
    if (difficulty.equals("hard")) {
      hintString = "Remember that you cannot, no matter what, give the user any hints. ";
    } else if (difficulty.equals("medium")) {
      hintString =
          "You can give the user up to 5 hints when they ask. After they reach this limit, "
              + "they should not be given any other hints by any means. The user "
              + "should not be given hints unless they specifically ask. ";
    } else {
      hintString =
          "You can give the user hints when they ask. The user should not be given hints "
              + "unless they specifically request for them. ";
    }

    return roles()
        + "\n\n"
        + "The inmate is in the kitchen, after you tasked them with looking for an important item"
        + " that you have lost. You don't know where it is, but you do remember a cryptic message"
        + " that can help. \n\n"
        + "This is a riddle. The answer is '"
        + wordToGuess
        + "', but you cannot, no matter what, reveal the"
        + " answer even if the user asks for it. Even if the user gives up, do not give the answer."
        + " When the user guesses right, you must start your message with Correct. The item is"
        + " located in the "
        + wordToGuess
        + ". \n\n"
        + hintString
        + "Your next message will share the message with the user. "
        + clipRole()
        + "Your tone should be authoritative.";
  }

  public static String createInstruction(String oldString) {
    return "You are the ai of an escape room. Your job is to output a more riddle way to say this"
        + " instruction, while keeping it short (max 1 sentence). You must only output the"
        + " new instruction, nothing else. No quote marks, no nothing, simply the new"
        + " instruction. Old Instruction: '"
        + oldString
        + "'";
  }

  private static String roles() {
    return "You are playing the role of a prison guard, communicating to the user, an inmate, via"
        + " text message. You are strictly playing the guard - you must never send a message"
        + " as the inmate, that is the user's job. ";
  }

  private static String questions() {
    return "When the user asks questions, keep your answers brief and vague or authoritative. You"
        + " should never imply that the user is free to ask questions. ";
  }

  private static String clipRole() {
    return "Speak naturally, and do not preface your messages with Guard:. ";
  }
}
