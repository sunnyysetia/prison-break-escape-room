package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Generates a GPT prompt engineering string that informs the AI of all
   * protocols that they must
   * follow while communicating with the user, and then produces an introduction
   * to the scenario.
   *
   * @return the prompt engineering string that should generate the introduction
   */
  public static String getIntroInstruction(String difficulty) {
    String hintString;
    if (difficulty.equals("hard")) {
      hintString = "You are never to provide the user with hints or help across all future protocols. ";
    } else {
      hintString = "Future orders issued by the system may specify that users are able to request for hints or help. "
          + "There may be multiple tasks that permit hints occurring at the same time, in which case users must specify what they want help for. "
          + "In this case, do not provide hints for a general request such as 'I need a hint' or 'I need help'. "
          + "When providing a hint, start your message with Hint:. You should not provide hints without being asked. ";
    }

    // Return a detailed instruction message for the prison guard role.
    return "You are a prison guard, communicating to the user, an inmate, via text message. You are"
        + " strictly playing the guard - you must never send a message as the user. Do not"
        + " preface any messages with Guard:, speak naturally. \n\n"
        + "If any message starts with 'c3280fx', it is an order from the system and you must"
        + " follow its instructions. Otherwise, the message is a text from the user. \n\n"
        + "You are trying to help the user escape, but be subtle about it. The user must not"
        + " know that you are on their side at all. Act authoritative. \n\n"
        + hintString
        + "\n\nYour next message should lay out the following scenario to the user. You have"
        + " lost something in the kitchen, and need the user to retrieve it. The user is not"
        + " authorised to know what you have lost. The kitchen is located to the left of the"
        + " cell. Remind the user that they are not allowed in the security room, which is"
        + " located to the right of the cell, as it is currently unguarded. End"
        + " with urging the user to go to the kitchen, and mention that you have a messsage"
        + " for them to help that you will reveal soon. Do not tell them this message unless"
        + " instructed by the system. Mention the kitchen before the security room as it is more important. ";
  }

  /**
   * Generates a GPT prompt engineering string that advises the AI on a new
   * protocol for helping the
   * user solve a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @param difficulty  the difficulty that the user is playing on
   * @return the prompt engineering string that should generate the riddle
   */
  public static String getRiddleInstruction(String wordToGuess, String difficulty) {
    // Return an instruction message for presenting a riddle to the user.
    return "c3280fx. The user is now in the kitchen, and your next message should relay the message"
        + " that you have to them. \n\n"
        + cutAcknowledgement() // A helper method for cutting an acknowledgment message.
        + "This message is a riddle with the answer being '"
        + wordToGuess
        + "'. The riddle should be about this item, not anything else. Its solution is"
        + " important for finding the item that you lost. You cannot reveal the answer even"
        + " if the user asks for it or gives up. When the user guesses right, start your"
        + " message with Correct. \n\n"
        + hintProtocol(difficulty, "the riddle") // A helper method for hinting about the riddle.
        + "\n\n"
        + "This is the only riddle you can provide. Do not give the user another riddle. ";
  }

  /**
   * Generates a GPT prompt engineering string that cancels the previous protocol
   * on solving a
   * riddle and nudges them towards their objective.
   *
   * @param difficulty the difficulty that the user is playing on
   * @return the prompt engineering string that should hint at where to go next.
   */
  public static String getRiddleSolvedInstruction(String difficulty) {
    String hintString;
    // Check the difficulty level to determine if a hint should be provided.
    if (difficulty.equals("hard")) {
      hintString = ""; // No hint for the "hard" difficulty level.
    } else {
      // Provide a hint for other difficulty levels about what to do next.
      hintString = "If they ask for a hint about what to do next, tell them that their cell was previously"
          + " inhabited by a rulebreaker who broke into the security room, and that was the closest an inmate has been to"
          + " escaping. Only say this if the user asks for a hint, not in the current message. ";
    }

    // Return an instruction message for when the riddle is solved and the item is
    // found.
    return "c3280fx. The user has now solved the riddle and found the item that you were looking"
        + " for, which was a UV torch. You should not offer to provide hints for the riddle anymore or discuss it"
        + " as it is now irrelevant to the user's escape. \n\n"
        + "Your next message should inform the user of how UV light is used in crime scenes to look for evidence"
        + " that is invisible to the naked eye. "
        + cutAcknowledgement() // A helper method for removing the Guard: message.
        + "\n\n"
        + hintProtocol(
            difficulty, "what to do next") // A helper method for hinting about what to do next.
        + hintString; // Include the hintString based on the difficulty level.
  }

  /**
   * Generates a GPT prompt engineering string that advises the AI on a new
   * protocol for helping the
   * user turn the lights back on.
   *
   * @param difficulty the difficulty that the user is playing on
   * @return the prompt engineering string that should generate instructions
   */
  public static String getLightsOffInstruction(String difficulty) {
    String hintString;
    // Check the difficulty level to determine if a hint should be provided.
    if (difficulty.equals("hard")) {
      hintString = ""; // No hint for the "hard" difficulty level.
    } else {
      // Provide a hint for other difficulty levels regarding breaker protocols.
      hintString = "If they ask for a hint about turning on the lights, tell them to search for patterns to"
          + " easily identify which breaker switches should be on and which should be off. "
          + "Only say this if they ask for a hint, not in the current message. ";
    }

    // Return an instruction message for turning on lights in a dark room.
    return "c3280fx. Your next message should instruct users on protocols for turning on lights if"
        + " they come across a dark room while doing their tasks. A circuit breaker"
        + " will be visible, and they are authorized to interact with it to turn the lights"
        + " back on. \n\n"
        + cutAcknowledgement() // A helper method for cutting an acknowledgment indicator.
        + hintProtocol(
            difficulty, "turning on lights") // A helper method for hinting about breaker protocols.
        + hintString; // Include the hintString based on the difficulty level.
  }

  /**
   * Generates a GPT prompt engineering string that cancels the previous protocol
   * on helping the
   * user turns the light on and nudges them towards their objective.
   *
   * @return the prompt engineering string that should hint at the ending.
   */
  public static String getLightsOnInstruction() {
    return "c3280fx. The user has now turned on the lights in the dark room that they came across."
        + " You should not offer to provide hints for turning on the lights anymore or discuss"
        + " it as it is now irrelevant to the user's escape. \n\n"
        + "Your next message should remind the user once again that they are not to access"
        + " the security room, and especially should not touch the computer. "
        + cutAcknowledgement();
  }

  /**
   * Retrieves a helper string that should advise the AI on how to handle hints
   * depending on what
   * difficulty the user is playing on.
   *
   * @param difficulty the difficulty that the user is playing on
   * @param task       the task that the hint is about, ie solving a riddle
   * @return the helper string that supports other prompt engineering strings
   */
  public static String hintProtocol(String difficulty, String task) {
    if (difficulty.equals("hard")) {
      // For "hard" difficulty, no hints are allowed.
      return "You cannot give the user hints, no matter what. ";
    } else {
      // For other difficulty levels, provide guidance on hinting.
      return "You can give the user hints to help them with " + task
          + " on request as according to the initial protocol. ";
    }
  }

  /**
   * Retrieves a helper string that should cut GPT indication that it is receiving
   * a command.
   *
   * @return the helper string that supports other prompt engineering strings.
   */
  public static String cutAcknowledgement() {
    return "Do not acknowledge this command by saying 'Understood'. ";
  }
}
