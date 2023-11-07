package de.olivergeisel.materialgenerator.finalization;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

	public File createTempDirectory() throws IOException {
		return Files.createTempDirectory("materialgenerator").toFile();
	}

	public void cleanupTemporaryFiles(File tempDir, File zipFile) throws IOException {
		if (zipFile != null && zipFile.exists()) {
			Files.delete(zipFile.toPath());
		}
		deleteDirectory(tempDir);
	}

	public void deleteDirectory(File directory) throws IOException {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					Files.delete(file.toPath());
				}
			}
		}
		Files.delete(directory.toPath());
	}


	public File createZipArchive(String zipName, File directory) throws IOException {
		File zipFile = File.createTempFile(zipName, ".zip");
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
			zipDirectory(directory, "", zipOut);
		}
		return zipFile;
	}

	public void saveToFile(String templateContent, File outputDir, String fileName) {
		try (Writer writer = new BufferedWriter(new FileWriter(new File(outputDir, fileName)))) {
			writer.write(templateContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void zipDirectory(File directory, String parentDir, ZipOutputStream zipOut) throws IOException {
		File[] files = directory.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				zipDirectory(file, parentDir + file.getName() + "/", zipOut);
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
}
