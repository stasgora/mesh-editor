package sgora.mesh.editor.model.paint;

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

	public Color getFXColor() {
		return new Color(red, green, blue, alpha);
	}

}
