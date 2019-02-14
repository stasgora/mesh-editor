package sgora.mesh.editor.config;

import sgora.mesh.editor.enums.LangConfigPosition;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.interfaces.LangConfigReader;
import sgora.mesh.editor.model.JsonConfig;

public class JsonLangConfigReader extends JsonConfigReader implements LangConfigReader {

	private final AppConfigReader appConfig;
	private final AppConfigReader appSettings;

	private JsonConfig[] configList = new JsonConfig[3];

	public JsonLangConfigReader(AppConfigReader appConfig, AppConfigReader appSettings) {
		this.appConfig = appConfig;
		this.appSettings = appSettings;
		configList[LangConfigPosition.DEF_LANG.pos] = loadJsonConfig(getLangFileName(appConfig.getString("default.language")));
	}

	@Override
	public boolean containsPath(String keyPath) {
		return false;
	}

	private String getLangFileName(String lang) {
		return lang + '.' + appConfig.getString("extension.localization");
	}

}
