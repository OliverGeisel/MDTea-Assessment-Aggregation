package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.generator.task_exctraction.TrueFalseExtractor;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.assessment.AssessmentTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.assessment.TrueFalseTemplate;
import org.slf4j.Logger;

import java.util.*;


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

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AssessmentGenerator.class);


	private final Set<AssessmentTemplate> taskInfos = new HashSet<>();

	public AssessmentGenerator() {
		super();
	}

	public AssessmentGenerator(GeneratorInput input) {
		super(input);
	}

	/**
	 * Returns the assessmentTemplateInfo for the given class.
	 *
	 * @param templateInfoClass the class of the templateInfo
	 * @return the templateInfo
	 * @throws NoTemplateInfoException if no templateInfo is found
	 */
	private <T extends AssessmentTemplate> T getTaskTemplateInfo(Class<T> templateInfoClass) throws
			NoTemplateInfoException {
		return (T) taskInfos.stream()
							.filter(it -> templateInfoClass.equals(it.getClass()))
							.findFirst().orElseThrow(() -> new NoTemplateInfoException(
						String.format("No Template %s found", templateInfoClass.getName())));
	}

	@Override
	protected void processTargets(List<ContentTarget> targets) throws IllegalStateException {
		if (targets.isEmpty()) {
			logger.warn("No targets found. This should not happen.");
			return;
		}
		var goal = targets.stream().findFirst().orElseThrow().getRelatedGoal();
		if (goal == null || targets.stream().anyMatch(it -> !it.getRelatedGoal().equals(goal)))
			throw new IllegalStateException("Targets with different goals found. This should not happen. Ignoring all"
											+ " targets.");
		int emptyCount = 0;
		for (var target : targets) {
			var expression = goal.getExpression();
			var aliases = target.getAliases();
			var topic = target.getTopic();
			try {
				var topicKnowledge = loadKnowledgeForStructure(aliases.complete());
				if (topicKnowledge.isEmpty()) {
					logger.info("No knowledge found for Topic {}", target);
					emptyCount++;
					continue;
				}
				topicKnowledge.forEach(it -> {
					it.setGoal(goal);
					it.addTopic(topic);
				});
				createMaterialFor(expression, topicKnowledge);
			} catch (NoSuchElementException e) {
				logger.info("No knowledge found for Target {}", target);
			}
		}
		if (emptyCount == targets.size()) {
			logger.warn("No knowledge found for any target. Goal: {} has no materials", goal);
		}
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

		return new LinkedList<MaterialAndMapping>(createTrueFalse(knowledge));
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
		var extractor = new TrueFalseExtractor();
		final var templateInfo = getTaskTemplateInfo(TrueFalseTemplate.class);
		// todo get Tasks, that are stored in knowledge (added while aggregation)
		for (var node : knowledge) {
			var questions = extractor.extract(node, templateInfo);
			materials.addAll(questions);
		}
		return materials;
	}

	public List<MaterialAndMapping> createSingleChoice(Set<KnowledgeNode> knowledge) {
		var materials = new LinkedList<MaterialAndMapping>();
		// Todo implement
		return materials;
	}

	public List<MaterialAndMapping> createMultipleChoice(Set<KnowledgeNode> knowledge) {
		var materials = new LinkedList<MaterialAndMapping>();
		// Todo implement
		return materials;
	}

	/**
	 * Creates a test for the given knowledge. The test contains the following materials:
	 *
	 * @param knowledge
	 * @param relatedMaterials
	 * @return
	 */
	public List<MaterialAndMapping> createTests(Set<KnowledgeNode> knowledge,
			List<MaterialAndMapping> relatedMaterials) {
		var materials = new LinkedList<MaterialAndMapping>();
		// Todo implement
		return materials;
	}

	//region setter/getter
	public void setAssessmentTemplateInfo(Set<AssessmentTemplate> assessmentTemplateInfo) {
		this.taskInfos.clear();
		this.taskInfos.addAll(assessmentTemplateInfo);
		changed();
	}
//endregion


}
