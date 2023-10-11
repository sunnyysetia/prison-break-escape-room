package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {

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

  public static void delUi(AppUi appUi) {
    sceneMap.remove(appUi);
    System.out.println("Deleted UI: " + appUi);
    System.out.println("Current UIs: " + sceneMap.keySet());
  }
}
