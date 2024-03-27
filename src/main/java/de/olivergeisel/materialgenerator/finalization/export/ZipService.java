package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service for creating zip archives.
 * <p>
 * Normally the zip file is created in the temporary directory of the system. Use the {@link #createTempDirectory()}
 * method to create the temporary directory and the {@link #cleanupTemporaryFiles(File, File)} method to delete the
 * temporary files.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see RawCourse
 * @see DownloadManager
 * @see Exporter
 * @since 0.2.0
 */
@Service
public class ZipService {

	private static final Logger logger = LoggerFactory.getLogger(ZipService.class);

	public File createTempDirectory() throws IOException {
		return Files.createTempDirectory("materialgenerator").toFile();
	}

	/**
	 * Cleans up the temporary files.
	 *
	 * @param tempDir the temporary directory
	 * @param zipFile the zip file
	 * @throws IOException if an error occurs during the cleanup
	 */
	public void cleanupTemporaryFiles(File tempDir, File zipFile) throws IOException {
		if (zipFile != null && zipFile.exists()) {
			Files.delete(zipFile.toPath());
		}
		deleteDirectory(tempDir);
	}

	/**
	 * Deletes a directory and all its content.
	 *
	 * @param directory the directory to delete
	 * @throws IOException if an error occurs during the deletion
	 */
	public void deleteDirectory(File directory) throws IOException {
		File[] files = directory.listFiles();
		if (files != null) { // delete children
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					Files.delete(file.toPath());
				}
			}
		}
		// delete directory/file itself
		Files.delete(directory.toPath());
	}


	/**
	 * Creates a zip archive from a directory.
	 *
	 * @param zipName   the name of the zip file
	 * @param directory the directory for the zip
	 * @return the zip file
	 * @throws IOException if an error occurs during the creation of the zip file
	 */
	public File createZipArchive(String zipName, File directory) throws IOException {
		File zipFile = File.createTempFile(zipName, ".zip");
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
			zipDirectory(directory, "", zipOut);
		}
		return zipFile;
	}

	/**
	 * Recursive method to create a zip archive from a directory.
	 *
	 * @param directory the directory to zip
	 * @param parentDir the parent directory
	 * @param zipOut   the zip output stream
	 * @throws IOException if an error occurs during the zipping
	 */
	public void zipDirectory(File directory, String parentDir, ZipOutputStream zipOut) throws IOException {
		File[] files = directory.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				zipDirectory(file, STR."\{parentDir}\{file.getName()}/", zipOut);
			} else {
				try (FileInputStream stream = new FileInputStream(file)) {
					zipOut.putNextEntry(new ZipEntry(parentDir + file.getName()));
					int length;
					byte[] buffer = new byte[1024];
					while ((length = stream.read(buffer)) > 0) {
						zipOut.write(buffer, 0, length);
					}
				}
			}
		}
	}

	/**
	 * Writes a file to another file.
	 *
	 * @param source the source file
	 * @param target the target file
	 */
	public void writeTo(File source, File target) {
		try (OutputStream out = new FileOutputStream(target); FileInputStream fis = new FileInputStream(source)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			logger.info(STR."Zip file successfully written to: \{target}");
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(STR."Error while writing zip file to: \{target}");
		}
	}
}
