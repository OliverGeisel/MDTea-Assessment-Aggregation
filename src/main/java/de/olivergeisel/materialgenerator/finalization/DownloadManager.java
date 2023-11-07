package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.StorageFileNotFoundException;
import de.olivergeisel.materialgenerator.finalization.parts.*;
import de.olivergeisel.materialgenerator.generation.material.ExampleMaterial;
import de.olivergeisel.materialgenerator.generation.material.ImageMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is responsible for creating the zip file containing the course. It uses the template engine to create the html files.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see Material
 * @see de.olivergeisel.materialgenerator.core.course.Course
 * @since 0.2.0
 */
@Service
public class DownloadManager {

	private final Logger              logger       = org.slf4j.LoggerFactory.getLogger(DownloadManager.class);
	private final ServletContext      servletContext;
	private final ImageService        imageService;
	private final ZipService          zipService;
	private       Map<String, String> overallInfos = new HashMap<>();

	public DownloadManager(ServletContext servletContext, ImageService imageService, ZipService zipService) {
		this.servletContext = servletContext;
		this.imageService = imageService;
		this.zipService = zipService;
	}

	private static MaterialHierarchy getMaterialHierarchy(CourseNavigation.MaterialLevel level,
			CourseNavigation navigation, List<GroupOrder> groups, GroupOrder nextGroup, int i) {
		MaterialHierarchy next;
		if (nextGroup == null) {
			next = new MaterialHierarchy(navigation.getNextChapter(), null, null, null, navigation.getCount() + 1,
					navigation.getSize());
		} else {
			next = new MaterialHierarchy(level.getChapter(), level.getGroup(), null, null, i + 1, groups.size());
		}
		return next;
	}


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

	public void createDownloadZip(String name, String template, RawCourse plan,
			HttpServletResponse response) {
		File tempDir = null;
		File zipFile = null;
		try {
			tempDir = zipService.createTempDirectory();
			exportCourse(plan, template, tempDir);
			zipFile = zipService.createZipArchive(name, tempDir);
			writeZipFileToResponse(name, zipFile, response);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				zipService.cleanupTemporaryFiles(tempDir, zipFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private TemplateEngine createTemplateEngine(String templateSet) {
		var templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templateSets/" + templateSet + "/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false);
		templateResolver.setCheckExistence(true);

		var templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}

	private void saveIncludes(File outputDir, String templateSet) {
		var classloader = this.getClass().getClassLoader();
		URL folder = classloader.getResource("");
		var outPath = outputDir.toPath();
		File sourceCopy = new File(folder.getFile(), "templateSets/" + templateSet + "/include");
		if (!sourceCopy.exists()) {
			return;
		}
		var copyPath = sourceCopy.toPath();
		var out = outPath.resolve(copyPath.relativize(copyPath));
		try (var files = Files.walk(copyPath)) {
			files.forEach(source -> {
				try {
					Path destination = out.resolve(copyPath.relativize(source));
					if (Files.isDirectory(source)) {
						Files.createDirectories(destination);
					} else {
						Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					logger.warn(e.toString());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportCourse(RawCourse plan, String templateSet, File outputDir)
			throws IOException {
		var templateEngine = createTemplateEngine(templateSet);
		Context context = new Context(Locale.GERMANY);
		CourseNavigation.MaterialLevel level = new CourseNavigation.MaterialLevel();
		var navigation = new CourseNavigation(level);
		overallInfos.put("courseName", plan.getMetadata().getName().orElse("Kurs"));
		var chapters = plan.getCourseOrder().getChapterOrder();
		for (int i = 0; i < chapters.size(); i++) {
			var chapter = plan.getCourseOrder().getChapterOrder().get(i);
			String chapterName = chapter.getName() == null || chapter.getName().isBlank() ?
					"Kapitel " + (chapters.indexOf(chapter) + 1) : chapter.getName();
			var nextChapter = i < chapters.size() - 1 ? chapters.get(i + 1) : null;
			var chapterNavigation = navigation.nextChapter(nextChapter);
			var subDir = Files.createDirectory(new File(outputDir, chapterName).toPath());
			var newLevel = new CourseNavigation.MaterialLevel(chapter.getName(), "", "", "");
			if (chapter.materialCount() == 0) {
				continue;
			}
			exportChapter(chapter, newLevel, chapterNavigation, context, subDir.toFile(), templateEngine);
		}
		context.clearVariables();
		context.setVariable("course", plan);
		for (var entry : overallInfos.entrySet()) {
			context.setVariable(entry.getKey(), entry.getValue());
		}
		var course = templateEngine.process("COURSE", context);
		zipService.saveToFile(course, outputDir, "Course.html");
		saveIncludes(outputDir, templateSet);
	}

	private void exportChapter(ChapterOrder chapter, CourseNavigation.MaterialLevel level, CourseNavigation navigation,
			Context context, File outputDir, TemplateEngine templateEngine)
			throws IOException {
		setOverallInfos(context, navigation, level);
		context.setVariable("chapter", chapter);
		var chapterOverview = templateEngine.process("CHAPTER", context);
		zipService.saveToFile(chapterOverview, outputDir, "overview.html");
		var groups = chapter.getGroupOrder();
		GroupOrder previousGroup = null;
		GroupOrder nextGroup = null;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < groups.size(); i++) {
			var group = groups.get(i);
			var newLevel = new CourseNavigation.MaterialLevel(level.getChapter(), group.getName());
			nextGroup = (i < groups.size() - 1) ? groups.get(i + 1) : null;
			MaterialHierarchy next = getMaterialHierarchy(level, navigation, groups, nextGroup, i);
			CourseNavigation newCourseNavigation = new CourseNavigation(newLevel,
					navigation.getCurrentMaterialHierarchy(), next, i, groups.size());
			if (group instanceof GroupOrder concreteGroup) {
				if (concreteGroup.materialCount() == 0) {
					continue;
				}
				String groupName = concreteGroup.getName() == null || group.getName().isBlank() ?
						"Gruppe " + (chapter.getGroupOrder().indexOf(group) + 1) : group.getName();
				var subDir = new File(outputDir, groupName);
				Files.createDirectory(subDir.toPath());
				exportGroup(concreteGroup, newLevel, navigation, context, subDir, templateEngine);
			}/* else if (subChapter instanceof TaskOrder task) {
				var subDir = new File(outputDir, task.getName());
				exportTask(task, context, subDir);
			} */ else {
				throw new IllegalArgumentException(
						"Unknown substructure component! Must be either group or task inside a chapter.");
			}
			previousNavigation = newCourseNavigation;
		}
	}

	private void exportGroup(GroupOrder group, CourseNavigation.MaterialLevel level, CourseNavigation navigation,
			Context context, File outputDir, TemplateEngine templateEngine)
			throws IOException {
		setOverallInfos(context, navigation, level);
		context.setVariable("group", group);
		var chapterOverview = templateEngine.process("GROUP", context);
		zipService.saveToFile(chapterOverview, outputDir, "overview.html");
		var tasks = group.getTaskOrder();
		TaskOrder previousTask = null;
		TaskOrder nextTask = null;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < tasks.size(); i++) {
			var task = tasks.get(i);
			var newTaskLevel = new CourseNavigation.MaterialLevel(level.getChapter(), level.getGroup(),
					task.getName());
			nextTask = (i < tasks.size() - 1) ? tasks.get(i + 1) : null;
			if (nextTask != null && nextTask.getMaterialOrder().isEmpty()) {
				nextTask = null;
			}
			MaterialHierarchy next = nextTask == null
					? new MaterialHierarchy(level.getChapter(), navigation.getNextGroup(), null, null,
					navigation.getCount() + 1, navigation.getSize())
					: new MaterialHierarchy(level.getChapter(), level.getGroup(), nextTask.getName(), null, i + 1,
					tasks.size());

			CourseNavigation newCourseNavigation = new CourseNavigation(newTaskLevel,
					previousNavigation.getCurrentMaterialHierarchy(),
					next, i, tasks.size());
			/*if (subChapter instanceof GroupOrder subGroup) {
				var subDir = new File(outputDir, subGroup.getName());
				Files.createDirectory(subDir.toPath());
				exportGroup(subGroup, context, subDir);
			} else */
			if (task instanceof TaskOrder concreteTask) {
				if (concreteTask.materialCount() == 0) {
					continue;
				}
				String taskName = concreteTask.getName() == null || concreteTask.getName().isBlank() ?
						"Task " + (group.getTaskOrder().indexOf(concreteTask) + 1) : concreteTask.getName();
				taskName = taskName.replaceAll(CourseNavigation.PATH_REPLACE_REGEX, "_");
				var subDir = new File(outputDir, taskName);
				Files.createDirectory(subDir.toPath());
				exportTask(concreteTask, newTaskLevel, newCourseNavigation, context, subDir, templateEngine);
			} else {
				throw new IllegalArgumentException(
						"Unknown substructure component! Must be either group or task inside a group.");
			}
			previousTask = task;
			previousNavigation = newCourseNavigation;
		}

	}

	private void exportTask(TaskOrder task, CourseNavigation.MaterialLevel level, CourseNavigation navigation,
			Context context, File outputDir, TemplateEngine templateEngine) {
		final int taskSize = task.getMaterialOrder().size();
		var materials = task.getMaterialOrder();
		Material nextMaterial;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < materials.size(); i++) {
			context.clearVariables();
			setOverallInfos(context);
			var material = materials.get(i);
			nextMaterial = (i < materials.size() - 1) ? materials.get(i + 1) : null;
			MaterialHierarchy next;
			if (nextMaterial == null) {
				next = new MaterialHierarchy(level.getChapter(), level.getGroup(), navigation.getNextTask(), null,
						navigation.getCount() + 1, navigation.getSize());
			} else {
				next = new MaterialHierarchy(level.getChapter(), level.getGroup(), level.getTask(),
						"MATERIAL_" + (i + 1), i + 1, materials.size());
			}
			CourseNavigation.MaterialLevel materialLevel = new CourseNavigation.MaterialLevel(level.getChapter(),
					level.getGroup(), task.getName(), "MATERIAL_" + i);
			CourseNavigation newNavigation = new CourseNavigation(materialLevel,
					previousNavigation.getCurrentMaterialHierarchy(), next, i, taskSize);
			exportMaterial(context, outputDir, templateEngine, i, material, materialLevel, newNavigation);
			previousNavigation = newNavigation;
		}
	}

	private void exportMaterial(Context context, File outputDir, TemplateEngine templateEngine, int materialNumber,
			Material material, CourseNavigation.MaterialLevel materialLevel,
			CourseNavigation newNavigation) {
		setOverallInfos(context);
		context.setVariable("material", material);
		context.setVariable("navigation", newNavigation);
		context.setVariable("rootPath", materialLevel.getPathToRoot());
		context.setVariable("title", material.getName());
		String processedHtml = templateEngine.process("MATERIAL", context);
		zipService.saveToFile(processedHtml, outputDir, String.format("Material_%s.html", materialNumber));
		if (material instanceof ImageMaterial image) {
			try {
				var imageFile = getImage(image.getImageName());
				Files.copy(imageFile.getInputStream(), new File(outputDir, image.getImageName()).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException | StorageFileNotFoundException e) {
				logger.warn(e.toString());
			}
		}
		if (material instanceof ExampleMaterial exampleMaterial && exampleMaterial.isImageExample()) {
			try {
				var image = getImage(exampleMaterial.getImageName());
				Files.copy(image.getInputStream(), new File(outputDir, exampleMaterial.getImageName()).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException | StorageFileNotFoundException e) {
				logger.warn(e.toString());
			}
		}
	}

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

	private Resource getImage(String name) throws StorageFileNotFoundException {
		return imageService.loadAsResource(name);
	}

	private void setOverallInfos(Context context, CourseNavigation navigation,
			CourseNavigation.MaterialLevel level) {
		for (var entry : overallInfos.entrySet()) {
			context.setVariable(entry.getKey(), entry.getValue());
		}
		context.setVariable("navigation", navigation);
		context.setVariable("rootPath", level.getPathToRoot());
	}

	//region setter/getter
	private void setOverallInfos(Context context) {
		for (var entry : overallInfos.entrySet()) {
			context.setVariable(entry.getKey(), entry.getValue());
		}
	}
//endregion
}
