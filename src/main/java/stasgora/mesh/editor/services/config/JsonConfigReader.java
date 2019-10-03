package stasgora.mesh.editor.services.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import stasgora.mesh.editor.model.config.JsonConfig;
import stasgora.mesh.editor.model.config.JsonFileConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JsonConfigReader {

	private static final Logger LOGGER = Logger.getLogger(JsonConfigReader.class.getName());

	protected <T> T getValue(JsonConfig config, String keyPath, BiFunction<JSONObject, String, T> getValue) {
		JSONObject parent = getParent(config, keyPath);
		String lastKey = getLastKey(keyPath);
		if (!parent.has(lastKey)) {
			logInvalidKey(config.name, lastKey, keyPath);
		}
		return getValue.apply(parent, lastKey);
	}

	protected <T> List<T> getList(JsonConfig config, String keyPath, BiFunction<JSONArray, Integer, T> getValue) {
		JSONObject parent = getParent(config, keyPath);
		String lastKey = getLastKey(keyPath);
		if (!parent.has(lastKey)) {
			logInvalidKey(config.name, lastKey, keyPath);
			return Collections.emptyList();
		}
		JSONArray jsonArray = parent.getJSONArray(lastKey);
		List<T> list = new ArrayList<>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(getValue.apply(jsonArray, i));
		}
		return list;
	}

	protected boolean containsPath(JsonConfig config, String keyPath) {
		JSONObject object = config.config;
		String[] keyChain = getKeyChain(keyPath);
		for (int i = 0; i < keyChain.length; i++) {
			if (!object.has(keyChain[i])) {
				return false;
			}
			if (i < keyChain.length - 1) {
				object = object.getJSONObject(keyChain[i]);
			}
		}
		return true;
	}

	protected String getLastKey(String keyPath) {
		String[] path = getKeyChain(keyPath);
		return path.length > 0 ? path[path.length - 1] : keyPath;
	}

	private String[] getKeyChain(String keyPath) {
		return keyPath.split("\\.");
	}

	protected JSONObject getParent(JsonConfig config, String keyPath) {
		List<String> keys = Arrays.asList(getKeyChain(keyPath));
		if (keys.size() <= 1) {
			return config.config;
		}
		return getJsonObject(config, String.join(".", keys.subList(0, keys.size() - 1)));
	}

	JSONObject getJsonObject(JsonConfig config, String keyPath) {
		String[] keys = getKeyChain(keyPath);
		JSONObject parent = config.config;

		for (String key : keys) {
			try {
				parent = parent.getJSONObject(key);
			} catch (JSONException e) {
				logInvalidKey(config.name, key, String.join(".", keys));
			}
		}
		return parent;
	}

	protected void logInvalidKey(String configName, String key, String keyPath) {
		LOGGER.log(Level.SEVERE, () -> String.format("Failed reading '%s' config property '%s' from path '%s'", configName, key, keyPath));
	}

	protected static JsonConfig createJsonConfig(InputStream inputStream, String fileName, boolean isFile) {
		JSONObject config = new JSONObject(new JSONTokener(inputStream));
		if(isFile)
			return new JsonFileConfig(new File(fileName), config);
		return new JsonConfig(fileName, config);
	}

	protected static JsonConfig loadJsonConfig(String fileName) {
		try (InputStream input = JsonAppConfigReader.class.getResourceAsStream(fileName)) {
			return createJsonConfig(input, fileName, false);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating Config Reader for resource '" + fileName + "'", e);
		}
		return null;
	}

}
