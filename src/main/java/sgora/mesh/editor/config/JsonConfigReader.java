package sgora.mesh.editor.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonConfigReader {

	private JSONObject config;

	private JsonConfigReader(JSONObject config) {
		this.config = config;
	}

	public double getDouble(String keyPath) {
		return getParent(keyPath).getDouble(getLastKey(keyPath));
	}

	public <T> T getValue(String keyPath) {
		return (T) getParent(keyPath).get(getLastKey(keyPath));
	}

	public <T> List<T> getList(String keyPath) {
		List<T> list = new ArrayList<>();
		JSONArray jsonArray = getParent(keyPath).getJSONArray(getLastKey(keyPath));
		jsonArray.forEach(item -> list.add((T) item));
		return list;
	}

	private String getLastKey(String keyPath) {
		String[] path = keyPath.split("\\.");
		return path.length > 0 ? path[path.length - 1] : keyPath;
	}

	private JSONObject getParent(String keyPath) {
		String[] path = keyPath.split("\\.");
		JSONObject parent = config;
		for (int i = 0; i < path.length - 1; i++) {
			parent = parent.getJSONObject(path[i]);
		}
		return parent;
	}

	public static JsonConfigReader fromResourceFile(String fileName) {
		try(InputStream input = JsonConfigReader.class.getResourceAsStream(fileName)) {
			return constructNew(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JsonConfigReader fromFile(String fileName) {
		try(InputStream input = new FileInputStream(new File(fileName))) {
			return constructNew(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JsonConfigReader constructNew(InputStream inputStream) {
		return new JsonConfigReader(new JSONObject(new JSONTokener(inputStream)));
	}

}