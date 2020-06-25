package gui.lwjgui.windows;

import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSContext;
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
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class represent the Graphics Settings Window
 */
public class GraphicsSettings extends Application implements Initializable {

    private static GraphicsSettings instance;

    private final NEmuSContext emulator;
    private Stage stage;

    @FXML
    private ListView<PostProcessingStep> postProcessingList;
    @FXML
    private ComboBox<PostProcessingStep> postProcessingComboBox;

    /**
     * Create a new instance of GraphicsSettings
     */
    public GraphicsSettings() {
        this.emulator = NEmuSUnified.getInstance().getEmulator();
    }

    /**
     * Does an instance of GraphicsSettings exist
     *
     * @return does an instance exist
     */
    public static boolean hasInstance() {
        return instance != null;
    }

    /**
     * Focus the current instance is it exist
     */
    public static void focusInstance() {
        if (instance != null) {
            instance.stage.setIconified(false);
            instance.stage.requestFocus();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        postProcessingComboBox.getItems().addAll(emulator.getPipeline().getAllSteps());

        populateList(emulator.getPipeline().getSteps());
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

    /**
     * Populate the listView with the right PostProcessingSteps
     *
     * @param steps a list of steps ids to add (orders
     */
    private void populateList(List<String> steps) {
        for (String id : steps) {
            for (PostProcessingStep step : postProcessingComboBox.getItems()) {
                if (id.equals(step.toString())) {
                    postProcessingList.getItems().add(step);
                    break;
                }
            }
        }
    }
}
