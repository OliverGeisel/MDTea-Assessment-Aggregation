package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.aggregation.model.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.model.element.*;
import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.model.relation.RelationType;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.*;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Generator for {@link Material} objects for MDTea.
 * <p>
 * This Generator can only create the simplest form of Material in
 * MDTea. It can create Definitions, Lists, Textes Code and Examples.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see Generator
 * @see Material
 * @see TemplateSet
 * @see KnowledgeModel
 * @see CoursePlan
 * @see KnowledgeNode
 * @since 0.2.0
 */
public class TranslateGenerator implements Generator {

	private static final String UNKNOWN = "Unknown";

	private final Logger            logger            = org.slf4j.LoggerFactory.getLogger(TranslateGenerator.class);
	private final Set<TemplateInfo> basicTemplateInfo = new HashSet<>();
	private       TemplateSet       templateSet;
	private       KnowledgeModel    model;
	private       CoursePlan        plan;
	private       boolean           unchanged         = false;
	private       GeneratorOutput   output;

	public TranslateGenerator() {

	}

	public TranslateGenerator(GeneratorInput input) {
		this(input.getTemplates(), input.getModel(), input.getPlan());
	}

	public TranslateGenerator(TemplateSet templateSet, KnowledgeModel model, CoursePlan plan) {
		this.templateSet = templateSet;
		this.model = model;
		this.plan = plan;
	}

	/**
	 * Get all Relations of a KnowledgeNode that match a RelationType.
	 * It searches in the relatedElements of the KnowledgeNode.
	 *
	 * @param mainKnowledge KnowledgeNode to search in
	 * @param type          RelationType to search for. Should be a Relation, that points to the main Element of the
	 *                      KnowledgeNode
	 * @return Set of Relations that match the RelationType
	 */
	private static Set<Relation> getWantedRelationsFromRelated(KnowledgeNode mainKnowledge, RelationType type) {
		return Arrays.stream(mainKnowledge.getRelatedElements())
					 .flatMap(it -> it.getRelations().stream().filter(relation -> relation.getType().equals(type)))
					 .collect(Collectors.toSet());
	}

	/**
	 * Get all Relations of a KnowledgeNode that match a RelationType. Search only in the mainElement of the
	 * KnowledgeNode.
	 *
	 * @param knowledgeNode KnowledgeNode to search in
	 * @param type          RelationType to search for. Should be a Relation, that goes from the main Element of the
	 *                      KnowledgeNode. Like DefinedBy
	 * @return Set of Relations that match the RelationType
	 */
	private static Set<Relation> getWantedRelationsFromMain(KnowledgeNode knowledgeNode, RelationType type) {
		return knowledgeNode.getMainElement().getRelations().stream()
							.filter(relation -> relation.getType().equals(type)).collect(Collectors.toSet());
	}

	private static Set<Relation> getWantedRelationsFrom(KnowledgeNode knowledgeNode, RelationType type) {
		var back = getWantedRelationsFromMain(knowledgeNode, type);
		back.addAll(getWantedRelationsFromRelated(knowledgeNode,
				type));
		return back;
	}

	private static String getUniqueMaterialName(List<MaterialAndMapping> materials, String startName,
			String alternativeName) {
		String name = startName;
		final String finalName = name;
		if (materials.stream().anyMatch(mat -> mat.material().getName().equals(finalName))) {
			name = alternativeName;
		}
		return name;
	}

	private static MaterialAndMapping createListMaterialCore(String headline, String materialName,
			RelationType relationType, ListTemplate templateInfo,
			KnowledgeNode mainKnowledge, KnowledgeElement mainTerm) {
		var partRelations = getWantedRelationsFromRelated(mainKnowledge, relationType);
		var mainId = mainTerm.getId();
		var partNames = partRelations.stream().filter(it -> it.getToId().equals(mainId))
									 .map(it -> it.getFrom().getContent()).toList();
		if (partNames.isEmpty()) {
			return null;
		}
		var partListMaterial = new ListMaterial(headline, partNames);
		partListMaterial.setTerm(mainTerm.getContent());
		partListMaterial.setTemplateInfo(templateInfo);
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
	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, String masterKeyword,
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
	 * @throws NoSuchElementException
	 */
	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, KnowledgeNode node)
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
	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, KnowledgeNode node, KnowledgeType type)
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
	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, String masterKeyword,
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
	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, String masterKeyword) {
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
	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge) {
		return knowledge.stream().filter(it -> it.getMainElement().hasType(KnowledgeType.TERM))
						.findFirst().orElseThrow();
	}

	private static MaterialAndMapping createAcronymInternal(List<String> acronyms, AcronymTemplate templateInfo,
			KnowledgeElement mainTerm) {
		Material material = new AcronymMaterial(acronyms, false, templateInfo, mainTerm);
		String name = "Akronyme für " + mainTerm.getContent();
		material.setName(name);
		MaterialMappingEntry mapping = new MaterialMappingEntry(material);
		mapping.add(mainTerm);
		var back = new MaterialAndMapping(material, mapping);
		back.material().setValues(Map.of("term", mainTerm.getContent()));
		return back;
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
	private static void collectElementsWithId(Set<Relation> relations, String id, List<String> names,
			List<KnowledgeElement> elements) {
		relations.forEach(it -> {
			if (it.getToId().equals(id)) {
				var synonymElement = it.getFrom();
				names.add(synonymElement.getContent());
				elements.add(synonymElement);
			}
		});

	}

	private static Set<Relation> getWantedRelationsKnowledge(Set<KnowledgeNode> knowledge, RelationType relationType) {
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

	private void changed() {
		setUnchanged(false);
	}

	/**
	 * Processes the plan and creates the material
	 */
	private void process() {
		var goals = plan.getGoals();
		output = new GeneratorOutput();
		try {
			processGoals(goals.stream().toList());
		} catch (NoTemplateInfoException e) {
			logger.error("No TemplateInfo found for {}", e.getMessage());
		}
		setUnchanged(true);
	}

	/**
	 * Creates {@link Material} for all given goals.
	 *
	 * @param goals List of {@link ContentGoal} to create {@link Material} for
	 */
	private void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			processTargets(goal.getContent());
		}
	}

	private void processTargets(Collection<ContentTarget> targets) throws IllegalStateException {
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
			var topic = target.getTopic();
			try {
				var topicKnowledge = loadKnowledgeForStructure(topic);
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

	/**
	 * Create all Materials for a given Expression and knowledge.
	 *
	 * @param expression the expression word to create Materials for a specific level.
	 * @param knowledge  the Knowledge to use.
	 */
	private void createMaterialFor(ContentGoalExpression expression, Set<KnowledgeNode> knowledge) {
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

	private List<MaterialAndMapping> materialForComment(Set<KnowledgeNode> knowledge) {
		return materialForCreate(knowledge);
	}

	private List<MaterialAndMapping> materialForCreate(Set<KnowledgeNode> knowledge) {
		return materialForControl(knowledge);
	}

	private List<MaterialAndMapping> materialForControl(Set<KnowledgeNode> knowledge) {
		return materialForUse(knowledge);
	}

	private List<MaterialAndMapping> materialForUse(Set<KnowledgeNode> knowledge) {
		return materialForTranslate(knowledge);
	}

	private List<MaterialAndMapping> materialForTranslate(Set<KnowledgeNode> knowledge) {
		// materials.add(createWikisWithExistingMaterial(knowledge, materials));
		return materialForKnow(knowledge);
	}

	private List<MaterialAndMapping> materialForKnow(Set<KnowledgeNode> knowledge) {
		if (knowledge.isEmpty()) {
			return List.of();
		}
		final var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN);
		var materials = materialForFirstLook(knowledge);
		try {
			materials.addAll(createProofs(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No proof found for {}", masterKeyword);
		}
		try {
			materials.addAll(createExamples(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No example found for {}", masterKeyword);
		}
		createImagesSave(knowledge, materials, masterKeyword);
		try {
			materials.addAll(createCode(knowledge));
		} catch (NoTemplateInfoException | NoSuchElementException e) {
			logger.info("No code found for {}", masterKeyword);
		}
		try {
			materials.addAll(createTexts(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No text found for {}", masterKeyword);
		}
		if (materials.isEmpty()) {
			logger.info("No KNOW Material found for {}", masterKeyword);
		}
		return materials;
	}

	private void createImagesSave(Set<KnowledgeNode> knowledge, List<MaterialAndMapping> materials,
			String masterKeyword) {
		try {
			materials.addAll(createImages(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No image found for {}", masterKeyword);
		}
	}

	private List<MaterialAndMapping> createDefinitionsSave(Set<KnowledgeNode> knowledge, String masterKeyword) {
		try {
			return createDefinitions(knowledge);
		} catch (NoSuchElementException | IllegalArgumentException e) {
			logger.info("No definition found for {}", masterKeyword);
		}
		return new LinkedList<>();
	}

	private List<MaterialAndMapping> materialForFirstLook(Set<KnowledgeNode> knowledge)
			throws NoSuchElementException {
		if (knowledge.isEmpty()) {
			return List.of();
		}
		final var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN);
		List<MaterialAndMapping> materials = createDefinitionsSave(knowledge, masterKeyword);
		materials.addAll(createListsSave(knowledge, masterKeyword));
		try {
			var synonyms = createSynonyms(knowledge);
			materials.addAll(synonyms);
		} catch (NoSuchElementException e) {
			logger.info("No synonym found for {}",
					knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN));
		}
		try {
			var acronyms = createAcronyms(knowledge);
			materials.addAll(acronyms);
		} catch (NoSuchElementException e) {
			logger.info("No acronym found for {}",
					knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN));
		}
		if (materials.isEmpty()) {
			logger.info("No FIRST_LOOK Material created for {}",
					knowledge.stream().findFirst().orElseThrow().getGoal().orElseThrow());
		}
		return materials;
	}

	/**
	 * Create Definition Materials for a KnowledgeNode
	 *
	 * @param knowledge KnowledgeNodes to create Materials for
	 * @return List of Materials and Mappings that are Definitions
	 * @throws NoTemplateInfoException  if no Definition Template is found
	 * @throws NoSuchElementException   if no KnowledgeNode is found that has a Term as mainElement
	 * @throws IllegalArgumentException if the Knowledge is empty
	 */
	private List<MaterialAndMapping> createDefinitions(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException, IllegalArgumentException {
		if (knowledge.isEmpty()) {
			throw new IllegalArgumentException("Knowledge is empty");
		}
		var templateInfo = getBasicTemplateInfo(DefinitionTemplate.class);
		var firstNode = knowledge.stream().findFirst().orElseThrow();
		var mainKnowledge = getMainKnowledge(knowledge, firstNode);
		List<MaterialAndMapping> back = new LinkedList<>();
		var mainTerm = mainKnowledge.getMainElement();
		var definitionRelations = getWantedRelationsKnowledge(knowledge, RelationType.DEFINED_BY);
		definitionRelations.forEach(it -> {
			var termElement = it.getFrom();
			var definitionElement = it.getTo();
			try {
				String name = getUniqueMaterialName(back, "Definition " + termElement.getContent(),
						definitionElement.getId());
				var values = Map.of("term", termElement.getContent(), "definition", definitionElement.getContent());
				var materialAndMapping =
						new MaterialCreator().createWikiMaterial(termElement, name, templateInfo, values,
								definitionElement);
				materialAndMapping.material().setStructureId(mainTerm.getStructureId());
				back.add(materialAndMapping);
			} catch (NoSuchElementException ignored) {
				logger.warn("No definition found for {}", termElement.getContent());
			}
		});
		return back;
	}

	private MaterialAndMapping createWikisWithExistingMaterial(Set<KnowledgeNode> knowledge,
			List<MaterialAndMapping> existingMaterials) {
		// var material = new WikiPageMaterial();
		// var mapping = new MaterialMappingEntry(material);
		return null;//new MaterialAndMapping(material, mapping);
	}

	private List<MaterialAndMapping> createListsSave(Set<KnowledgeNode> knowledge, String masterKeyword) {
		try {
			return createLists(knowledge);
		} catch (NoSuchElementException e) {
			logger.info("No list found for {}", masterKeyword);
			return new LinkedList<>();
		}
	}

	/**
	 * Create a Synonym Material for a KnowledgeNode with Synonyms
	 *
	 * @param knowledge KnowledgeNode to create Material for
	 * @return A material with synonyms
	 * @throws NoTemplateInfoException if no Synonym Template is found
	 * @throws NoSuchElementException  if no KnowledgeElement is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createLists(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		ListTemplate templateInfo = getBasicTemplateInfo(ListTemplate.class);
		var mainKnowledge = getMainKnowledge(knowledge);
		var mainTerm = mainKnowledge.getMainElement();
		var back = new ArrayList<MaterialAndMapping>();
		var newList = createListMaterialCore("Besteht aus", "Liste " + mainTerm.getContent() + " besteht aus",
				RelationType.PART_OF, templateInfo, mainKnowledge, mainTerm);
		if (newList != null) {
			back.add(newList);
		}

		newList = createListMaterialCore("NUTZT", "Liste " + mainTerm.getContent() + " nutzt", RelationType.IS_USED_BY,
				templateInfo, mainKnowledge, mainTerm);
		if (newList != null) {
			back.add(newList);
		}
		return back;
	}

	/**
	 * Create a List Material for a KnowledgeNode with Synonyms
	 *
	 * @param knowledge KnowledgeNode to create Material for
	 * @return A material with synonyms and a mapping. If no synonyms are found, null is returned
	 * @throws NoTemplateInfoException if no Synonym Template is found
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createSynonyms(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(SynonymTemplate.class);
		var back = new LinkedList<MaterialAndMapping>();
		var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElseThrow();
		var mainKnowledge = getMainKnowledge(knowledge, masterKeyword);
		var mainTerm = mainKnowledge.getMainElement();

		var synonymRelations = getWantedRelationsKnowledge(knowledge, RelationType.IS_SYNONYM_FOR);
		var synonymsMap = new HashMap<KnowledgeElement, List<KnowledgeElement>>();
		for (var relation : synonymRelations) {
			var normalTerm = relation.getTo();
			var synoElement = relation.getFrom();
			synonymsMap.putIfAbsent(normalTerm, new LinkedList<>());
			synonymsMap.get(normalTerm).add(synoElement);
		}
		for (var synoEntry : synonymsMap.entrySet()) {
			var term = synoEntry.getKey();
			Material material =
					new SynonymMaterial(synoEntry.getValue().stream().map(KnowledgeElement::getContent).toList(), false,
							templateInfo, term);
			material.setName("Synonyme für " + term.getContent());
			material.setStructureId(mainTerm.getStructureId());
			material.setValues(Map.of("term", term.getContent()));

			MaterialMappingEntry mapping = new MaterialMappingEntry(material);
			mapping.add(mainTerm);
			mapping.addAll(synoEntry.getValue().toArray(new KnowledgeElement[0]));
			back.add(new MaterialAndMapping(material, mapping));
		}
		return back;
	}

	/**
	 * Create a List Material for a KnowledgeNode with Acronyms
	 *
	 * @param knowledge KnowledgeNode to create Material for
	 * @return A material with acronyms and a mapping. If no acronyms are found, an empty list is returned
	 * @throws NoTemplateInfoException if no Acronym Template is found
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createAcronyms(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(AcronymTemplate.class);
		var back = new LinkedList<MaterialAndMapping>();
		var first = knowledge.stream().findFirst().orElseThrow();
		var mainKnowledge = getMainKnowledge(knowledge, first);
		var mainTerm = mainKnowledge.getMainElement();
		var acryRelations = getWantedRelationsKnowledge(knowledge, RelationType.IS_ACRONYM_FOR);
		var acronyms = new HashMap<KnowledgeElement, List<KnowledgeElement>>();
		for (var relation : acryRelations) {
			var longFormElement = relation.getTo();
			var acronymElement = relation.getFrom();
			acronyms.putIfAbsent(longFormElement, new LinkedList<>());
			acronyms.get(longFormElement).add(acronymElement);
		}
		for (var accEntry : acronyms.entrySet()) {
			var res = createAcronymInternal(accEntry.getValue().stream().map(KnowledgeElement::getContent).toList(),
					templateInfo,
					accEntry.getKey());
			res.mapping().addAll(accEntry.getValue().toArray(new KnowledgeElement[0]));
			res.material().setStructureId(mainTerm.getStructureId());
			back.add(new MaterialAndMapping(res.material(), res.mapping()));
		}
		return back;
	}

	private List<MaterialAndMapping> createImages(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(ImageTemplate.class);
		if (knowledge.isEmpty()) {
			throw new IllegalArgumentException("Knowledge is empty!");
		}
		var mainTerm = getMainKnowledge(knowledge).getMainElement();
		List<MaterialAndMapping> back = new LinkedList<>();
		var imageRelations = getWantedRelationsKnowledge(knowledge, RelationType.RELATED);
		imageRelations.forEach(it -> {
			KnowledgeElement image;
			KnowledgeElement term;
			try {
				var to = it.getTo();
				var from = it.getFrom();
				image = to.hasType(KnowledgeType.IMAGE) ? to : from;
				term = to.hasType(KnowledgeType.TERM) ? to : from;
			} catch (IllegalStateException ignored) {
				logger.warn("The relation '{}' has no complete linking", it);
				return;
			}
			try {
				Image imageElement = (Image) image;
				Material imageMaterial = new ImageMaterial(imageElement, templateInfo);
				var name = imageElement.getHeadline().isBlank() ? "Bild: %s".formatted(imageElement.getImageName()) :
						imageElement.getHeadline();
				imageMaterial.setName(name);
				imageMaterial.setTerm(term.getContent());
				imageMaterial.setValues(Map.of("term", term.getContent(), "content", imageElement.getContent()));
				imageMaterial.setStructureId(mainTerm.getStructureId());
				MaterialMappingEntry mapping = new MaterialMappingEntry(imageMaterial);
				mapping.add(mainTerm, imageElement, term);
				back.add(new MaterialAndMapping(imageMaterial, mapping));
			} catch (ClassCastException ignored) {
				logger.debug("No images found for {}", term.getContent());
			}
		});
		return back;

	}

	/**
	 * Create a List of Materials of type Text for a KnowledgeNode.
	 *
	 * @param knowledge KnowledgeNode to create Material for
	 * @return A material with texts and a mapping. If no texts are found, empty list is returned.
	 * @throws NoTemplateInfoException if no Text Template is found
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createTexts(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException, IllegalArgumentException {
		var templateInfo = getBasicTemplateInfo(TextTemplate.class);
		if (knowledge.isEmpty()) {
			throw new IllegalArgumentException("Knowledge is empty!");
		}
		var mainKnowledge = getMainKnowledge(knowledge);
		var mainTerm = mainKnowledge.getMainElement();
		List<MaterialAndMapping> back = new LinkedList<>();
		var textRelations = getWantedRelationsKnowledge(knowledge, RelationType.RELATED);
		textRelations.forEach(it -> {
			KnowledgeElement text;
			KnowledgeElement term;
			try {
				var to = it.getTo();
				var from = it.getFrom();
				text = to.hasType(KnowledgeType.TEXT) ? to : from;
				term = to.hasType(KnowledgeType.TERM) ? to : from;
			} catch (IllegalStateException ignored) {
				logger.warn("The relation '{}' has no complete linking", it);
				return;
			}
			try {
				Text textElement = (Text) text;
				Material textMaterial = new TextMaterial(textElement, templateInfo);
				textMaterial.setName(textElement.getHeadline());
				textMaterial.setTerm(term.getContent());
				textMaterial.setTemplateInfo(templateInfo);
				textMaterial.setValues(Map.of("term", term.getContent(), "content", textElement.getContent()));
				textMaterial.setStructureId(mainTerm.getStructureId());
				MaterialMappingEntry mapping = new MaterialMappingEntry(textMaterial);
				mapping.add(mainTerm, textElement, term);
				back.add(new MaterialAndMapping(textMaterial, mapping));
			} catch (ClassCastException ignored) {
				logger.debug("No text for relation {}", it);
			}
		});
		return back;
	}

	/**
	 * Create a Material that contains Code
	 *
	 * @param knowledge The KnowledgeNode to create the Material for
	 * @return A List of Materials with Code
	 * @throws NoSuchElementException  If no Code is found
	 * @throws NoTemplateInfoException If no TemplateInfo for code is found
	 */
	private List<MaterialAndMapping> createCode(Set<KnowledgeNode> knowledge)
			throws NoTemplateInfoException, NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(CodeTemplate.class);
		List<MaterialAndMapping> back = new LinkedList<>();
		var codeKnowledgeNodes = knowledge.stream()
										  .filter(it -> it.getMainElement().hasType(KnowledgeType.CODE))
										  .map(it -> (Code) it.getMainElement()).toList();
		if (codeKnowledgeNodes.isEmpty()) {
			throw new NoSuchElementException("No code found");
		}
		for (var codeElement : codeKnowledgeNodes) {
			Material codeMaterial = new CodeMaterial(codeElement.getLanguage(), codeElement.getCodeLines(),
					codeElement.getCaption(), codeElement);
			codeMaterial.setTemplateInfo(templateInfo);
			MaterialMappingEntry mapping = new MaterialMappingEntry(codeMaterial);
			mapping.add(codeElement);
			back.add(new MaterialAndMapping(codeMaterial, mapping));
		}
		return back;
	}

	private List<MaterialAndMapping> createExamples(Set<KnowledgeNode> knowledge)
			throws IllegalArgumentException, NoTemplateInfoException {
		var templateInfo = getBasicTemplateInfo(ExampleTemplate.class);
		if (knowledge.isEmpty()) {
			throw new IllegalArgumentException("knowledge is empty!");
		}
		var mainTerm = getMainKnowledge(knowledge).getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var relations = getWantedRelationsKnowledge(knowledge, RelationType.HAS_EXAMPLE);
		relations.forEach(it -> {
			var term = it.getFrom();
			var example = it.getTo();
			String name = getUniqueMaterialName(back, "Beispiel " + term.getContent(), term.getId());
			var values = Map.of("term", term.getContent(), "example", example.getContent());
			var materialAndMapping = new MaterialCreator().createExampleMaterial(example, term, name,
					templateInfo, values, example);
			materialAndMapping.material().setStructureId(mainTerm.getStructureId());
			materialAndMapping.mapping().add(mainTerm);
			back.add(materialAndMapping);
		});
		return back;
	}

	private List<MaterialAndMapping> createProofs(Set<KnowledgeNode> knowledge) {
		// todo improve proof material
		var templateInfo = getBasicTemplateInfo(ProofTemplate.class);
		var mainKnowledge = knowledge.stream()
									 .filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM))
									 .findFirst().orElseThrow();
		List<MaterialAndMapping> back = new ArrayList<>();
		var mainTerm = mainKnowledge.getMainElement();
		var relations = getWantedRelationsFromRelated(mainKnowledge, RelationType.PROOFS);
		relations.forEach(it -> {
			var proofId = it.getFromId();
			try {
				var proofElement = Arrays.stream(mainKnowledge.getRelatedElements())
										 .filter(elem -> elem.getId().equals(proofId)).findFirst().orElseThrow();
				var name = getUniqueMaterialName(back, "Beweis " + mainTerm.getContent(), proofId);
				var values = Map.of("term", mainTerm.getContent(), "proof", proofElement.getContent());
				var materialAndMapping = new MaterialCreator().createProofMaterial(proofElement, mainTerm, name,
						templateInfo,
						values, proofElement);
				back.add(materialAndMapping);
			} catch (Exception ignored) {
				logger.debug("No proof found for {}", mainTerm.getContent());
			}
		});
		return back;
	}

	/**
	 * Returns the basic templateInfo for the given class.
	 *
	 * @param templateInfoClass the class of the templateInfo
	 * @return the templateInfo
	 * @throws NoTemplateInfoException if no templateInfo is found
	 */
	private <T extends TemplateInfo> T getBasicTemplateInfo(Class<T> templateInfoClass) throws
			NoTemplateInfoException {
		return (T) basicTemplateInfo.stream()
									.filter(it -> templateInfoClass.equals(it.getClass()))
									.findFirst().orElseThrow(() -> new NoTemplateInfoException(
						String.format("No Template %s found", templateInfoClass.getName())));
	}

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId, Collection<String> extra) {
		return loadKnowledgeForStructure(structureId, extra.toArray(new String[0]));
	}

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId, String... extra) {
		if (structureId == null) {
			return Collections.emptySet();
		}
		var back = new HashSet<>(loadKnowledgeForStructure(structureId));
		for (String target : extra) {
			if (target == null) {
				continue;
			}
			back.addAll(loadKnowledgeForStructure(target));
		}
		return back;
	}

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId) {
		if (structureId == null) {
			return Collections.emptySet();
		}
		return model.getKnowledgeNodesIncludingSimilarFor(structureId);
	}

	/**
	 * Initial method to set the input for the generator. All parameters cant be null.
	 *
	 * @param templates Templates that should be used for the generation.
	 * @param model     The current KnowledgeModel.
	 * @param plan      Plan with curriculum for which the generator should generate materials.
	 */
	@Override
	public void input(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		this.templateSet = templates;
		this.model = model;
		this.plan = plan;
	}

	/**
	 * Initial method to set the input for the generator. This needs only the GeneratorInput object.
	 *
	 * @param input All input to be needed for the generator. Must be valid and not be null.
	 */
	@Override
	public void input(GeneratorInput input) {
		input(input.getTemplates(), input.getModel(), input.getPlan());
	}

	/**
	 * Method to start the generation process. This method should be called after the input method.
	 *
	 * @return True if the generation was successful, false if not.
	 */
	@Override
	public boolean update() {
		if (!isReady() || isUnchanged()) {
			return false;
		}
		process();
		setUnchanged(true);
		return true;
	}

	/**
	 * Method to get the output of the generation process. This method should be called after the update method.
	 *
	 * @return all Materials for the given input.
	 */
	@Override
	public GeneratorOutput output() {
		return this.output;
	}

	/**
	 * Loads the knowledge for the given expression
	 *
	 * @param elementId target expression
	 * @return KnowledgeNode for the given expression
	 * @throws NoSuchElementException if the expression is not found in the model
	 */
	private KnowledgeNode loadKnowledgeForElement(String elementId) throws NoSuchElementException {
		return model.getKnowledgeNode(elementId);
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
	private boolean isUnchanged() {
		return unchanged;
	}

	private void setUnchanged(boolean unchanged) {
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

	/**
	 * Sets the basic templateInfo.
	 * Will change the inner state of the generator.
	 *
	 * @param basicTemplateInfo the basic templateInfo to set
	 */
	public void setBasicTemplateInfo(Set<BasicTemplate> basicTemplateInfo) {
		this.basicTemplateInfo.clear();
		this.basicTemplateInfo.addAll(basicTemplateInfo);
		changed();
	}
//endregion
}
