package de.olivergeisel.materialgenerator.finalization.parts;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends CrudRepository<Goal, UUID> {
	Optional<Goal> findByName(String name);

	boolean existsByName(String name);

	@Override
	Streamable<Goal> findAll();
}
