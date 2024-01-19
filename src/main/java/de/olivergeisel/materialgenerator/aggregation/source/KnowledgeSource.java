package de.olivergeisel.materialgenerator.aggregation.source;

public abstract class KnowledgeSource {
	private String id;
	private String name;
	private String content;

	protected KnowledgeSource(String id, String name) {
		this.id = id;
		this.name = name;
	}

	//region setter/getter
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeSource that)) return false;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
