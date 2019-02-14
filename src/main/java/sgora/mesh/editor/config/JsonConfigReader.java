package sgora.mesh.editor.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import sgora.mesh.editor.model.JsonConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JsonConfigReader {

	private static final Logger LOGGER = Logger.getLogger(JsonConfigReader.class.getName());

	protected <T> T getValue(JsonConfig config, String keyPath, BiFunction<JSONObject, String, T> getValue) {
		return getValue.apply(getParent(config, keyPath), getLastKey(keyPath));
	}

	protected <T> List<T> getList(JsonConfig config, String keyPath, BiFunction<JSONArray, Integer, T> getValue) {
		JSONArray jsonArray = getParent(config, keyPath).optJSONArray(getLastKey(keyPath));
		List<T> list = new ArrayList<>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++)
			list.add(getValue.apply(jsonArray, i));
		return list;
	}

	protected boolean containsPath(JsonConfig config, String keyPath) {
		JSONObject object = config.config;
		String[] keyChain = getKeyChain(keyPath);
		for (int i = 0; i < keyChain.length; i++) {
			if (!object.has(keyChain[i]))
				return false;
			if(i < keyChain.length - 1)
				object = object.getJSONObject(keyChain[i]);
		}
		return true;
	}

	private String getLastKey(String keyPath) {
		String[] path = getKeyChain(keyPath);
		return path.length > 0 ? path[path.length - 1] : keyPath;
	}

	private String[] getKeyChain(String keyPath) {
		return keyPath.split("\\.");
	}

	private JSONObject getParent(JsonConfig config, String keyPath) {
		String[] path = getKeyChain(keyPath);
		JSONObject parent = config.config;

		for (int i = 0; i < path.length - 1; i++) {
			try {
				parent = parent.getJSONObject(path[i]);
			} catch (JSONException e) {
				LOGGER.log(Level.SEVERE, "Failed reading config property '" + path[i] + "' from path '" + keyPath + "'");
			}
		}
		return parent;
	}

	protected static JsonConfig createJsonConfig(InputStream inputStream, String fileName) {
		return new JsonConfig(fileName, new JSONObject(new JSONTokener(inputStream)));
	}

	protected static JsonConfig loadJsonConfig(String fileName) {
		try(InputStream input = new FileInputStream(new File(fileName))) {
			return createJsonConfig(input, fileName);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating Config Reader for resource '" + fileName + "'", e);
		}
		return null;
	}

}
