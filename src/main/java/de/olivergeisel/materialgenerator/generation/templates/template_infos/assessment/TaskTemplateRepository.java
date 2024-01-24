package de.olivergeisel.materialgenerator.generation.templates.template_infos.assessment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.UUID;

public interface TaskTemplateRepository extends CrudRepository<AssessmentTemplate, UUID> {

	@Override
	Streamable<AssessmentTemplate> findAll();
}
