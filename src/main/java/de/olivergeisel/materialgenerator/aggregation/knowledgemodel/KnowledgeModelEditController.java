package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.forms.AddElementForm;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeLeaf;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
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

	@ResponseBody
	@GetMapping("elements/json")
	public ResponseEntity<List<KnowledgeElement>> getElementsForStructure(@RequestParam("id") String id) {
		return new ResponseEntity<>(knowledgeModelService.getElementsForStructure(id),
				HttpStatus.OK);
	}

	@GetMapping("/elements")
	public String getElements(Model model) {
		model.addAttribute("elements", knowledgeModelService.getAllElements());
		return "knowledgeModel/elements";
	}

	@GetMapping("/elements/add")
	public String addElement(Model model, AddElementForm form) {
		model.addAttribute("form", form);
		model.addAttribute("knowledgeTypes", KnowledgeType.values());
		model.addAttribute("structures", knowledgeModelService.getStructureIds());
		return "knowledgeModel/element-add";
	}

	@PostMapping("/elements/add")
	public String addElementPost(@Valid AddElementForm form, RedirectAttributes redirectAttributes) {
		knowledgeModelService.addElement(form);
		redirectAttributes.addFlashAttribute("added", "Element added successfully!");
		return "redirect:/knowledge-model/elements";
	}


	@GetMapping("/elements/edit")
	public String editElement(Model model, @RequestParam("id") String id) {
		model.addAttribute("element", knowledgeModelService.findElementById(id).orElseThrow());
		return "knowledgeModel/element-edit";
	}

	@GetMapping("/elements/delete")
	public String deleteElement(Model model, @RequestParam("id") String id) {
		knowledgeModelService.deleteElement(id);
		return "redirect:/knowledge-model/elements";
	}

	@GetMapping("/elements/link")
	public String linkElement(Model model) {
		model.addAttribute("elements", knowledgeModelService.getAllElements());
		model.addAttribute("relationTyps", RelationType.values());
		return "knowledgeModel/element-linking";
	}

	@PostMapping("/elements/link")
	public String linkElement(@RequestParam("from") String elementId,
			@RequestParam("relationType") RelationType relationType, @RequestParam("to") String linkedElementId) {
		knowledgeModelService.linkElements(elementId, relationType, linkedElementId);
		return "redirect:/knowledge-model/elements";
	}

	@GetMapping("/relations")
	public String getRelations(Model model) {
		model.addAttribute("relations", knowledgeModelService.getAllRelations());
		return "knowledgeModel/relations";
	}

	@GetMapping("/structure")
	public String getStructure(Model model) {
		model.addAttribute("structure", knowledgeModelService.getAllStructureElements());
		return "knowledgeModel/structure";
	}

	@PostMapping("/structure/add")
	public String addStructureObject(@RequestParam("id") String newStructureId,
			@RequestParam("parent") String parentId,
			@RequestParam(value = "leaf", defaultValue = "true") boolean leaf,
			@RequestParam("withTerm") boolean withTerm) {

		var parent = knowledgeModelService.findStructureById(parentId).orElseThrow();

		if (!(parent instanceof KnowledgeFragment fragment)) {
			throw new IllegalArgumentException("Cannot add a structure to a leaf");
		}
		KnowledgeObject newObject = leaf ? new KnowledgeLeaf(newStructureId) : new KnowledgeFragment(newStructureId);
		knowledgeModelService.addStructureTo(fragment, newObject);
		if (withTerm) {
			var form = new AddElementForm(newStructureId, "", newStructureId, KnowledgeType.TERM, null, "false");
			knowledgeModelService.addElement(form);
		}
		return "redirect:/knowledge-model/structure";
	}

}
