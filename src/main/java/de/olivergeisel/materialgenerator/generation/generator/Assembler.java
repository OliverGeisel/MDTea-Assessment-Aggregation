package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.ComplexMaterial;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;

import java.util.List;

/**
 * An Assembler is a special form or sub form of a generator. It is used to assemble a specific type of material from
 * a given set of {@link Material}s. This new created material is a {@link ComplexMaterial}. One of these materials
 * is a {@link TestMaterial}.
 * <p>
 * Assembler don't require the {@link KnowledgeModel}. All information needed to assemble the {@link ComplexMaterial}
 * should be in the given set of materials, the {@link KnowledgeNode}s and the given configuration or {@link CoursePlan}.
 * </p>
 *
 * @param <T> is the Type of {@link Material} you get.
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @see Generator
 * @see ComplexMaterial
 * @since 1.1.0
 */
public interface Assembler<T extends ComplexMaterial> {


	/**
	 * Assembles a new {@link ComplexMaterial} from the given set of {@link Material}s. The new material is a
	 * {@link ComplexMaterial} and should be a {@link TestMaterial}.
	 *
	 * @return The new assembled {@link ComplexMaterial}.
	 */
	List<MaterialAndMapping<T>> assemble();
}
