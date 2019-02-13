package sgora.mesh.editor.interfaces;

import java.util.List;

public interface ConfigReader {
	double getDouble(String keyPath);

	<T> T getValue(String keyPath);

	<T> List<T> getList(String keyPath);

	boolean containsPath(String keyPath);
}
