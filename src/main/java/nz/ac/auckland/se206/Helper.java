package nz.ac.auckland.se206;

import javafx.scene.control.TextArea;

public class Helper {
  public static void typeWrite(TextArea sceneTextArea, String message, int interval) {
    int i = 0;
    while (i < message.length()) {
      sceneTextArea.setText(sceneTextArea.getText() + message.charAt(i));
      sceneTextArea.appendText("");
      sceneTextArea.setScrollTop(Double.MAX_VALUE);
      i++;
      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
