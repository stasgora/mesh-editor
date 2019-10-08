package dev.sgora.mesheditor.services.history;

import dev.sgora.mesheditor.services.history.actions.UserAction;

public interface ActionHistoryService {
	void undo();

	void redo();

	void registerAction(UserAction action);

	void clearActionHistory();
}
