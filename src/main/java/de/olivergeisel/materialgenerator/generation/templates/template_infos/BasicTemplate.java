package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
public abstract class BasicTemplate extends TemplateInfo {

	protected BasicTemplate(TemplateType type) {
		super(type);
	}

	protected BasicTemplate(TemplateType type, UUID mainTermId) {
		super(type, mainTermId);
	}

	protected BasicTemplate() {
		super();
	}

}
