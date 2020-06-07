package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NEmuS extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new Thread(() -> {
            NEmuS_Release nes = new NEmuS_Release();
            nes.loopGameWindow();
        }).start();
        while (NEmuS_Release.getInstance() == null);
        Parent root = FXMLLoader.load(getClass().getResource("settingsWindow.fxml"));
        stage.setTitle("Settings");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        if (args[0].equals("DEBUG"))
            new NEmuS_Debug();
        else
            launch(args);
    }
}
