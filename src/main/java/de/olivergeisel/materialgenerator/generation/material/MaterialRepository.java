package de.olivergeisel.materialgenerator.generation.material;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MaterialRepository extends CrudRepository<Material, UUID> {

}
