package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Manager;
import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.extraction.ModelParameters;
import de.olivergeisel.materialgenerator.aggregation.extraction.ServerNotAvailableException;
import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModelService;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.TimeoutException;


@Controller
@RequestMapping("/aggregation")
@SessionAttributes("process")
public class AggregationController {

	private static final List<String> DEFAULT_ACTIONS = List.of("remove", "reject", "accept");

	private static final Logger              logger = LoggerFactory.getLogger(AggregationController.class);
	private static final Map<String, String> modelListName;
	private static final String              ZUFALL = "ZUFALL";

	static {
		modelListName = ModelNameLoader.load();
	}

	private final GPT_Manager gptManager;
	private final KnowledgeModelService modelService;

	public AggregationController(GPT_Manager gptManager, KnowledgeModelService modelService) {
		this.gptManager = gptManager;
		this.modelService = modelService;
	}

	private static GPT_Request.ModelLocation setParamsToProcess(AggregationProcess process, String fragment,
			String apiKey,
			String modelName, String locationString, ModelParameters modelParameters) {
		if (process.getSources().isEmpty() && !fragment.isBlank()) {
			process.setCurrentFragment(fragment.strip());
		}
		process.setApiKey(apiKey);
		if (modelName.equals(ZUFALL)) {
			process.setModelName(modelListName.keySet().stream().toList().get(new Random().nextInt(1,
					modelListName.size() - 1)));
		} else {
			process.setModelName(modelName);
		}
		process.setModelParameters(modelParameters);
		var location = GPT_Request.ModelLocation.valueOf(locationString.toUpperCase());
		process.setModelLocation(location);
		return location;
	}

	@GetMapping({"", "/"})
	public String index(Model model) {
		var elements = modelService.elementCount();
		var relations = modelService.relationCount();
		var terms = modelService.termCount();
		var definitions = modelService.definitionCount();
		var examples = modelService.exampleCount();
		var statistik = new Aggregation(elements, relations, terms, definitions, examples);
		model.addAttribute("aggregation", statistik);
		model.addAttribute("structureCount", modelService.structureCount());
		return "aggregation/overview";
	}

	@GetMapping("collect")
	public String collect() {
		return "aggregation/collect";
	}

	@PostMapping("collect")
	public String collectPost(@RequestParam("sources") MultipartFile[] files, AggregationProcess process) {
		if (files == null || files.length == 0) {
			return "redirect:/aggregation/collect?error=Keine Dateien hochgeladen";
		}
		process.addSources(Arrays.stream(files).toList());
		return "redirect:/aggregation/preprocessing";
	}

	@GetMapping("only-one-fragment")
	public String onlyOneFragment(@RequestParam(value = "error", required = false) String error, Model model) {
		model.addAttribute("error", error);
		return "aggregation/only-one-fragment";
	}

	@PostMapping("only-one-fragment")
	public String onlyOneFragmentPost(@RequestParam(value = "fragment") String fragment,
			@ModelAttribute("process") AggregationProcess process) {
		if (fragment == null || fragment.isBlank()) {
			return "redirect:/aggregation/collect?error=Fragment ist zu kurz";
		}
		process.setCurrentFragment(fragment);

		return "redirect:/aggregation/top-level-scan";
	}

	@GetMapping("preprocessing")
	public String preprocessing() {
		return "aggregation/preprocessing";
	}

	@PostMapping("preprocessing")
	public String preprocessingPost(@RequestParam("sources") MultipartFile[] files, AggregationProcess process) {
		if (files != null && files.length > 0) {
			logger.info("Dateien hochgeladen:");
			for (MultipartFile file : files) {
				logger.info("Datei: {} hochgeladen", file.getOriginalFilename());
			}
			return "redirect:/aggregation/top-level-scan";
		}
		return "redirect:/aggregation/collect";
	}

	@GetMapping("top-level-scan")
	String topLevelScan(Model model, @ModelAttribute("process") AggregationProcess process,
			@ModelAttribute("form") AggregationConfigForm form) {
		model.addAttribute("models", getModelNameList());
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/top-level-scan";
	}

	@PostMapping("top-level-scan")
	String topLevelScanPost(Model model, AggregationConfigForm form,
			@ModelAttribute("process") AggregationProcess process) {
		var location = setParamsToProcess(process, form.getFragment(), form.getApiKey(), form.getModelName(),
				form.getConnectionType(), form.getModelParameters());
		var prompt = new TermPrompt(form.getFragment());
		try {
			var answer = gptManager.requestTerms(prompt, form.getUrl(),
					modelListName.get(process.getModelName()), location,
					form.getMaxTokens(), form.getTemperature(), form.getTopP(), 0.2, form.getRetries());
			var extractor = new TermElementExtractor();
			var terms = extractor.extractAll(answer, process.getModelLocation());
			terms.forEach(it -> it.setStructureId(process.getAreaOfKnowledge()));
			process.suggest(terms);
		} catch (ServerNotAvailableException | TimeoutException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("models", getModelNameList());
			model.addAttribute("form", form);
			model.addAttribute("structures", modelService.getStructureIds());
			return "aggregation/top-level-scan";
		}
		if (process.getStepNumber() == 0) {
			process.nextStep();
		}
		return "redirect:/aggregation/top-level-scan/terms";
	}

	@GetMapping("top-level-scan/terms")
	String topLevelScanTerms(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 1) {
			if (process.getStepNumber() == 0) {
				return "redirect:/aggregation/top-level-scan";
			}
			var step = process.getStep();
			return STR."redirect:/aggregation/top-level-scan/\{step.toLowerCase()}";
		}
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("suggestedTerms", process.getTerms().getSuggestedElements());
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/terms";
	}

	@GetMapping("top-level-scan/terms/add")
	String topLevelScanTermsAdd(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 1) {
			return "redirect:/aggregation/top-level-scan";
		}
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/terms-add";
	}

	@PostMapping("top-level-scan/terms/add")
	String topLevelScanTermsAddPost(@ModelAttribute("process") AggregationProcess process,
			@RequestParam("content") String content,
			@RequestParam(value = "structureId", required = false) Optional<String> structureId) {
		var newTerm = new Term(content, STR."\{content}-TERM", "term");
		structureId.ifPresent(it -> {if (!it.strip().isBlank()) newTerm.setStructureId(it);});
		process.add(List.of(newTerm));
		return "redirect:/aggregation/top-level-scan/terms";
	}

	@GetMapping("top-level-scan/terms/edit")
	String topLevelScanTermsEdit(Model model, @ModelAttribute("process") AggregationProcess process, @RequestParam(
			"id") String id) {
		if (process.getStepNumber() != 1) {
			return "redirect:/aggregation/top-level-scan";
		}
		var term = process.getTerms().findById(id);
		model.addAttribute("term", term);
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/terms-edit";
	}

	@PostMapping("top-level-scan/terms/edit")
	String topLevelScanTermsEditPost(Model model, @ModelAttribute("process") AggregationProcess process,
			@RequestParam("id") String id, @RequestParam("content") String content,
			@RequestParam(value = "structureId", required = false) String structureId) {
		var term = process.getTerms().findById(id);
		term.setContent(content);
		if (structureId != null && !structureId.isBlank()) {
			term.setStructureId(structureId);
		}
		model.addAttribute("term", term);
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/terms-edit";
	}

	@PostMapping("top-level-scan/terms/next")
	String topLevelScanTermsNext(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 1) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.nextStep();
		return "redirect:/aggregation/top-level-scan/definitions";
	}

	@PostMapping("top-level-scan/terms/{action}")
	String topLevelScanTermsPost(@ModelAttribute("process") AggregationProcess process,
			@PathVariable("action") String action, @RequestParam("id") String id, AggregationConfigForm form) {
		switch (action) {
			case "remove" -> process.removeById(id);
			case "reject" -> process.rejectById(id);
			case "accept" -> process.approveById(id);
			case "update-config" -> setParamsToProcess(process, form.getFragment(), form.getApiKey(),
					form.getModelName(), form.getConnectionType(), form.getModelParameters());
			default -> throw new IllegalArgumentException(STR."Unknown action: \{action}");
		}
		return "redirect:/aggregation/top-level-scan/terms";
	}

	@GetMapping("top-level-scan/definitions")
	String topLevelScanDefinitions(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 2) {
			return "redirect:/aggregation/top-level-scan";
		}
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("acceptedDefinitions", process.getDefinitions().getAcceptedElements());
		model.addAttribute("suggestedDefinitions", process.getDefinitions().getSuggestedElements());
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/definitions";
	}

	@PostMapping("top-level-scan/definitions")
	String topLevelScanDefinitionsPost(Model model, @ModelAttribute("process") AggregationProcess process,
			DefinitionRequestForm form) {
		if (process.getStepNumber() != 2) {
			return "redirect:/aggregation/top-level-scan";
		}
		var location = setParamsToProcess(process, form.getFragment(), form.getApiKey(), form.getModelName(),
				form.getConnectionType(), form.getModelParameters());
		var terms = process.getTerms().getAcceptedElements().stream().map(KnowledgeElement::getContent).toList();
		var prompt = new DefinitionPrompt(process.getCurrentFragment(), terms);
		try {
			var answer = gptManager.requestDefinitions(prompt, form.getUrl(),
					modelListName.get(process.getModelName()), location, process.getModelParameters());
			var extractor = new DefinitonElementExtractor();
			var definitions = extractor.extractAll(answer, process.getModelLocation());
			definitions.forEach(it -> it.setStructureId(process.getAreaOfKnowledge()));
			process.suggest(definitions);
			for (var definition : definitions) {
				var termName = extractor.getTermFor(definition.getId());
				var term = process.getTerms().getAcceptedElements().stream()
								  .filter(it -> it.getContent().equals(termName))
								  .findFirst();
				term.ifPresent(it -> process.linkTermsToDefinition(definition.getId(), it, List.of()));
			}

		} catch (ServerNotAvailableException | TimeoutException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("models", getModelNameList());
			model.addAttribute("form", form);
			model.addAttribute("structures", modelService.getStructureIds());
			return "aggregation/definitions";
		}
		return "redirect:/aggregation/top-level-scan/definitions";
	}

	@GetMapping("top-level-scan/definitions/edit")
	String topLevelScanDefinitionsEdit(Model model, @ModelAttribute("process") AggregationProcess process,
			@RequestParam("id") String id) {
		var definition = process.getDefinitions().getAcceptedElements().stream().filter(it -> it.getId().equals(id))
								.findFirst().orElseThrow();
		process.getRelations().stream().filter(it -> it.getType().equals(RelationType.DEFINED_BY)
													 && it.getTo().equals(definition))
			   .findFirst().ifPresent(it -> model.addAttribute("mainTerm", it.getFrom()));
		model.addAttribute("terms", process.getTerms().getAcceptedElements());
		model.addAttribute("definition", definition);
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/definition-edit";
	}

	@PostMapping("top-level-scan/definitions/edit")
	String topLevelScanDefinitionsEditPost(@ModelAttribute("process") AggregationProcess process,
			@RequestParam("id") String id, @RequestParam("content") String content,
			@RequestParam(value = "mainTerm", required = false) String mainTermId,
			@RequestParam(value = "structureId", required = false) String structureId) {
		var definition = process.getDefinitions().getAcceptedElements().stream().filter(it -> it.getId().equals(id))
								.findFirst().orElseThrow();
		if (mainTermId != null && !mainTermId.isBlank()) {
			// remove old relation
			var mainTerm = process.getTerms().getAcceptedElements().stream().filter(it -> it.getId().equals(mainTermId))
								  .findFirst().orElseThrow();
			var definitionRelation = process.getRelationsByFromId(definition.getId()).stream()
											.filter(it -> it.getType().equals(RelationType.DEFINES)
														  && it.getFrom().equals(definition)
														  && it.getTo().equals(mainTerm))
											.findFirst();
			var reverseDefinitionRelation = process.getRelationsByToId(definition.getId()).stream()
												   .filter(it -> it.getType().equals(RelationType.DEFINED_BY)
																 && it.getFrom().equals(mainTerm)
																 && it.getTo().equals(definition))
												   .findFirst();
			definitionRelation.ifPresent(it -> process.getRelations().remove(it));
			reverseDefinitionRelation.ifPresent(it -> process.getRelations().remove(it));
			process.linkTermsToDefinition(id, mainTerm, List.of()); // add new
		}
		definition.setContent(content);
		if (structureId != null && !structureId.isBlank()) {
			definition.setStructureId(structureId);
		}
		return STR."redirect:edit?id=\{id}";
	}

	@PostMapping("top-level-scan/definitions/{action}")
	String topLevelScanDefinitionsPost(@ModelAttribute("process") AggregationProcess process,
			@PathVariable("action") String action,
			@RequestParam(value = "id", defaultValue = "NONE") String id,
			@RequestParam(value = "terms", required = false) Set<String> termIds,
			@RequestParam(value = "mainTerm", required = false) String mainTermId) {
		switch (action) {
			case "remove" -> process.removeById(id);
			case "reject" -> process.rejectById(id);
			case "accept" -> process.approveById(id);
			case "link" -> {
				var terms = process.getTerms().getAcceptedElements().stream().filter(it -> termIds.contains(it.getId()))
								   .toList();
				var mainTerm =
						process.getTerms().getAcceptedElements().stream().filter(it -> it.getId().equals(mainTermId))
							   .findFirst().orElseThrow();
				process.linkTermsToDefinition(id, mainTerm, terms);
			}
			case "unlink" -> {
				var terms = process.getTerms().getAcceptedElements().stream().filter(it -> termIds.contains(it.getId()))
								   .toList();
				process.unlinkTermsFromDefinition(id, terms);
			}
			case "next" -> {
				process.nextStep();
				return "redirect:/aggregation/top-level-scan/examples";
			}
			default -> throw new IllegalArgumentException(STR."Unknown action: \{action}");
		}
		return "redirect:/aggregation/top-level-scan/definitions";
	}


	@GetMapping("top-level-scan/tasks")
	String topLevelScanTasks(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 3) {
			return "redirect:/aggregation/top-level-scan";
		}
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("acceptedTasks", process.getTasks().getAcceptedElements());
		model.addAttribute("suggestedTasks", process.getTasks().getSuggestedElements());
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/tasks";
	}

	@PostMapping("top-level-scan/tasks")
	String topLevelScanTasksPost(Model model, @ModelAttribute("process") AggregationProcess process,
			ItemRequestForm form) {
		if (process.getStepNumber() != 5) {
			return "redirect:/aggregation/top-level-scan";
		}
		var location = setParamsToProcess(process, form.getFragment(), form.getApiKey(), form.getModelName(),
				form.getConnectionType(), form.getModelParameters());
		var prompt = new ItemPrompt(form.getFragment());
		try {
			var answer = gptManager.requestTasks(prompt, form.getUrl(),
					modelListName.get(process.getModelName()), location, process.getModelParameters());
			var extractor = new ItemElementExtractor();
			var tasks = extractor.extractAll(answer, process.getModelLocation());
			tasks.forEach(it -> it.setStructureId(process.getAreaOfKnowledge()));
			process.suggest(tasks);
		} catch (ServerNotAvailableException | TimeoutException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("models", getModelNameList());
			model.addAttribute("form", form);
			model.addAttribute("structures", modelService.getStructureIds());
			return "aggregation/tasks";
		}
		return "redirect:/aggregation/top-level-scan/tasks";
	}

	@GetMapping("compile")
	public String compile() {
		return "aggregation/compile";
	}

	@GetMapping("deep-scan")
	public String deepScan() {
		return "aggregation/deep-scan";
	}

	@GetMapping("deep-scan/handle")
	public String deepScanHandle() {
		return "aggregation/deep-scan-handle";
	}

	@GetMapping("integration")
	public String integration(Model model, @ModelAttribute("process") AggregationProcess process) {
		model.addAttribute("ready", !process.isComplete() && (process.hasElements() || process.hasRelations()));
		model.addAttribute("message", process.isComplete() ? "Der Prozess ist abgeschlossen" : "");
		return "aggregation/integration";
	}

	@PostMapping("integration")
	public String integrationPost(@ModelAttribute("process") AggregationProcess process) {
		modelService.integrate(process);
		process.setComplete();
		return "redirect:";
	}

	@GetMapping("debug")
	String debug(Model model, @ModelAttribute("process") AggregationProcess process) {
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("suggestedTerms", process.getTerms().getSuggestedElements());
		return "aggregation/debug";
	}

	@PostMapping("debug")
	String debugAct(Model model, @ModelAttribute("process") AggregationProcess process) {
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("suggestedTerms", process.getTerms().getSuggestedElements());
		return "aggregation/debug";
	}

	//region setter/getter
	@ModelAttribute("process")
	AggregationProcess getProcess() {
		return new AggregationProcess();
	}

	private static List<String> getModelNameList() {
		List<String> list = new LinkedList<>();
		list.add(ZUFALL);
		list.addAll(modelListName.keySet());
		return list;
	}
//endregion

	private record Aggregation(long elements, long relations, long terms, long definitions, long examples) {

		public Aggregation() {
			this(0, 0, 0, 0, 0);
		}
	}
}
