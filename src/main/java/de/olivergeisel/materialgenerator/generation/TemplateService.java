package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.generation.templates.BasicTemplates;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSetRepository;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.ExtraTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
public class TemplateService {

	public static final  String       PLAIN                              = "blank";
	public static final  String       ILLUSTRATED                        = "color";
	public static final  List<String> OPTIONS                            = List.of(PLAIN, ILLUSTRATED);
	public static final  String       TEMPLATE_SET_FROM_TEMPLATES_FOLDER = "../templateSets/";
	private static final String       TEMPLATE_SET_PATH                  = "src/main/resources/templateSets/";
	private static final String       HTML                               = ".html";

	private final TemplateSetRepository repository;

	public TemplateService(TemplateSetRepository repository) {
		this.repository = repository;
	}

	public TemplateSet createTemplateSet(String templateSetName) {
		File newFolder = new File(TEMPLATE_SET_PATH + templateSetName);
		if (newFolder.exists()) {
			throw new IllegalArgumentException("TemplateSet with name " + templateSetName + " already exists");
		}
		newFolder.mkdir();
		return null;
	}

	public boolean deleteTemplateSet(String id) {
		File dir = new File(TEMPLATE_SET_PATH + id);
		if (dir.exists()) {
			try {
				Files.delete(dir.toPath());
			} catch (IOException ignored) {
				return false;
			}
		}
		return true;
	}

	public TemplateSet getTemplateSet(String name) {
		return repository.findByName(name).orElseThrow();
	}

	private ExtraTemplate loadTemplate(File file) {
		var typeString = file.getName().replace(HTML, "").toUpperCase();
		TemplateType type = TemplateType.valueOf(typeString);
		return new ExtraTemplate(type, typeString);
	}

	private TemplateSet loadTemplateSet(File dir) {
		TemplateSet templateSet = new TemplateSet();
		templateSet.setName(dir.getName());
		var extraTemplates = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
								   .filter(file -> !BasicTemplates.TEMPLATES.contains(
										   file.getName().replace(HTML, "").toUpperCase())).toList();
		for (File file : extraTemplates) {
			if (file.isDirectory()) {
				templateSet.addAllTemplates(loadExtraTemplates(file).toArray(new TemplateInfo[0]));
			} else {
				templateSet.addTemplate(loadTemplate(file));
			}
		}
		return templateSet;
	}

	private Set<? extends TemplateInfo> loadExtraTemplates(File dir) {
		Set<TemplateInfo> templateSet = new HashSet<>();
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				templateSet.addAll(loadExtraTemplates(file));
			} else {
				templateSet.add(loadTemplate(file));
			}
		}
		return templateSet;
	}
}
