package sgora.mesh.editor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.canvas.ImgCanvas;

import java.io.File;

public class AppController {

    private Stage stage;

    public ImgCanvas canvas;
    public AnchorPane canvasPane;
    public SplitPane splitPane;

    void init(Stage stage) {
        this.stage = stage;

        canvasPane.widthProperty().addListener((observable, oldVal, newVal) -> onCanvasAreaResized());
        canvasPane.heightProperty().addListener((observable, oldVal, newVal) -> onCanvasAreaResized());
        canvas.setOnScroll(event -> canvas.onScroll(event));
        canvas.setOnMouseMoved(event -> canvas.onMouseMove(event));
        canvas.setOnMouseDragged(event -> canvas.onMouseDrag(event));
        canvas.setOnMousePressed(event -> canvas.onDragStarted(event));
    }

    private void onCanvasAreaResized() {
        canvas.setWidth(canvasPane.getWidth());
        canvas.setHeight(canvasPane.getHeight());
    }

    public void loadImage(ActionEvent event) {
        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("Choose Image");
        imageChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
        File image = imageChooser.showOpenDialog(stage);

        if(image == null)
            return;
        canvas.setBaseImage(new Image("file:" + image.getAbsolutePath()));
    }

    public void exitApp(ActionEvent event) {
        Platform.exit();
    }

}
