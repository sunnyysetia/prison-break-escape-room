package nz.ac.auckland.se206.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MainGameController {

  // This pane contains all the three panes below, we move this pane left and right
  @FXML private Pane roomCollectioPane;

  // These three panes below are the individual rooms
  @FXML private Pane prisonCellPane;
  @FXML private Pane cafeteriaPane;
  @FXML private Pane courtyardPane;

  // Create a transition for each direction
  private TranslateTransition leftSwitch = new TranslateTransition();
  private TranslateTransition rightSwitch = new TranslateTransition();

  // to ensure smooth transition and prevent freeze while transitioning between rooms, use a
  // seperate thread
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

  // plays the animation for moving left
  @FXML
  public void leftPane(MouseEvent event) {
    leftSwitch.play();
    System.out.println("Left switch clicked");
  }

  // plays the animation for moving right
  @FXML
  public void rightPane(MouseEvent event) {
    rightSwitch.play();
    System.out.println("Right switch clicked");
  }
}
