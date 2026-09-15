package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;

import java.util.List;

public class SubGroupStrategy<T extends ComplexMaterial> implements AssemblerStrategy<T> {


	@Override
	public <CM extends T> List<MaterialAndMapping<? extends CM>> assemble(KnowledgeNode knowledgeNode,
			List<MaterialAndMapping<? extends Material>> relatedMaterials, TestConfiguration configuration) {
		return List.of();
	}
}
