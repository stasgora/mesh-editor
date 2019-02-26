package sgora.mesh.editor.services;

import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.services.triangulation.TriangulationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspaceActionHandler {

	private static final Logger LOGGER = Logger.getLogger(WorkspaceActionHandler.class.getName());

	private FileUtils fileUtils;
	private Project project;
	private TriangulationService triangulationService;

	public WorkspaceActionHandler(FileUtils fileUtils, Project project, TriangulationService triangulationService) {
		this.fileUtils = fileUtils;
		this.project = project;
		this.triangulationService = triangulationService;
	}

	public void openProject(File location) {
		try {
			fileUtils.load(location);
			project.loaded.set(true);
			project.file.set(location);
			project.stateSaved.set(true);
			project.notifyListeners();
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed loading project from '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void saveProject(File location) {
		try {
			location = fileUtils.getProjectFileWithExtension(location);
			fileUtils.save(location);
			project.file.set(location);
			project.stateSaved.set(true);
		} catch (ProjectIOException e) {
			LOGGER.log(Level.SEVERE, "Failed saving project to '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void createNewProject(File location) {
		try(FileInputStream fileStream = new FileInputStream(location)) {
			fileUtils.loadImage(fileStream);
			triangulationService.createNewMesh();
			project.loaded.set(true);
			project.file.set(null);
			project.stateSaved.set(false);
			project.notifyListeners();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed creating new project at '" + location.getAbsolutePath() + "'", e);
		}
	}

	public void closeProject() {
		project.mesh.set(null);
		project.baseImage.set(null);
		project.rawImageFile = null;

		project.loaded.set(false);
		project.file.set(null);
		project.notifyListeners();
	}

}
