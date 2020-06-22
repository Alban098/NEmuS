package gui.lwjgui.windows;

import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSWindow;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import openGL.postProcessing.PostProcessingStep;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphicsSettings extends Application implements Initializable {

    private static GraphicsSettings instance;

    private NEmuSWindow emulator;
    private Stage stage;

    @FXML
    private ListView<PostProcessingStep> postProcessingList;
    @FXML
    private ComboBox<PostProcessingStep> postProcessingComboBox;

    public GraphicsSettings() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static void focusInstance() {
        instance.stage.setIconified(false);
        instance.stage.requestFocus();
    }

    /**
     * Initialize the Settings Window
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        postProcessingComboBox.getItems().addAll(emulator.getPipeline().getAllSteps());
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(windowEvent -> instance = null);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("graphicsSettings.fxml")));
        stage.setScene(scene);
        stage.setTitle("Graphics Settings");
        //TODO Icon
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.show();
        instance.stage = stage;
    }

    /**
     * Will trigger an add filter event to the Emulator
     */
    @FXML
    public void addProcessingStep() {
        if (postProcessingComboBox.getValue() != null) {
            try {
                postProcessingList.getItems().add(postProcessingComboBox.getValue());
                postProcessingList.layout();
                emulator.getPipeline().setSteps(postProcessingList.getItems());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Will trigger a filter shift left event to the Emulator
     */
    @FXML
    public void shiftLeftPostProcessingStep() {
        PostProcessingStep selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == 0)
            return;
        PostProcessingStep switched = postProcessingList.getItems().get(index - 1);
        postProcessingList.getItems().set(index, switched);
        postProcessingList.getItems().set(index - 1, selected);
        postProcessingList.getSelectionModel().select(index - 1);
       emulator.getPipeline().setSteps(postProcessingList.getItems());
    }

    /**
     * Will trigger a filter shift right event to the Emulator
     */
    @FXML
    public void shiftRightPostProcessingStep() {
        PostProcessingStep selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == postProcessingList.getItems().size() - 1)
            return;
        PostProcessingStep switched = postProcessingList.getItems().get(index + 1);
        postProcessingList.getItems().set(index, switched);
        postProcessingList.getItems().set(index + 1, selected);
        postProcessingList.getSelectionModel().select(index + 1);
       emulator.getPipeline().setSteps(postProcessingList.getItems());
    }

    /**
     * Will trigger a filter remove event to the Emulator
     */
    @FXML
    public void removeSelectedFilter() {
        postProcessingList.getItems().remove(postProcessingList.getSelectionModel().getSelectedItem());
        emulator.getPipeline().setSteps(postProcessingList.getItems());
    }
}
