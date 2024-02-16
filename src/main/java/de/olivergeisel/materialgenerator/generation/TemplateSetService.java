package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSetRepository;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * A Service to manage the {@link TemplateSet}s.
 */
@Service
public class TemplateSetService {

	public static final List<String> MINIMAL_SUPPORTED_TEMPLATES = List.of("ACRONYM", "CODE", "DEFINITION", "EXAMPLE",
			"IMAGE", "LIST", "SUMMARY", "SYNONYM", "TASK", "TEXT");

	public static final  String       PLAIN                              = "blank";
	public static final  String       ILLUSTRATED                        = "color";
	public static final  List<String> OPTIONS                            = List.of(PLAIN, ILLUSTRATED);
	public static final  String       TEMPLATE_SET_FROM_TEMPLATES_FOLDER = "../templateSets/";
	private static final String       TEMPLATE_SET_PATH                  = "src/main/resources/templateSets/";
	private static final String       HTML                               = ".html";

	private final TemplateSetRepository repository;

	public TemplateSetService(TemplateSetRepository repository) {
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

	/**
	 * Load a specific {@link TemplateType} from a file.
	 *
	 * @param file The file to load the {@link TemplateType} from.
	 * @return
	 */
	private TemplateType loadTemplate(File file) {
		var templateTypeString = file.getName().replace(HTML, "").toUpperCase();
		return TemplateType.valueOf(templateTypeString);
	}

	/**
	 * Loads the Templates for a {@link TemplateSet} from a directory.
	 * @param dir The directory to load the templates from.
	 * @return The loaded {@link TemplateSet}.
	 */
	private TemplateSet loadTemplateSet(File dir) {
		TemplateSet templateSet = new TemplateSet();
		templateSet.setName(dir.getName());
		var extraTemplates = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
								   .filter(file -> !MINIMAL_SUPPORTED_TEMPLATES.contains(
										   file.getName().replace(HTML, "").toUpperCase())).toList();
		for (File file : extraTemplates) {
			if (file.isDirectory()) {
				templateSet.addAllTemplates(loadExtraTemplates(file).toArray(new TemplateType[0]));
			} else {
				templateSet.addTemplate(loadTemplate(file));
			}
		}
		return templateSet;
	}

	/**
	 * Tem
	 *
	 * @param dir
	 * @return
	 */
	private Set<TemplateType> loadExtraTemplates(File dir) {
		Set<TemplateType> templateSet = new HashSet<>();
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
