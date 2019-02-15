package sgora.mesh.editor.model;

import org.json.JSONObject;

public class JsonConfig {

	public String name;
	public JSONObject config;

	public JsonConfig(String name, JSONObject config) {
		this.name = name;
		this.config = config;
	}

}
