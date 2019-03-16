package sgora.mesh.editor.services.files;

import sgora.mesh.editor.ObjectGraphFactory;
import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.files.FileUtils;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.model.project.Project;
import sgora.mesh.editor.model.project.VisualProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspaceActionExecutor {

	private static final Logger LOGGER = Logger.getLogger(WorkspaceActionExecutor.class.getName());

	private FileUtils fileUtils;
	private final Project project;
	private ObjectGraphFactory objectGraphFactory;

	public WorkspaceActionExecutor(FileUtils fileUtils, Project project, ObjectGraphFactory objectGraphFactory) {
		this.fileUtils = fileUtils;
		this.project = project;
		this.objectGraphFactory = objectGraphFactory;
	}

	void openProject(File location) {
		LoadState state = project.loadState.get();
		try {
			fileUtils.load(location);
			state.loaded.set(true);
			state.file.set(location);
			state.stateSaved.set(true);
			project.notifyListeners();
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed loading project from '" + location.getAbsolutePath() + "'", e);
		}
	}

	void saveProject(File location) {
		LoadState state = project.loadState.get();
		try {
			location = fileUtils.getProjectFileWithExtension(location);
			fileUtils.save(location);
			state.file.set(location);
			state.stateSaved.set(true);
			state.notifyListeners();
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed saving project to '" + location.getAbsolutePath() + "'", e);
		}
	}

	void createNewProject(File location) {
		LoadState state = project.loadState.get();
		try(FileInputStream fileStream = new FileInputStream(location)) {
			fileUtils.loadImage(fileStream);
			objectGraphFactory.createProjectModel();

			state.loaded.set(true);
			state.file.set(null);
			state.stateSaved.set(false);
			project.notifyListeners();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating new project at '" + location.getAbsolutePath() + "'", e);
		}
	}

	void closeProject() {
		LoadState state = project.loadState.get();
		CanvasData canvasData = project.canvasData.get();
		canvasData.mesh.set(null);
		canvasData.baseImage.set(null);
		canvasData.rawImageFile = null;

		state.loaded.set(false);
		state.file.set(null);
		state.stateSaved.set(true);
		project.notifyListeners();
	}

}
