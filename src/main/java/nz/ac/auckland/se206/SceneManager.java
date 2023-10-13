package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

/** Organises the scenes and roots into a map for consistency. */
public class SceneManager {

  /** All different scenes that can be used in the game. */
  public enum AppUi {
    WAITING_LOBBY,
    ROOM,
    SEIZURE_WARNING
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();

  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static Parent getUi(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  /**
   * Deletes a UI from the scene map and reports it to the console.
   *
   * @param appUi the UI that is to be deleted.
   */
  public static void delUi(AppUi appUi) {
    sceneMap.remove(appUi);
    System.out.println("Deleted UI: " + appUi);
    System.out.println("Current UIs: " + sceneMap.keySet());
  }
}
