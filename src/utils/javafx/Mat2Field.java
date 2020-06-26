package utils.javafx;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import openGL.filters.Parameter;
import org.joml.Matrix2f;
import org.joml.Vector2f;

public class Mat2Field extends VBox {

    public Mat2Field(Parameter parameter) {
        super();
        if (parameter.value instanceof Matrix2f) {
            Matrix2f val = (Matrix2f) parameter.value;
            for (int i = 0; i < 2; i++) {
                HBox fieldRow = new HBox();
                Vector2f row = new Vector2f();
                val.getRow(i, row);
                TextField x = new TextField(String.valueOf(row.x));
                TextField y = new TextField(String.valueOf(row.y));
                x.setMaxWidth(40);
                y.setMaxWidth(40);
                int finalI = i;
                x.textProperty().addListener((observable, oldValue1, newValue1) -> {
                    if (!newValue1.matches("\\d+([.]\\d{0,4})?"))
                        x.setText(oldValue1);
                    row.x = Float.parseFloat(x.getText());
                    val.setRow(finalI, row);
                    parameter.value = val;
                });
                y.textProperty().addListener((observable, oldValue1, newValue1) -> {
                    if (!newValue1.matches("\\d+([.]\\d{0,4})?"))
                        y.setText(oldValue1);
                    row.y = Float.parseFloat(y.getText());
                    val.setRow(finalI, row);
                    parameter.value = val;
                });
                fieldRow.getChildren().add(x);
                fieldRow.getChildren().add(y);
                getChildren().add(fieldRow);
            }
        }
    }
}