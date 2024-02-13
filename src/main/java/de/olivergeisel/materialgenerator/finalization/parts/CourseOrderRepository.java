package de.olivergeisel.materialgenerator.finalization.parts;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CourseOrderRepository extends CrudRepository<RawCourseOrder, UUID> {
}

