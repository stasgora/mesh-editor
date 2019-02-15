package sgora.mesh.editor.interfaces;

import java.util.List;

public interface AppConfigReader {

	double getDouble(String keyPath);
	String getString(String keyPath);
	int getInt(String keyPath);
	boolean getBool(String keyPath);

	List<String> getStringList(String keyPath);

	boolean containsPath(String keyPath);

}
