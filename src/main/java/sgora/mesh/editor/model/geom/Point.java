package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.observables.ControlledObservable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Point extends ControlledObservable implements Serializable {

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
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && x == ((Point) obj).x && y == ((Point) obj).y;
	}

}
