package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.knowledge.KnowledgeManagement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.finalization.FinalizationService;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.generation.generator.AssessmentGenerator;
import de.olivergeisel.materialgenerator.generation.generator.Generator;
import de.olivergeisel.materialgenerator.generation.generator.GeneratorInput;
import de.olivergeisel.materialgenerator.generation.generator.TransferGenerator;
import de.olivergeisel.materialgenerator.generation.material.MappingRepository;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static de.olivergeisel.materialgenerator.generation.TemplateSetService.PLAIN;

@Service
@Transactional
public class GeneratorService {

	private final KnowledgeManagement     knowledgeManagement;
	private final FinalizationService     finalizationService;
	private final TemplateSetRepository   templateSetRepository;
	private final MaterialRepository      materialRepository;
	private final MappingRepository       mappingRepository;

	public GeneratorService(KnowledgeManagement knowledgeManagement, FinalizationService finalizationService,
			TemplateSetRepository templateSetRepository, MaterialRepository materialRepository,
			MappingRepository mappingRepository) {
		this.knowledgeManagement = knowledgeManagement;
		this.finalizationService = finalizationService;
		this.templateSetRepository = templateSetRepository;
		this.materialRepository = materialRepository;
		this.mappingRepository = mappingRepository;
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
		var transferMaterials = createTransferMaterials(coursePlan, templateSet);
		var assessmentMaterials = createAssessmentMaterials(coursePlan, templateSet);
		var materials = new LinkedList<MaterialAndMapping>();
		materials.addAll(transferMaterials);
		materials.addAll(assessmentMaterials);
		return materials;
	}

	private List<MaterialAndMapping> createAssessmentMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		var input = new GeneratorInput(templateSet, knowledgeManagement.getKnowledge(), coursePlan);
		var generator = new AssessmentGenerator(input);
		return runGeneration(generator);
	}

	private LinkedList<MaterialAndMapping> createTransferMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		TransferGenerator generator = new TransferGenerator();
		generator.input(templateSet, knowledgeManagement.getKnowledge(), coursePlan);
		return runGeneration(generator);
	}

	private LinkedList<MaterialAndMapping> runGeneration(Generator generator) throws IllegalStateException {
		if (!generator.isReady()) {
			throw new IllegalStateException("Generator is not ready");
		}
		generator.update();
		var output = generator.output();
		var tempMaterials = output.getMaterialAndMapping();
		var materials = new LinkedList<MaterialAndMapping>();
		for (var toAdd : tempMaterials) { // Add only if not already in list (prevent duplicates - check by content)
			if (materials.stream().noneMatch(it -> it.material().isIdentical(toAdd.material()))) {
				materials.add(toAdd);
			}
		}
		// Save all materials and mappings and templateInfos
		materialRepository.saveAll(materials.stream().map(MaterialAndMapping::material).toList());
		mappingRepository.saveAll(materials.stream().map(MaterialAndMapping::mapping).toList());
		return materials;
	}

}
