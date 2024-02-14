package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ElementRepository extends Neo4jRepository<KnowledgeElement, String> {

	KnowledgeElement findByContent(String content);

	KnowledgeElement findByType(KnowledgeType type);

	KnowledgeElement findByTypeAndAndContent(KnowledgeType type, String content);

	long countByType(KnowledgeType type);


}
