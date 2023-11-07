package de.olivergeisel.materialgenerator.core.knowledge.metamodel.source;

public class InternalMedia extends KnowledgeSource {

	private String content;

	public InternalMedia(String id, String name) {
		super(id, name);
	}

	//region setter/getter
	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}
//endregion
}
