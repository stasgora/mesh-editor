package stasgora.mesh.editor.model.project;

import io.github.stasgora.observetree.Observable;
import io.github.stasgora.observetree.SettableProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PropertyContainer extends Observable {
	private final Logger LOGGER = Logger.getLogger(getClass().getName());

	private List<SettableProperty> properties;

	public PropertyContainer() {
		this.properties = scanFields();
		properties.forEach(this::addSubObservable);
	}

	private List<SettableProperty> scanFields() {
		return Arrays.stream(getClass().getFields()).filter(field -> SettableProperty.class.isAssignableFrom(field.getType())).map(field -> {
			try {
				return (SettableProperty) field.get(this);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, "Resolving property field " + field.getName() + " failed", e);
			}
			return null;
		}).collect(Collectors.toList());
	}

	public void writeProperties(ObjectOutputStream out) throws IOException, ClassNotFoundException {
		iterateProperties((property, stream) -> stream.writeObject(property), out);
	}

	public void readProperties(ObjectInputStream in) throws IOException, ClassNotFoundException {
		iterateProperties((property, stream) -> property.set(stream.readObject()), in);
		notifyListeners();
	}

	public void saveDefaultValues() {
		iterateProperties(SettableProperty::saveAsDefaultValue);
	}

	public void restoreDefaultValues() {
		iterateProperties(SettableProperty::resetToDefaultValue);
	}

	private <T> void iterateProperties(StreamAction<T> propertyAction, T param) throws IOException, ClassNotFoundException {
		for (SettableProperty property : properties) {
			if(property.get() instanceof PropertyContainer)
				((PropertyContainer) property.get()).iterateProperties(propertyAction, param);
			else
				propertyAction.execute(property, param);
		}
	}

	private void iterateProperties(Consumer<SettableProperty> propertyAction) {
		for (SettableProperty property : properties) {
			if(property.get() instanceof PropertyContainer)
				((PropertyContainer) property.get()).iterateProperties(propertyAction);
			else
				propertyAction.accept(property);
		}
	}

	@FunctionalInterface
	private interface StreamAction<T> {
		void execute(SettableProperty property, T stream) throws IOException, ClassNotFoundException;
	}
}
