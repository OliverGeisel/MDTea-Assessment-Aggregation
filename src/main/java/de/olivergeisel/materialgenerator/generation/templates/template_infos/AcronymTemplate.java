package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
public class AcronymTemplate extends ListTemplate {


	public AcronymTemplate() {
		super(TemplateType.ACRONYM);
	}

	public AcronymTemplate(UUID mainTermId) {
		super(TemplateType.ACRONYM, mainTermId);
	}

}
