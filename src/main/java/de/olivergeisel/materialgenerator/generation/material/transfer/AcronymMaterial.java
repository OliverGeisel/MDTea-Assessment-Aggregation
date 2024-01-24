package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.Entity;

import java.util.List;

/**
 * Material that contains a list of acronyms
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see ListMaterial
 * @since 0.2.0
 */
@Entity
public class AcronymMaterial extends ListMaterial {

	protected AcronymMaterial() {
		super();
	}

	protected AcronymMaterial(MaterialType type, TemplateInfo templateInfo) {
		super(type, templateInfo);
	}

	/**
	 * Create a new AcronymMaterial
	 *
	 * @param acronyms     List of acronyms
	 * @param numerated    list is numerated or not
	 * @param templateInfo TemplateInfo for the material
	 * @param element      KnowledgeElement that is represented by the material
	 */
	public AcronymMaterial(List<String> acronyms, boolean numerated, TemplateInfo templateInfo,
			KnowledgeElement element) {
		super(MaterialType.WIKI, templateInfo, "Abkürzung für " + element.getContent(), acronyms, numerated, element);
	}
}
