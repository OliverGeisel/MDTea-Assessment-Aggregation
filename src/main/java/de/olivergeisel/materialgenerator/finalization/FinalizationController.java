package de.olivergeisel.materialgenerator.finalization;


import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.export.DownloadManager;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourseRepository;
import de.olivergeisel.materialgenerator.generation.material.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class FinalizationController {

	private static final String TEMPLATE_SET_FROM_TEMPLATES_FOLDER = "../templateSets/";

	private static final String              PATH           = "finalization/";
	private static final String              REDIRECT_EDIT  = "redirect:/edit/";
	private static final String              THEMEN_SECTION = "#Themen";
	private final        RawCourseRepository repository;

	private final FinalizationService service;
	private final MaterialRepository  materialRepository;

	public FinalizationController(RawCourseRepository repository, FinalizationService service,
			MaterialRepository materialRepository) {
		this.repository = repository;
		this.service = service;
		this.materialRepository = materialRepository;
	}


	@GetMapping({"edit", "edit.html", "edit/"})
	public String editOverview(Model model) {
		model.addAttribute("courses", repository.findAll());
		return PATH + "edit";
	}

	@GetMapping({"edit/{id}", "edit/{id}/"})
	public String editCourse(@PathVariable UUID id, Model model) {
		return repository.findById(id).map(course -> {
			model.addAttribute("course", course);
			model.addAttribute("collectionIds", course.getOrder().getCollectionsNameAndId());
			model.addAttribute("RELEVANCE",
					Arrays.stream(Relevance.values()).filter(it -> it != Relevance.TO_SET).toList());
			return PATH + "edit-course";
		}).orElse(REDIRECT_EDIT);
	}

	private String getTemplateForComplex(TemplateType type) {
		return switch (type.getType()) {
			case "OVERVIEW" -> "view-overview";
			case "SUMMARY" -> "view-summary";
			case "TEST" -> "view-test";
			default -> throw new IllegalStateException(STR."Unexpected value: \{type}");
		};
	}

	@GetMapping({"edit/{id}/viewComplex",})
	public String viewComplex(@PathVariable("id") UUID id, @RequestParam("materialId") UUID materialId, Model model) {
		var material = materialRepository.findById(materialId);
		material.ifPresent(it -> model.addAttribute("material", it));
		repository.findById(id).ifPresent(course -> model.addAttribute("course", course));
		return material.map(it -> PATH + getTemplateForComplex(it.getTemplateType())).orElse(REDIRECT_EDIT);
	}


	@PostMapping({"edit/{id}/delete",})
	public String deleteCourse(@PathVariable UUID id, HttpServletRequest request) {
		repository.deleteById(id);
		return REDIRECT_EDIT;
	}

	@PostMapping({"edit/{id}/deletePart",})
	public String deleteCoursePart(@PathVariable UUID id, @RequestParam("id") UUID partId) {
		repository.findById(id).ifPresent(course -> {
			course.getOrder().remove(partId);
			repository.save(course);
		});
		return REDIRECT_EDIT + id + THEMEN_SECTION;
	}

	@PostMapping({"edit/{id}/unassignPart",})
	public String unassignCoursePart(@PathVariable UUID id, @RequestParam("id") UUID partId) {
		repository.findById(id).ifPresent(course -> {
			var material = course.getOrder().findMaterial(partId);
			course.getUnassignedMaterials().add(material);
			course.getOrder().remove(partId);
			repository.save(course);
		});
		return REDIRECT_EDIT + id + THEMEN_SECTION;
	}

	@PostMapping({"edit/{id}/assignPart",})
	public String assignCoursePart(@PathVariable UUID id, @RequestParam("id") UUID partId,
			@RequestParam("collectionId") UUID collectionId) {
		service.assignMaterialToCollection(id, partId, collectionId);
		return REDIRECT_EDIT + id + THEMEN_SECTION;
	}

	@PostMapping("edit/{id}/export")
	public void exportCourse(@PathVariable UUID id,
			@RequestParam(value = "kind", defaultValue = "HTML") DownloadManager.ExportKind kind,
			HttpServletResponse response) {
		service.exportCourse(id, kind, response);
	}

	@PostMapping("/edit/download-material")
	public void downloadMaterial(@RequestParam("materialId") UUID materialId, HttpServletResponse response) {
		service.downloadMaterial(materialId, response);
	}

	@PostMapping("edit/{id}/relevance")
	public String exportCourse(@PathVariable UUID id, @RequestParam("task") UUID taskID,
			@RequestParam Relevance relevance) {
		service.setRelevance(id, taskID, relevance);
		return REDIRECT_EDIT + id + THEMEN_SECTION;
	}

	@PostMapping({"edit/{id}",})
	public String editCourse(@PathVariable UUID id,
			@RequestParam(value = "chapter", required = false) UUID parentChapterId,
			@RequestParam(value = "group", required = false) UUID parentGroupId,
			@RequestParam(value = "task", required = false) UUID parentTaskId,
			@RequestParam(value = "up", required = false) UUID idUp,
			@RequestParam(value = "down", required = false) UUID idDown, Model model) {
		if (idUp != null) {
			service.moveUp(id, parentChapterId, parentGroupId, parentTaskId, idUp);
		} else if (idDown != null) {
			service.moveDown(id, parentChapterId, parentGroupId, parentTaskId, idDown);
		}
		return repository.findById(id).map(course -> {
			model.addAttribute("course", course);
			return REDIRECT_EDIT + course.getId() + THEMEN_SECTION;
		}).orElse(REDIRECT_EDIT);
	}


	@GetMapping("view")
	public String viewOverview(@RequestParam("materialId") UUID materialId,
			@RequestParam("templateSet") String templateSet, Model model) {
		AtomicReference<String> materialType = new AtomicReference<>();
		// TODO not active
		return STR."\{TEMPLATE_SET_FROM_TEMPLATES_FOLDER}\{templateSet}/\{materialType}";
	}

	@GetMapping("export")
	public String exportOverview(Model model) {
		model.addAttribute("courses", repository.findAll().filter(RawCourse::isValid));
		return PATH + "export";
	}
}
