package utils.javafx;

import javafx.scene.control.TextField;
import openGL.filters.Parameter;

public class IntegerField extends TextField {

    public IntegerField(Parameter parameter) {
        super(parameter.value.toString());
        setMaxWidth(40);
        textProperty().addListener((observable, oldValue1, newValue1) -> {
            if (!newValue1.matches("\\d{0,20}([.]\\d{0,4})?"))
                setText(oldValue1);
            parameter.value = Integer.parseInt(getText());
        });
    }
}