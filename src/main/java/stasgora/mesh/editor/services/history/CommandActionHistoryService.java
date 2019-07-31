package stasgora.mesh.editor.services.history;

import stasgora.mesh.editor.services.history.actions.UserAction;
import stasgora.mesh.editor.model.project.LoadState;

import java.util.Stack;

public class CommandActionHistoryService implements ActionHistoryService {
	private Stack<UserAction> undoActionStack = new Stack<>();
	private Stack<UserAction> redoActionStack = new Stack<>();

	public CommandActionHistoryService(LoadState loadState) {
		loadState.loaded.addListener(() -> {
			if(!loadState.loaded.get())
				clearActionHistory();
		});
	}

	@Override
	public void undo() {
		if(undoActionStack.isEmpty())
			return;
		undoActionStack.peek().unExecute();
		redoActionStack.push(undoActionStack.pop());
	}

	@Override
	public void redo() {
		if(redoActionStack.isEmpty())
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
