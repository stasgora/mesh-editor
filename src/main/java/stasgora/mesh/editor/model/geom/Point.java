package stasgora.mesh.editor.model.geom;

import io.github.stasgora.observetree.Observable;

import java.io.Serializable;
import java.util.Objects;

public class Point extends Observable implements Serializable {

	public double x, y;

	private static final double ROUNDING_MULT = 100;
	private static final long serialVersionUID = 1L;

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
		return set(point.x, point.y);
	}

	public Point set(double x, double y) {
		setValues(x, y);
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

	public Point clamp(Point max) {
		return clamp(new Point(), max);
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

	public Point divideByScalar(double amount) {
		setValues(x / amount, y / amount);
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

	public boolean isBetween(Point min, Point max) {
		return x >= min.x && x < max.x && y >= min.y && y < max.y;
	}

	private void setValues(double x, double y) {
		boolean changed = this.x != x || this.y != y;
		this.x = x;
		this.y = y;
		if(changed) {
			onValueChanged();
		}
	}

	@Override
	public String toString() {
		return "(" + Math.round(x * ROUNDING_MULT) / ROUNDING_MULT + ", " + Math.round(y * ROUNDING_MULT) / ROUNDING_MULT + ')';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Point point = (Point) o;
		return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

}
