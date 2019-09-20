package stasgora.mesh.editor.model;

import org.json.JSONObject;

public class JsonConfig {

	public final String name;
	public final JSONObject config;

	public JsonConfig(String name, JSONObject config) {
		this.name = name;
		this.config = config;
	}

}
