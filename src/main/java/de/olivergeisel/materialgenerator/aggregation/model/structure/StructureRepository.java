package de.olivergeisel.materialgenerator.aggregation.model.structure;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface StructureRepository extends Neo4jRepository<KnowledgeObject, String> {
}
