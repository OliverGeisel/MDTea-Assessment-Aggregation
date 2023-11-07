package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.generation.material.Material;
import lombok.Getter;

import java.nio.file.Paths;

@Getter
public class CourseNavigation {
	public static final String PATH_REPLACE_REGEX = "[^a-zA-Z0-9äöüÄÖÜß\\s\\-_]";

	private final int           size;
	private final int           prevSize;
	private final int           nextSize;
	private final int           previousCount;
	private final int           nextCount;
	private       boolean       hasPrevious;
	private       boolean       hasNext;
	private       int           count;
	private       String        nextChapter;
	private       String        previousChapter;
	private       String        overview;
	private       String        nextTask;
	private       String        previousTask;
	private       String        nextGroup;
	private       String        previousGroup;
	private       String        nextMaterial;
	private       String        previousMaterial;
	private       MaterialLevel level;

	public CourseNavigation(MaterialLevel level) {
		this(level, new MaterialHierarchy(), new MaterialHierarchy(), 0, 0);
	}

	public CourseNavigation(MaterialLevel level, MaterialHierarchy previous, MaterialHierarchy next, int number,
			int size) {
		hasPrevious = number > 0;
		hasNext = number < size - 1;
		this.size = size;
		this.prevSize = previous.size();
		this.nextSize = next.size();
		this.previousCount = previous.count();
		this.nextCount = next.count();
		count = number;
		this.level = level;
		previousChapter = previous.chapter();
		previousGroup = previous.group();
		previousTask = previous.task();
		previousMaterial = previous.material();
		nextChapter = next.chapter();
		nextGroup = next.group();
		nextTask = next.task();
		nextMaterial = next.material();
	}

	public CourseNavigation(CourseNavigation courseNavigation) {
		this.count = courseNavigation.count;
		this.hasNext = courseNavigation.hasNext;
		this.hasPrevious = courseNavigation.hasPrevious;
		this.level = new MaterialLevel(courseNavigation.level.chapter, courseNavigation.level.group,
				courseNavigation.level.task, courseNavigation.level.material);
		this.nextChapter = courseNavigation.nextChapter;
		this.nextGroup = courseNavigation.nextGroup;
		this.nextTask = courseNavigation.nextTask;
		this.overview = courseNavigation.overview;
		this.previousChapter = courseNavigation.previousChapter;
		this.previousGroup = courseNavigation.previousGroup;
		this.previousTask = courseNavigation.previousTask;
		this.nextMaterial = courseNavigation.nextMaterial;
		this.previousMaterial = courseNavigation.previousMaterial;
		this.size = courseNavigation.size;
		this.prevSize = courseNavigation.prevSize;
		this.nextSize = courseNavigation.nextSize;
		this.previousCount = courseNavigation.previousCount;
		this.nextCount = courseNavigation.nextCount;

	}

	/**
	 * Return the next Element in the Navigation.
	 * If the Navigation is at the end, it will return the next Navigation on Task level.
	 *
	 * @return the next Element in the Navigation.
	 * @throws IllegalStateException if the Navigation is at the end and there is no next Navigation.
	 */
	public CourseNavigation next() throws IllegalStateException {
		if (hasNextMaterial()) {
			var newLevel = new MaterialLevel(level.chapter, level.group, level.task, nextMaterial);
			var current = getCurrentMaterialHierarchy();
			return new CourseNavigation(newLevel, current, getNextMaterialHierarchy(), count + 1, size);
		} else if (hasNextTask()) {
			var newLevel = new MaterialLevel(level.chapter, level.group, nextTask);
			var current = getCurrentMaterialHierarchy();
			return new CourseNavigation(newLevel, current, getNextMaterialHierarchy(), count + 1, size);
		} else if (hasNextGroup()) {
			var newLevel = new MaterialLevel(level.chapter, nextGroup);
			var current = getCurrentMaterialHierarchy();
			return new CourseNavigation(newLevel, current, getNextMaterialHierarchy(), count + 1, size);
		} else if (hasNextChapter()) {
			var newLevel = new MaterialLevel(nextChapter);
			var current = getCurrentMaterialHierarchy();
			return new CourseNavigation(newLevel, current, getNextMaterialHierarchy(), count + 1, size);
		} else {
			throw new IllegalStateException("There is no next Navigation");
		}
	}

	public CourseNavigation nextChapter(ChapterOrder nextChapter) {
		var back = new CourseNavigation(this);
		if (nextChapter != null) {
			back.nextChapter = nextChapter.getName();
			back.previousChapter = this.getLevel().chapter;
		}
		back.count++;
		return back;
	}

	public CourseNavigation nextGroup(GroupOrder nextGroup) {
		var back = new CourseNavigation(this);
		if (nextGroup != null) {
			back.nextGroup = nextGroup.getName();
			back.previousGroup = this.getLevel().group;
		}
		back.count++;
		return back;
	}

	public CourseNavigation nextTask(TaskOrder nextTask) {
		var back = new CourseNavigation(this);
		if (nextTask != null) {
			back.nextTask = nextTask.getName();
			back.previousTask = this.getLevel().task;
		}
		back.count++;
		return back;
	}

	public CourseNavigation nextMaterial(Material nextMaterial) {
		var back = new CourseNavigation(this);
		if (nextMaterial != null) {
			back.nextMaterial = nextMaterial.getName();
			back.previousMaterial = this.getLevel().material;
		}
		back.count++;
		return back;
	}

	public boolean hasPreviousChapter() {
		return previousChapter != null;
	}

	public boolean hasPreviousGroup() {
		return previousGroup != null;
	}

	public boolean hasPreviousTask() {
		return previousTask != null;
	}

	public boolean hasPreviousMaterial() {
		return previousMaterial != null;
	}

	public boolean hasNextChapter() {
		return nextChapter != null;
	}

	public boolean hasNextGroup() {
		return nextGroup != null;
	}

	public boolean hasNextTask() {
		return nextTask != null;
	}

	public boolean hasNextMaterial() {
		return nextMaterial != null;
	}

	//region setter/getter

	public MaterialHierarchy getCurrentMaterialHierarchy() {
		return new MaterialHierarchy(level.chapter, level.group, level.task, level.material, size, count);
	}

	public MaterialHierarchy getPreviousMaterialHierarchy() {
		return new MaterialHierarchy(previousChapter, previousGroup, previousTask, previousMaterial, prevSize,
				previousCount);
	}

	public MaterialHierarchy getNextMaterialHierarchy() {
		return new MaterialHierarchy(nextChapter, nextGroup, nextTask, nextMaterial, nextSize, nextCount);
	}

	public void setPreviousMaterial(String previousMaterial) {
		this.previousMaterial = previousMaterial;
	}


	public void setNextMaterial(String nextMaterial) {
		this.nextMaterial = nextMaterial;
	}
//endregion

	@Getter
	public static class MaterialLevel {
		private String chapter;
		private String group;
		private String task;
		private String material;
		private Level  level;

		public MaterialLevel() {
			this("", "", "", "");
		}

		public MaterialLevel(String chapter) {
			this(chapter, "", "", "");
		}

		public MaterialLevel(String chapter, String group) {
			this(chapter, group, "", "");
		}

		public MaterialLevel(String chapter, String group, String task) {
			this(chapter, group, task, "");
		}

		public MaterialLevel(String chapter, String group, String task, String material) {
			this.chapter = chapter;
			this.group = group;
			this.task = task;
			this.material = material;
			if (chapter != null && !chapter.isEmpty()) {
				level = Level.CHAPTER;
			}
			if (group != null && !group.isEmpty()) {
				level = Level.GROUP;
			}
			if (task != null && !task.isEmpty()) {
				level = Level.TASK;
			}
			if (material != null && !material.isEmpty()) {
				level = Level.MATERIAL;
			}
		}

		//region setter/getter
		public String getPathToRoot() {
			var path = Paths.get(chapter.replaceAll(PATH_REPLACE_REGEX, "_"),
					group.replaceAll(PATH_REPLACE_REGEX, "_"),
					task.replaceAll(PATH_REPLACE_REGEX, "_"));
			var root = Paths.get("");
			return path.relativize(root).toString().replace("\\", "/");
		}
//endregion

		private enum Level {
			CHAPTER,
			GROUP,
			TASK,
			MATERIAL,
			UNKNOWN
		}
	}
}
