package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.material.transfer.ListMaterial;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class SynonymMaterial extends ListMaterial {

	protected SynonymMaterial() {
		super();
	}

	protected SynonymMaterial(MaterialType type, TemplateInfo templateInfo) {
		super(type, templateInfo);
	}

	public SynonymMaterial(List<String> synonyms, boolean numerated, TemplateInfo templateInfo,
			KnowledgeElement element) {
		super(MaterialType.WIKI, templateInfo, "Synonyme für " + element.getContent(), synonyms, numerated, element);
	}
}