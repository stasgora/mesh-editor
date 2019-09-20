package stasgora.mesh.editor.model.paint;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class SerializableColor implements Serializable {
	private double red;
	private double green;
	private double blue;
	private double alpha;

	private static final long serialVersionUID = 1L;

	public SerializableColor() {
		this(1, 1, 1, 1);
	}

	public SerializableColor(Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.alpha = color.getOpacity();
	}

	public SerializableColor(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color toFXColor() {
		return new Color(red, green, blue, alpha);
	}

	public java.awt.Color toAwtColor() {
		return new java.awt.Color((float) red, (float) green, (float) blue, (float) alpha);
	}

	public SerializableColor averageWith(SerializableColor color) {
		red = (red + color.red) / 2;
		green = (green + color.green) / 2;
		blue = (blue + color.blue) / 2;
		alpha = (alpha + color.alpha) / 2;
		return this;
	}

	public double getRed() {
		return red;
	}

	public SerializableColor setRed(double red) {
		this.red = red;
		return this;
	}

	public double getGreen() {
		return green;
	}

	public SerializableColor setGreen(double green) {
		this.green = green;
		return this;
	}

	public double getBlue() {
		return blue;
	}

	public SerializableColor setBlue(double blue) {
		this.blue = blue;
		return this;
	}

	public double getAlpha() {
		return alpha;
	}

	public SerializableColor setAlpha(double alpha) {
		this.alpha = alpha;
		return this;
	}
}
