package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.ElementRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationRepository;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.RootStructureElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.StructureRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KnowledgeModelService {

	private final ElementRepository elementRepository;

	private final RelationRepository relationRepository;

	private final StructureRepository structureRepository;

	public KnowledgeModelService(ElementRepository elementRepository, RelationRepository relationRepository,
			StructureRepository structureRepository) {
		this.elementRepository = elementRepository;
		this.relationRepository = relationRepository;
		this.structureRepository = structureRepository;
	}

	public Optional<KnowledgeObject> getKnowledgeObject(String id) {
		return structureRepository.findById(id);
	}

	public long elementCount() {
		return elementRepository.count();
	}

	public long relationCount() {
		return relationRepository.count();
	}

	public long termCount() {
		return elementRepository.countByType(KnowledgeType.TERM);
	}

	public long structureCount() {
		return structureRepository.count();
	}

//region setter/getter
	public RootStructureElement getRoot() {
		return structureRepository.findRoot();
	}
//endregion
}
