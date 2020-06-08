package gui;

import gui.interfaces.NEmuS_Release;
import gui.interfaces.NEmuS_Runnable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import openGL.postProcessing.*;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsWindow implements Initializable {

    @FXML private Button pauseButton;
    @FXML private Button ramLeftButton;
    @FXML private Button ramLeftPlusButton;
    @FXML private Button ramRightButton;
    @FXML private Button ramRightPlusButton;
    @FXML private Button cpuStepButton;
    @FXML private Button frameStepButton;
    @FXML private Button paletteSwapButton;
    @FXML private ListView<PostProcessingStep> postProcessingList;
    @FXML private ComboBox<PostProcessingStep> postProcessingComboBox;

    private static SettingsWindow instance;

    public static SettingsWindow getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        postProcessingComboBox.getItems().addAll(NEmuS_Runnable.getInstance().getPipeline().getAllSteps());
        if (NEmuS_Runnable.getInstance() instanceof NEmuS_Release) {
            ramLeftButton.setDisable(true);
            ramLeftPlusButton.setDisable(true);
            ramRightButton.setDisable(true);
            ramRightPlusButton.setDisable(true);
            paletteSwapButton.setDisable(true);
        }
        cpuStepButton.setDisable(true);
        frameStepButton.setDisable(true);
        instance = this;
    }

    @FXML
    public void addProcessingStep(ActionEvent actionEvent) {
        if (postProcessingComboBox.getValue() != null) {
            try {
                postProcessingList.getItems().add(postProcessingComboBox.getValue());
                postProcessingList.layout();
                NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void shiftLeftPostProcessingStep(ActionEvent event) {
        PostProcessingStep selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == 0)
            return;
        PostProcessingStep switched = postProcessingList.getItems().get(index-1);
        postProcessingList.getItems().set(index, switched);
        postProcessingList.getItems().set(index-1, selected);
        postProcessingList.getSelectionModel().select(index-1);
        NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }

    @FXML
    public void shiftRightPostProcessingStep(ActionEvent event) {
        PostProcessingStep selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == postProcessingList.getItems().size() - 1)
            return;
        PostProcessingStep switched = postProcessingList.getItems().get(index+1);
        postProcessingList.getItems().set(index, switched);
        postProcessingList.getItems().set(index+1, selected);
        postProcessingList.getSelectionModel().select(index+1);
        NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }

    @FXML
    public void fireLoadROMEvent(ActionEvent event) {
        FileChooser romLoader = new FileChooser();
        romLoader.setInitialDirectory(new File("./"));
        romLoader.getExtensionFilters().add(new FileChooser.ExtensionFilter("iNES file", "*.nes"));
        File file = romLoader.showOpenDialog(null);
        if (file != null) {
            NEmuS_Runnable.getInstance().fireLoadROMEvent(file.getAbsolutePath());
            cpuStepButton.setDisable(true);
            frameStepButton.setDisable(true);
            pauseButton.setText("Pause");
        }
    }

    @FXML
    public void fireResetEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().fireResetEvent();
        cpuStepButton.setDisable(true);
        frameStepButton.setDisable(true);
        pauseButton.setText("Pause");
    }

    @FXML
    public void triggerPauseEvent(ActionEvent event) {
        if (NEmuS_Runnable.getInstance().pause()) {
            cpuStepButton.setDisable(!NEmuS.DEBUG_MODE || NEmuS_Runnable.getInstance().isEmulationRunning());
            frameStepButton.setDisable(NEmuS_Runnable.getInstance().isEmulationRunning());
            if (!NEmuS_Runnable.getInstance().isEmulationRunning())
                pauseButton.setText("Resume");
            else
                pauseButton.setText("Pause");
        }
    }

    @FXML
    public void removeSelectedFilter(ActionEvent event) {
        postProcessingList.getItems().remove(postProcessingList.getSelectionModel().getSelectedItem());
        NEmuS_Runnable.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }

    @FXML
    public void cpuStepEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().cpuStepEvent();
    }

    @FXML
    public void ramPageRightEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().ramPageRightEvent();
    }

    @FXML
    public void ramPageLeftEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().ramPageLeftEvent();
    }

    @FXML
    public void ramPageRightPlusEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().ramPageRightPlusEvent();
    }

    @FXML
    public void ramPageLeftPlusEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().ramPageLeftPlusEvent();
    }

    @FXML
    public void frameStepEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().frameStepEvent();
    }

    @FXML
    public void paletteSwapEvent(ActionEvent event) {
        NEmuS_Runnable.getInstance().paletteSwapEvent();
    }

}
