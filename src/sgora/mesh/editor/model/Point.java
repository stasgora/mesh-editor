package sgora.mesh.editor.model;

public class Point {

    public double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public Point() {
        this(0, 0);
    }

    public Point clamp(Point min, Point max) {
        x = Math.max(min.x, Math.min(max.x, x));
        y = Math.max(min.y, Math.min(max.y, y));
        return this;
    }

    public Point clamp(Point max) {
        return clamp(new Point(), max);
    }

    public Point min(Point other) {
        x = Math.max(x, other.x);
        y = Math.max(y, other.y);
        return this;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass()) && x == ((Point) obj).x && y == ((Point) obj).y;
    }

    public Point multiplyByScalar(double amount) {
        x *= amount;
        y *= amount;
        return this;
    }

    public Point multiply(Point point) {
        x *= point.x;
        y *= point.y;
        return this;
    }

    public Point divide(Point point) {
        x /= point.x;
        y /= point.y;
        return this;
    }

    public Point substract(Point point) {
        x -= point.x;
        y -= point.y;
        return this;
    }

    public Point add(Point point) {
        x += point.x;
        y += point.y;
        return this;
    }

}
