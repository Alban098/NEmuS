package utils.javafx;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import openGL.filters.Parameter;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class Mat3Field extends VBox {

    public Mat3Field(Parameter parameter) {
        super();
        if (parameter.value instanceof Matrix3f) {
            Matrix3f val = (Matrix3f) parameter.value;
            for (int i = 0; i < 3; i++) {
                HBox fieldRow = new HBox();
                Vector3f row = new Vector3f();
                val.getRow(i, row);
                TextField x = new TextField(String.valueOf(row.x));
                TextField y = new TextField(String.valueOf(row.y));
                TextField z = new TextField(String.valueOf(row.z));
                x.setMaxWidth(40);
                y.setMaxWidth(40);
                z.setMaxWidth(40);
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
                z.textProperty().addListener((observable, oldValue1, newValue1) -> {
                    if (!newValue1.matches("\\d+([.]\\d{0,4})?"))
                        z.setText(oldValue1);
                    row.z = Float.parseFloat(z.getText());
                    val.setRow(finalI, row);
                    parameter.value = val;
                });
                fieldRow.getChildren().add(x);
                fieldRow.getChildren().add(y);
                fieldRow.getChildren().add(z);
                getChildren().add(fieldRow);
            }
        }
    }
}