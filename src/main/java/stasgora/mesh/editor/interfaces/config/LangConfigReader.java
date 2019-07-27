package stasgora.mesh.editor.interfaces.config;

import java.util.List;

public interface LangConfigReader {

	String getText(String keyPath);

	List<String> getMultipartText(String keyPath);

	void onSetMainLanguage();

	boolean containsPath(String keyPath);

}
