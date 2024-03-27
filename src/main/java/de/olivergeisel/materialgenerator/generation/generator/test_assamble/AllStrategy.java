package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;

import java.util.List;

public class AllStrategy<T extends ComplexMaterial> implements AssemblerStrategy<T> {


	@Override
	public List<MaterialAndMapping<T>> assemble(KnowledgeNode knowledgeNode, List<MaterialAndMapping> relatedMaterials,
			TestConfiguration configuration) {
		return null;
	}
}
