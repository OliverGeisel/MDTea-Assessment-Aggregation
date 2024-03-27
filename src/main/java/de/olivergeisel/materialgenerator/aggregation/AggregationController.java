package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Manager;
import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.extraction.ModelParameters;
import de.olivergeisel.materialgenerator.aggregation.extraction.ServerNotAvailableException;
import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModelService;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.ModelStatistic;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.forms.AddElementForm;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	private static GPT_Request.ModelLocation setParamsToProcess(AggregationProcess process,
			AggregationConfigForm form) {
		return setParamsToProcess(process, form.getFragment(), form.getApiKey(), form.getModelName(),
				form.getConnectionType(), form.getFragmentLanguage(), form.getTargetLanguage(),
				form.getModelParameters());
	}

	private static GPT_Request.ModelLocation setParamsToProcess(AggregationProcess process, String fragment,
			String apiKey, String modelName, String locationString,
			String fragmentLanguage, String targetLanguage, ModelParameters modelParameters) {
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
		process.setFragmentLanguage(fragmentLanguage);
		process.setTargetLanguage(targetLanguage);
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
		var items = modelService.count(KnowledgeType.ITEM);
		var statistik = new ModelStatistic(terms, definitions, examples, items, relations, elements);
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
		process.reset();
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

	@GetMapping({"top-level-scan", "top-level-scan/", "top-level-scan/initial"})
	String topLevelScan(Model model, @ModelAttribute("process") AggregationProcess process,
			@ModelAttribute("form") AggregationConfigForm form) {
		model.addAttribute("models", getModelNameList());
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/top-level-scan";
	}

	@PostMapping("top-level-scan")
	String topLevelScanPost(Model model, AggregationConfigForm form,
			@ModelAttribute("process") AggregationProcess process) {
		var location = setParamsToProcess(process, form);
		var prompt = new TermPrompt(form.getFragment(), form.getFragmentLanguage(), form.getTargetLanguage());
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

	//region terms

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

	@GetMapping("top-level-scan/term")
	String viewTerm(Model model, @ModelAttribute("process") AggregationProcess process, @RequestParam("id") String id) {
		var term = process.getTerms().findById(id);
		model.addAttribute("term", term);
		return "aggregation/term-view";
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
			@RequestParam("id") String id, @RequestParam("content") String content, @RequestParam("newId") String newId,
			@RequestParam(value = "structureId", required = false) String structureId) {
		var term = process.getTerms().findById(id);
		term.setContent(content);
		if (!newId.equals(term.getId())) {
			term.editId(newId);
			// todo check if new id must be changed in relations
		}
		if (structureId != null && !structureId.isBlank()) {
			term.setStructureId(structureId);
		}
		model.addAttribute("term", term);
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/terms-edit";
	}

	@PostMapping("top-level-scan/terms/{action}")
	String topLevelScanTermsPost(@ModelAttribute("process") AggregationProcess process,
			@PathVariable("action") String action, @RequestParam(value = "id", required = false) String id,
			AggregationConfigForm form) {
		if (DEFAULT_ACTIONS.contains(action)) {
			doDefaultAction(process, action, id);
		} else {
			switch (action) {
				case "update-config" -> setParamsToProcess(process, form);
				default -> throw new IllegalArgumentException(STR."Unknown action: \{action}");
			}
		}
		return "redirect:/aggregation/top-level-scan/terms";
	}

	@PostMapping("top-level-scan/terms/next")
	String topLevelScanTermsNext(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 1) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.nextStep();
		return "redirect:/aggregation/top-level-scan/definitions";
	}
	//endregion

	//region definitions
	@GetMapping("top-level-scan/definitions")
	String topLevelScanDefinitions(Model model, @ModelAttribute("process") AggregationProcess process,
			DefinitionRequestForm form) {
		if (process.getStepNumber() != 2) {
			return "redirect:/aggregation/top-level-scan";
		}
		model.addAttribute("models", getModelNameList());
		model.addAttribute("form", form);
		model.addAttribute("terms", modelService.findElementByType(KnowledgeType.TERM)
												.stream().map(KnowledgeElement::getContent).toList());
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
		var location = setParamsToProcess(process, form);
		var terms = process.getTerms().getAcceptedElements().stream().map(KnowledgeElement::getContent);
		var allTerms = new LinkedList<>(form.getTerms());
		allTerms.addAll(terms.toList());
		var prompt = new DefinitionPrompt(process.getCurrentFragment(), allTerms,
				form.getFragmentLanguage(), form.getTargetLanguage());
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
			model.addAttribute("terms", modelService.findElementByType(KnowledgeType.TERM)
													.stream().map(KnowledgeElement::getContent).toList());
			model.addAttribute("structures", modelService.getStructureIds());
			return "aggregation/definitions";
		}
		return "redirect:/aggregation/top-level-scan/definitions";
	}

	@GetMapping("top-level-scan/definitions/edit")
	String topLevelScanDefinitionsEdit(Model model, @ModelAttribute("process") AggregationProcess process,
			@RequestParam("id") String id) {
		var definition = process.getDefinitions().findById(id);
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
		var definition = process.getDefinitions().findById(id);
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
		if (DEFAULT_ACTIONS.contains(action)) {
			doDefaultAction(process, action, id);
		} else {
			switch (action) {
				case "link" -> {
					var terms =
							process.getTerms().getAcceptedElements().stream().filter(it -> termIds.contains(it.getId()))
								   .toList();
					var mainTerm =
							process.getTerms().getAcceptedElements().stream()
								   .filter(it -> it.getId().equals(mainTermId))
								   .findFirst().orElseThrow();
					process.linkTermsToDefinition(id, mainTerm, terms);
				}
				case "unlink" -> {
					var terms =
							process.getTerms().getAcceptedElements().stream().filter(it -> termIds.contains(it.getId()))
								   .toList();
					process.unlinkTermsFromDefinition(id, terms);
				}
				default -> throw new IllegalArgumentException(STR."Unknown action: \{action}");
			}
		}
		return "redirect:/aggregation/top-level-scan/definitions";
	}

	@PostMapping("top-level-scan/definitions/next")
	String topLevelScanDefinitionsNext(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 2) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.nextStep();
		return "redirect:/aggregation/top-level-scan/examples";
	}

	@PostMapping("top-level-scan/definitions/back")
	String topLevelScanDefinitionsBack(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 2) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.previousStep();
		return "redirect:/aggregation/top-level-scan/terms";
	}
	//endregion

	//region examples

	@GetMapping("top-level-scan/examples")
	String topLevelScanExamples(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 3) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("acceptedExamples", process.getExamples().getAcceptedElements());
		model.addAttribute("suggestedExamples", process.getExamples().getSuggestedElements());
		model.addAttribute("exampleRelations",
				process.getRelations().stream().filter(it -> it.getType().equals(RelationType.EXAMPLE_FOR)).toList());
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/examples";
	}


	@PostMapping("top-level-scan/examples")
	String topLevelScanExamplesPost(Model model, @ModelAttribute("process") AggregationProcess process,
			AggregationConfigForm form) {
		if (process.getStepNumber() != 3) {
			return "redirect:/aggregation/top-level-scan";
		}
		var location = setParamsToProcess(process, form);
		var prompt = new ExamplePrompt(form.getFragment(),
				form.getFragmentLanguage(), form.getTargetLanguage());
		try {
			var answer = gptManager.requestExamples(prompt, form.getUrl(),
					modelListName.get(process.getModelName()), location, process.getModelParameters());
			var allTerms = new ArrayList<>(
					modelService.findElementByType(KnowledgeType.TERM).stream().map(it -> (Term) it).toList());
			allTerms.addAll(process.getTerms().getAcceptedElements());
			var extractor = new ExampleElementExtractor(allTerms);
			var examples = extractor.extractAll(answer, process.getModelLocation());
			examples.forEach(it -> it.setStructureId(process.getAreaOfKnowledge()));
			process.suggest(examples);
			for (var example : examples) {
				var termName = extractor.getTermFor(example);
				var term = process.getTerms().getAcceptedElements().stream()
								  .filter(it -> it.getContent().equals(termName))
								  .findFirst();
				term.ifPresent(it -> process.link(example, it, RelationType.EXAMPLE_FOR));
			}
			process.getRelations().addAll(extractor.getRelations());
			var newTerms = extractor.getNewTerms();
			process.add(newTerms);
			newTerms.forEach(it -> it.setStructureId(process.getAreaOfKnowledge()));

		} catch (ServerNotAvailableException | TimeoutException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("models", getModelNameList());
			model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
			model.addAttribute("acceptedExamples", process.getExamples().getAcceptedElements());
			model.addAttribute("suggestedExamples", process.getExamples().getSuggestedElements());
			model.addAttribute("exampleRelations",
					process.getRelations().stream().filter(it -> it.getType().equals(RelationType.EXAMPLE_FOR))
						   .toList());
			model.addAttribute("structures", modelService.getStructureIds());
			return "aggregation/examples";
		}
		return "redirect:/aggregation/top-level-scan/examples";
	}

	@PostMapping("top-level-scan/examples/{action}")
	String topLevelScanExampleActions(@ModelAttribute("process") AggregationProcess process,
			@PathVariable("action") String action, @RequestParam(value = "id", defaultValue = "NONE") String id) {
		return doDefaultAction(process, action, id);
	}

	@PostMapping("top-level-scan/examples/removeRelation")
	String topLevelScanExampleRemoveRelation(@ModelAttribute("process") AggregationProcess process,
			@RequestParam(value = "id", defaultValue = "NONE") UUID id) {
		var relation = process.getRelations().stream().filter(it -> it.getId().equals(id)).findFirst();
		relation.ifPresent(process::unlink);
		return "redirect:/aggregation/top-level-scan/examples";
	}

	@PostMapping("top-level-scan/examples/next")
	String topLevelScanExamplesNext(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 3) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.nextStep();
		return "redirect:/aggregation/top-level-scan/items";
	}

	@PostMapping("top-level-scan/examples/back")
	String topLevelScanExamplesBack(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 3) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.previousStep();
		return "redirect:/aggregation/top-level-scan/definitions";
	}
	//endregion

	//region items

	@GetMapping("top-level-scan/items")
	String topLevelScanTasks(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 4) {
			return "redirect:/aggregation/top-level-scan";
		}
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("acceptedItems", process.getItems().getAcceptedElements());
		model.addAttribute("suggestedItems", process.getItems().getSuggestedElements());
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/items";
	}

	@PostMapping("top-level-scan/items")
	String topLevelScanItemsPost(Model model, @ModelAttribute("process") AggregationProcess process,
			ItemRequestForm form) {
		if (process.getStepNumber() != 4) {
			return "redirect:/aggregation/top-level-scan";
		}
		var location = setParamsToProcess(process, form);
		var prompt = new ItemPrompt(form.getFragment(), form.getFragmentLanguage(), form.getTargetLanguage());
		try {
			var answer = gptManager.requestItems(prompt, form.getUrl(), modelListName.get(process.getModelName()),
					location, process.getModelParameters());
			var extractor = new ItemElementExtractor();
			var items = extractor.extractAll(answer, process.getModelLocation());
			items.forEach(it -> it.setStructureId(process.getAreaOfKnowledge()));
			process.suggest(items);
		} catch (ServerNotAvailableException | TimeoutException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("models", getModelNameList());
			model.addAttribute("form", form);
			model.addAttribute("structures", modelService.getStructureIds());
			return "aggregation/items";
		}
		return "redirect:/aggregation/top-level-scan/items";
	}

	@GetMapping("top-level-scan/items/add")
	String topLevelScanItemsAdd(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 4) {
			return "redirect:/aggregation/top-level-scan";
		}
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/item-add";
	}

	@PostMapping("top-level-scan/items/add")
	String topLevelScanItemsAddPost(@ModelAttribute("process") AggregationProcess process,
			AddElementForm form) {
		var item = createItem(form);
		item.setStructureId(form.getStructureId());
		process.add(List.of(item));
		return "redirect:/aggregation/top-level-scan/items";
	}


	private Item createItem(AddElementForm form) {
		// todo move to service to reduce complexity and duplication
		return switch (form.getItemType()) {
			case TRUE_FALSE -> new TrueFalseItem(form.getContent(), form.isTrue(),
					STR."\{form.getHeadline()}-TRUE_FALSE_ITEM");
			case SINGLE_CHOICE -> {
				var answers = new LinkedList<String>();
				answers.add(form.getCorrectAnswers().getFirst());
				answers.addAll(form.getWrongAnswers().stream().filter(it -> !it.isBlank()).map(String::strip)
								   .toList());
				yield new SingleChoiceItem(form.getContent(), answers,
						STR."\{form.getHeadline()}-SINGLE_CHOICE_ITEM");
			}
			case MULTIPLE_CHOICE -> {
				var answers = new LinkedList<>(form.getCorrectAnswers().stream().filter(it -> !it.isBlank())
												   .map(String::strip).toList());
				var correct = answers.size();
				answers.addAll(form.getWrongAnswers().stream().filter(it -> !it.isBlank()).toList());
				yield new MultipleChoiceItem(form.getContent(), answers, correct,
						STR."\{form.getHeadline()}-MULTIPLE_CHOICE_ITEM");
			}
			case FILL_OUT_BLANKS -> {
				var blanks = form.getBlanks().stream().filter(it -> !it.isBlank()).map(String::strip).toList();
				yield new FillOutBlanksItem(form.getContent(), blanks,
						STR."\{form.getHeadline()}-FILL_OUT_BLANKS_ITEM");
			}
			default -> throw new IllegalStateException(STR."Unexpected value: \{form.getItemType()}");
		};
	}

	@GetMapping("top-level-scan/items/edit")
	String topLevelScanItemsEdit(Model model, @ModelAttribute("process") AggregationProcess process,
			@RequestParam("id") String id) {
		var item = process.getItems().findById(id);
		model.addAttribute("item", item);
		model.addAttribute("structures", modelService.getStructureIds());
		return "aggregation/item-edit";
	}

	@PostMapping("top-level-scan/items/edit")
	String topLevelScanItemsEditPost(@ModelAttribute("process") AggregationProcess process,
			@RequestParam("id") String id, AddElementForm form) {
		var item = process.getItems().findById(id);
		switch (form.getItemType()) {
			case TRUE_FALSE -> {
				var trueFalseItem = (TrueFalseItem) item;
				trueFalseItem.setContent(form.getContent());
				trueFalseItem.setCorrect(form.isTrue());
			}
			case SINGLE_CHOICE -> {
				var singleChoiceItem = (SingleChoiceItem) item;
				singleChoiceItem.setContent(form.getContent());
				singleChoiceItem.setCorrectAnswer(form.getCorrectAnswers().getFirst());
				singleChoiceItem.setAlternativeAnswers(form.getWrongAnswers().stream().filter(it -> !it.isBlank())
														   .map(String::strip).toList());
			}
			case MULTIPLE_CHOICE -> {
				var multipleChoiceItem = (MultipleChoiceItem) item;
				multipleChoiceItem.setContent(form.getContent());
				var correct = form.getCorrectAnswers().stream().filter(it -> !it.isBlank()).map(String::strip).toList();
				multipleChoiceItem.setCorrectAnswers(correct);
				var alternative =
						form.getWrongAnswers().stream().filter(it -> !it.isBlank()).map(String::strip).toList();
				multipleChoiceItem.setAlternativeAnswers(alternative);
			}
			case FILL_OUT_BLANKS -> {
				var fillOutBlanksItem = (FillOutBlanksItem) item;
				fillOutBlanksItem.setContent(form.getContent());
				fillOutBlanksItem.setBlanks(form.getBlanks().stream().filter(it -> !it.isBlank()).toList());
			}
			default -> throw new IllegalStateException(STR."Unexpected value: \{form.getItemType()}");
		}
		item.setStructureId(form.getStructureId());
		return STR."redirect:edit?id=\{id}";
	}


	@PostMapping("top-level-scan/items/{action}")
	String topLevelScanItemsActions(@ModelAttribute("process") AggregationProcess process,
			@PathVariable("action") String action, @RequestParam(value = "id", defaultValue = "NONE") String id) {
		if (DEFAULT_ACTIONS.contains(action)) {
			return doDefaultAction(process, action, id);
		}
		throw new IllegalArgumentException(STR."Unknown action: \{action}");
	}

	private String doDefaultAction(AggregationProcess process, String action, String id) {
		switch (action) {
			case "remove" -> process.removeById(id);
			case "reject" -> process.rejectById(id);
			case "accept" -> process.approveById(id);
			default -> throw new IllegalArgumentException(STR."Unknown action: \{action}");
		}
		return "redirect:/aggregation/top-level-scan/examples";
	}

	@PostMapping("top-level-scan/items/next")
	String topLevelScanItemsNext(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 4) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.nextStep();
		process.nextStep(); //  Skip Code for now Todo add the code step
		return "redirect:/aggregation/integration";
	}

	@PostMapping("top-level-scan/items/back")
	String topLevelScanItemsBack(@ModelAttribute("process") AggregationProcess process) {
		if (process.getStepNumber() != 4) {
			return STR."redirect:/aggregation/top-level-scan/\{process.getStep().toLowerCase()}";
		}
		process.previousStep();
		return "redirect:/aggregation/top-level-scan/examples";
	}
	//endregion

	@GetMapping("compile")
	public String compile() {
		return "aggregation/compile";
	}

	@GetMapping("deep-scan")
	public String deepScan() {
		return "aggregation/deep-scan";
	}

	//region integration
	@GetMapping("integration")
	public String integration(Model model, @ModelAttribute("process") AggregationProcess process) {
		model.addAttribute("ready", !process.isComplete() && (process.hasElements() || process.hasRelations()));
		model.addAttribute("message", process.isComplete() ? "Der Prozess ist abgeschlossen" : "");
		return "aggregation/integration";
	}

	@PostMapping("integration")
	public String integrationPost(@ModelAttribute("process") AggregationProcess process,
			RedirectAttributes redirectAttributes) {
		var stat = modelService.integrate(process);
		process.setComplete();
		redirectAttributes.addFlashAttribute("message",
				STR."Es wurden \{stat.elements()} Elemente und \{stat.relations()} Relationen integriert");
		return "redirect:";
	}

	@PostMapping("integration/reset")
	public String integrationReset(Model model, @ModelAttribute("process") AggregationProcess process) {
		if (!process.isComplete()) {
			model.addAttribute("ready", !process.isComplete() && (process.hasElements() || process.hasRelations()));
			model.addAttribute("message", "Der Prozess ist noch nicht abgeschlossen");
			return "aggregation/integration";
		}
		process.reset();
		return "redirect:/aggregation/top-level-scan";
	}
	//endregion

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
}
