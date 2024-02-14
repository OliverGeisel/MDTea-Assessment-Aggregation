package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.ElementRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Initialize the knowledge model in this Prototype.
 * Use the command line arguments --init to create a new model and --load to load the model from the json-file.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeModel
 * @since 1.1.0
 */
@Component
public class InitModel implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(InitModel.class);

	private final StructureRepository structureRepository;
	private final ElementRepository   elementRepository;
	private final RelationRepository  relationRepository;
	private final KnowledgeModelService knowledgeManagement;
	private final KnowledgeModelService knowledgeModelService;

	public InitModel(StructureRepository structureRepository, ElementRepository elementRepository,
			RelationRepository relationRepository, KnowledgeModelService knowledgeManagement,
			KnowledgeModelService knowledgeModelService) {
		this.structureRepository = structureRepository;
		this.elementRepository = elementRepository;
		this.relationRepository = relationRepository;
		this.knowledgeManagement = knowledgeManagement;
		this.knowledgeModelService = knowledgeModelService;
	}

	@Override
	public void run(String... args) throws Exception {
		if (Arrays.asList(args).contains("--init")) {
			var root = new RootStructureElement();
			var java = new KnowledgeLeaf("Java");
			var computerScience = new KnowledgeFragment("Computer Science", java);
			root.addObject(computerScience);
			structureRepository.save(java);
			structureRepository.save(computerScience);
			structureRepository.save(root);
		}
		// load the model from the json-file
		if (Arrays.asList(args).contains("--load")) {
			logger.info("Loading model from json");
			var model = knowledgeManagement;
			// Elements
			var elements = model.getAllElements();
			var relations = new HashSet<Relation>();
			for (var element : elements) {
				relations.addAll(element.getRelations());
				elementRepository.save(element);
			}
			// Relations
			relationRepository.saveAll(relations);
			// Structure
			var rootOfNewModel = model.getRoot();
			// replace root with a fragment
			var rootFragment = new KnowledgeFragment(rootOfNewModel.getName());
			for (var child : rootOfNewModel.getChildren()) {
				rootFragment.addObject(child);
			}
			AtomicReference<KnowledgeObject> rootToSave = new AtomicReference<>(rootFragment);
			var fragmentInModel = structureRepository.findById(rootFragment.getName());
			fragmentInModel.ifPresentOrElse(it -> {
				if (it instanceof RootStructureElement root) {
					for (var child : rootOfNewModel.getChildren()) {
						root.addObject(child);
					}
				} else if (it instanceof KnowledgeFragment fragment) {
					for (var child : rootOfNewModel.getChildren()) {
						fragment.addObject(child);
					}
				} else {
					knowledgeModelService.leafToNode(it.getName());
				}
				rootToSave.set(it);
			}, () -> {
				var correctRoot = knowledgeModelService.getRoot();
				correctRoot.addObject(rootFragment);
				structureRepository.save(correctRoot);
			});
			saveStructure(rootToSave.get());
			logger.info("Model from json loaded");
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
