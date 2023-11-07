package de.olivergeisel.materialgenerator.generation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/templates")
public class TemplateController {

	private static final String PATH = "template_module/";

	private final TemplateService templateService;


	public TemplateController(TemplateService templateService) {
		this.templateService = templateService;
	}

	@GetMapping("detail/{id}")
	public String getTemplateDetail(@PathVariable("id") String id, Model model) {
		model.addAttribute("template", templateService.getTemplateSet(id));
		return PATH + "detail";
	}

	@PostMapping("upload")
	public String uploadTemplate() {
		return PATH + "upload";
	}

	//region setter/getter
	@GetMapping({"/", ""})
	public String getTemplate() {
		return PATH + "index";
	}
//endregion
}
