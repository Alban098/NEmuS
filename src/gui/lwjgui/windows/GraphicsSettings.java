package gui.lwjgui.windows;

import javafx.geometry.Pos;
import utils.javafx.*;
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
        postProcessingComboBox.getItems().addAll(Filter.getAll());
        postProcessingList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            filter_desc.clear();
            filter_panel.getChildren().clear();
            if (newValue != null) {
                filter_desc.setText(newValue.filter.getDescription());
                for (Parameter param : newValue.parameters) {
                    Label title = new Label(param.name);
                    VBox field_pane = new VBox();
                    field_pane.alignmentProperty().setValue(Pos.TOP_CENTER);
                    field_pane.getChildren().add(title);
                    field_pane.setPadding(new Insets(5,5,5,5));
                    switch (param.type) {
                        case BOOLEAN -> field_pane.getChildren().add(new BooleanField(param));
                        case INTEGER -> field_pane.getChildren().add(new IntegerField(param));
                        case FLOAT -> field_pane.getChildren().add(new FloatField(param));
                        case VEC2 -> field_pane.getChildren().add(new Vec2Field(param));
                        case VEC3 -> field_pane.getChildren().add(new Vec3Field(param));
                        case VEC4 -> field_pane.getChildren().add(new Vec4Field(param));
                        case MAT2 -> field_pane.getChildren().add(new Mat2Field(param));
                        case MAT3 -> field_pane.getChildren().add(new Mat3Field(param));
                        case MAT4 -> field_pane.getChildren().add(new Mat4Field(param));
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
