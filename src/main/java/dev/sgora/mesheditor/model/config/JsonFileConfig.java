package dev.sgora.mesheditor.model.config;

import org.json.JSONObject;

import java.io.File;

public class JsonFileConfig extends JsonConfig {
	public final File configFile;

	public JsonFileConfig(File configFile, JSONObject config) {
		super(configFile.getName(), config);
		this.configFile = configFile;
	}
}
