package sgora.mesh.editor.config;

import org.json.JSONArray;
import org.json.JSONObject;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.model.JsonConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonAppConfigReader extends JsonConfigReader implements AppConfigReader {

	private static final Logger LOGGER = Logger.getLogger(JsonAppConfigReader.class.getName());

	private JsonConfig config;

	public JsonAppConfigReader(JsonConfig config) {
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

	public static JsonAppConfigReader forResource(String fileName) {
		return new JsonAppConfigReader(loadJsonConfig(fileName));
	}

	public static JsonAppConfigReader forFile(String fileName) {
		try(InputStream input = new FileInputStream(new File(fileName))) {
			return new JsonAppConfigReader(createJsonConfig(input, fileName));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating Config Reader for resource '" + fileName + "'", e);
		}
		return null;
	}

	@Override
	public boolean containsPath(String keyPath) {
		return containsPath(config, keyPath);
	}

}
