package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.util.Streamable;

public interface ElementRepository extends Neo4jRepository<KnowledgeElement, String> {

	Streamable<KnowledgeElement> findByContent(String content);

	Streamable<KnowledgeElement> findByType(KnowledgeType type);

	KnowledgeElement findByTypeAndAndContent(KnowledgeType type, String content);

	long countByType(KnowledgeType type);


}
