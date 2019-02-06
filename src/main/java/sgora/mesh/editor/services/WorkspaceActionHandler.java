package sgora.mesh.editor.services;

import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.containers.ProjectModel;
import sgora.mesh.editor.model.geom.Mesh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorkspaceActionHandler {

	private ProjectModel project;
	private FileUtils fileUtils;

	public WorkspaceActionHandler(ProjectModel project) {
		this.project = project;
		fileUtils = new ProjectFileUtils(project);
	}

	public void openProject(File location) {
		try {
			fileUtils.load(location);
			project.loaded.set(true);
			project.file.set(location);
			project.stateSaved.set(true);
			project.notifyListeners();
		} catch (ProjectIOException e) {
			e.printStackTrace();
		}
	}

	public void saveProject(File location) {
		try {
			location = fileUtils.getProjectFileWithExtension(location);
			fileUtils.save(location);
			project.file.set(location);
			project.stateSaved.set(true);
		} catch (ProjectIOException e) {
			e.printStackTrace();
		}
	}

	public void createNewProject(File location) {
		try(FileInputStream fileStream = new FileInputStream(location)) {
			fileUtils.loadImage(fileStream);
			project.mesh.set(new Mesh());
			project.loaded.set(true);
			project.file.set(null);
			project.stateSaved.set(true);
			project.notifyListeners();
		} catch (IOException e) {
			e.printStackTrace();
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
