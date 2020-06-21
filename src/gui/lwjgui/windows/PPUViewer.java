package gui.lwjgui.windows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PPUViewer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("ppuViewer.fxml")));
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.show();
    }
}
