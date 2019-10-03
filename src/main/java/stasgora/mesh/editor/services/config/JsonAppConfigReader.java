package stasgora.mesh.editor.services.config;

import org.json.JSONArray;
import org.json.JSONObject;
import stasgora.mesh.editor.model.config.JsonConfig;
import stasgora.mesh.editor.services.config.interfaces.AppConfigReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class JsonAppConfigReader extends JsonConfigReader implements AppConfigReader {
	protected static final Logger LOGGER = Logger.getLogger(JsonAppConfigReader.class.getName());

	protected JsonConfig config;

	protected JsonAppConfigReader(JsonConfig config) {
		this.config = config;
	}

	@Override
	public double getDouble(String keyPath) {
		return this.<Double>getValue(config, keyPath, JSONObject::optDouble);
	}

	@Override
	public String getString(String keyPath) {
		return this.getValue(config, keyPath, JSONObject::optString);
	}

	@Override
	public int getInt(String keyPath) {
		return this.<Integer>getValue(config, keyPath, JSONObject::optInt);
	}

	@Override
	public boolean getBool(String keyPath) {
		return this.<Boolean>getValue(config, keyPath, JSONObject::optBoolean);
	}

	@Override
	public List<String> getStringList(String keyPath) {
		return this.getList(config, keyPath, JSONArray::getString);
	}

	@Override
	public JSONObject getJsonObject(String keyPath) {
		return getJsonObject(config, keyPath);
	}

	static JsonAppConfigReader forResource(String fileName) {
		return new JsonAppConfigReader(loadJsonConfig(fileName));
	}

	@Override
	public boolean containsPath(String keyPath) {
		return containsPath(config, keyPath);
	}

}
