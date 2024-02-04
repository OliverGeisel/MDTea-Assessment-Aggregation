package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * This class is responsible for creating the zip file containing the course.
 * It uses the template engine to create the html files. Only HTML-files supportet at the moment.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @see de.olivergeisel.materialgenerator.core.course.Course
 * @since 0.2.0
 */
@Service
public class DownloadManager {

	private static final Logger logger = LoggerFactory.getLogger(DownloadManager.class);

	private final ZipService    zipService;
	private final HTML_Exporter htmlExporter;
	private final OPAL_Exporter opalExporter;

	public DownloadManager(ZipService zipService, HTML_Exporter htmlExporter, OPAL_Exporter opalExporter) {
		this.zipService = zipService;
		this.htmlExporter = htmlExporter;
		this.opalExporter = opalExporter;
	}

	/**
	 * Creates a zip file containing the course.
	 *
	 * @param name            The name of the zip file
	 * @param templateSetName The template set to use
	 * @param course          The course to export
	 * @param response        The response to write the zip file to download the course directly
	 * @param exporter        The exporter to use to create the course
	 * @throws IllegalArgumentException if the response, course, templateSetName, or exporter is null
	 */
	public void createCourseInZip(String name, String templateSetName, RawCourse course, HttpServletResponse response,
			Exporter exporter) throws IllegalArgumentException {
		File tempDir = null;
		File zipFile = null;
		if (exporter == null) {
			throw new IllegalArgumentException("Exporter is null");
		}
		if (course == null) {
			throw new IllegalArgumentException("Course is null");
		}
		try {
			tempDir = zipService.createTempDirectory();
			exporter.export(course, templateSetName, tempDir);
			zipFile = zipService.createZipArchive(name, tempDir);
			writeZipFileToResponse(name, zipFile, response);
		} catch (IOException e) {
			logger.error(e.toString());
		} finally {
			try {
				zipService.cleanupTemporaryFiles(tempDir, zipFile);
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
	}


	/**
	 * Final step to write the zip file to the (http-)response
	 *
	 * @param name     The name of the zip file
	 * @param zipFile  The zip file to write
	 * @param response The response to write the file to
	 * @throws IOException if an error occurs while writing the file
	 */
	private void writeZipFileToResponse(String name, File zipFile, HttpServletResponse response) throws IOException {
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s.zip", name));
		try (OutputStream out = response.getOutputStream(); FileInputStream fis = new FileInputStream(zipFile)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
		}
	}

	/**
	 * downloads a single html file
	 *
	 * @param name     The name of the file
	 * @param response The response to write the file to
	 * @deprecated use createCourseInZip instead. Single export is not usefully at the moment
	 */
	@Deprecated
	public void createSingle(String name, HttpServletResponse response) {
		Context context = new Context(Locale.GERMANY);
		context.setVariable("wert", "My Value");
		var templateEngine = new TemplateEngine();
		String processedHtml = templateEngine.process("myTemp", context);
		response.setContentType("text/html");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s.html", name));
		try {
			response.getWriter().write(processedHtml);
		} catch (IOException e) {
			logger.warn(e.toString());
		}
	}

	/**
	 * Creates a zip file containing the course in the wanted format.
	 * See {@link ExportKind} for supported formats.
	 *
	 * @param name            The name of the zip file
	 * @param templateSetName The template set to use
	 * @param course          The course to export
	 * @param response        The response to write the zip file to download the course directly
	 * @throws IllegalArgumentException if the response, course, templateSetName, or kind is null
	 * @see ExportKind
	 */
	public void createAndDownload(ExportKind kind, String name, RawCourse course, String templateSetName,
			HttpServletResponse response) throws IllegalArgumentException {
		if (response == null) {
			throw new IllegalArgumentException("Response is null");
		}
		Exporter exporter = switch (kind) {
			case HTML -> htmlExporter;
			case OPAL -> opalExporter;
			default -> throw new IllegalArgumentException(STR."Unsupported export kind: \{kind}");
		};
		createCourseInZip(name, templateSetName, course, response, exporter);
	}

	/**
	 * The supported export formats for the course. The matching exporter is used to create the zip file.
	 */
	public enum ExportKind {
		PDF,
		HTML,
		OPAL
	}
}
