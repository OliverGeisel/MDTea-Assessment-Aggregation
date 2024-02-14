package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.StructureRepository;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import org.springframework.stereotype.Service;

/**
 * Service for the structure ({@link KnowledgeObject}s) of the knowledge model. Main focus on searching the structure
 * and deliver {@link KnowledgeElement}s or {@link KnowledgeNode}s
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @see KnowledgeObject
 * @see KnowledgeNode
 * @since 1.1.0
 */
@Service
public class KnowledgeStructureService {

	private final StructureRepository structure;


	public KnowledgeStructureService(StructureRepository structure) {this.structure = structure;}


	/**
	 * Looks for a {@link KnowledgeObject} with the given id or similar id.
	 *
	 * @param structureId the id of the structure
	 * @return true if there is a similar KnowledgeObject with the given id
	 */
	private boolean hasStructureSimilar(String structureId) {
		return containsSimilar(structureId);
	}

	public boolean containsSimilar(String structureId) {
		return getSimilarObjectById(structureId) != null;
	}

	public KnowledgeObject getSimilarObjectById(String structureId) {
		return structure.findSimilar(structureId);
	}

}
