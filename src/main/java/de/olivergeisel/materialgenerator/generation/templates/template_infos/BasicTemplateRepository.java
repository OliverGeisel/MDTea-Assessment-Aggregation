package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.UUID;

public interface BasicTemplateRepository extends CrudRepository<BasicTemplate, UUID> {

	@Override
	Streamable<BasicTemplate> findAll();
}
