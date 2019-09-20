package stasgora.mesh.editor.services.files;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.scene.image.Image;
import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;

import java.io.*;

@Singleton
class ProjectFileUtils implements FileUtils {

	private final CanvasData canvasData;
	private final AppConfigReader appConfig;
	private final VisualProperties visualProperties;

	@Inject
	ProjectFileUtils(CanvasData canvasData, @AppConfig AppConfigReader appConfig, VisualProperties visualProperties) {
		this.canvasData = canvasData;
		this.appConfig = appConfig;
		this.visualProperties = visualProperties;
	}

	@Override
	public void save(File location) throws ProjectIOException {
		try {
			location.createNewFile();
			try (FileOutputStream fileStream = new FileOutputStream(location, false);
			     ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
				objectStream.writeObject(canvasData.mesh.get());
				visualProperties.writeProperties(objectStream);
				fileStream.write(canvasData.getRawImageFile());
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void load(File location) throws ProjectIOException {
		try (FileInputStream fileStream = new FileInputStream(location);
		     ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
			canvasData.mesh.set((Mesh) objectStream.readObject());
			visualProperties.readProperties(objectStream);
			loadImage(fileStream);
		} catch (IOException | ClassNotFoundException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void loadImage(FileInputStream fileStream) throws ProjectIOException {
		canvasData.setRawImageFile(readFileIntoMemory(fileStream));
		try (ByteArrayInputStream imageStream = new ByteArrayInputStream(canvasData.getRawImageFile())) {
			canvasData.baseImage.set(new Image(imageStream));
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public byte[] readFileIntoMemory(FileInputStream fileStream) throws ProjectIOException {
		try (ByteArrayOutputStream imageStream = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[4096];
			int read;
			while ((read = fileStream.read(buffer)) != -1) {
				imageStream.write(buffer, 0, read);
			}
			return imageStream.toByteArray();
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public File getProjectFileWithExtension(File projectFile) {
		return getFileWithExtension(projectFile, appConfig.getString("extension.project"));
	}

	public File getFileWithExtension(File file, String extension) {
		String projectExtension = "." + extension;
		if (!file.getName().endsWith(projectExtension)) {
			return new File(file.getPath() + projectExtension);
		}
		return file;
	}

}
