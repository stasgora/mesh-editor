package stasgora.mesh.editor.interfaces.action.history;

public interface ActionHistoryService {
	void undo();
	void redo();
	void registerAction(UserAction action);
	void clearActionHistory();
}
