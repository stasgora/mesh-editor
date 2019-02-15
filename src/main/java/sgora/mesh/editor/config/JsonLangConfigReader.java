package sgora.mesh.editor.config;

import javafx.collections.ObservableMap;
import org.json.JSONArray;
import org.json.JSONObject;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.interfaces.LangConfigReader;
import sgora.mesh.editor.model.JsonConfig;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonLangConfigReader extends JsonConfigReader implements LangConfigReader {

	private static final Logger LOGGER = Logger.getLogger(JsonLangConfigReader.class.getName());

	private final AppConfigReader appConfig;
	private final AppConfigReader appSettings;
	private ObservableMap<String, Object> fxmlNamespace;

	private List<JsonConfig> configList = new ArrayList<>();

	public JsonLangConfigReader(AppConfigReader appConfig, AppConfigReader appSettings, ObservableMap<String, Object> fxmlNamespace) {
		this.appConfig = appConfig;
		this.appSettings = appSettings;
		this.fxmlNamespace = fxmlNamespace;
		configList.add(loadJsonConfig(getLangFileName(appConfig.getString("default.language"))));
		onSetMainLanguage();
	}

	@Override
	public String getText(String keyPath) {
		for (JsonConfig config : configList) {
			if(containsPath(config, keyPath)) {
				return getValue(config, keyPath, JSONObject::optString);
			}
		}
		logMissingKey(keyPath);
		return "";
	}

	@Override
	public List<String> getMultipartText(String keyPath) {
		for (JsonConfig config : configList) {
			if(containsPath(config, keyPath)) {
				return getList(config, keyPath, JSONArray::optString);
			}
		}
		logMissingKey(keyPath);
		return Collections.emptyList();
	}

	@Override
	public void onSetMainLanguage() {
		populateLanguageList();
		populateFXMLNamespace();
	}

	@Override
	public boolean containsPath(String keyPath) {
		for (JsonConfig config : configList) {
			if(containsPath(config, keyPath)) {
				return true;
			}
		}
		return false;
	}

	private void populateLanguageList() {
		configList.subList(0, configList.size() - 1).clear();
		String defLang = appConfig.getString("default.language");
		String mainLang = appSettings.getString("settings.language");
		if(mainLang.equals(defLang)) {
			return;
		}
		if(mainLang.contains("_")) {
			String generalizedLang = mainLang.split("_")[0];
			if(!generalizedLang.equals(defLang)) {
				configList.add(0, loadJsonConfig(getLangFileName(generalizedLang)));
			}
		}
		configList.add(0, loadJsonConfig(getLangFileName(mainLang)));
	}

	private void populateFXMLNamespace() {
		//iterate over default language property tree
		JSONObject root = configList.get(configList.size() - 1).config.getJSONObject("fxml");
		scanChildren("", root);
	}

	private void scanChildren(String keyPath, JSONObject current) {
		for (String key : current.keySet()) {
			Object child = current.get(key);
			String childKey = keyPath.isEmpty() ? key : keyPath + "." + key;
			if(child instanceof JSONObject) {
				scanChildren(childKey, (JSONObject) child);
			} else {
				fxmlNamespace.put(childKey.replace('.', '_'), getText("fxml." + childKey));
			}
		}
	}

	private void logMissingKey(String keyPath) {
		String languages = String.join(" -> ", configList.stream().map(item -> item.name).toArray(String[]::new));
		LOGGER.log(Level.SEVERE, "Failed reading language property (" + languages + ") from path '" + keyPath + "'");
	}

	private String getLangFileName(String lang) {
		return Paths.get("/i18n", lang + '.' + appConfig.getString("extension.localization")).toString();
	}

}
