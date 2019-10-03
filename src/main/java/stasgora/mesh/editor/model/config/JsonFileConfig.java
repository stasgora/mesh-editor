package stasgora.mesh.editor.model.config;

import org.json.JSONObject;

import java.io.File;

public class JsonFileConfig extends JsonConfig {
	public File configFile;

	public JsonFileConfig(File configFile, JSONObject config) {
		super(configFile.getName(), config);
		this.configFile = configFile;
	}
}
