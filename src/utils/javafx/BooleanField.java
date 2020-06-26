package utils.javafx;

import javafx.scene.control.CheckBox;
import openGL.filters.Parameter;


public class BooleanField extends CheckBox {

    public BooleanField(Parameter parameter) {
        super();
        setSelected((Boolean) parameter.value);
        this.setOnMouseClicked(event -> parameter.value = isSelected());
    }
}