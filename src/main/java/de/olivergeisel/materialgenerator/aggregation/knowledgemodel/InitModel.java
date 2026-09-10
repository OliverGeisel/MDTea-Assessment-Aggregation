package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.Neo4JCleanOnStart;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.ElementRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeModelLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Initialize the knowledge model in this Prototype.
 * Use the command line arguments --init to create a new model and --load to load the model from the json-file.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeModel
 * @see Neo4JCleanOnStart
 * @since 1.1.0
 */
@Component
@Order(2) // Run after cleaning the database
@Transactional("neo4jTransactionManager")
public class InitModel implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(InitModel.class);

	private final StructureRepository   structureRepository;
	private final ElementRepository     elementRepository;
	private final RelationRepository    relationRepository;
	private final KnowledgeModelService knowledgeModelService;
	private final Neo4jTemplate         neo4jTemplate;

	@Autowired
	private ApplicationContext applicationContext;

	public InitModel(StructureRepository structureRepository, ElementRepository elementRepository,
			RelationRepository relationRepository,
			KnowledgeModelService knowledgeModelService, Neo4jTemplate neo4jTemplate) {
		this.structureRepository = structureRepository;
		this.elementRepository = elementRepository;
		this.relationRepository = relationRepository;
		this.knowledgeModelService = knowledgeModelService;
		this.neo4jTemplate = neo4jTemplate;
	}

	@Transactional("neo4jTransactionManager")
	protected void loadIntoGraphdb() {
		logger.info("Loading model from json");
		var model = new KnowledgeModelLoader().getKnowledge();
		// Elements
		List<KnowledgeElement> elements = model.getAllElements();
		var relations = new HashSet<Relation>();
		for (var element : elements) {
			relations.addAll(element.getRelations());
			elementRepository.save(element);
		}
		// Relations
		relationRepository.saveAll(relations);
		// Structure
		var rootOfNewModel = model.getRoot();
		logger.info(STR."Root of new model: \{rootOfNewModel.getName()}");
		// replace root with a fragment
		var rootFragment = new KnowledgeFragment(rootOfNewModel.getName());
		for (var child : rootOfNewModel.getChildren()) {
			rootFragment.addObject(child);
		}
		AtomicReference<KnowledgeObject> rootToSave = new AtomicReference<>(rootFragment);
		var fragmentInModel = structureRepository.findById(rootFragment.getName());
		fragmentInModel.ifPresentOrElse(it -> {
			if (it instanceof RootStructureElement root) { // root is a fragment
				for (var child : rootOfNewModel.getChildren()) {
					root.addObject(child);
				}
			} else if (it instanceof KnowledgeFragment fragment) { // root of new model exists as fragment
				logger.info("Root of new model exists as fragment. Add children to it");
				for (var child : rootOfNewModel.getChildren()) {
					fragment.addObject(child);
				}
			} else { // root of new model exists as leaf -> replace it with a fragment
				logger.info("Root of new model is a leaf. Replace with fragment");
				knowledgeModelService.leafToNode(it.getName());
				var newLeaf = (KnowledgeFragment) structureRepository.findById(it.getName()).get();
				for (var child : rootOfNewModel.getChildren()) {
					newLeaf.addObject(child);
				}
				rootToSave.set(newLeaf);
				return;
			}
			rootToSave.set(it);
		}, () -> { // root of new model does not exist in the database -> append it
			logger.info("new root not in model. Append to root");
			var correctRoot = knowledgeModelService.getRoot();
			correctRoot.addObject(rootFragment);
			rootToSave.set(correctRoot);
		});
		saveStructure(rootToSave.get());
		logger.info("Model from json loaded");
	}

	@Override
	public void run(String... args) throws Exception {
		if (Arrays.asList(args).contains("--init")) {

			var root = new RootStructureElement();
			var java = new KnowledgeLeaf("Programming language");
			var computerScience = new KnowledgeFragment("Computer Science", java);
			root.addObject(computerScience);
			structureRepository.save(java);
			structureRepository.save(computerScience);
			structureRepository.save(root);
		}
		// load the model from the json-file
		if (Arrays.asList(args).contains("--load")) {
			loadIntoGraphdb();
		}
	}

	private void saveStructure(KnowledgeObject object) {
		if (object instanceof KnowledgeLeaf) {
			structureRepository.save(object);
		} else if (object instanceof KnowledgeFragment fragment) {
			for (var element : fragment.getChildren()) {
				saveStructure(element);
			}
			structureRepository.save(fragment);
		}
	}
}
