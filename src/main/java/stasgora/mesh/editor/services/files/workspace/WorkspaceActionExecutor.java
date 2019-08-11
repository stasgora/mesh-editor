package stasgora.mesh.editor.services.files.workspace;

import stasgora.mesh.editor.ObjectGraphFactory;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.files.FileUtils;
import stasgora.mesh.editor.services.files.ProjectIOException;
import stasgora.mesh.editor.services.mesh.rendering.SvgMeshRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class WorkspaceActionExecutor {

	private static final Logger LOGGER = Logger.getLogger(WorkspaceActionExecutor.class.getName());

	private FileUtils fileUtils;
	private final Project project;
	private ObjectGraphFactory objectGraphFactory;
	private SvgMeshRenderer svgMeshRenderer;

	public WorkspaceActionExecutor(FileUtils fileUtils, Project project, ObjectGraphFactory objectGraphFactory, SvgMeshRenderer svgMeshRenderer) {
		this.fileUtils = fileUtils;
		this.project = project;
		this.objectGraphFactory = objectGraphFactory;
		this.svgMeshRenderer = svgMeshRenderer;
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
		try (FileInputStream fileStream = new FileInputStream(location)) {
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
		try (FileOutputStream fileStream = new FileOutputStream(fileUtils.getFileWithExtension(location, "svg"))) {
			fileStream.write(svgMeshRenderer.renderAsString().getBytes(StandardCharsets.UTF_8));
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
