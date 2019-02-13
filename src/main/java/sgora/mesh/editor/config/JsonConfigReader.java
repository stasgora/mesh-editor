package sgora.mesh.editor.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import sgora.mesh.editor.interfaces.ConfigReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonConfigReader implements ConfigReader {

	private static final Logger LOGGER = Logger.getLogger(JsonConfigReader.class.getName());

	private JSONObject config;
	private String fileName;

	private JsonConfigReader(JSONObject config, String fileName) {
		this.config = config;
		this.fileName = fileName;
	}

	@Override
	public double getDouble(String keyPath) {
		return getParent(keyPath).optDouble(getLastKey(keyPath));
	}

	@Override
	public String getString(String keyPath) {
		return getParent(keyPath).optString(getLastKey(keyPath));
	}

	@Override
	public int getInt(String keyPath) {
		return getParent(keyPath).optInt(getLastKey(keyPath));
	}

	@Override
	public boolean getBool(String keyPath) {
		return getParent(keyPath).optBoolean(getLastKey(keyPath));
	}

	@Override
	public <T> T getValue(String keyPath) {
		return (T) getParent(keyPath).opt(getLastKey(keyPath));
	}

	@Override
	public <T> List<T> getList(String keyPath) {
		List<T> list = new ArrayList<>();
		JSONArray jsonArray = getParent(keyPath).optJSONArray(getLastKey(keyPath));
		jsonArray.forEach(item -> list.add((T) item));
		return list;
	}

	@Override
	public boolean containsPath(String keyPath) {
		return getParent(keyPath, false).has(getLastKey(keyPath));
	}

	private String getLastKey(String keyPath) {
		String[] path = keyPath.split("\\.");
		return path.length > 0 ? path[path.length - 1] : keyPath;
	}

	private JSONObject getParent(String keyPath) {
		return getParent(keyPath, true);
	}

	private JSONObject getParent(String keyPath, boolean checkLastKey) {
		String[] path = keyPath.split("\\.");
		JSONObject parent = config;

		Consumer<Integer> logReadError = index -> {
			LOGGER.log(Level.SEVERE, "Failed reading '" + fileName + "' config property '" + path[index] + "' from path '" + keyPath + "'");
		};
		for (int i = 0; i < path.length - 1; i++) {
			try {
				parent = parent.getJSONObject(path[i]);
			} catch (JSONException e) {
				logReadError.accept(i);
			}
		}
		if(checkLastKey && !parent.has(path[path.length - 1]))
			logReadError.accept(path.length - 1);
		return parent;
	}

	public static JsonConfigReader forResource(String fileName) {
		try(InputStream input = JsonConfigReader.class.getResourceAsStream(fileName)) {
			return constructNew(input, fileName);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating Config Reader for resource '" + fileName + "'", e);
		}
		return null;
	}

	public static JsonConfigReader forFile(String fileName) {
		try(InputStream input = new FileInputStream(new File(fileName))) {
			return constructNew(input, fileName);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating Config Reader for file '" + fileName + "'", e);
		}
		return null;
	}

	private static JsonConfigReader constructNew(InputStream inputStream, String fileName) {
		return new JsonConfigReader(new JSONObject(new JSONTokener(inputStream)), fileName);
	}

}
