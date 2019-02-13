package sgora.mesh.editor.services;

import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.model.geom.Mesh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorkspaceActionHandler {

	private FileUtils fileUtils;
	private Project project;

	public WorkspaceActionHandler(FileUtils fileUtils, Project project) {
		this.fileUtils = fileUtils;
		this.project = project;
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
			project.stateSaved.set(false);
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
