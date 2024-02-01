package de.olivergeisel.materialgenerator.generation.templates;

import de.olivergeisel.materialgenerator.generation.templates.template_infos.ExtraTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfoRepository;
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
	private final        TemplateInfoRepository templateInfoRepository;

	public TemplateSetInitializer(TemplateSetRepository repository, TemplateInfoRepository templateInfoRepository) {
		this.repository = repository;
		this.templateInfoRepository = templateInfoRepository;
	}

	private ExtraTemplate[] getExtraTemplates(File templatePath) {
		var extraTemplates = Arrays.stream(Objects.requireNonNull(templatePath.listFiles())).filter(it -> {
			var name = it.getName().replace(".html", "").toUpperCase();
			return !ignoredFiles.contains(name) && !BasicTemplates.TEMPLATES.contains(name);
		}).toList();
		return extraTemplates.stream().map(it -> {
			var name = it.getName().replace(".html", "").toUpperCase();
			return new ExtraTemplate(new TemplateType(name), it.getName());
		}).toArray(ExtraTemplate[]::new);
	}

	private void saveBasicTemplates() {
		var basicTemplates = BasicTemplates.getInstance();
		templateInfoRepository.saveAll(Arrays.stream(basicTemplates.getTemplates()).toList());
	}

	@Override
	public void run(String... args) throws IllegalArgumentException, URISyntaxException {
		File templatePath;
		saveBasicTemplates();
		var baseURI = TemplateSetInitializer.class.getClassLoader().getResource(TEMPLATE_PATH);
		if (baseURI == null) {
			throw new IllegalArgumentException("Template path not found");
		}
		templatePath = new File(baseURI.toURI());
		for (File file : templatePath.listFiles()) {
			if (file.isDirectory()) {
				var tempSet = new TemplateSet(file.getName());
				tempSet.addAllTemplates(getExtraTemplates(file));
				templateInfoRepository.saveAll(tempSet.getExtraTemplates());
				repository.save(tempSet);
			}
		}
	}
}
