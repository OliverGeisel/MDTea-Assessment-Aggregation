package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface StructureRepository extends Neo4jRepository<KnowledgeObject, String> {

	Optional<KnowledgeObject> findById(String id);

	Optional<KnowledgeObject> findByLinkedElementsContains(KnowledgeElement element);

	default RootStructureElement findRoot() {
		return (RootStructureElement) findAll().stream().filter(RootStructureElement.class::isInstance).findFirst()
											   .orElseThrow();
	}

	default RootStructureElement findRootAlternative() {
		return (RootStructureElement) findById("Knowledge-Root").orElseThrow();
	}
}
