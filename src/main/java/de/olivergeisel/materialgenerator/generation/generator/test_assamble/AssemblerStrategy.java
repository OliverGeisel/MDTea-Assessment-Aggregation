package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;

import java.util.List;

public abstract class AssemblerStrategy {

	public abstract List<TestMaterial> assemble(KnowledgeNode knowledgeNode, List<MaterialAndMapping> relatedMaterials,
			TestConfiguration configuration);
}
