package sgora.mesh.editor.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import sgora.mesh.editor.model.Point;

public class ImgCanvas extends Canvas {

    private Image baseImage;
    private boolean drawImage = true;

    private ImageBox imageBox;

    private Point size;
    private Point lastMouseDragPoint;
    private Point mousePos = new Point(0, 0);

    public ImgCanvas() {
        size = new Point(getWidth(), getHeight());
        widthProperty().addListener(evt -> onResize());
        heightProperty().addListener(evt -> onResize());
    }

    private void onResize() {
        if(getWidth() == 0 || getHeight() == 0)
            return;

        size.x = getWidth();
        size.y = getHeight();

        if(baseImage == null)
            return;

        imageBox.onResizeCanvas(new Point(size));
        draw();
    }

    private void draw() {
        if(baseImage == null)
            return;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, size.x, size.y);

        if(drawImage) {
            gc.drawImage(baseImage, imageBox.getImagePos().x, imageBox.getImagePos().y, imageBox.getImageSize().x, imageBox.getImageSize().y);
        }
    }

    public void onScroll(ScrollEvent event) {
        imageBox.onScroll(new Point(mousePos), -event.getDeltaY());
        draw();
    }

    public void onDragStarted(MouseEvent event) {
        lastMouseDragPoint = new Point(event.getX(), event.getY());
    }

    public void onMouseDrag(MouseEvent event) {
        Point mousePos = new Point(event.getX(), event.getY());
        imageBox.onMouseDrag(new Point(mousePos).substract(lastMouseDragPoint));
        lastMouseDragPoint = mousePos;
        draw();
    }

    public void onMouseMove(MouseEvent event) {
        mousePos.x = event.getX();
        mousePos.y = event.getY();
    }

    public void setBaseImage(Image image) {
        this.baseImage = image;
        this.imageBox = new ImageBox(new Point(baseImage.getWidth(), baseImage.getHeight()), new Point(size));
        draw();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

}
