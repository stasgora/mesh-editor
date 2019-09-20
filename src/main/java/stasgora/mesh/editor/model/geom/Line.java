package stasgora.mesh.editor.model.geom;

import java.util.logging.Logger;

public class Line {
	private static final Logger LOGGER = Logger.getLogger(Line.class.getName());

	public final double a;
	public final double b;
	public final double c;

	public Line(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Point intersectWith(Line line) {
		double delta = a * line.b - line.a * b;
		if (delta == 0) {
			LOGGER.warning("Lines are parallel");
			return null;
		}
		return new Point((b * line.c - line.b * c) / delta, (line.a * c - a * line.c) / delta);
	}

	public static Line bisectionOf(Point a, Point b) {
		return new Line(2 * (a.getX() - b.getX()), 2 * (a.getY() - b.getY()), b.getX() * b.getX() + b.getY() * b.getY() - a.getX() * a.getX() - a.getY() * a.getY());
	}

}
