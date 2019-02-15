package sgora.mesh.editor.interfaces;

import java.util.List;

public interface LangConfigReader {

	String getText(String keyPath);

	List<String> getMultipartText(String keyPath);

	void onSetMainLanguage();

	boolean containsPath(String keyPath);

}
