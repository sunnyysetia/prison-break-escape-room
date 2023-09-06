package nz.ac.auckland.se206.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MainGameController {

  @FXML private Pane roomCollectioPane;
  @FXML private Pane prisonCellPane;
  @FXML private Pane cafeteriaPane;
  @FXML private Pane courtyardPane;

  private TranslateTransition leftSwitch = new TranslateTransition();
  private TranslateTransition rightSwitch = new TranslateTransition();

  Thread levelSwitchThread =
      new Thread(
          () -> {
            leftSwitch.setNode(roomCollectioPane);
            leftSwitch.setDuration(javafx.util.Duration.millis(1000));
            leftSwitch.setByX(-600);
            rightSwitch.setNode(roomCollectioPane);
            rightSwitch.setDuration(javafx.util.Duration.millis(1000));
            rightSwitch.setByX(600);
          });

  public void initialize() {}

  @FXML
  public void leftPane(MouseEvent event) {
    leftSwitch.play();
    System.out.println("Left switch clicked");
  }

  @FXML
  public void rightPane(MouseEvent event) {
    rightSwitch.play();
    System.out.println("Right switch clicked");
  }
}
