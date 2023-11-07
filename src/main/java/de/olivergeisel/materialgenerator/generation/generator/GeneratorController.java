package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.FileSystemStorageService;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParser;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParserException;
import de.olivergeisel.materialgenerator.core.knowledge.IncompleteJSONException;
import de.olivergeisel.materialgenerator.generation.GeneratorService;
import de.olivergeisel.materialgenerator.generation.material.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static de.olivergeisel.materialgenerator.generation.TemplateService.*;

@Controller
@RequestMapping("/generator")
public class GeneratorController {

	private static final String UPLOAD = "UPLOAD";
	private static final String PATH   = "generation/";

	private final GeneratorService         service;
	private final FileSystemStorageService storageService;
	private final TemplateSetRepository    templateSetRepository;
	private final MaterialRepository       materialRepository;

	public GeneratorController(GeneratorService service, FileSystemStorageService storageService,
			TemplateSetRepository templateSetRepository, MaterialRepository materialRepository) {
		this.service = service;
		this.storageService = storageService;
		this.templateSetRepository = templateSetRepository;
		this.materialRepository = materialRepository;
	}

	@GetMapping({"", "/", ".html"})
	public String generator(Model model) {
		model.addAttribute("blank", PLAIN);
		model.addAttribute("color", ILLUSTRATED);
		var templates = templateSetRepository.findAll();
		var restTemplates = templates.filter(template -> !OPTIONS.contains(template.getName())).toList();
		model.addAttribute("templates", restTemplates);
		return PATH + "template-selection";
	}

	@GetMapping("/plan-selection")
	public String generatorAuto(@RequestParam("template") String templateSetName, Model model) {
		var curriculums = new LinkedList<String>();
		curriculums.add(UPLOAD);
		curriculums.addAll(storageService.loadAll().map(path -> path.getFileName().toString()).toList());
		model.addAttribute("templateSetName", templateSetName);
		model.addAttribute("curriculums", curriculums);
		return PATH + "plan-selection";
	}

	@Deprecated
	@GetMapping("/generator-manuel")
	public String generatorManuel(Model model) {
		model.addAttribute("templates", OPTIONS);
		var definitions = List.of("Netzwerk-def", "Verbund-def", "ComputerSystem-def");
		model.addAttribute("definitions", definitions);
		return "generator";
	}

	@GetMapping("overview/{plan}")
	public String overviewGenerationId(@PathVariable String plan, @RequestParam String template, Model model) {
		return overviewGeneration(plan, template, model);
	}

	@GetMapping("overview")
	public String overviewGeneration(@RequestParam String plan, @RequestParam String template, Model model) {
		String planName;
		CoursePlanParser parser = new CoursePlanParser();
		CoursePlan coursePlan;
		var file = storageService.load(plan);
		try {
			coursePlan = parser.parseFromFile(file.toFile());
		} catch (IOException e) {
			throw new CoursePlanParserException(e);
		}
		planName = file.getFileName().toString();

		model.addAttribute("plan", planName);
		model.addAttribute("template", template);
		model.addAttribute("structure", coursePlan.getStructure());
		model.addAttribute("meta", coursePlan.getMetadata());
		return PATH + "overview-all";
	}

	@PostMapping("overview")
	public String overviewGeneration(@RequestParam MultipartFile plan, @RequestParam String curriculum,
			@RequestParam String template, Model model)
			throws FileNotFoundException, WrongFileTypeException, IncompleteJSONException {
		String planName;
		if (curriculum.isBlank() || curriculum.equals(UPLOAD)) {
			CoursePlanParser parser = new CoursePlanParser();
			if (plan == null || plan.isEmpty()) {
				throw new IllegalArgumentException("No plan uploaded");
			}
			var planType = plan.getContentType();
			if (planType == null || !planType.equals("application/json")) {
				throw new WrongFileTypeException(
						String.format("Wrong file type. Must be application/json. But was %s", plan.getContentType()));
			}
			try {
				parser.parseFromFile(plan.getInputStream());
			} catch (IOException | CoursePlanParserException e) {
				throw new IncompleteJSONException(e);
			}
			storageService.store(plan);
			planName = plan.getOriginalFilename();
			storageService.store(plan);
		} else {
			planName = storageService.load(curriculum).getFileName().toString();
		}
		model.addAttribute("plan", planName);
		model.addAttribute("template", template);
		CoursePlanParser parser = new CoursePlanParser();
		var completePlan = parser.parseFromFile(storageService.load(planName).toFile());
		model.addAttribute("structure", completePlan.getStructure());
		model.addAttribute("meta", completePlan.getMetadata());
		return PATH + "overview-all";
	}

	@GetMapping("generate")
	public String generate(@RequestParam String plan, @RequestParam String template) throws FileNotFoundException {
		var coursePlanFile = storageService.load(plan);
		var parser = new CoursePlanParser();
		var coursePlan = parser.parseFromFile(coursePlanFile.toFile());
		var rawcourse = service.generateRawCourse(coursePlan, template);
		return "redirect:/edit/" + rawcourse.getId();
	}

	@GetMapping("materials")
	public String showMaterials(Model model) {
		model.addAttribute("materials", materialRepository.findAll());
		return PATH + "materials";
	}

}
