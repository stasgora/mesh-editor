package stasgora.mesh.editor.services.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.config.annotation.AppSettings;

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
	AppConfigReader appSettings() {
		return JsonAppConfigReader.forFile("config/app.settings");
	}
}
