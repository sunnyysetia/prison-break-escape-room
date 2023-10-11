package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class AccessWarningController {

    @FXML
    private Pane accessWarning;

    @FXML
    public void enterGame(MouseEvent event) throws IOException {
        SceneManager.addUi(SceneManager.AppUi.WAITING_LOBBY, App.loadFxml("waitinglobby"));
        App.setUi(AppUi.WAITING_LOBBY);
    }
}
