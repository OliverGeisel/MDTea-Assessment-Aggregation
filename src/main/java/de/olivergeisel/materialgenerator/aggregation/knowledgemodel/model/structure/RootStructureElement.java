package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure;


import org.springframework.data.neo4j.core.schema.Node;

@Node
public class RootStructureElement extends KnowledgeFragment {

	private String key;

	public RootStructureElement() {
		super("Knowledge-Root", null);
	}

	//region setter/getter
	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}
//endregion

}
