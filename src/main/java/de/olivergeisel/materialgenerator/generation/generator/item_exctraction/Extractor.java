package de.olivergeisel.materialgenerator.generation.generator.item_exctraction;


import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import java.util.List;

/**
 * An extractor is responsible for extracting a task from a {@link KnowledgeNode}.
 * It will return a list of {@link MaterialAndMapping}.
 * T is the Type of {@link Material}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeNode
 * @see ItemMaterial
 * @see Material
 * @since 1.1.0
 */
public abstract class Extractor<T extends Material> {

	public abstract List<MaterialAndMapping<T>> extract(KnowledgeNode knowledgeNode, TemplateType templateType);
}
