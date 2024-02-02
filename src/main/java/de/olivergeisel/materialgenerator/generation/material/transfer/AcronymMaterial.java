package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.List;

/**
 * Material that contains a list of acronyms
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ListMaterial
 * @since 0.2.0
 */
@Entity
public class AcronymMaterial extends ListMaterial {

	protected AcronymMaterial() {
		super();
	}

	protected AcronymMaterial(MaterialType type, TemplateType abstractTemplateCategory) {
		super(type, abstractTemplateCategory);
	}

	/**
	 * Create a new AcronymMaterial
	 *
	 * @param acronyms                 List of acronyms
	 * @param numerated                list is numerated or not
	 * @param abstractTemplateCategory AbstractTemplateCategory for the material
	 * @param element                  KnowledgeElement that is represented by the material
	 */
	public AcronymMaterial(List<String> acronyms, boolean numerated, TemplateType abstractTemplateCategory,
			KnowledgeElement element) {
		super(MaterialType.WIKI, abstractTemplateCategory, STR."Abkürzung für \{element.getContent()}", acronyms,
				numerated, element);
	}
}
