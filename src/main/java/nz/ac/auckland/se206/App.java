package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.EscapeRoomController;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.utils.SoundUtils;

/**
 * This is the entry point of the JavaFX application, while you can change this
 * class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;

  public static void main(final String[] args) {
    Font.loadFont(App.class.getResourceAsStream("/fonts/Metropolis.ttf"), 16);
    Font.loadFont(App.class.getResourceAsStream("/fonts/STENCIL.ttf"), 16);
    Font.loadFont(App.class.getResourceAsStream("/fonts/eraser.ttf"), 16);
    Font.loadFont(App.class.getResourceAsStream("/fonts/Segment7-4Gml.ttf"), 28);
    Font.loadFont(App.class.getResourceAsStream("/fonts/DIGITALDREAMFATNARROW.ttf"), 28);
    Font.loadFont(App.class.getResourceAsStream("/fonts/autoradiographic-rg.ttf"), 60);
    launch();
  }

  /**
   * Returns the node associated to the input file. The method expects that the
   * file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  public static Parent loadFxml(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static void setUi(AppUi newUi) {
    scene.setRoot(SceneManager.getUi(newUi));
  }

  /**
   * This method is invoked when the application starts. It loads and shows the
   * "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    SceneManager.addUi(SceneManager.AppUi.SEIZURE_WARNING, App.loadFxml("accesswarning"));
    Parent root = SceneManager.getUi(AppUi.SEIZURE_WARNING);

    stage.setTitle("Escape Prison");
    stage.setOnCloseRequest(
        (event) -> {
          TextToSpeech textToSpeech = new TextToSpeech();
          textToSpeech.terminate();
          EscapeRoomController.soundUtils.stopSound();
          EscapeRoomController.soundUtils.stopAudio();
        });
    stage.setResizable(false);

    scene = new Scene(root, 1022, 720);
    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }
}
