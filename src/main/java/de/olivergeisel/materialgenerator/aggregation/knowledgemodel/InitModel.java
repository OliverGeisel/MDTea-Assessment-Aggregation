package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeLeaf;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.RootStructureElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.StructureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class InitModel implements CommandLineRunner {


	private final StructureRepository structureRepository;

	public InitModel(StructureRepository structureRepository) {this.structureRepository = structureRepository;}

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
	}
}
