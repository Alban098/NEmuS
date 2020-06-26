package utils.javafx;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import openGL.filters.Parameter;
import org.joml.Vector4f;

public class Vec4Field extends VBox {

    public Vec4Field(Parameter parameter) {
        super();
        if (parameter.value instanceof Vector4f) {
            Vector4f val = (Vector4f) parameter.value;
            TextField x = new TextField(String.valueOf(val.x));
            TextField y = new TextField(String.valueOf(val.y));
            TextField z = new TextField(String.valueOf(val.z));
            TextField w = new TextField(String.valueOf(val.w));
            x.setMaxWidth(40);
            y.setMaxWidth(40);
            z.setMaxWidth(40);
            w.setMaxWidth(40);
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
            z.textProperty().addListener((observable, oldValue1, newValue1) -> {
                if (!newValue1.matches("\\d{0,20}([.]\\d{0,4})?"))
                    z.setText(oldValue1);
                val.z = Integer.parseInt(z.getText());
                parameter.value = val;
            });
            w.textProperty().addListener((observable, oldValue1, newValue1) -> {
                if (!newValue1.matches("\\d{0,20}([.]\\d{0,4})?"))
                    w.setText(oldValue1);
                val.w = Integer.parseInt(w.getText());
                parameter.value = val;
            });
            getChildren().add(x);
            getChildren().add(y);
            getChildren().add(z);
            getChildren().add(w);
        }
    }
}