package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import openGL.postProcessing.*;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsWindow implements Initializable {

    @FXML private ListView<PostProcessingStep> postProcessingList;
    @FXML private ComboBox<PostProcessingStep> postProcessingComboBox;

    private static SettingsWindow instance;

    public static SettingsWindow getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        postProcessingComboBox.getItems().addAll( NEmuS_Release.getInstance().getPipeline().getAllSteps());
        instance = this;
    }

    @FXML
    public void addProcessingStep(ActionEvent actionEvent) {
        if (postProcessingComboBox.getValue() != null) {
            try {
                postProcessingList.getItems().add(postProcessingComboBox.getValue());
                postProcessingList.layout();
                NEmuS_Release.getInstance().getPipeline().setSteps(postProcessingList.getItems());
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
        NEmuS_Release.getInstance().getPipeline().setSteps(postProcessingList.getItems());
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
        NEmuS_Release.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }

    @FXML
    public void fireLoadROMEvent(ActionEvent event) {
        FileChooser romLoader = new FileChooser();
        romLoader.setInitialDirectory(new File("./"));
        romLoader.getExtensionFilters().add(new FileChooser.ExtensionFilter("iNES file", "*.nes"));
        File file = romLoader.showOpenDialog(null);
        if (file != null)
            NEmuS_Release.getInstance().fireLoadROMEvent(file.getAbsolutePath());
    }

    @FXML
    public void fireResetEvent(ActionEvent event) {
        NEmuS_Release.getInstance().fireResetEvent();
    }

    @FXML
    public void triggerPauseEvent(ActionEvent event) {
        NEmuS_Release.getInstance().pause();
    }

    @FXML
    public void removeSelectedFilter(ActionEvent event) {
        postProcessingList.getItems().remove(postProcessingList.getSelectionModel().getSelectedItem());
        NEmuS_Release.getInstance().getPipeline().setSteps(postProcessingList.getItems());
    }


}
