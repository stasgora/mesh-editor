package stasgora.mesh.editor.services.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.config.annotation.AppSettings;
import stasgora.mesh.editor.services.config.interfaces.AppConfigManager;
import stasgora.mesh.editor.services.config.interfaces.AppConfigReader;

public class ConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(LangConfigReader.class).to(JsonLangConfigReader.class);
	}

	@AppConfig
	@Provides @Singleton
	AppConfigReader appConfig() {
		return JsonAppConfigReader.forResource("/app.config");
	}

	@AppSettings
	@Provides @Singleton
	AppConfigManager appSettings() {
		return JsonAppConfigManager.forFile("config/app.settings");
	}
}
