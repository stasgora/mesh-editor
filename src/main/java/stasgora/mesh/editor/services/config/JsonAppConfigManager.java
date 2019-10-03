package stasgora.mesh.editor.services.config;

import org.json.JSONArray;
import org.json.JSONObject;
import stasgora.mesh.editor.model.config.JsonConfig;
import stasgora.mesh.editor.model.config.JsonFileConfig;
import stasgora.mesh.editor.services.config.interfaces.AppConfigManager;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonAppConfigManager extends JsonAppConfigReader implements AppConfigManager {
	protected final Logger logger = Logger.getLogger(JsonAppConfigManager.class.getName());

	public JsonAppConfigManager(JsonConfig config) {
		super(config);
	}

	@Override
	public void setDouble(String keyPath, double value) {
		setValue(config, keyPath, value);
	}

	@Override
	public void setString(String keyPath, String value) {
		setValue(config, keyPath, value);
	}

	@Override
	public void setInt(String keyPath, int value) {
		setValue(config, keyPath, value);
	}

	@Override
	public void setBool(String keyPath, boolean value) {
		setValue(config, keyPath, value);
	}

	@Override
	public void setStringList(String keyPath, List<String> list) {
		setValue(config, keyPath, new JSONArray(list));
	}

	@Override
	public void setJsonObject(String keyPath, JSONObject object) {
		setValue(config, keyPath, object);
	}

	private void saveConfig() {
		if(!(config instanceof JsonFileConfig))
			return;
		try(FileWriter writer = new FileWriter(((JsonFileConfig) config).configFile)) {
			writer.write(config.config.toString(4).replaceAll(" {4}", "\t"));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Saving config failed", e);
		}
	}

	protected <T> void setValue(JsonConfig config, String keyPath, T value) {
		JSONObject parent = getParent(config, keyPath);
		String lastKey = getLastKey(keyPath);
		parent.put(lastKey, value);
		saveConfig();
	}

	static JsonAppConfigManager forFile(String fileName) {
		try (InputStream input = new FileInputStream(new File(fileName))) {
			return new JsonAppConfigManager(createJsonConfig(input, fileName, true));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating Config manager for resource '" + fileName + "'", e);
		}
		return null;
	}
}
