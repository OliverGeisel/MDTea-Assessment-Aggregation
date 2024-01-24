package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Manager;
import de.olivergeisel.materialgenerator.aggregation.extraction.GPT_Request;
import de.olivergeisel.materialgenerator.aggregation.extraction.ServerNotAvailableException;
import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.TermElementExtractor;
import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.TermPrompt;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModelService;
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

	private static final Logger              logger        = LoggerFactory.getLogger(AggregationController.class);
	private static final Map<String, String> modelListName = Map.of("Mistral OpenOrca", "mistral-7b-openorca.Q4_0.gguf",
			"Mistral Instruct", "mistral-7b-instruct-v0.1.Q4_0.gguf",
			"GPT4All Falcon", "gpt4all-falcon-newbpe-q4_0.gguf",
			"Hermes", "nous-hermes-llama2-13b.Q4_0.gguf",
			"Mistral german", "em_german_mistral_v01.Q4_0.gguf");
	private static final String ZUFALL = "ZUFALL";

	private final GPT_Manager gptManager;
	private final KnowledgeModelService modelService;

	public AggregationController(GPT_Manager gptManager, KnowledgeModelService modelService) {
		this.gptManager = gptManager;
		this.modelService = modelService;
	}

	@GetMapping("")
	public String index(Model model) {
		var elements = modelService.elementCount();
		var relations = modelService.relationCount();
		var terms = modelService.termCount();
		var statistik = new Aggregation(elements, relations, terms, 0, 0);
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
		return "aggregation/top-level-scan";
	}

	@PostMapping("top-level-scan")
	String topLevelScanPost(Model model, AggregationConfigForm form,
			@ModelAttribute("process") AggregationProcess process) {
		if (process.getSources().isEmpty()) {
			process.setCurrentFragment(form.getFragment());
		}
		process.setApiKey(form.getApiKey());
		if (form.getModelName().equals(ZUFALL)) {
			process.setModelName(modelListName.keySet().stream().toList().get(new Random().nextInt(1,
					modelListName.size() - 1)));
		} else {
			process.setModelName(form.getModelName());
		}
		var location = GPT_Request.ModelLocation.valueOf(form.getConnectionType().toUpperCase());
		process.setModelLocation(location);
		var prompt = new TermPrompt(form.getFragment());
		try {
			var answer = gptManager.requestTerms(prompt, form.getUrl(),
					modelListName.get(process.getModelName()), location,
					form.getMaxTokens(), form.getTemperature(), form.getTopP(), 0.2, form.getRetries());
			var termExtractor = new TermElementExtractor();
			var terms = termExtractor.extractAll(answer, process.getModelLocation());
			process.suggest(terms);
		} catch (ServerNotAvailableException | TimeoutException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("models", getModelNameList());
			model.addAttribute("form", form);
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
			return "redirect:/aggregation/top-level-scan";
		}
		model.addAttribute("models", getModelNameList());
		model.addAttribute("acceptedTerms", process.getTerms().getAcceptedElements());
		model.addAttribute("suggestedTerms", process.getTerms().getSuggestedElements());
		return "aggregation/terms";
	}

	@PostMapping("top-level-scan/terms/{action}")
	String topLevelScanTermsPost(@ModelAttribute("process") AggregationProcess process,
			@PathVariable("action") String action, @RequestParam("id") String id) {
		switch (action) {
			case "remove" -> process.removeById(id);
			case "reject" -> process.rejectById(id);
			case "accept" -> process.approveById(id);
			default -> throw new IllegalArgumentException("Unknown action: " + action);
		}

		return "redirect:/aggregation/top-level-scan/terms";
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
	public String integration() {
		return "aggregation/integration";
	}

	@PostMapping("complete")
	public String complete(@ModelAttribute("process") AggregationProcess process) {
		process.setComplete();
		return "aggregation/complete";
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
