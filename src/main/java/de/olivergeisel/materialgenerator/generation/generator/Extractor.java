package de.olivergeisel.materialgenerator.generation.generator;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.generator.item_exctraction.TrueFalseExtractor;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import java.util.List;

/**
 * An extractor is responsible for extracting a {@link Material} from a {@link KnowledgeNode} or a whole {@link KnowledgeModel}.
 * It will return a list of {@link MaterialAndMapping}. These materials are created by searching and analyzing the
 * {@link KnowledgeModel}. If the search is syntactic or semantically is determined by the implementation of the
 * {@link Extractor}.
 * <p>
 * The {@link TrueFalseExtractor} in this project is an example for an extractor that extracts
 * {@link ItemMaterial}s from a syntactic search in the {@link KnowledgeModel}.
 * </p>
 *
 * @param <T> is the Type of {@link Material} you get.
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeNode
 * @see ItemMaterial
 * @see Material
 * @see MaterialAndMapping
 * @see TrueFalseExtractor
 * @since 1.1.0
 */
public interface Extractor<T extends Material> {

	List<MaterialAndMapping<T>> extract(KnowledgeNode knowledgeNode, TemplateType templateType);
}
