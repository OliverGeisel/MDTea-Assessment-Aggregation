package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModelService;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.finalization.FinalizationService;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.generator.AssessmentGenerator;
import de.olivergeisel.materialgenerator.generation.generator.Generator;
import de.olivergeisel.materialgenerator.generation.generator.GeneratorInput;
import de.olivergeisel.materialgenerator.generation.generator.TransferGenerator;
import de.olivergeisel.materialgenerator.generation.material.MappingRepository;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static de.olivergeisel.materialgenerator.generation.TemplateSetService.PLAIN;


/**
 * Service to create new {@link Material}s for a new {@link RawCourse}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @see RawCourse
 * @since 0.2.0
 */
@Service
@Transactional
public class GeneratorService {

	private final KnowledgeModelService knowledgeManagement;
	private final FinalizationService   finalizationService;
	private final TemplateSetRepository templateSetRepository;
	private final MaterialRepository    materialRepository;
	private final MappingRepository     mappingRepository;

	public GeneratorService(KnowledgeModelService knowledgeManagement, FinalizationService finalizationService,
			TemplateSetRepository templateSetRepository, MaterialRepository materialRepository,
			MappingRepository mappingRepository) {
		this.knowledgeManagement = knowledgeManagement;
		this.finalizationService = finalizationService;
		this.templateSetRepository = templateSetRepository;
		this.materialRepository = materialRepository;
		this.mappingRepository = mappingRepository;
	}

	/**
	 * Generate a new {@link RawCourse} from the given {@link CoursePlan} and the given template.
	 *
	 * @param coursePlan the {@link CoursePlan} to generate the {@link RawCourse} from
	 * @param template   the template to use for the generation
	 * @return the generated {@link RawCourse}
	 */
	public RawCourse generateRawCourse(CoursePlan coursePlan, String template) {
		if (templateSetRepository.findByName(template).isEmpty()) {
			template = PLAIN;
		}
		var templateSet = templateSetRepository.findByName(template).orElseThrow();
		var materials = createMaterials(coursePlan, templateSet);
		return finalizationService.createRawCourse(coursePlan, template, materials);
	}

	/**
	 * Create the materials for the given {@link CoursePlan}. The materials are created from the given
	 * {@link KnowledgeModel}.
	 *
	 * @param coursePlan  the {@link CoursePlan} to create the materials for
	 * @param templateSet the template set to use for the generation
	 * @return the created materials
	 */
	private List<MaterialAndMapping> createMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		var transferMaterials = createTransferMaterials(coursePlan, templateSet);
		var assessmentMaterials = createAssessmentMaterials(coursePlan, templateSet);
		var materials = new LinkedList<MaterialAndMapping>();
		materials.addAll(transferMaterials);
		materials.addAll(assessmentMaterials);
		return materials;
	}

	/**
	 * Create the assessment materials for the given {@link CoursePlan}.
	 *
	 * @param coursePlan  the {@link CoursePlan} to create the materials for
	 * @param templateSet the template set to use for the generation
	 * @return the created assessment materials
	 */
	private List<MaterialAndMapping> createAssessmentMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		var input = new GeneratorInput(templateSet, knowledgeManagement, coursePlan);
		var generator = new AssessmentGenerator(input);
		return runGeneration(generator);
	}

	/**
	 * Create the transfer materials for the given {@link CoursePlan}.
	 *
	 * @param coursePlan  the {@link CoursePlan} to create the materials for
	 * @param templateSet the template set to use for the generation
	 * @return the created transfer materials
	 */
	private LinkedList<MaterialAndMapping> createTransferMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		TransferGenerator generator = new TransferGenerator();
		generator.input(templateSet, knowledgeManagement, coursePlan);
		return runGeneration(generator);
	}

	/**
	 * Internal method to run the generation of materials for a given generator. Save all materials and mappings to
	 * the database.
	 *
	 * @param generator the generator to use for the generation
	 * @return the generated materials
	 * @throws IllegalStateException if the generator is not ready
	 */
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
