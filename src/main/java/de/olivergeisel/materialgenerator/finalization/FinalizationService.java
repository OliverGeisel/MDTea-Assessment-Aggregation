package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.parts.*;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class FinalizationService {

	private final DownloadManager downloadManager;

	private final CourseOrderRepository                courseOrderRepository;
	private final ChapterOrderRepository               chapterOrderRepository;
	private final GroupOrderRepository                 groupOrderRepository;
	private final TaskOrderRepository                  taskOrderRepository;
	private final RawCourseRepository                  rawCourseRepository;
	private final CourseMetadataFinalizationRepository metadataRepository;
	private final GoalRepository                       goalRepository;

	public FinalizationService(DownloadManager downloadManager, CourseOrderRepository courseOrderRepository,
			ChapterOrderRepository chapterOrderRepository, GroupOrderRepository groupOrderRepository,
			TaskOrderRepository taskOrderRepository, RawCourseRepository rawCourseRepository,
			CourseMetadataFinalizationRepository metadataRepository, GoalRepository goalRepository) {
		this.downloadManager = downloadManager;
		this.courseOrderRepository = courseOrderRepository;
		this.chapterOrderRepository = chapterOrderRepository;
		this.groupOrderRepository = groupOrderRepository;
		this.taskOrderRepository = taskOrderRepository;
		this.rawCourseRepository = rawCourseRepository;
		this.metadataRepository = metadataRepository;
		this.goalRepository = goalRepository;
	}

	public RawCourse createRawCourse(CoursePlan coursePlan, String template, Collection<MaterialAndMapping> materials) {
		var cGoals = coursePlan.getGoals();
		var goals = createGoals(cGoals);
		var rawCourse = new RawCourse(coursePlan, template, goals);
		rawCourse.assignMaterial(materials);
		saveMetadata(rawCourse.getMetadata());
		saveMaterialOrder(rawCourse.getCourseOrder());
		return rawCourseRepository.save(rawCourse);
	}

	private Set<Goal> createGoals(Set<ContentGoal> goals) {
		var back = new HashSet<Goal>();
		for (var contentGoal : goals) {
			var goal = new Goal(contentGoal);
			goal = goalRepository.save(goal);
			back.add(goal);
		}
		return back;
	}

	private void saveMaterialOrder(CourseOrder courseOrder) {
		saveChapterOrder(courseOrder.getChapterOrder());
		courseOrderRepository.save(courseOrder);
	}

	private void saveMetadata(CourseMetadataFinalization metadata) {
		metadataRepository.save(metadata);
	}

	private void saveChapterOrder(List<ChapterOrder> chapterOrder) {
		chapterOrder.forEach(chapter -> {
			saveGroupOrder(chapter.getGroupOrder());
			chapterOrderRepository.save(chapter);
		});
	}

	private void saveGroupOrder(List<GroupOrder> groupOrder) {
		groupOrder.forEach(group -> {
			saveTaskOrder(group.getTaskOrder());
			groupOrderRepository.save(group);
		});
	}

	private void saveTaskOrder(List<TaskOrder> taskOrder) {
		taskOrderRepository.saveAll(taskOrder);
	}

	public void moveUp(UUID id, UUID parentChapterId, UUID parentGroupId, UUID parentTaskId, UUID idUp)
			throws NoSuchElementException, IllegalStateException {
		var course = rawCourseRepository.findById(id).orElseThrow();
		var order = course.getCourseOrder();
		var element = order.find(idUp);
		switch (element) {
			case ChapterOrder chapter -> order.moveUp(chapter);
			case GroupOrder group -> {
				var chapter = order.findChapter(parentChapterId);
				chapter.moveUp(group);
			}
			case TaskOrder task -> {
				var group = order.findGroup(parentGroupId);
				group.moveUp(task);
			}
			case Material material -> {
				var task = order.findTask(parentTaskId);
				task.moveUp(material);
			}
			default -> throw new IllegalStateException("Unexpected value: " + element);
		}
		rawCourseRepository.save(course);
	}

	public void moveDown(UUID id, UUID parentChapterId, UUID parentGroupId, UUID parentTaskId, UUID idDown)
			throws NoSuchElementException, IllegalStateException {
		var course = rawCourseRepository.findById(id).orElseThrow();
		var order = course.getCourseOrder();
		var element = order.find(idDown);
		switch (element) {
			case ChapterOrder chapter -> order.moveDown(chapter);
			case GroupOrder group -> {
				var chapter = order.findChapter(parentChapterId);
				chapter.moveDown(group);
			}
			case TaskOrder task -> {
				var group = order.findGroup(parentGroupId);
				group.moveDown(task);
			}
			case Material material -> {
				var task = order.findTask(parentTaskId);
				task.moveDown(material);
			}
			default -> throw new IllegalStateException("Unexpected value: " + element);
		}
		rawCourseRepository.save(course);
	}

	public void exportCourse(UUID id, HttpServletResponse response) {
		generateAndDownloadTemplates(rawCourseRepository.findById(id).orElseThrow(), response);
	}

	public void generateAndDownloadTemplates(RawCourse plan, HttpServletResponse response) {
		var zipName = plan.getMetadata().getName().orElse("course");
		downloadManager.createDownloadZip(zipName, plan.getTemplateName(), plan, response);
	}

	public void setRelevance(UUID id, UUID taskId, Relevance relevance)
			throws IllegalArgumentException, NoSuchElementException {
		var course = rawCourseRepository.findById(id).orElseThrow();
		var order = course.getCourseOrder();
		var taskOrder = order.findTask(taskId);
		if (taskOrder != null) {
			taskOrder.setRelevance(relevance);
			rawCourseRepository.save(course);
		} else {
			throw new IllegalArgumentException("Task not found");
		}
	}
}
