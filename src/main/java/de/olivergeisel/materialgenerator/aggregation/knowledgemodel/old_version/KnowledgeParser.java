package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.ElementGenerator;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationGenerator;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeLeaf;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.aggregation.source.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parse a JSON-File to a {@link KnowledgeModel}.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see KnowledgeModel
 * @see KnowledgeElement
 * @see KnowledgeObject
 * @see Relation
 * @since 0.0.1
 */
public class KnowledgeParser {


	public static final String KNOWLEDGE_STRUCTURE = "structure";

	public static final String TYPE          = "type";
	public static final String ID            = "id";
	public static final String STRUCTURE     = "structure";
	public static final String CONTENT       = "content";
	public static final String RELATIONS     = "relations";
	public static final String RELATION_TYPE = "relation_type";
	public static final String RELATION_ID   = "relation_id";
	// Source fields
	public static final String NAME          = "name";
	// Structure fields
	public static final String CHILDREN      = "children";

	Logger logger = LoggerFactory.getLogger(KnowledgeParser.class);

	private KnowledgeElement createElement(String type, String id, String structure, String content,
			List<Map<String, String>> relationsJSON) {
		var newElement = ElementGenerator.create(type, id, structure, content);
		var relations = createRelation(relationsJSON, newElement);
		newElement.addRelations(relations);
		return newElement;
	}

	private Set<Relation> createRelation(List<Map<String, String>> relationsJSON, KnowledgeElement fromElement) {
		var back = new HashSet<Relation>();
		for (var relation : relationsJSON) {
			var newRelation = RelationGenerator.create(relation.get(RELATION_TYPE), fromElement.getId(),
					relation.get(RELATION_ID));
			newRelation.setFrom(fromElement);
			back.add(newRelation);
		}
		return back;
	}

	private KnowledgeElement createKnowledgeElement(Map<String, ?> jsonElement) {
		var type = jsonElement.get(TYPE).toString();
		var content = jsonElement.get(CONTENT).toString();
		var id = jsonElement.get(ID).toString();
		var structure = jsonElement.get(STRUCTURE).toString();
		var relationsJSON = (List<Map<String, String>>) jsonElement.get(RELATIONS);
		return createElement(type, id, structure, content, relationsJSON);
	}

	public KnowledgeModel parseFromFile(InputStream file) throws RuntimeException {
		KnowledgeModel back;
		var parser = new JSONParser(file);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			logger.error("Error while parsing JSON-File: {}", e.getMessage());
			throw new RuntimeException(e);
		}
		if (parsedObject instanceof Map<?, ?> knowledgeModel) {
			Map<String, ?> structure = (Map<String, ?>) knowledgeModel.get(KNOWLEDGE_STRUCTURE);
			List<Map<String, ?>> knowledge = (List<Map<String, ?>>) knowledgeModel.get("knowledge");
			List<Map<String, ?>> source = (List<Map<String, ?>>) knowledgeModel.get("sources");
			KnowledgeStructure parsedStructure;
			try {
				parsedStructure = parseStructure(structure);
			} catch (IncompleteJSONException e) {
				parsedStructure = new KnowledgeStructure();
				logger.warn("empty structure in JSON-File: {}", e.getMessage());
			}
			var parsedSource = parseSource(source);
			var parsedKnowledge = parseKnowledge(knowledge);
			back = new KnowledgeModelLoaded(parsedStructure.getRoot());
			back.addSource(parsedSource);
			back.addKnowledge(parsedKnowledge);
		} else {
			logger.warn("No valid knowledge-File ! Create empty KnowledgeModel");
			back = new KnowledgeModelLoaded();
		}
		return back;
	}

	public KnowledgeModel parseFromFile(File jsonFile) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(jsonFile);
		return parseFromFile(input);
	}

	private Set<KnowledgeElement> parseKnowledge(List<Map<String, ?>> knowledgeJSON) {
		if (knowledgeJSON.isEmpty()) {
			return new HashSet<>();
		}
		Set<KnowledgeElement> back = new HashSet<>();
		for (Map<String, ?> element : knowledgeJSON) {
			back.add(createKnowledgeElement(element));
		}
		return back;
	}

	/**
	 * Parse a part of the structure and its children.
	 *
	 * @param partJSON the part to parse.
	 * @return the parsed part as KnowledgeObject or null if part is empty.
	 */
	private KnowledgeObject parseKnowledgeObject(Map<String, ?> partJSON) throws IncompleteJSONException {
		KnowledgeObject back;
		// check if json is empty
		if (partJSON.isEmpty()) {
			throw new IncompleteJSONException("KnowledgeObject is empty!");
		}
		var name = partJSON.get(NAME).toString(); // name is id!
		var parts = (List<Map<String, ?>>) partJSON.get(CHILDREN);
		// todo use key-field -> define in KnowledgeObject
		// check if it's a Leaf or Fragment
		if (parts.isEmpty()) {
			back = new KnowledgeLeaf(name);
		} else {
			var newFragment = new KnowledgeFragment(name);
			for (var childParts : parts) {
				try {
					newFragment.addObject(parseKnowledgeObject(childParts));
				} catch (IncompleteJSONException e) {
					logger.warn("Incomplete JSON-Object: {}", e.getMessage());
				} catch (Exception e) {
					logger.error("Error while parsing JSON-Object: {}", e.getMessage());
				}
			}
			back = newFragment;
		}
		return back;
	}

	private Set<KnowledgeSource> parseSource(List<Map<String, ?>> sourceJSON) {
		Set<KnowledgeSource> back = new HashSet<>();
		if (sourceJSON.isEmpty()) {
			return back;
		}
		for (var source : sourceJSON) {
			String type = source.get(TYPE).toString();
			String id = source.get(ID).toString();
			String name = source.get(NAME).toString();
			String content = source.get(CONTENT).toString();
			var newSource = switch (SourceType.valueOf(type.toUpperCase())) {
				case INTERNAL_MEDIA -> new InternalMedia(id, name);
				case UNKNOWN_SOURCE -> UnknownSource.getInstance();
				case NOT_RESOLVABLE_REFERENCE -> new NotResolvableReference(id, name);
				case RESOLVABLE_REFERENCE -> new ResolvableReference(id, name);
			};
			newSource.setContent(content);
			back.add(newSource);
		}
		return back;
	}

	private KnowledgeStructure parseStructure(Map<String, ?> structureJSON) throws IncompleteJSONException {
		var back = new KnowledgeStructure();
		var root = back.getRoot();
		root.setKey("_root");
		// check if empty
		if (structureJSON.isEmpty()) {
			throw new IncompleteJSONException("Structure is empty!");
		}
		root.setName(structureJSON.get(NAME).toString());
		var children = (List<Map<String, ?>>) structureJSON.get(CHILDREN);
		for (var child : children) {
			try {
				root.addObject(parseKnowledgeObject(child));
			} catch (IncompleteJSONException e) {
				logger.warn(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return back;
	}
}
