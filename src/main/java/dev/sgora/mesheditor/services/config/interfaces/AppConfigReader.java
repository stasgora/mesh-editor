package dev.sgora.mesheditor.services.config.interfaces;

import org.json.JSONObject;

import java.util.List;

public interface AppConfigReader {

	double getDouble(String keyPath);

	String getString(String keyPath);

	int getInt(String keyPath);

	boolean getBool(String keyPath);

	List<String> getStringList(String keyPath);

	JSONObject getJsonObject(String keyPath);

	boolean containsPath(String keyPath);

	AppConfigReader opt();

}
