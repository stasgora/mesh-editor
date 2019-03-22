package sgora.mesh.editor.services.mapping;

import org.json.JSONObject;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.model.observables.Observable;
import sgora.mesh.editor.model.observables.SettableProperty;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigModelMapper {

	private static final Logger LOGGER = Logger.getLogger(ConfigModelMapper.class.getName());

	private AppConfigReader appConfig;

	private static final String CONFIG_TYPE_FIELD = "type";

	public ConfigModelMapper(AppConfigReader appConfig) {
		this.appConfig = appConfig;
	}

	public void mapConfigPathToModelObject(Observable modelObject, String configPath) {
		JSONObject configPathRoot = appConfig.getJsonObject(configPath);
		for (String propertyKey : configPathRoot.keySet()) {
			Field modelField = getModelField(modelObject, propertyKey);
			if(modelField == null) {
				continue;
			}
			Object configValue = getConfigFieldValue(configPathRoot, propertyKey, modelField);
			if(configValue == null) {
				continue;
			}
			try {
				SettableProperty modelFieldValue = (SettableProperty) modelField.get(modelObject);
				modelFieldValue.set(configValue);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, "Getting model field value failed", e);
			}
		}
	}

	private Object getConfigFieldValue(JSONObject configPathRoot, String propertyKey, Field modelField) {
		Type modelFieldType = modelField.getGenericType();
		if(!(modelFieldType instanceof ParameterizedType)) {
			return null;
		}
		String modelValueType = ((ParameterizedType) modelFieldType).getActualTypeArguments()[0].getClass().getCanonicalName();
		Object configValue = configPathRoot.get(propertyKey);
		String configValueType;
		if (configValue instanceof JSONObject) {
			configValueType = ((JSONObject) configValue).optString(CONFIG_TYPE_FIELD);
		} else {
			configValueType = configValue.getClass().getCanonicalName();
		}
		if(!modelValueType.equals(configValueType)) {
			LOGGER.log(Level.WARNING, "Model field type " + modelFieldType + " does not match config value type " + configValueType);
			return null;
		}
		return configValue;
	}

	private Field getModelField(Observable modelObject, String fieldName) {
		Field modelField;
		try {
			modelField = modelObject.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			LOGGER.log(Level.WARNING, "Unmappable config field " + fieldName);
			return null;
		}
		if(!SettableProperty.class.isAssignableFrom(modelField.getType())) {
			return null;
		}
		return modelField;
	}

}
