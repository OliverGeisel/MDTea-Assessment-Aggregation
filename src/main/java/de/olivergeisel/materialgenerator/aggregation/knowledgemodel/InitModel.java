package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.ElementRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeManagement;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Initialize the knowledge model in this Prototype.
 * Use the command line arguments --init to create a new model and --load to load the model from the json-file.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeManagement
 * @since 1.1.0
 */
@Component
public class InitModel implements CommandLineRunner {


	private final StructureRepository structureRepository;
	private final ElementRepository   elementRepository;
	private final RelationRepository  relationRepository;
	private final KnowledgeManagement knowledgeManagement;

	public InitModel(StructureRepository structureRepository, ElementRepository elementRepository,
			RelationRepository relationRepository, KnowledgeManagement knowledgeManagement) {
		this.structureRepository = structureRepository;
		this.elementRepository = elementRepository;
		this.relationRepository = relationRepository;
		this.knowledgeManagement = knowledgeManagement;
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
			var model = knowledgeManagement.getKnowledge();
			// Elements
			var elements = model.getElements();
			var relations = new HashSet<Relation>();
			for (var element : elements) {
				relations.addAll(element.getRelations());
				elementRepository.save(element);
			}
			// Relations
			relationRepository.saveAll(relations);
			// Structure
			var rootModel = model.getRoot();
			saveStructure(rootModel);
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
