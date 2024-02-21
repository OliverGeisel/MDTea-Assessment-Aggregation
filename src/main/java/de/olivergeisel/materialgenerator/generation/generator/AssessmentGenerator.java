package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Item;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.SingleChoiceItem;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.TrueFalseItem;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.ItemConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.SingleChoiceConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.generator.item_exctraction.TrueFalseExtractor;
import de.olivergeisel.materialgenerator.generation.generator.test_assamble.TestAssembler;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import de.olivergeisel.materialgenerator.generation.material.assessment.SingleChoiceItemMaterial;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import de.olivergeisel.materialgenerator.generation.material.assessment.TrueFalseItemMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


/**
 * This class is the main class for the generation of assessment materials. It implements the {@link Generator}
 * interface.
 * <p>
 * This Generator generate Questions and Tasks for the given {@link CoursePlan} and {@link KnowledgeModel}.
 * The Types of the Questions/Tasks are:
 *  <ul>
 *      <li>True/False</li>
 *      <li>Single Choice</li>
 *      <li>Multiple Choice</li>
 *      <li>Fill in the Blanks</li>
 *      <li>Assignments</li>
 *  </ul>
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Generator
 * @see GeneratorInput
 * @see GeneratorOutput
 * @see TemplateSet
 * @see KnowledgeModel
 * @see CoursePlan
 * @see Material
 * @since 1.1.0
 */
public class AssessmentGenerator extends AbstractGenerator {

	private static final Logger logger = LoggerFactory.getLogger(AssessmentGenerator.class);

	public AssessmentGenerator() {
		super();
	}

	public AssessmentGenerator(GeneratorInput input) {
		super(input);
	}

	private Set<Item> getLinkedItems(Set<KnowledgeNode> knowledge) {
		return knowledge.stream()
						.flatMap(it -> Arrays.stream(it.getLinkedElements()))
						.filter(it -> it instanceof Item)
						.map(it -> (Item) it)
						.collect(Collectors.toSet());
	}

	@Override
	protected List<MaterialAndMapping> materialForComment(Set<KnowledgeNode> knowledge) {
		return materialForCreate(knowledge);
	}

	@Override
	protected List<MaterialAndMapping> materialForCreate(Set<KnowledgeNode> knowledge) {
		return materialForControl(knowledge);
	}

	@Override
	protected List<MaterialAndMapping> materialForControl(Set<KnowledgeNode> knowledge) {
		return materialForUse(knowledge);
	}

	@Override
	protected List<MaterialAndMapping> materialForUse(Set<KnowledgeNode> knowledge) {
		return materialForTranslate(knowledge);
	}

	@Override
	protected List<MaterialAndMapping> materialForTranslate(Set<KnowledgeNode> knowledge) {
		// materials.add(createWikisWithExistingMaterial(knowledge, materials));
		return materialForKnow(knowledge);
	}

	@Override
	protected List<MaterialAndMapping> materialForKnow(Set<KnowledgeNode> knowledge) {
		return materialForFirstLook(knowledge);
	}

	@Override
	protected List<MaterialAndMapping> materialForFirstLook(Set<KnowledgeNode> knowledge)
			throws NoSuchElementException {

		var materials = new LinkedList<>(createTrueFalse(knowledge));
		var singleChoice =
				createSingleChoice(knowledge, plan.getTestConfiguration().getConfiguration(ItemType.SINGLE_CHOICE));
		/*var multipleChoice = createMultipleChoice(knowledge, plan.getTestConfiguration());
		var fillOut = createFillOut(knowledge, plan.getTestConfiguration());*/

		materials.addAll(singleChoice);
		// tests
		var tests = createTests(knowledge, materials, plan.getTestConfiguration());
		materials.addAll(tests);
		return materials;
	}

	/**
	 * This method creates the materials for the given knowledge. It creates the following materials:
	 * <ul>
	 *     <li>Fill Out the blanks</li>
	 * </ul>
	 * <h3>Example</h3>
	 * "MDTea has the four phases: <b>Aggregation</b>, |_____________|, <b>Finalization</b> and <b>Synchronization</b>"
	 * <br>
	 * In the blank field the correct answer hast to be filled in.
	 *
	 * @param knowledge The knowledge to create the materials for.
	 * @return A list of materials with the questions.
	 */
	public List<MaterialAndMapping> createFillOut(Set<KnowledgeNode> knowledge) {
		var materials = new LinkedList<MaterialAndMapping>();

		// Todo implement

		return materials;
	}

	/**
	 * This method creates the True/False-Questions for the given knowledge.
	 * <h3>Examples</h3>
	 * <h4>1. Example</h4>
	 * "MDTea has the four phases: <b>Aggregation</b>, <u><b>Disaggregation</b></u>, <b>Finalization</b> and
	 * <b>Synchronization</b>" (False)
	 * <h4>2. Example</h4>
	 * "switch is a keyword in Java" (True)
	 * The student has to decide if the statement is true or false.
	 *
	 * @param knowledge The knowledge to create the True/False-questions for.
	 * @return A list of materials with the questions.
	 */
	public List<MaterialAndMapping> createTrueFalse(Set<KnowledgeNode> knowledge) {
		var materials = new LinkedList<MaterialAndMapping>();
		// collect - get all questions from knowledge
		final var templateInfo = TemplateType.ITEM;
		var firstNode = knowledge.stream().findFirst().orElseThrow();
		var termNode = getTermNode(knowledge, firstNode);
		var structure = termNode.getStructurePoint();
		var mainTerm = termNode.getMainElement();
		// If an Item is not connected to a Term, then find here
		var items = getLinkedItems(knowledge);
		items = items.stream().filter(it -> it instanceof TrueFalseItem).collect(Collectors.toSet());
		for (var item : items) {
			var trueFalseItem = (TrueFalseItem) item;
			createTrueFalseInner(materials, structure, mainTerm, trueFalseItem, mainTerm.getStructureId());
		}
		// Items in relations to the main term
		var relationsWithQuestion = getWantedRelationsKnowledge(knowledge, RelationType.RELATED);
		relationsWithQuestion.forEach(it -> {
			if (!(it.getTo() instanceof TrueFalseItem item)) {
				return;
			}
			var term = it.getFrom();
			if (ItemType.TRUE_FALSE.equals(item.getItemType())) {
				createTrueFalseInner(materials, structure, term, item, mainTerm.getStructureId());
			}
		});

		// extraction - find extra questions
		var extractor = new TrueFalseExtractor();
		// todo get Tasks, that are stored in knowledge (added while aggregation)
		for (var node : knowledge) {
			var questions = extractor.extract(node, templateInfo);
			materials.addAll(questions);
		}

		return materials;
	}

	/**
	 * Create a True/False-Question for the given item and add it to the given materials.
	 *
	 * @param materials   The materials to add the question to.
	 * @param structure   The structure of the knowledge.
	 * @param mainTerm    Term the question is related to.
	 * @param item        The item to create the question for.
	 * @param structureId The id of the structure.
	 */
	private void createTrueFalseInner(LinkedList<MaterialAndMapping> materials, KnowledgeObject structure,
			KnowledgeElement mainTerm, TrueFalseItem item, String structureId) {
		try {
			String name = getUniqueMaterialName(materials, STR."Frage zu \{mainTerm.getContent()}", item.getId());
			var question = item.getQuestion();
			var isTrue = item.isCorrect();
			var material = new TrueFalseItemMaterial(question, isTrue);
			material.setStructureId(structure.getId());
			var mappingEntry = new MaterialMappingEntry(material, item, mainTerm);
			var mapping = new MaterialAndMapping(material, mappingEntry);
			mapping.material().setStructureId(structureId);
			materials.add(mapping);
		} catch (Exception e) {
			logger.error("Error while creating material for term {}", mainTerm.getContent(), e);
		}
	}


	/**
	 * This method creates the Single-Choice-Questions for the given knowledge.
	 * <h3>Examples</h3>
	 * A single choice question is a question with a question and multiple answers. Only one answer is correct.
	 * <h4>1. Example</h4>
	 * "What is the capital of Germany?"
	 * <ul>
	 *     <li>Berlin</li>
	 *     <li>Paris</li>
	 *     <li>London</li>
	 *     <li>Madrid</li>
	 *     <li>Rome</li>
	 * </ul>
	 *
	 * @param knowledge     The knowledge to create the Single-Choice-questions for.
	 * @param configuration The configuration for the Single-Choice-questions.
	 * @return A list of materials with the questions.
	 * @throws IllegalArgumentException If the configuration is not for Single-Choice-questions.
	 */
	public List<MaterialAndMapping> createSingleChoice(Set<KnowledgeNode> knowledge, ItemConfiguration configuration)
			throws IllegalArgumentException {
		if (configuration == null || !configuration.getForItemType().equals(ItemType.SINGLE_CHOICE)) {
			throw new IllegalArgumentException("Configuration is not for SingleChoice");
		}
		var singleChoiceConfiguration = (SingleChoiceConfiguration) configuration;
		var materials = new LinkedList<MaterialAndMapping>();
		// collect - get all questions from knowledge
		final var templateInfo = TemplateType.ITEM;
		var firstNode = knowledge.stream().findFirst().orElseThrow();
		var termNode = getTermNode(knowledge, firstNode);
		var structure = termNode.getStructurePoint();
		var mainTerm = termNode.getMainElement();
		// If an Item is not connected to a Term, then find here
		var items = getLinkedItems(knowledge);
		items = items.stream().filter(it -> it instanceof SingleChoiceItem).collect(Collectors.toSet());
		for (var item : items) {
			var singleChoiceItem = (SingleChoiceItem) item;
			createSingleChoiceInner(materials, structure, mainTerm, singleChoiceItem, mainTerm.getStructureId(),
					singleChoiceConfiguration);
		}
		// Items in relations to the main term
		var relationsWithQuestion = getWantedRelationsKnowledge(knowledge, RelationType.RELATED);
		relationsWithQuestion.forEach(it -> {
			if (!(it.getTo() instanceof SingleChoiceItem item)) {
				return;
			}
			var term = it.getFrom();
			if (ItemType.SINGLE_CHOICE.equals(item.getItemType())) {
				createSingleChoiceInner(materials, structure, term, item, mainTerm.getStructureId(),
						singleChoiceConfiguration);
			}
		});

		// todo extraction - find extra questions
		return materials;
	}

	private void createSingleChoiceInner(LinkedList<MaterialAndMapping> materials, KnowledgeObject structure,
			KnowledgeElement mainTerm, SingleChoiceItem item, String structureId,
			SingleChoiceConfiguration configuration) {
		try {
			String name = getUniqueMaterialName(materials, STR."Frage zu \{mainTerm.getContent()}", item.getId());
			var question = item.getContent();
			var correct = item.getCorrectAnswer();
			var alternatives = item.getAlternativeAnswers();
			var material = new SingleChoiceItemMaterial(question, correct, alternatives, configuration.clone());
			material.setStructureId(structure.getId());
			var mappingEntry = new MaterialMappingEntry(material, item, mainTerm);
			var mapping = new MaterialAndMapping(material, mappingEntry);
			mapping.material().setStructureId(structureId);
			materials.add(mapping);
		} catch (Exception e) {
			logger.error("Error while creating single choice material for term {}", mainTerm.getContent(), e);
		}
	}

	public List<MaterialAndMapping> createMultipleChoice(Set<KnowledgeNode> knowledge) {
		var materials = new LinkedList<MaterialAndMapping>();
		// Todo implement
		return materials;
	}

	/**
	 * Creates a test for the given knowledge. The test contains the following materials:
	 *
	 * @param knowledge        The knowledge to create the test for.
	 * @param relatedMaterials The related materials to the knowledge.
	 * @return A list of tests.
	 */
	public List<MaterialAndMapping> createTests(Set<KnowledgeNode> knowledge,
			LinkedList<MaterialAndMapping> relatedMaterials, TestConfiguration testConfiguration) {
		var assembler = new TestAssembler<>(knowledge.stream().findFirst().get(), relatedMaterials, testConfiguration);
		List<MaterialAndMapping<TestMaterial>> tests = assembler.assemble();
		return new LinkedList<>(tests);
	}


	@Override
	public void input(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		super.input(templates, model, plan);
	}

	@Override
	public boolean createSimpleMaterial() {
		return false;
	}

	@Override
	public boolean createComplexMaterial() {
		return false;
	}
}
