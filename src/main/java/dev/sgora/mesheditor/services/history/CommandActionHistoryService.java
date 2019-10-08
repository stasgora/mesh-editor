package dev.sgora.mesheditor.services.history;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.project.LoadState;
import dev.sgora.mesheditor.services.history.actions.UserAction;
import dev.sgora.mesheditor.services.history.actions.node.NodeModifiedAction;
import dev.sgora.mesheditor.services.mesh.generation.TriangulationService;

import java.util.ArrayDeque;
import java.util.Deque;

@Singleton
class CommandActionHistoryService implements ActionHistoryService {
	private final Deque<UserAction> undoActionStack = new ArrayDeque<>();
	private final Deque<UserAction> redoActionStack = new ArrayDeque<>();

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
