package sgora.mesh.editor.services.mapping;

import org.json.JSONObject;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.model.observables.Observable;
import sgora.mesh.editor.model.observables.SettableProperty;

import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigModelMapper {

	private static final Logger LOGGER = Logger.getLogger(ConfigModelMapper.class.getName());

	private AppConfigReader appConfig;

	private static final String CONFIG_TYPE_FIELD = "objectType";

	public ConfigModelMapper(AppConfigReader appConfig) {
		this.appConfig = appConfig;
	}

	public void mapConfigPathToModelObject(Observable modelObject, String configPath) {
		mapConfigPathToModelObject(modelObject, configPath, false);
	}

	public void mapConfigPathToModelObject(Observable modelObject, String configPath, boolean asDefaultValue) {
		JSONObject configPathRoot = appConfig.getJsonObject(configPath);
		for (String propertyKey : configPathRoot.keySet()) {
			Field modelField = getModelField(modelObject, propertyKey);
			if(modelField == null) {
				continue;
			}
			Type modelFieldType = modelField.getGenericType();
			if(!(modelFieldType instanceof ParameterizedType)) {
				continue;
			}
			Type modelValueType = ((ParameterizedType) modelFieldType).getActualTypeArguments()[0];
			Object configValue = getConfigFieldValue(configPathRoot, propertyKey, modelValueType);
			if(configValue == null) {
				continue;
			}
			SettableProperty modelFieldValue;
			try {
				modelFieldValue = (SettableProperty) modelField.get(modelObject);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, "Getting model container field failed", e);
				continue;
			}
			if (configValue instanceof JSONObject) {
				configValue = constructModelValueObject(modelValueType, configValue);
				if(configValue == null) {
					continue;
				}
			}
			if(!asDefaultValue) {
				modelFieldValue.set(configValue);
			} else {
				modelFieldValue.setDefaultValue(configValue);
			}
		}
	}

	private Object constructModelValueObject(Type modelValueType, Object configValue) {
		Object modelValue = createModelValueObject(modelValueType);
		if(modelValue == null) {
			return null;
		}
		Class<?> modelValueClass = modelValue.getClass();
		JSONObject configParameter = (JSONObject) configValue;
		for (String modelKey : (configParameter).keySet()) {
			if(modelKey.equals(CONFIG_TYPE_FIELD)) {
				continue;
			}
			Field field;
			try {
				field = modelValueClass.getDeclaredField(modelKey);
				field.setAccessible(true);
				field.set(modelValue, configParameter.get(modelKey));
			} catch (NoSuchFieldException e) {
				LOGGER.log(Level.SEVERE, "Getting model field failed", e);
				continue;
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, "Setting model field value failed", e);
				continue;
			}
		}
		return modelValue;
	}

	private Object createModelValueObject(Type modelValueType) {
		Object modelValue;
		try {
			Constructor<? extends Type> constructor = ((Class) modelValueType).getConstructor();
			modelValue = constructor.newInstance();
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, "No default constructor found for model class", e);
			return null;
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, "Creating model object instance failed", e);
			return null;
		}
		return modelValue;
	}

	private Object getConfigFieldValue(JSONObject configPathRoot, String propertyKey, Type modelValueType) {
		String modelValueTypeName = modelValueType.getTypeName();
		Object configValue = configPathRoot.get(propertyKey);
		String configValueType;
		if (configValue instanceof JSONObject) {
			configValueType = ((JSONObject) configValue).optString(CONFIG_TYPE_FIELD);
			if(configValueType == null) {
				return null;
			}
		} else {
			configValueType = configValue.getClass().getCanonicalName();
		}
		if(!modelValueTypeName.equals(configValueType)) {
			LOGGER.log(Level.WARNING, "Model field type " + modelValueType + " does not match config value type " + configValueType);
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
