package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class CustomRelation extends Relation {

	private RelationType customType;

	protected CustomRelation() {
		super();
		customType = RelationType.CUSTOM;
	}

	public CustomRelation(String name, RelationType type) {
		super(name, "UNKNOWN", "UNKNOWN", type);
		customType = type;
	}

	public CustomRelation(String name, String fromId, String toId, RelationType type) {
		super(name, fromId, toId, type);
		customType = type;
	}

	public CustomRelation(String name, KnowledgeElement from, KnowledgeElement to, RelationType type) {
		super(name, from.getId(), to.getId(), type);
		customType = type;
	}

	//region setter/getter
	@Override
	public RelationType getType() {
		return customType;
	}
//endregion
}
