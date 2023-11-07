package de.olivergeisel.materialgenerator.generation.templates;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;
import java.util.UUID;

public interface TemplateSetRepository extends CrudRepository<TemplateSet, UUID> {
	@Override
	Streamable<TemplateSet> findAll();

	Optional<TemplateSet> findByName(String name);
}
