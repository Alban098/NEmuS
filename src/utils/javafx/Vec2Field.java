package utils.javafx;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import openGL.filters.Parameter;
import org.joml.Vector2f;

public class Vec2Field extends VBox {

    public Vec2Field(Parameter parameter) {
        super();
        if (parameter.value instanceof Vector2f) {
            Vector2f val = (Vector2f) parameter.value;
            TextField x = new TextField(String.valueOf(val.x));
            TextField y = new TextField(String.valueOf(val.y));
            x.setMaxWidth(40);
            y.setMaxWidth(40);
            x.textProperty().addListener((observable, oldValue1, newValue1) -> {
                if (!newValue1.matches("\\d{0,20}([.]\\d{0,4})?"))
                    x.setText(oldValue1);
                val.x = Integer.parseInt(x.getText());
                parameter.value = val;
            });
            y.textProperty().addListener((observable, oldValue1, newValue1) -> {
                if (!newValue1.matches("\\d{0,20}([.]\\d{0,4})?"))
                    y.setText(oldValue1);
                val.y = Integer.parseInt(y.getText());
                parameter.value = val;
            });
            getChildren().add(x);
            getChildren().add(y);
        }
    }
}