package dev.sgora.mesheditor.ui;

import javafx.scene.control.Label;

public class CopyableLabel extends Label {

	public CopyableLabel() {
	}

	public CopyableLabel(CopyableLabel label) {
		setFont(label.getFont());
		setTextFill(label.getTextFill());
	}
}
