package utils.javafx;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import openGL.filters.Parameter;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Mat4Field extends VBox {

    public Mat4Field(Parameter parameter) {
        super();
        if (parameter.value instanceof Matrix4f) {
            Matrix4f val = (Matrix4f) parameter.value;
            for (int i = 0; i < 4; i++) {
                HBox fieldRow = new HBox();
                Vector4f row = new Vector4f();
                val.getRow(i, row);
                TextField x = new TextField(String.valueOf(row.x));
                TextField y = new TextField(String.valueOf(row.y));
                TextField z = new TextField(String.valueOf(row.z));
                TextField w = new TextField(String.valueOf(row.w));
                x.setMaxWidth(40);
                y.setMaxWidth(40);
                z.setMaxWidth(40);
                w.setMaxWidth(40);
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
                w.textProperty().addListener((observable, oldValue1, newValue1) -> {
                    if (!newValue1.matches("\\d+([.]\\d{0,4})?"))
                        w.setText(oldValue1);
                    row.w = Float.parseFloat(w.getText());
                    val.setRow(finalI, row);
                    parameter.value = val;
                });
                fieldRow.getChildren().add(x);
                fieldRow.getChildren().add(y);
                fieldRow.getChildren().add(z);
                fieldRow.getChildren().add(w);
                getChildren().add(fieldRow);
            }
        }
    }
}