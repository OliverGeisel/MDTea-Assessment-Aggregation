package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class SynonymMaterial extends ListMaterial {

	protected SynonymMaterial() {
		super();
	}

	protected SynonymMaterial(MaterialType type) {
		super(type, TemplateType.SYNONYM);
	}

	public SynonymMaterial(List<String> synonyms, boolean numerated, TemplateType abstractTemplateCategory,
			KnowledgeElement element) {
		super(MaterialType.WIKI, abstractTemplateCategory, "Synonyme für " + element.getContent(), synonyms, numerated,
				element);
	}
}
