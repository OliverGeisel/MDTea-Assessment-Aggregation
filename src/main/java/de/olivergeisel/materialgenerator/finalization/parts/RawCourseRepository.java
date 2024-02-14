package de.olivergeisel.materialgenerator.finalization.parts;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.UUID;

public interface RawCourseRepository extends CrudRepository<RawCourse, UUID> {

	@Override
	Streamable<RawCourse> findAll();

}
