package sgora.mesh.editor.services.files;

import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.model.project.VisualProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspaceActionHandler {

	private static final Logger LOGGER = Logger.getLogger(WorkspaceActionHandler.class.getName());

	private FileUtils fileUtils;
	private TriangulationService triangulationService;

	private SettableObservable<LoadState> loadState;
	private SettableObservable<VisualProperties> visualProperties;
	private SettableObservable<CanvasData> canvasData;

	public WorkspaceActionHandler(FileUtils fileUtils, SettableObservable<LoadState> loadState, TriangulationService triangulationService,
	                              SettableObservable<VisualProperties> visualProperties, SettableObservable<CanvasData> canvasData) {
		this.fileUtils = fileUtils;
		this.loadState = loadState;
		this.triangulationService = triangulationService;
		this.visualProperties = visualProperties;
		this.canvasData = canvasData;
	}

	public void openProject(File location) {
		LoadState state = loadState.get();
		try {
			fileUtils.load(location);
			state.loaded.set(true);
			state.file.set(location);
			state.stateSaved.set(true);
			state.notifyListeners();
			canvasData.get().notifyListeners();
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed loading project from '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void saveProject(File location) {
		try {
			location = fileUtils.getProjectFileWithExtension(location);
			fileUtils.save(location);
			loadState.get().file.set(location);
			loadState.get().stateSaved.set(true);
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed saving project to '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void createNewProject(File location) {
		LoadState state = loadState.get();
		try(FileInputStream fileStream = new FileInputStream(location)) {
			fileUtils.loadImage(fileStream);
			createProjectModel();

			state.loaded.set(true);
			state.file.set(null);
			state.stateSaved.set(false);
			state.notifyListeners();
			canvasData.get().notifyListeners();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating new project at '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void closeProject() {
		LoadState state = loadState.get();
		CanvasData canvasData = this.canvasData.get();
		canvasData.mesh.set(null);
		canvasData.baseImage.set(null);
		canvasData.rawImageFile = null;

		state.loaded.set(false);
		state.file.set(null);
		state.notifyListeners();
		canvasData.notifyListeners();
	}

	private void createProjectModel() {
		triangulationService.createNewMesh();
		visualProperties.set(new VisualProperties());
	}

}
