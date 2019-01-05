package sgora.mesh.editor.canvas;

import sgora.mesh.editor.model.Point;

public class ImageBox {

    private Point imagePos, imageSize;

    private Point canvasSize, baseImageSize;

    private static final double DEF_BORDER = 0.1;
    private static final double ZOOM_SPEED = 0.0025;
    private static final double DRAG_SPEED = 1;
    private static final double MIN_ZOOM = 0.2;
    private static final double MAX_ZOOM = 100;

    public ImageBox(Point baseImageSize, Point canvasSize) {
        this.baseImageSize = baseImageSize;
        this.canvasSize = canvasSize;
        calcImageBox();
    }

    public void onScroll(Point zoomPos, double zoomAmount) {
        zoomAmount *= ZOOM_SPEED;
        zoomPos.substract(imagePos).multiplyByScalar(zoomAmount);
        zoomAmount = 1 - zoomAmount;

        Point newImgSize = new Point(imageSize).multiplyByScalar(zoomAmount);
        if(newImgSize.x < canvasSize.x * MIN_ZOOM || newImgSize.y < canvasSize.y * MIN_ZOOM
                || newImgSize.x > canvasSize.x * MAX_ZOOM || newImgSize.y > canvasSize.y * MAX_ZOOM)
            return;
        imagePos.add(zoomPos);
        imageSize = newImgSize;
    }

    public void onMouseDrag(Point moveAmount) {
        imagePos.add(moveAmount.multiplyByScalar(DRAG_SPEED)).clamp(new Point(imageSize).multiplyByScalar(-1), canvasSize);
    }

    public void onResizeCanvas(Point canvasSize) {
        Point sizeRatio = new Point(canvasSize).divide(this.canvasSize);

        this.imagePos.multiply(sizeRatio);
        this.imageSize.multiplyByScalar((sizeRatio.y + sizeRatio.x) / 2);
        this.canvasSize = canvasSize;
    }

    private void calcImageBox() {
        double imgRatio = baseImageSize.x / baseImageSize.y;

        if(imgRatio > canvasSize.x / canvasSize.y) {
            double imgWidth = canvasSize.x * (1 - DEF_BORDER);
            double imgHeight = imgWidth / imgRatio;
            imagePos = new Point(canvasSize.x * DEF_BORDER * 0.5, (canvasSize.y - imgHeight) / 2);
            imageSize = new Point(imgWidth, imgHeight);
        } else {
            double imgHeight = canvasSize.y * (1 - DEF_BORDER);
            double imgWidth = imgRatio * imgHeight;
            imagePos = new Point((canvasSize.x - imgWidth) / 2, canvasSize.y * DEF_BORDER * 0.5);
            imageSize = new Point(imgWidth, imgHeight);
        }
    }

    public Point getImagePos() {
        return imagePos;
    }

    public Point getImageSize() {
        return imageSize;
    }

}
