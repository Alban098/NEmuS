package gui.lwjgui.windows;

import gui.lwjgui.NEmuSUnified;
import gui.lwjgui.NEmuSContext;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import openGL.filters.Filter;
import openGL.filters.FilterInstance;
import openGL.filters.Parameter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
    private ListView<FilterInstance> postProcessingList;
    @FXML
    private ComboBox<Filter> postProcessingComboBox;
    @FXML
    private TextArea filter_desc;
    @FXML
    private HBox filter_panel;

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
        postProcessingComboBox.getItems().addAll(Filter.values());
        postProcessingList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            filter_desc.clear();
            filter_panel.getChildren().clear();
            if (newValue != null) {
                filter_desc.setText(newValue.filter.getDescription());
                for (Parameter param : newValue.parameters) {
                    Label title = new Label(param.name);
                    TextField field1, field2, field3, field4;
                    VBox field_pane = new VBox();
                    field_pane.getChildren().add(title);
                    field_pane.setPadding(new Insets(5,5,5,5));
                    switch (param.type) {
                        case INTEGER:
                            field1 = new TextField(param.value.toString());
                            field1.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d+"))
                                    field1.setText(oldValue1);
                                param.value = Integer.parseInt(field1.getText());
                            });
                            field_pane.getChildren().add(field1);
                            break;
                        case FLOAT:
                            field1 = new TextField(param.value.toString());
                            field1.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field1.setText(oldValue1);
                                param.value = Float.parseFloat(field1.getText().equals("") ? "0" : field1.getText());
                            });
                            field_pane.getChildren().add(field1);
                            break;
                        case VEC2:
                            Vector2f v2f = (Vector2f) param.value;
                            field1 = new TextField(String.valueOf(v2f.x));
                            field2 = new TextField(String.valueOf(v2f.y));
                            field1.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field1.setText(oldValue1);
                                v2f.x = Float.parseFloat(field1.getText().equals("") ? "0" : field1.getText());
                                param.value = v2f;
                            });
                            field2.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field2.setText(oldValue1);
                                v2f.y = Float.parseFloat(field2.getText().equals("") ? "0" : field2.getText());
                                param.value = v2f;
                            });
                            field_pane.getChildren().add(field1);
                            field_pane.getChildren().add(field2);
                            break;
                        case VEC3:
                            Vector3f v3f = (Vector3f) param.value;
                            field1 = new TextField(String.valueOf(v3f.x));
                            field2 = new TextField(String.valueOf(v3f.y));
                            field3 = new TextField(String.valueOf(v3f.z));
                            field1.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field1.setText(oldValue1);
                                v3f.x = Float.parseFloat(field1.getText().equals("") ? "0" : field1.getText());
                                param.value = v3f;
                            });
                            field2.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field2.setText(oldValue1);
                                v3f.y = Float.parseFloat(field2.getText().equals("") ? "0" : field2.getText());
                                param.value = v3f;
                            });
                            field3.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field3.setText(oldValue1);
                                v3f.z = Float.parseFloat(field3.getText().equals("") ? "0" : field3.getText());
                                param.value = v3f;
                            });
                            field_pane.getChildren().add(field1);
                            field_pane.getChildren().add(field2);
                            field_pane.getChildren().add(field3);
                            break;
                        case VEC4:
                            Vector4f v4f = (Vector4f) param.value;
                            field1 = new TextField(String.valueOf(v4f.x));
                            field2 = new TextField(String.valueOf(v4f.y));
                            field3 = new TextField(String.valueOf(v4f.z));
                            field4 = new TextField(String.valueOf(v4f.w));
                            field1.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field1.setText(oldValue1);
                                v4f.x = Float.parseFloat(field1.getText().equals("") ? "0" : field1.getText());
                                param.value = v4f;
                            });
                            field2.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field2.setText(oldValue1);
                                v4f.y = Float.parseFloat(field2.getText().equals("") ? "0" : field2.getText());
                                param.value = v4f;
                            });
                            field3.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field3.setText(oldValue1);
                                v4f.z = Float.parseFloat(field3.getText().equals("") ? "0" : field3.getText());
                                param.value = v4f;
                            });
                            field4.textProperty().addListener((observable, oldValue1, newValue1) -> {
                                if (!newValue1.matches("\\d{0,7}([.]\\d{0,4})?"))
                                    field4.setText(oldValue1);
                                v4f.w = Float.parseFloat(field4.getText().equals("") ? "0" : field4.getText());
                                param.value = v4f;
                            });
                            field_pane.getChildren().add(field1);
                            field_pane.getChildren().add(field2);
                            field_pane.getChildren().add(field3);
                            field_pane.getChildren().add(field4);
                            break;
                    }
                    filter_panel.getChildren().add(field_pane);
                }
            }
        });
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
                postProcessingList.getItems().add(new FilterInstance(postProcessingComboBox.getValue(), postProcessingComboBox.getValue().getDefaultParameters()));
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
        FilterInstance selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == 0)
            return;
        FilterInstance switched = postProcessingList.getItems().get(index - 1);
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
        FilterInstance selected = postProcessingList.getSelectionModel().getSelectedItem();
        int index = postProcessingList.getSelectionModel().getSelectedIndex();
        if (index == postProcessingList.getItems().size() - 1)
            return;
        FilterInstance switched = postProcessingList.getItems().get(index + 1);
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
     * @param filters a list of filters to add (orders)
     */
    private void populateList(List<FilterInstance> filters) {
          postProcessingList.getItems().addAll(filters);
    }
}
