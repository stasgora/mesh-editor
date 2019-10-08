package dev.sgora.mesheditor.services.files;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.scene.image.Image;
import dev.sgora.mesheditor.model.geom.Mesh;
import dev.sgora.mesheditor.model.project.CanvasData;
import dev.sgora.mesheditor.model.project.VisualProperties;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigReader;
import dev.sgora.mesheditor.services.config.annotation.AppConfig;

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

	@Override
	public File getFileWithExtension(File file, String extension) {
		String projectExtension = "." + extension;
		if (!file.getName().endsWith(projectExtension)) {
			return new File(file.getPath() + projectExtension);
		}
		return file;
	}

	@Override
	public String getProjectFileName(File projectFile) {
		String name = projectFile.getName();
		return name.substring(0, name.length() - appConfig.getString("extension.project").length() - 1);
	}

}
