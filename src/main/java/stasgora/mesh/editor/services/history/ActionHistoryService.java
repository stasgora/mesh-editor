package stasgora.mesh.editor.services.history;

import stasgora.mesh.editor.services.history.actions.UserAction;

public interface ActionHistoryService {
	void undo();
	void redo();
	void registerAction(UserAction action);
	void clearActionHistory();
}
