package sgora.mesh.editor.services.files;

import sgora.mesh.editor.ObjectGraphFactory;
import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.files.FileUtils;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.model.project.Project;
import sgora.mesh.editor.services.ui.UiDialogUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspaceActionExecutor {

	private static final Logger LOGGER = Logger.getLogger(WorkspaceActionExecutor.class.getName());

	private FileUtils fileUtils;
	private final Project project;
	private ObjectGraphFactory objectGraphFactory;
	private SvgService svgService;

	public WorkspaceActionExecutor(FileUtils fileUtils, Project project, ObjectGraphFactory objectGraphFactory, SvgService svgService) {
		this.fileUtils = fileUtils;
		this.project = project;
		this.objectGraphFactory = objectGraphFactory;
		this.svgService = svgService;
	}

	void openProject(File location) throws ProjectIOException {
		LoadState state = project.loadState;
		fileUtils.load(location);
		state.loaded.set(true);
		state.file.set(location);
		state.stateSaved.set(true);
		project.notifyListeners();
	}

	void saveProject(File location) throws ProjectIOException {
		LoadState state = project.loadState;
		location = fileUtils.getProjectFileWithExtension(location);
		fileUtils.save(location);
		state.file.set(location);
		state.stateSaved.set(true);
		state.notifyListeners();
	}

	void createNewProject(File location) throws ProjectIOException {
		LoadState state = project.loadState;
		try(FileInputStream fileStream = new FileInputStream(location)) {
			fileUtils.loadImage(fileStream);
			objectGraphFactory.createProjectModel();

			state.loaded.set(true);
			state.file.set(null);
			state.stateSaved.set(false);
			project.notifyListeners();
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	void exportProjectAsSvg(File location) throws ProjectIOException {
		try(FileOutputStream fileStream = new FileOutputStream(fileUtils.getFileWithExtension(location, "svg"))) {
			fileStream.write(svgService.createSvg().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	void closeProject() {
		LoadState state = project.loadState;
		CanvasData canvasData = project.canvasData;
		canvasData.mesh.set(null);
		canvasData.baseImage.set(null);
		canvasData.rawImageFile = null;

		state.loaded.set(false);
		state.file.set(null);
		state.stateSaved.set(true);
		project.notifyListeners();
	}

}
