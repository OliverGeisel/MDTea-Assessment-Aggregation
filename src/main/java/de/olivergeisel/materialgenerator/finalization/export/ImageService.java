package de.olivergeisel.materialgenerator.finalization.export;

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


/**
 * Service for storing and retrieving images.
 * <p>
 * The images are stored in the directory specified by the property {@code application.images}.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see StorageService
 * @see StorageException
 * @see StorageFileNotFoundException
 * @since 0.2.0
 */
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
	 * Store an image.
	 *
	 * @param file Image to store
	 * @throws StorageException if the image could not be stored. This could be due to an empty file or a file that could not be written.
	 */
	@Override
	public void store(MultipartFile file) throws StorageException {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = getRootLocation()
					.resolve(Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(getRootLocation().toAbsolutePath())) {
				// This is a security check
				throw new StorageException("Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	public boolean hasImage(String filename) {
		return Files.exists(getRootLocation().resolve(filename));
	}

	/**
	 * Load all images.
	 *
	 * @return a Stream of all images
	 * @throws StorageException if the images could not be read
	 */
	@Override
	public Stream<Path> loadAll() throws StorageException {
		try (var files = Files.walk(getRootLocation(), 1)) {
			return files
					.filter(path -> !path.equals(getRootLocation()))
					.map(getRootLocation()::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	/**
	 * Get the path to an image.
	 *
	 * @param filename Name of Image you want
	 * @return Path to the Image
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
	 * Delete all images.
	 * <p>
	 *     This method is not implemented.
	 *
	 */
	@Override
	public void deleteAll() {

	}

	//region setter/getter

	/**
	 * Get the root location of the image storage.
	 *
	 * @return the root location
	 */
	public Path getRootLocation() {
		return Paths.get(rootLocation);
	}
//endregion
}
