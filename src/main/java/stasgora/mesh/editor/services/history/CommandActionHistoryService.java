package stasgora.mesh.editor.services.history;

import stasgora.mesh.editor.interfaces.action.history.ActionHistoryService;
import stasgora.mesh.editor.interfaces.action.history.UserAction;

import java.util.Stack;

public class CommandActionHistoryService implements ActionHistoryService {
	private Stack<UserAction> undoActionStack = new Stack<>();
	private Stack<UserAction> redoActionStack = new Stack<>();

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
}
