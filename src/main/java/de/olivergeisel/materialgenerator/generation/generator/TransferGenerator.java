package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.generator.transfer_assamble.TransferAssembler;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialCreator;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.transfer.*;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import org.slf4j.Logger;

import java.util.*;

/**
 * A Generator for {@link Material} objects for MDTea.
 * <p>
 * This Generator can only create the simplest form of Material in
 * MDTea. It can create Definitions, Lists, Textes Code and Examples.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Generator
 * @see Material
 * @see TemplateSet
 * @see KnowledgeModel
 * @see CoursePlan
 * @see KnowledgeNode
 * @since 0.2.0
 */
public class TransferGenerator extends AbstractGenerator {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TransferGenerator.class);

	public TransferGenerator() {
		super();
	}

	public TransferGenerator(GeneratorInput input) {
		super(input);
	}

	public TransferGenerator(TemplateSet templateSet, KnowledgeModel model, CoursePlan plan) {
		super(templateSet, model, plan);
	}


	protected List<MaterialAndMapping> materialForComment(Set<KnowledgeNode> knowledge) {
		return materialForCreate(knowledge);
	}

	protected List<MaterialAndMapping> materialForCreate(Set<KnowledgeNode> knowledge) {
		return materialForControl(knowledge);
	}

	protected List<MaterialAndMapping> materialForControl(Set<KnowledgeNode> knowledge) {
		return materialForUse(knowledge);
	}

	protected List<MaterialAndMapping> materialForUse(Set<KnowledgeNode> knowledge) {
		return materialForTranslate(knowledge);
	}

	protected List<MaterialAndMapping> materialForTranslate(Set<KnowledgeNode> knowledge) {
		// materials.add(createWikisWithExistingMaterial(knowledge, materials));
		var firstLookMaterials = materialForKnow(knowledge);
		for (var node : knowledge) {
			var assembler = new TransferAssembler(firstLookMaterials, node);
			var summary = assembler.createSummary();
			var overview = assembler.createOverview(summary);
			firstLookMaterials.addAll(summary);
			firstLookMaterials.addAll(overview);
		}
		return firstLookMaterials;
	}

	@Override
	protected List<MaterialAndMapping> materialForKnow(Set<KnowledgeNode> knowledge) {
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

	@Override
	protected List<MaterialAndMapping> materialForFirstLook(Set<KnowledgeNode> knowledge)
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


	//region Material Creation
	private List<MaterialAndMapping> createDefinitionsSave(Set<KnowledgeNode> knowledge, String masterKeyword) {
		try {
			return createDefinitions(knowledge);
		} catch (NoSuchElementException | IllegalArgumentException e) {
			logger.info("No definition found for {}", masterKeyword);
		}
		return new LinkedList<>();
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
		final var templateInfo = TemplateType.DEFINITION;
		var firstNode = knowledge.stream().findFirst().orElseThrow();
		var mainKnowledge = getTermNode(knowledge, firstNode);
		List<MaterialAndMapping> back = new LinkedList<>();
		var mainTerm = mainKnowledge.getMainElement();
		var definitionRelations = getWantedRelationsKnowledge(knowledge, RelationType.DEFINED_BY);
		definitionRelations.forEach(it -> {
			var termElement = it.getFrom();
			var definitionElement = it.getTo();
			try {
				String name = getUniqueMaterialName(back, STR."Definition \{termElement.getContent()}",
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

	private void createImagesSave(Set<KnowledgeNode> knowledge, List<MaterialAndMapping> materials,
			String masterKeyword) {
		try {
			materials.addAll(createImages(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No image found for {}", masterKeyword);
		}
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
		final TemplateType templateInfo = TemplateType.LIST;
		var mainKnowledge = getMainKnowledge(knowledge);
		var mainTerm = mainKnowledge.getMainElement();
		var back = new ArrayList<MaterialAndMapping>();
		var newList = createListMaterialCore("Besteht aus", STR."Liste \{mainTerm.getContent()} besteht aus",
				RelationType.PART_OF, templateInfo, mainKnowledge, mainTerm);
		if (newList != null) {
			back.add(newList);
		}

		newList = createListMaterialCore("NUTZT", STR."Liste \{mainTerm.getContent()} nutzt", RelationType.IS_USED_BY,
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
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createSynonyms(Set<KnowledgeNode> knowledge) throws NoSuchElementException {
		var templateInfo = TemplateType.SYNONYM;
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
			material.setName(STR."Synonyme für \{term.getContent()}");
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
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createAcronyms(Set<KnowledgeNode> knowledge) throws NoSuchElementException {
		var back = new LinkedList<MaterialAndMapping>();
		var first = knowledge.stream().findFirst().orElseThrow();
		var mainKnowledge = getTermNode(knowledge, first);
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
			var terms = accEntry.getValue().stream().map(KnowledgeElement::getContent).toList();
			var res = createAcronymInternal(terms, accEntry.getKey());
			res.mapping().addAll(accEntry.getValue().toArray(new KnowledgeElement[0]));
			res.material().setStructureId(mainTerm.getStructureId());
			back.add(new MaterialAndMapping(res.material(), res.mapping()));
		}
		return back;
	}

	private List<MaterialAndMapping> createImages(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = TemplateType.IMAGE;
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
		var templateInfo = TemplateType.TEXT;
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
				Material textMaterial = new TextMaterial(textElement);
				textMaterial.setName(textElement.getHeadline());
				textMaterial.setTerm(term.getContent());
				textMaterial.setTemplateType(templateInfo);
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
	 * @throws NoTemplateInfoException If no AbstractTemplateCategory for code is found
	 */
	private List<MaterialAndMapping> createCode(Set<KnowledgeNode> knowledge)
			throws NoTemplateInfoException, NoSuchElementException {
		var templateInfo = TemplateType.CODE;
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
			codeMaterial.setTemplateType(templateInfo);
			MaterialMappingEntry mapping = new MaterialMappingEntry(codeMaterial);
			mapping.add(codeElement);
			back.add(new MaterialAndMapping(codeMaterial, mapping));
		}
		return back;
	}

	private List<MaterialAndMapping> createExamples(Set<KnowledgeNode> knowledge)
			throws IllegalArgumentException, NoTemplateInfoException {
		var templateInfo = TemplateType.EXAMPLE;
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
		var templateInfo = TemplateType.PROOF;
		var mainKnowledge = knowledge.stream()
									 .filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM))
									 .findFirst().orElseThrow();
		List<MaterialAndMapping> back = new ArrayList<>();
		var mainTerm = mainKnowledge.getMainElement();
		var relations = mainKnowledge.getWantedRelationsFromRelated(RelationType.PROOFS);
		relations.forEach(it -> {
			var proofId = it.getFromId();
			try {
				var proofElement = Arrays.stream(mainKnowledge.getRelatedElements())
										 .filter(elem -> elem.getId().equals(proofId)).findFirst().orElseThrow();
				var name = getUniqueMaterialName(back, STR."Beweis \{mainTerm.getContent()}", proofId);
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

	private MaterialAndMapping createAcronymInternal(List<String> acronyms, KnowledgeElement mainTerm) {
		Material material = new AcronymMaterial(acronyms, false, TemplateType.ACRONYM, mainTerm);
		String name = STR."Akronyme für \{mainTerm.getContent()}";
		material.setName(name);
		MaterialMappingEntry mapping = new MaterialMappingEntry(material);
		mapping.add(mainTerm);
		var back = new MaterialAndMapping(material, mapping);
		back.material().setValues(Map.of("term", mainTerm.getContent()));
		return back;
	}

	protected MaterialAndMapping createListMaterialCore(String headline, String materialName,
			RelationType relationType, TemplateType templateInfo, KnowledgeNode mainKnowledge,
			KnowledgeElement mainTerm) {
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
	//endregion

	//region helper methods
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
	//endregion

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
	 * {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean createSimpleMaterial() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean createComplexMaterial() {
		return false;
	}

}
