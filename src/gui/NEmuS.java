package gui;

import gui.interfaces.NEmuS_Debug;
import gui.interfaces.NEmuS_Release;
import gui.interfaces.NEmuS_Runnable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NEmuS extends Application {

    public static final boolean DEBUG_MODE = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        new Thread(() -> {
            NEmuS_Runnable nes;
            if (DEBUG_MODE)
                nes = new NEmuS_Debug();
            else
                nes = new NEmuS_Release();
            nes.loopGameWindow();
        }).start();
        while (NEmuS_Runnable.getInstance() == null) ;
        Parent root = FXMLLoader.load(getClass().getResource("settingsWindow.fxml"));
        stage.setTitle("Settings");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }
}
