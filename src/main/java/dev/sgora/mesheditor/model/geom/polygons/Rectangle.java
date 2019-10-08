package dev.sgora.mesheditor.model.geom.polygons;

import dev.sgora.mesheditor.model.geom.Point;
import io.github.stasgora.observetree.Observable;

import java.io.Serializable;

public class Rectangle extends Observable implements Serializable {
	private Point position;
	private Point size;

	private static final long serialVersionUID = 1L;

	public Rectangle() {
		position = new Point();
		addSubObservable(position);
		size = new Point();
		addSubObservable(size);
	}

	public boolean contains(Point point) {
		return point.getX() >= position.getX() && point.getX() <= position.getX() + size.getX() && point.getY() >= position.getY() && point.getY() <= position.getY() + size.getY();
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Point getSize() {
		return size;
	}

	public void setSize(Point size) {
		this.size = size;
	}
}
