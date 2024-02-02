package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.Term;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.transfer.ListMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import org.slf4j.Logger;

import java.util.*;

public abstract class AbstractGenerator implements Generator {
	protected static final String UNKNOWN = "Unknown";

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractGenerator.class);

	protected       TemplateSet       templateSet;
	protected       KnowledgeModel    model;
	protected       CoursePlan        plan;
	protected       boolean           unchanged         = false;
	protected       GeneratorOutput   output;


	protected AbstractGenerator() {
		output = new GeneratorOutput();
	}

	protected AbstractGenerator(GeneratorInput input) {
		this.templateSet = input.getTemplates();
		this.model = input.getModel();
		this.plan = input.getPlan();
	}

	protected AbstractGenerator(TemplateSet templateSet, KnowledgeModel model, CoursePlan plan) {
		this();
		this.templateSet = templateSet;
		this.model = model;
		this.plan = plan;
	}


	protected static String getUniqueMaterialName(List<MaterialAndMapping> materials, String startName,
			String alternativeName) {
		String name = startName;
		final String finalName = name;
		if (materials.stream().anyMatch(mat -> mat.material().getName().equals(finalName))) {
			name = alternativeName;
		}
		return name;
	}

	protected static MaterialAndMapping createListMaterialCore(String headline, String materialName,
			RelationType relationType, TemplateType templateInfo,
			KnowledgeNode mainKnowledge, KnowledgeElement mainTerm) {
		var partRelations = mainKnowledge.getWantedRelationsFromRelated(relationType);
		var mainId = mainTerm.getId();
		var partNames = partRelations.stream().filter(it -> it.getToId().equals(mainId))
									 .map(it -> it.getFrom().getContent()).toList();
		if (partNames.isEmpty()) {
			return null;
		}
		var partListMaterial = new ListMaterial(headline, partNames);
		partListMaterial.setTerm(mainTerm.getContent());
		partListMaterial.setTemplateType(templateInfo);
		partListMaterial.setTermId(mainTerm.getId());
		partListMaterial.setName(materialName);
		partListMaterial.setStructureId(mainTerm.getStructureId());
		partListMaterial.setValues(Map.of("term", mainTerm.getContent()));
		var mapping = new MaterialMappingEntry(partListMaterial);
		mapping.add(mainTerm);
		mapping.addAll(partRelations.stream().filter(it -> it.getToId().equals(mainId)).map(Relation::getFrom)
									.toArray(KnowledgeElement[]::new));
		return new MaterialAndMapping(partListMaterial, mapping);
	}

	/**
	 * Find a {@link Term} in a {@link KnowledgeNode} that fits the masterKeyword or one of the topics.
	 * <p>
	 * The topics are searched first and then the masterKeyword. So the topics have a higher priority.
	 *
	 * @param knowledge     Set of KnowledgeNodes to search in
	 * @param masterKeyword Keyword to search for
	 * @param topics        List of topics to search for
	 * @return KnowledgeNode that fits the masterKeyword or one of the topics
	 * @throws NoSuchElementException if no KnowledgeNode fits the masterKeyword or one of the topics
	 */
	protected static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, String masterKeyword,
			List<String> topics) throws NoSuchElementException {
		return getMainKnowledge(knowledge, masterKeyword, topics, KnowledgeType.TERM);
	}

	/**
	 * Find a {@link Term} of given type in a set of {@link KnowledgeNode} that fits the masterKeyword or one of the
	 * topics.
	 *
	 * @param knowledge Set of KnowledgeNodes to search in
	 * @param node      KnowledgeNode with masterKeyword and topics
	 * @return Term that fits the masterKeyword or one of the topics of the node
	 * @throws NoSuchElementException if no KnowledgeNode fits the masterKeyword or one of the topics.
	 */
	protected static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, KnowledgeNode node)
			throws NoSuchElementException {
		return getMainKnowledge(knowledge, node.getMasterKeyWord().orElseThrow(), node.getTopics(), KnowledgeType.TERM);
	}

	/**
	 * Find a {@link KnowledgeNode} of given type in a set of {@link KnowledgeNode} that fits the masterKeyword or one of the topics.
	 *
	 * @param knowledge Set of KnowledgeNodes to search in
	 * @param node      KnowledgeNode with masterKeyword and topics
	 * @param type      KnowledgeType to search for
	 * @return KnowledgeNode that fits the masterKeyword or one of the topics
	 * @throws NoSuchElementException if no KnowledgeNode fits the masterKeyword or one of the topics
	 */
	protected static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, KnowledgeNode node,
			KnowledgeType type)
			throws NoSuchElementException {
		return getMainKnowledge(knowledge, node.getMasterKeyWord().orElseThrow(), node.getTopics(), type);
	}

	/**
	 * Find a {@link KnowledgeNode} of given type in a set of {@link KnowledgeNode} that fits the masterKeyword or one
	 * of the topics.
	 * <p>
	 * The topics are searched first and then the masterKeyword. So the topics have a higher priority.
	 *
	 * @param knowledge     Set of KnowledgeNodes to search in
	 * @param masterKeyword Keyword to search for
	 * @param topics        List of topics to search for
	 * @param type          KnowledgeType to search for
	 * @return KnowledgeNode that fits the masterKeyword or one of the topics
	 * @throws NoSuchElementException if no KnowledgeNode fits the masterKeyword or one of the topics
	 */
	protected static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, String masterKeyword,
			List<String> topics, KnowledgeType type) throws NoSuchElementException {
		for (var node : knowledge) {
			var mainElement = node.getMainElement();
			if (mainElement.hasType(type)
				&& (topics.stream().anyMatch(topic -> mainElement.getContent().contains(topic))
					|| mainElement.getContent().contains(masterKeyword))) {
				return node;
			}
		}
		throw new NoSuchElementException("No %s found for masterKeyword %s and topics %s"
				.formatted(type, masterKeyword, topics));
	}

	/**
	 * Get a {@link KnowledgeNode} that fits the mainKeyword. The node is a {@link Term}.
	 *
	 * @param knowledge     Set of KnowledgeNodes to search in
	 * @param masterKeyword Keyword to search for
	 * @return KnowledgeNode that fits the mainKeyword
	 * @throws NoSuchElementException if no KnowledgeNode fits the mainKeyword or no KnowledgeNode is a Term
	 */
	protected static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, String masterKeyword) {
		return knowledge.stream().filter(it -> it.getMainElement().hasType(KnowledgeType.TERM)
											   && it.getMainElement().getContent().contains(masterKeyword))
						.findFirst()
						.orElseThrow();
	}

	/**
	 * Get a {@link KnowledgeNode} that fits the mainKeyword. The node is a {@link Term}.
	 *
	 * @param knowledge Set of KnowledgeNodes to search in
	 * @return KnowledgeNode that is the first TERM element
	 * @throws NoSuchElementException if no KnowledgeNode is a Term
	 */
	protected static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge) {
		return knowledge.stream().filter(it -> it.getMainElement().hasType(KnowledgeType.TERM))
						.findFirst().orElseThrow();
	}


	/**
	 * Collect all Elements with a given id from a Set of Relations.
	 * Compares the toId with given id
	 *
	 * @param names     List to collect the names in
	 * @param relations Relations to search in
	 * @param id        id to search for
	 * @param elements  List to collect the elements in
	 */
	protected static void collectElementsWithId(Set<Relation> relations, String id, List<String> names,
			List<KnowledgeElement> elements) {
		relations.forEach(it -> {
			if (it.getToId().equals(id)) {
				var synonymElement = it.getFrom();
				names.add(synonymElement.getContent());
				elements.add(synonymElement);
			}
		});

	}

	protected static Set<Relation> getWantedRelationsKnowledge(Set<KnowledgeNode> knowledge,
			RelationType relationType) {
		var back = new HashSet<Relation>();
		for (var node : knowledge) {
			var relations = node.getRelations();
			for (var relation : relations) {
				if (relation.hasType(relationType)) {
					back.add(relation);
				} else if (relation.getType().getInverted().equals(relationType)) {
					var fromElement = relation.getFrom();
					relation.getTo().getRelations().stream()
							.filter(it -> it.hasType(relationType) && it.getTo().equals(fromElement))
							.forEach(back::add);
				}
			}
		}
		return back;
	}

	public void input(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		this.templateSet = templates;
		this.model = model;
		this.plan = plan;
	}

	public void input(GeneratorInput input) {
		this.templateSet = input.getTemplates();
		this.model = input.getModel();
		this.plan = input.getPlan();
	}

	public boolean update() {
		if (!isReady()) {
			logger.error("Generator is not ready to start the generation process.");
			return false;
		}
		process();
		return true;

	}

	/**
	 * Method to get the output of the generation process. This method should be called after the update method.
	 *
	 * @return all Materials for the given input.
	 */
	public GeneratorOutput output() {
		return output;
	}

	protected void changed() {
		setUnchanged(false);
	}


	/**
	 * This method is the main method for the generation process. It should be called after the input method.
	 * <p>
	 * This method call all methods to generate all materials for the given input.
	 * The result is stored in the {@link #output} object.
	 * This method can be called multiple times. But if you add new materials to the output object, the
	 * {@link #unchanged} flag must be set to false. If nothing was changed, the flag must be set to true.
	 * </p>
	 */
	protected void process() {
		var goals = plan.getGoals();
		output = new GeneratorOutput();
		try {
			processGoals(goals.stream().toList());
		} catch (NoTemplateInfoException e) {
			logger.error("No AbstractTemplateCategory found for {}", e.getMessage());
		}
		setUnchanged(true);
	}

	/**
	 * Creates {@link Material} for all given goals.
	 *
	 * @param goals List of {@link ContentGoal} to create {@link Material} for
	 */
	protected void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			processTargets(goal.getContent());
		}
	}

	/**
	 * Load all {@link KnowledgeNode}s for a given structureId.
	 *
	 * @param structureId structureId to load the KnowledgeNodes for
	 * @return Set of KnowledgeNodes for the given structureId. If no KnowledgeNode is found, an empty unmodifiable
	 * Set is returned.
	 */
	protected Set<KnowledgeNode> loadKnowledgeForStructure(String structureId) {
		if (structureId == null) {
			return Collections.emptySet();
		}
		return model.getKnowledgeNodesIncludingSimilarFor(structureId);
	}

	/**
	 * Load all {@link KnowledgeNode}s for a given structureId. But the order of the structureIds is important.
	 * The first structureId that has a KnowledgeNode is used.
	 *
	 * @param structureIds structureIds to load the KnowledgeNodes for
	 * @return a Set of KnowledgeNodes for the given structureIds. If no KnowledgeNode is found, an empty unmodifiable
	 */
	protected Set<KnowledgeNode> loadKnowledgeForStructure(List<String> structureIds) {
		if (structureIds == null) {
			return Collections.emptySet();
		}
		for (var structureId : structureIds) {
			var result = loadKnowledgeForStructure(structureId);
			if (!result.isEmpty()) {
				return result;
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Create the materials for a given list of {@link ContentTarget}s.
	 * <p>
	 * Every topic has a mapping to the related
	 * {@link StructureElement}s in the {@link CoursePlan}. Every Target must look if the generated
	 * {@link Material} match to the structure.
	 * </p>
	 *
	 * @param targets List of {@link ContentTarget}s to create {@link Material} for
	 * @throws IllegalStateException if the targets have different {@link ContentGoal}s.
	 */
	protected abstract void processTargets(List<ContentTarget> targets) throws IllegalStateException;

	protected abstract List<MaterialAndMapping> materialForComment(Set<KnowledgeNode> knowledge);

	protected abstract List<MaterialAndMapping> materialForCreate(Set<KnowledgeNode> knowledge);

	protected abstract List<MaterialAndMapping> materialForControl(Set<KnowledgeNode> knowledge);

	protected abstract List<MaterialAndMapping> materialForUse(Set<KnowledgeNode> knowledge);

	protected abstract List<MaterialAndMapping> materialForTranslate(Set<KnowledgeNode> knowledge);

	protected abstract List<MaterialAndMapping> materialForKnow(Set<KnowledgeNode> knowledge);

	protected abstract List<MaterialAndMapping> materialForFirstLook(Set<KnowledgeNode> knowledge)
			throws NoSuchElementException;

	/**
	 * Create <b>all</b> Materials for a given Expression and knowledge.
	 *
	 * @param expression the expression word to create Materials for a specific level.
	 * @param knowledge  the Knowledge to use.
	 */
	protected void createMaterialFor(ContentGoalExpression expression, Set<KnowledgeNode> knowledge) {
		var materialAndMapping = switch (expression) {
			case FIRST_LOOK -> materialForFirstLook(knowledge);
			case KNOW -> materialForKnow(knowledge);
			case USE -> materialForUse(knowledge);
			case TRANSLATE -> materialForTranslate(knowledge);
			case CREATE -> materialForCreate(knowledge);
			case COMMENT -> materialForComment(knowledge);
			case CONTROL -> materialForControl(knowledge);
		};
		output.addAll(materialAndMapping);
	}


//region setter/getter

	/**
	 * {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	public boolean isReady() {
		return templateSet != null && model != null && plan != null;
	}

	/**
	 * checks if the input is unchanged
	 *
	 * @return {@literal true} if the input is unchanged, {@literal false} if not
	 */
	protected boolean isUnchanged() {
		return unchanged;
	}

	protected void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}

	/**
	 * Sets the templateSet.
	 * Will change the inner state of the generator.
	 *
	 * @param templateSet the templateSet to set
	 */
	public void setTemplateSet(TemplateSet templateSet) {
		this.templateSet = templateSet;
		changed();
	}

	/**
	 * Sets the model.
	 * Will change the inner state of the generator.
	 *
	 * @param model the model to set
	 */
	public void setModel(KnowledgeModel model) {
		this.model = model;
		changed();
	}

	/**
	 * Sets the plan
	 * Will change the inner state of the generator.
	 *
	 * @param plan the plan to set
	 */
	public void setPlan(CoursePlan plan) {
		this.plan = plan;
		changed();
	}
//endregion

}
