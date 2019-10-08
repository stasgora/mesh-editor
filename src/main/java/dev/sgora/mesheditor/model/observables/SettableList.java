package dev.sgora.mesheditor.model.observables;

import io.github.stasgora.observetree.Observable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class SettableList<T> extends Observable implements Serializable {
	private List<T> modelList = new ArrayList<>();

	private static final long serialVersionUID = 1L;

	public SettableList() { }

	public SettableList(List<T> modelList) {
		this.modelList.addAll(modelList);
	}

	public List<T> get() {
		return new ArrayList<>(modelList);
	}

	public void set(List<T> modelList) {
		if(this.modelList.equals(modelList))
			return;
		this.modelList = modelList;
		onValueChanged();
	}

	public void setAndNotify(List<T> modelList) {
		set(modelList);
		notifyListeners();
	}

	public void modify(UnaryOperator<List<T>> operator) {
		set(operator.apply(modelList));
	}
}
