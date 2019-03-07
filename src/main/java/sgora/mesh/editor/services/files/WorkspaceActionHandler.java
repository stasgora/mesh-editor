package sgora.mesh.editor.services.files;

import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.ProjectState;
import sgora.mesh.editor.model.project.VisualProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspaceActionHandler {

	private static final Logger LOGGER = Logger.getLogger(WorkspaceActionHandler.class.getName());

	private FileUtils fileUtils;
	private ProjectState projectState;
	private TriangulationService triangulationService;
	private SettableObservable<VisualProperties> visualProperties;

	public WorkspaceActionHandler(FileUtils fileUtils, ProjectState projectState, TriangulationService triangulationService, SettableObservable<VisualProperties> visualProperties) {
		this.fileUtils = fileUtils;
		this.projectState = projectState;
		this.triangulationService = triangulationService;
		this.visualProperties = visualProperties;
	}

	public void openProject(File location) {
		try {
			fileUtils.load(location);
			projectState.loaded.set(true);
			projectState.file.set(location);
			projectState.stateSaved.set(true);
			projectState.notifyListeners();
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed loading project from '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void saveProject(File location) {
		try {
			location = fileUtils.getProjectFileWithExtension(location);
			fileUtils.save(location);
			projectState.file.set(location);
			projectState.stateSaved.set(true);
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed saving project to '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void createNewProject(File location) {
		try(FileInputStream fileStream = new FileInputStream(location)) {
			fileUtils.loadImage(fileStream);
			//TODO move this
			triangulationService.createNewMesh();
			visualProperties.set(new VisualProperties());
			projectState.loaded.set(true);
			projectState.file.set(null);
			projectState.stateSaved.set(false);
			projectState.notifyListeners();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating new project at '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void closeProject() {
		projectState.mesh.set(null);
		projectState.baseImage.set(null);
		projectState.rawImageFile = null;

		projectState.loaded.set(false);
		projectState.file.set(null);
		projectState.notifyListeners();
	}

}
