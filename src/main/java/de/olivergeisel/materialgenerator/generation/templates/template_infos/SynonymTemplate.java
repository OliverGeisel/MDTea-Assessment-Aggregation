package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
public class SynonymTemplate extends ListTemplate {


	public SynonymTemplate() {
		super(TemplateType.SYNONYM);
	}


	public SynonymTemplate(UUID maiTermId, String synonym) {
		super(TemplateType.SYNONYM, maiTermId);
	}

}
