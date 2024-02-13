package de.olivergeisel.materialgenerator.finalization.export;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;

public class ExportUtils {

	/**
	 * Creates the default template engine for the given template set.
	 *
	 * @param templateSet name of the template set
	 * @return the template engine
	 */
	public static TemplateEngine createTemplateEngine(String templateSet) {
		var templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix(STR."templateSets/\{templateSet}/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false);
		templateResolver.setCheckExistence(true);

		var templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}


	/**
	 * Saves the Material content to a file in the output directory.
	 *
	 * @param templateContent The content of the template to save.
	 * @param outputDir       The directory to save the template to.
	 * @param fileName        The name of the file to save the template to.
	 */
	public static void saveToFile(String templateContent, File outputDir, String fileName) {
		try (Writer writer = new BufferedWriter(new FileWriter(new File(outputDir, fileName)))) {
			writer.write(templateContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
