package sgora.mesh.editor.model.paint;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class SerializableColor implements Serializable {

	public double red;
	public double green;
	public double blue;
	public double alpha;

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

	public Color getFXColor() {
		return new Color(red, green, blue, alpha);
	}

	public SerializableColor setRed(double red) {
		this.red = red;
		return this;
	}

	public SerializableColor setGreen(double green) {
		this.green = green;
		return this;
	}

	public SerializableColor setBlue(double blue) {
		this.blue = blue;
		return this;
	}

	public SerializableColor setAlpha(double alpha) {
		this.alpha = alpha;
		return this;
	}

}
