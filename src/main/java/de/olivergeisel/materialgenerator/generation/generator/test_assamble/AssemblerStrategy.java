package de.olivergeisel.materialgenerator.generation.generator.test_assamble;

import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.configuration.TestConfiguration;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;

import java.util.List;

/**
 * Interface for the strategy pattern to assemble materials
 *
 * @param <T> the type of the material to be assembled
 */
public interface AssemblerStrategy<T extends ComplexMaterial> {

	List<MaterialAndMapping<T>> assemble(KnowledgeNode knowledgeNode, List<MaterialAndMapping> relatedMaterials,
			TestConfiguration configuration);
}
