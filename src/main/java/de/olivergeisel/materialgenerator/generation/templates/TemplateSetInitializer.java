package de.olivergeisel.materialgenerator.generation.templates;

import de.olivergeisel.materialgenerator.generation.TemplateSetService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Component
public class TemplateSetInitializer implements CommandLineRunner {

	private static final Set<String>            ignoredFiles  = Set.of("exclude", "include", "INCLUDE", "ignore",
			"help", "COURSE", "MATERIAL", "CHAPTER", "GROUP");
	private static final String                 TEMPLATE_PATH = "templateSets";
	private final        TemplateSetRepository  repository;

	public TemplateSetInitializer(TemplateSetRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... args) throws IllegalArgumentException, URISyntaxException {
		File templatePath;
		var baseURI = TemplateSetInitializer.class.getClassLoader().getResource(TEMPLATE_PATH);
		if (baseURI == null) {
			throw new IllegalArgumentException("Template path not found");
		}
		templatePath = new File(baseURI.toURI());
		for (File file : templatePath.listFiles()) {
			if (file.isDirectory()) {
				var tempSet = new TemplateSet(file.getName());
				tempSet.addAllTemplates(getExtraTemplates(file));
				repository.save(tempSet);
			}
		}
	}

	private TemplateType[] getExtraTemplates(File templatePath) {
		var extraTemplates = Arrays.stream(Objects.requireNonNull(templatePath.listFiles())).filter(it -> {
			var name = it.getName().replace(".html", "").toUpperCase();
			return !ignoredFiles.contains(name) && TemplateSetService.MINIMAL_SUPPORTED_TEMPLATES.contains(name);
		}).toList();
		return extraTemplates.stream().map(it -> {
			var name = it.getName().replace(".html", "").toUpperCase();
			return new TemplateType(name);
		}).toArray(TemplateType[]::new);
	}
}
