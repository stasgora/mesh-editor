package stasgora.mesh.editor.services.config.interfaces;

import org.json.JSONObject;

import java.util.List;

public interface AppConfigWriter {

	void setDouble(String keyPath, double value);

	void setString(String keyPath, String value);

	void setInt(String keyPath, int value);

	void setBool(String keyPath, boolean value);

	void setStringList(String keyPath, List<String> list);

	void setJsonObject(String keyPath, JSONObject object);

}
