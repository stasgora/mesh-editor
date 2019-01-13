package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.ObservableModel;

public class Point extends ObservableModel {

	public double x, y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {
		this(point.x, point.y);
	}

	public Point() {
		this(0, 0);
	}

	public Point set(Point point) {
		setValues(point.x, point.y);
		return this;
	}

	public Point abs() {
		setValues(Math.abs(x), Math.abs(y));
		return this;
	}

	public Point clamp(Point min, Point max) {
		setValues(Math.max(min.x, Math.min(max.x, x)), Math.max(min.y, Math.min(max.y, y)));
		return this;
	}

	public Point multiplyByScalar(double amount) {
		setValues(x * amount, y * amount);
		return this;
	}

	public Point multiply(Point point) {
		setValues(x * point.x, y * point.y);
		return this;
	}

	public Point divide(Point point) {
		setValues(x / point.x, y / point.y);
		return this;
	}

	public Point subtract(Point point) {
		setValues(x - point.x, y - point.y);
		return this;
	}

	public Point add(Point point) {
		setValues(x + point.x, y + point.y);
		return this;
	}

	private void setValues(double x, double y) {
		if(this.x != x || this.y != y)
			onValueChanged();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ')';
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && x == ((Point) obj).x && y == ((Point) obj).y;
	}

}
