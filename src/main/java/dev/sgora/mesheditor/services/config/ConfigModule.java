package dev.sgora.mesheditor.services.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.sgora.mesheditor.services.config.annotation.AppConfig;
import dev.sgora.mesheditor.services.config.annotation.AppSettings;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigManager;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigReader;

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
