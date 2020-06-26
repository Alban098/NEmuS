package utils.javafx;

import javafx.scene.control.TextField;
import openGL.filters.Parameter;

public class FloatField extends TextField {

    public FloatField(Parameter parameter) {
        super(parameter.value.toString());
        setMaxWidth(40);
        textProperty().addListener((observable, oldValue1, newValue1) -> {
            if (!newValue1.matches("\\d+([.]\\d{0,4})?"))
                setText(oldValue1);
            parameter.value = Float.parseFloat(getText());
        });
    }
}
