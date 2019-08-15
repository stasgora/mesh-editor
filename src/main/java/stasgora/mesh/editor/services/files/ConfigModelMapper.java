package stasgora.mesh.editor.services.files;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.stasgora.observetree.Observable;
import io.github.stasgora.observetree.SettableProperty;
import org.json.JSONObject;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ConfigModelMapper {

	private static final Logger LOGGER = Logger.getLogger(ConfigModelMapper.class.getName());
	private static final String CONFIG_TYPE_FIELD = "objectType";

	private AppConfigReader appConfig;

	@Inject
	ConfigModelMapper(@AppConfig AppConfigReader appConfig) {
		this.appConfig = appConfig;
	}

	public void map(Object model, String configPath) {
		JSONObject configPathRoot = appConfig.getJsonObject(configPath);
		for (String propertyKey : configPathRoot.keySet()) {
			Field modelField = getModelField(model, propertyKey);
			if (modelField == null)
				continue;
			Class modelFieldType = modelField.getType();
			boolean isFieldSettable = SettableProperty.class.isAssignableFrom(modelFieldType);
			Object modelFieldValue = getModelFieldValue(model, modelField);
			if (modelFieldValue == null)
				continue;
			SettableProperty modelSettableField = null;
			if (isFieldSettable) {
				modelSettableField = (SettableProperty) modelFieldValue;
				modelFieldValue = modelSettableField.get();
				modelFieldType = (Class) ((ParameterizedType) modelField.getGenericType()).getActualTypeArguments()[0];
			}
			Object configFieldValue = configPathRoot.get(propertyKey);
			if (configFieldValue instanceof JSONObject) {
				map(modelFieldValue, configPath + "." + propertyKey);
				if (isFieldSettable)
					callSettableOnValueChanged(modelSettableField);
			} else {
				Object fieldValue = getPrimitiveConfigFieldValue(configFieldValue, modelFieldType);
				if (isFieldSettable)
					modelSettableField.set(fieldValue);
				else
					setModelField(modelField, model, fieldValue);
			}
		}
	}

	private Object getPrimitiveConfigFieldValue(Object configFieldValue, Class modelFieldType) {
		if (modelFieldType.equals(Double.class) && configFieldValue.getClass().equals(Integer.class)) {
			return ((Integer) configFieldValue).doubleValue();
		}
		if (modelFieldType.isEnum() && configFieldValue.getClass().equals(String.class)) {
			return Enum.valueOf(modelFieldType, (String) configFieldValue);
		}
		return configFieldValue;
	}

	private void callSettableOnValueChanged(SettableProperty object) {
		try {
			Method method = Observable.class.getDeclaredMethod("onValueChanged");
			method.setAccessible(true);
			method.invoke(object);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			LOGGER.log(Level.WARNING, "Calling settable onValueChanged failed ", e);
		}
	}

	private Field getModelField(Object modelObject, String fieldName) {
		try {
			return modelObject.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			LOGGER.log(Level.WARNING, "Unmappable config field " + fieldName, e);
			return null;
		}
	}

	private void setModelField(Field modelField, Object model, Object fieldValue) {
		try {
			modelField.set(model, fieldValue);
		} catch (IllegalAccessException e) {
			LOGGER.log(Level.WARNING, "Setting model field value failed", e);
		}
	}

	private Object getModelFieldValue(Object model, Field modelField) {
		Object modelFieldValue;
		try {
			modelFieldValue = modelField.get(model);
		} catch (IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "Getting model field failed", e);
			return null;
		}
		return modelFieldValue;
	}

}
