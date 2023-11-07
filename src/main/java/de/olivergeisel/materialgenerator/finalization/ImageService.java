package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.StorageException;
import de.olivergeisel.materialgenerator.StorageFileNotFoundException;
import de.olivergeisel.materialgenerator.generation.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class ImageService implements StorageService {

	@Value("${application.images}")
	private String rootLocation;

	public ImageService() {
	}

	/**
	 * Initialize the directory.
	 *
	 * @throws StorageException if the image storage could not be created
	 */
	@Override
	public void init() throws StorageException {
		try {
			Files.createDirectories(getRootLocation());
		} catch (IOException e) {
			throw new StorageException("Could not initialize image storage", e);
		}
	}

	/**
	 * @param file Image to store
	 */
	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = getRootLocation().resolve(
															Paths.get(file.getOriginalFilename()))
													.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(getRootLocation().toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	/**
	 * @return
	 */
	@Override
	public Stream<Path> loadAll() {
		try (var files = Files.walk(getRootLocation(), 1)) {
			return files
					.filter(path -> !path.equals(getRootLocation()))
					.map(getRootLocation()::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	/**
	 * @param filename
	 * @return
	 */
	@Override
	public Path load(String filename) {
		return getRootLocation().resolve(filename);
	}

	/**
	 * @param filename Name of Image you want
	 * @return an Image as Resource.
	 * @throws StorageFileNotFoundException if no file with this name was found
	 */
	@Override
	public Resource loadAsResource(String filename) throws StorageFileNotFoundException {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	/**
	 *
	 */
	@Override
	public void deleteAll() {

	}

	//region setter/getter
	public Path getRootLocation() {
		return Paths.get(rootLocation);
	}
//endregion
}
