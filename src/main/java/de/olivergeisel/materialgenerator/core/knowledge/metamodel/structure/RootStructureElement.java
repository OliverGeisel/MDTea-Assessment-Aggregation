package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;


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
