package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

/**
 * A loader for a whole knowledge model as json. The model is loaded from the file system.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeModel
 * @since 0.2.0
 */
@Service
public class KnowledgeModelLoader {

	private static final String KNOWLEDGE_PATH = "src/main/resources/data/knowledge/knowledgedata.json";

	private final KnowledgeModel knowledge;

	/**
	 * Constructor for the KnowledgeModelLoader.
	 * <p>
	 * Loads the default knowledge model from the file system. The file is located at the path
	 * "src/main/resources/data/knowledge/knowledgedata.json".
	 */
	public KnowledgeModelLoader() {
		knowledge = parseFromPath(KNOWLEDGE_PATH);
	}

	private static KnowledgeModel parseFromInputStream(InputStream input) {
		KnowledgeParser parser = new KnowledgeParser();
		try {
			return parser.parseFromFile(input);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	private static KnowledgeModel parseFromFile(File file) {
		KnowledgeParser parser = new KnowledgeParser();
		try {
			return parser.parseFromFile(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static KnowledgeModel parseFromPath(String path) {
		File file = new File(path);
		return parseFromFile(file);
	}

	public Set<KnowledgeElement> findRelatedElements(String elementId) {
		return getKnowledge().findRelatedElements(elementId);
	}

	//region setter/getter
	public KnowledgeModel getKnowledge() {
		return knowledge;
	}
//endregion

}
