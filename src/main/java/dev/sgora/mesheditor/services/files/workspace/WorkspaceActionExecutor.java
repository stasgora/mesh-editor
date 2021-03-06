package dev.sgora.mesheditor.services.files.workspace;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.project.CanvasData;
import dev.sgora.mesheditor.model.project.LoadState;
import dev.sgora.mesheditor.model.project.VisualProperties;
import dev.sgora.mesheditor.services.drawing.ImageBox;
import dev.sgora.mesheditor.services.mesh.generation.TriangulationService;
import dev.sgora.mesheditor.services.mesh.rendering.SvgMeshRenderer;
import dev.sgora.mesheditor.services.files.FileUtils;
import dev.sgora.mesheditor.services.files.ProjectIOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Singleton
public class WorkspaceActionExecutor {
	private final FileUtils fileUtils;
	private final LoadState loadState;
	private final CanvasData canvasData;
	private final SvgMeshRenderer svgMeshRenderer;
	private final ImageBox imageBox;
	private final TriangulationService triangulationService;
	private final VisualProperties visualProperties;

	@Inject
	WorkspaceActionExecutor(FileUtils fileUtils, LoadState loadState, CanvasData canvasData, SvgMeshRenderer svgMeshRenderer,
	                        ImageBox imageBox, TriangulationService triangulationService, VisualProperties visualProperties) {
		this.fileUtils = fileUtils;
		this.loadState = loadState;
		this.canvasData = canvasData;
		this.svgMeshRenderer = svgMeshRenderer;
		this.imageBox = imageBox;
		this.triangulationService = triangulationService;
		this.visualProperties = visualProperties;
	}

	void openProject(File location) throws ProjectIOException {
		LoadState state = loadState;
		fileUtils.load(location);
		state.loaded.set(true);
		state.file.set(location);
		state.stateSaved.set(true);

		notifyListeners();
	}

	File saveProject(File location) throws ProjectIOException {
		location = fileUtils.getProjectFileWithExtension(location);
		fileUtils.save(location);
		loadState.file.set(location);
		loadState.stateSaved.set(true);

		loadState.notifyListeners();
		return location;
	}

	void createNewProject(File location) throws ProjectIOException {
		try (FileInputStream fileStream = new FileInputStream(location)) {
			fileUtils.loadImage(fileStream);
			createProjectModel();

			loadState.loaded.set(true);
			loadState.file.set(null);
			loadState.stateSaved.set(false);

			notifyListeners();
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	void exportProjectAsSvg(File location) throws ProjectIOException {
		try (FileOutputStream fileStream = new FileOutputStream(fileUtils.getFileWithExtension(location, "svg"))) {
			fileStream.write(svgMeshRenderer.renderSvg().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	void closeProject() {
		canvasData.mesh.set(null);
		canvasData.baseImage.set(null);
		canvasData.setRawImageFile(null);

		loadState.loaded.set(false);
		loadState.file.set(null);
		loadState.stateSaved.set(true);

		loadState.notifyListeners();
		canvasData.notifyListeners();
	}

	private void notifyListeners() {
		loadState.notifyListeners();
		canvasData.notifyListeners();
		visualProperties.notifyListeners();
	}

	private void createProjectModel() {
		imageBox.calcImageBox();
		triangulationService.createNewMesh();
		visualProperties.restoreDefaultValues();
	}

}
