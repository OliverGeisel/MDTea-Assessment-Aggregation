package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.aggregation.KnowledgeManagement;
import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.finalization.FinalizationService;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.generation.generator.TranslateGenerator;
import de.olivergeisel.materialgenerator.generation.material.MappingRepository;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSetRepository;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.BasicTemplateRepository;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static de.olivergeisel.materialgenerator.generation.TemplateService.PLAIN;

@Service
@Transactional
public class GeneratorService {

	private final KnowledgeManagement     knowledgeManagement;
	private final FinalizationService     finalizationService;
	private final TemplateSetRepository   templateSetRepository;
	private final MaterialRepository      materialRepository;
	private final MappingRepository       mappingRepository;
	private final TemplateInfoRepository  templateInfoRepository;
	private final BasicTemplateRepository basicTemplateRepository;

	public GeneratorService(KnowledgeManagement knowledgeManagement, FinalizationService finalizationService,
			TemplateSetRepository templateSetRepository, MaterialRepository materialRepository,
			MappingRepository mappingRepository, TemplateInfoRepository templateInfoRepository,
			BasicTemplateRepository basicTemplateRepository) {
		this.knowledgeManagement = knowledgeManagement;
		this.finalizationService = finalizationService;
		this.templateSetRepository = templateSetRepository;
		this.materialRepository = materialRepository;
		this.mappingRepository = mappingRepository;
		this.templateInfoRepository = templateInfoRepository;
		this.basicTemplateRepository = basicTemplateRepository;
	}

	public Set<KnowledgeElement> getMaterials(String term) {
		return knowledgeManagement.findRelatedData(term);
	}

	public RawCourse generateRawCourse(CoursePlan coursePlan, String template) {
		if (templateSetRepository.findByName(template).isEmpty()) {
			template = PLAIN;
		}
		var templateSet = templateSetRepository.findByName(template).orElseThrow();
		var materials = createMaterials(coursePlan, templateSet);
		return finalizationService.createRawCourse(coursePlan, template, materials);
	}

	private List<MaterialAndMapping> createMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		TranslateGenerator generator = new TranslateGenerator();
		generator.setBasicTemplateInfo(basicTemplateRepository.findAll().toSet());
		generator.input(templateSet, knowledgeManagement.getKnowledge(), coursePlan);
		if (generator.isReady()) {
			generator.update();
		}
		var output = generator.output();
		var tempmaterials = output.getMaterialAndMapping();
		var materials = new LinkedList<MaterialAndMapping>();
		for (var toAdd : tempmaterials) {
			if (materials.stream().noneMatch(it -> it.material().isIdentical(toAdd.material()))) {
				materials.add(toAdd);
			}
		}
		templateInfoRepository.saveAll(materials.stream().map(it -> it.material().getTemplateInfo()).toList());
		materialRepository.saveAll(materials.stream().map(MaterialAndMapping::material).toList());
		mappingRepository.saveAll(materials.stream().map(MaterialAndMapping::mapping).toList());
		return materials;
	}

}
