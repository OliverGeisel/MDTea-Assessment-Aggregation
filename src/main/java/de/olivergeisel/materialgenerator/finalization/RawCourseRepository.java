package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RawCourseRepository extends CrudRepository<RawCourse, UUID> {


}
