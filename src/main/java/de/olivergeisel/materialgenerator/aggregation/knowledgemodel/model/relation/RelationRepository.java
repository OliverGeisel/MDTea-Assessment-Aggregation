package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation;


import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

/**
 * Repository for {@link Relation}s.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Relation
 * @see Neo4jRepository
 * @see UUID
 * @see RelationType
 * @since 1.1.0
 */
public interface RelationRepository extends Neo4jRepository<Relation, UUID> {


}
