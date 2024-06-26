package de.olivergeisel.materialgenerator.finalization.export;


import de.olivergeisel.materialgenerator.finalization.parts.*;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.transfer.ExampleMaterial;
import de.olivergeisel.materialgenerator.generation.material.transfer.ImageMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static de.olivergeisel.materialgenerator.finalization.export.ExportUtils.createTemplateEngine;
import static de.olivergeisel.materialgenerator.finalization.export.ExportUtils.saveToFile;

/**
 * Exports a {@link RawCourse} as a collection of HTML files.
 *
 * @see Exporter
 * @since 0.2.0
 * @version 1.1.0
 * @author Oliver Geisel
 */
@Service
public class HTML_Exporter extends Exporter {

	private static final Logger logger = LoggerFactory.getLogger(HTML_Exporter.class);

	/**
	 * Shortcut for the context
	 */
	private final Map<String, String> overallInfos = new HashMap<>();

	public HTML_Exporter(ImageService imageService) {
		super(imageService);
	}


	@Override
	public void export(RawCourse rawCourse, String templateSet, File dirToWrite) throws IOException {
		exportCourse(rawCourse, templateSet, dirToWrite);
	}

	/**
	 * Exports the course to the output directory.
	 *
	 * @param plan        The course to export.
	 * @param templateSet The template set to use.
	 * @param outputDir   The directory to save the course to.
	 * @throws IOException If an error occurs while exporting the course.
	 */
	private void exportCourse(RawCourse plan, String templateSet, File outputDir) throws IOException {
		var templateEngine = createTemplateEngine(templateSet);
		Context context = new Context(Locale.GERMANY);
		CourseNavigation.MaterialLevel level = new CourseNavigation.MaterialLevel();
		var navigation = new CourseNavigation(level);
		overallInfos.put("courseName", plan.getMetadata().getName().orElse("Kurs"));
		var chapters = plan.getOrder().getChapterOrder();
		for (int i = 0; i < chapters.size(); i++) {
			var chapter = chapters.get(i);
			if (chapter.materialCount() == 0) { // skip chapters without material
				continue;
			}
			String chapterName = chapter.getName() == null || chapter.getName().isBlank() ?
					STR."Kapitel \{chapters.indexOf(chapter) + 1}" : chapter.getName();
			var nextChapter = i < chapters.size() - 1 ? chapters.get(i + 1) : null;
			var chapterNavigation = navigation.nextChapter(nextChapter);
			var newLevel = new CourseNavigation.MaterialLevel(chapter.getName(), "", "", "");
			var chapterDir = Files.createDirectory(new File(outputDir, chapterName).toPath());
			exportChapter(chapter, newLevel, chapterNavigation, context, chapterDir.toFile(), templateEngine);
		}
		context.clearVariables();
		context.setVariable("course", plan);
		overallInfos.forEach(context::setVariable);
		var course = templateEngine.process("COURSE", context);
		saveToFile(course, outputDir, "Course.html");
		try {
			saveIncludes(outputDir, templateSet);
		} catch (URISyntaxException e) {
			logger.warn(e.toString());
		}
	}

	private void exportChapter(ChapterOrder chapter, CourseNavigation.MaterialLevel level, CourseNavigation navigation,
			Context context, File outputDir, TemplateEngine templateEngine) throws IOException {
		setOverallInfos(context, navigation, level);
		context.setVariable("chapter", chapter);
		var chapterOverview = templateEngine.process("CHAPTER", context);
		saveToFile(chapterOverview, outputDir, "overview.html");
		var groups = chapter.getGroupOrder();
		GroupOrder previousGroup = null;
		GroupOrder nextGroup = null;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < groups.size(); i++) {
			var group = groups.get(i);
			var newLevel = new CourseNavigation.MaterialLevel(level.getChapter(), group.getName());
			nextGroup = (i < groups.size() - 1) ? groups.get(i + 1) : null;
			MaterialHierarchy next = getMaterialHierarchy(level, navigation, groups, nextGroup, i);
			CourseNavigation newCourseNavigation =
					new CourseNavigation(newLevel, navigation.getCurrentMaterialHierarchy(), next, i, groups.size());
			if (group instanceof GroupOrder concreteGroup) {
				if (concreteGroup.materialCount() == 0) {
					continue;
				}
				String groupName = concreteGroup.getName() == null || group.getName().isBlank() ?
						STR."Gruppe \{chapter.getGroupOrder().indexOf(group) + 1}" : group.getName();
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
			Context context, File outputDir, TemplateEngine templateEngine) throws IOException {
		setOverallInfos(context, navigation, level);
		context.setVariable("group", group);
		var chapterOverview = templateEngine.process("GROUP", context);
		saveToFile(chapterOverview, outputDir, "overview.html");
		var tasks = group.getTaskOrder();
		TaskOrder previousTask = null;
		TaskOrder nextTask = null;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < tasks.size(); i++) {
			var task = tasks.get(i);
			var newTaskLevel = new CourseNavigation.MaterialLevel(level.getChapter(), level.getGroup(), task.getName());
			nextTask = (i < tasks.size() - 1) ? tasks.get(i + 1) : null;
			if (nextTask != null && nextTask.getMaterials().isEmpty()) {
				nextTask = null;
			}
			MaterialHierarchy next = nextTask == null ?
					new MaterialHierarchy(level.getChapter(), navigation.getNextGroup(), null, null,
							navigation.getCount() + 1, navigation.getSize()) :
					new MaterialHierarchy(level.getChapter(), level.getGroup(), nextTask.getName(), null, i + 1,
							tasks.size());

			CourseNavigation newCourseNavigation =
					new CourseNavigation(newTaskLevel, previousNavigation.getCurrentMaterialHierarchy(), next, i,
							tasks.size());
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
						STR."Task \{group.getTaskOrder().indexOf(concreteTask) + 1}" : concreteTask.getName();
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
		final int taskSize = task.getMaterials().size();
		var materials = task.getMaterials();
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
						STR."MATERIAL_\{i + 1}", i + 1, materials.size());
			}
			CourseNavigation.MaterialLevel materialLevel =
					new CourseNavigation.MaterialLevel(level.getChapter(), level.getGroup(), task.getName(),
							STR."MATERIAL_\{i}");
			CourseNavigation newNavigation =
					new CourseNavigation(materialLevel, previousNavigation.getCurrentMaterialHierarchy(), next, i,
							taskSize);
			exportMaterial(context, outputDir, templateEngine, i, material, materialLevel, newNavigation);
			previousNavigation = newNavigation;
		}
	}

	private void exportMaterial(Context context, File outputDir, TemplateEngine templateEngine, int materialNumber,
			Material material, CourseNavigation.MaterialLevel materialLevel, CourseNavigation newNavigation) {
		setOverallInfos(context);
		context.setVariable("material", material);
		context.setVariable("navigation", newNavigation);
		context.setVariable("rootPath", materialLevel.getPathToRoot());
		context.setVariable("title", material.getName());
		String processedHtml = templateEngine.process("MATERIAL", context);
		saveToFile(processedHtml, outputDir, String.format("Material_%s.html", materialNumber));
		if (material instanceof ImageMaterial image) {
			copyImage(image.getImageName(), outputDir);
		}
		if (material instanceof ExampleMaterial exampleMaterial && exampleMaterial.isImageExample()) {
			copyImage(exampleMaterial.getImageName(), outputDir);
		}
	}


	private void setOverallInfos(Context context, CourseNavigation navigation, CourseNavigation.MaterialLevel level) {
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
