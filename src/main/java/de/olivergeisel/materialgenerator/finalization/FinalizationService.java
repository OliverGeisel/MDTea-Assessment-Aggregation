package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.export.DownloadManager;
import de.olivergeisel.materialgenerator.finalization.export.Exporter;
import de.olivergeisel.materialgenerator.finalization.parts.*;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfigurationParser;
import de.olivergeisel.materialgenerator.generation.generator.test_assamble.TestAssembler;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * The FinalizationService is a service that provides methods to edit a course.
 * Additionally, it enables the export of a course.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see RawCourse
 * @see Exporter
 * @since 0.2.0
 */
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
	private final MaterialRepository materialRepository;

	public FinalizationService(DownloadManager downloadManager, CourseOrderRepository courseOrderRepository,
			ChapterOrderRepository chapterOrderRepository, GroupOrderRepository groupOrderRepository,
			TaskOrderRepository taskOrderRepository, RawCourseRepository rawCourseRepository,
			CourseMetadataFinalizationRepository metadataRepository, GoalRepository goalRepository,
			MaterialRepository materialRepository) {
		this.downloadManager = downloadManager;
		this.courseOrderRepository = courseOrderRepository;
		this.chapterOrderRepository = chapterOrderRepository;
		this.groupOrderRepository = groupOrderRepository;
		this.taskOrderRepository = taskOrderRepository;
		this.rawCourseRepository = rawCourseRepository;
		this.metadataRepository = metadataRepository;
		this.goalRepository = goalRepository;
		this.materialRepository = materialRepository;
	}

	public RawCourse createRawCourse(CoursePlan coursePlan, String template, Collection<MaterialAndMapping> materials) {
		var cGoals = coursePlan.getGoals();
		var goals = createGoals(cGoals);
		var rawCourse = new RawCourse(coursePlan, template, goals);
		rawCourse.assignMaterial(materials);
		saveMetadata(rawCourse.getMetadata());
		saveMaterialOrder(rawCourse.getOrder());
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

	private void saveMaterialOrder(RawCourseOrder rawCourseOrder) {
		saveChapterOrder(rawCourseOrder.getChapterOrder());
		courseOrderRepository.save(rawCourseOrder);
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
		var order = course.getOrder();
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
			default -> throw new IllegalStateException(STR."Unexpected value: \{element}");
		}
		rawCourseRepository.save(course);
	}

	public void moveDown(UUID id, UUID parentChapterId, UUID parentGroupId, UUID parentTaskId, UUID idDown)
			throws NoSuchElementException, IllegalStateException {
		var course = rawCourseRepository.findById(id).orElseThrow();
		var order = course.getOrder();
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
			default -> throw new IllegalStateException(STR."Unexpected value: \{element}");
		}
		rawCourseRepository.save(course);
	}

	public void exportCourse(UUID id, DownloadManager.ExportKind kind, HttpServletResponse response) {
		generateAndDownloadTemplates(rawCourseRepository.findById(id).orElseThrow(), kind, response);
	}

	public void generateAndDownloadTemplates(RawCourse plan, DownloadManager.ExportKind kind,
			HttpServletResponse response) {
		var zipName = plan.getMetadata().getName().orElse("course");
		downloadManager.createAndDownload(kind, zipName, plan, plan.getTemplateName(), response);
	}

	public void setRelevance(UUID id, UUID taskId, Relevance relevance)
			throws IllegalArgumentException, NoSuchElementException {
		var course = rawCourseRepository.findById(id).orElseThrow();
		var order = course.getOrder();
		var taskOrder = order.findTask(taskId);
		if (taskOrder != null) {
			taskOrder.setRelevance(relevance);
			rawCourseRepository.save(course);
		} else {
			throw new IllegalArgumentException("Task not found");
		}
	}

	/**
	 * Updates the metadata of a course
	 * @param id the id of the course
	 * @param map the form containing the new metadata
	 * @throws NoSuchElementException if the course is not found
	 */
	public void updateMeta(UUID id, Map<String, Object> map) throws NoSuchElementException {
		var form = new MetaForm(map);
		var course = rawCourseRepository.findById(id).orElseThrow();
		var metadata = course.getMetadata();
		metadata.setName(form.getName());
		metadata.setDescription(form.getDescription());
		metadata.setYear(Integer.toString(form.getYear()));
		metadata.setLevel(form.getLevel());
		var keys = form.getExtras().get("key");
		var values = form.getExtras().get("value");
		var extras = new HashMap<String, String>();
		for (int i = 0; i < keys.size(); i++) {
			extras.put(keys.get(i), values.get(i));
		}
		for (var entry : extras.entrySet()) {
			metadata.addOtherInfo(entry.getKey(), entry.getValue());
		}
		saveMetadata(metadata);
	}

	public TestMaterial generateTest(String topic) {
		TestConfiguration configuration;
		try {
			configuration = TestConfigurationParser.getDefaultConfiguration();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		var assembler = new TestAssembler(null, null, configuration);
		List<TestMaterial> materials = assembler.assemble();
		return materials.stream().findFirst().orElse(null);
	}

	public void downloadMaterial(UUID materialId, HttpServletResponse response) {
		var material = materialRepository.findById(materialId);
		material.ifPresent(value -> downloadManager.downloadTest(value, response));
	}

	public void assignMaterialToCollection(UUID id, UUID partId, UUID collectionId) {
		rawCourseRepository.findById(id).ifPresent(course -> {
			var material = course.getUnassignedMaterials().stream().filter(it -> it.getId().equals(partId)).findFirst();
			var collection = course.getOrder().find(collectionId);
			if (material.isPresent() && collection != null) {
				boolean assigned = false;
				switch (collection) {
					case GroupOrder group -> {
						if (material.get() instanceof ComplexMaterial complexMaterial) {
							group.assignComplex(complexMaterial);
							assigned = true;
						}
					}
					case TaskOrder task -> {
						task.append(material.orElseThrow());
						assigned = true;
					}
					default -> {
					}
				}
				if (assigned) {
					course.getUnassignedMaterials().remove(material.get());
				}
			}
			rawCourseRepository.save(course);
		});
	}
}
