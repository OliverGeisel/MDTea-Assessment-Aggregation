package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.util.Streamable;

import java.util.Collection;
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

	Streamable<Relation> findByType(RelationType type);

	Streamable<Relation> findByTo(KnowledgeElement to);

	Collection<Relation> findDistinctByTo(KnowledgeElement to);
}
