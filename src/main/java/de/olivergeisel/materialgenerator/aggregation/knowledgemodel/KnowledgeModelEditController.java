package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/knowledge-model")
public class KnowledgeModelEditController {

	private final KnowledgeModelService knowledgeModelService;


	public KnowledgeModelEditController(KnowledgeModelService knowledgeModelService) {
		this.knowledgeModelService = knowledgeModelService;
	}

	@GetMapping("")
	public String getKnowledgeModel(Model model, @RequestParam(required = false) String id, @RequestParam(value =
			"depth", required = false, defaultValue = "4") int depth) {
		if (id != null) {
			try {
				model.addAttribute("rootElement", knowledgeModelService.getKnowledgeObject(id).orElseThrow());
			} catch (NoSuchElementException e) {
				model.addAttribute("error", STR."No element with id \{id} found");
				model.addAttribute("rootElement", knowledgeModelService.getRoot());
			}
		} else {
			model.addAttribute("rootElement", knowledgeModelService.getRoot());
		}
		model.addAttribute("maxDepth", Math.max(depth, 1));

		return "knowledgeModel/overview";
	}
}
