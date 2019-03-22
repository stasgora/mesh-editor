package sgora.mesh.editor.interfaces.config;

import org.json.JSONObject;
import sgora.mesh.editor.model.JsonConfig;

import java.util.List;

public interface AppConfigReader {

	double getDouble(String keyPath);
	String getString(String keyPath);
	int getInt(String keyPath);
	boolean getBool(String keyPath);

	List<String> getStringList(String keyPath);

	JSONObject getJsonObject(String keyPath);

	boolean containsPath(String keyPath);

}
