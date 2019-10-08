package dev.sgora.mesheditor.services.history;

import com.google.inject.AbstractModule;

public class ActionHistoryModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ActionHistoryService.class).to(CommandActionHistoryService.class);
	}
}
