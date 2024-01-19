package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.model.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

@Service
public class KnowledgeManagement {

	private static final String KNOWLEDGE_PATH = "src/main/resources/data/knowledge/knowledgedata.json";

	private final KnowledgeModel knowledge;

	public KnowledgeManagement() {
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

	public Set<KnowledgeElement> findRelatedData(String elementId) {
		return getKnowledge().findAll(elementId);
	}

	//region setter/getter
	public KnowledgeModel getKnowledge() {
		return knowledge;
	}
//endregion

}
