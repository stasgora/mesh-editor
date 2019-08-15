package stasgora.mesh.editor.services.history;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.history.actions.UserAction;
import stasgora.mesh.editor.services.history.actions.node.NodeModifiedAction;
import stasgora.mesh.editor.services.mesh.generation.TriangulationService;

import java.util.Stack;

@Singleton
class CommandActionHistoryService implements ActionHistoryService {
	private final Stack<UserAction> undoActionStack = new Stack<>();
	private final Stack<UserAction> redoActionStack = new Stack<>();

	@Inject
	CommandActionHistoryService(LoadState loadState, TriangulationService triangulationService) {
		loadState.loaded.addListener(() -> {
			if (!loadState.loaded.get())
				clearActionHistory();
		});
		NodeModifiedAction.setNodeMethodReferences(triangulationService::addNode, triangulationService::removeNode);
	}

	@Override
	public void undo() {
		if (undoActionStack.isEmpty())
			return;
		undoActionStack.peek().unExecute();
		redoActionStack.push(undoActionStack.pop());
	}

	@Override
	public void redo() {
		if (redoActionStack.isEmpty())
			return;
		redoActionStack.peek().execute();
		undoActionStack.push(redoActionStack.pop());
	}

	@Override
	public void registerAction(UserAction action) {
		undoActionStack.push(action);
		redoActionStack.clear();
	}

	@Override
	public void clearActionHistory() {
		undoActionStack.clear();
		redoActionStack.clear();
	}

}
