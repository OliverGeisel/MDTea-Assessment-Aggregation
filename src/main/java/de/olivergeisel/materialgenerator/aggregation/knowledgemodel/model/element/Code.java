package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;
import java.util.Objects;

@Node
public class Code extends SimpleElement {

	private String language;
	private String caption;
	private String codeLines;

	protected Code() {
		super();
	}

	public Code(String content, String id, String type) {
		this(content, id, type, null);

	}

	public Code(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
		var elements = content.replace("\\n", "\n").split("\n");
		language = elements[0];
		caption = elements[1];
		var builder = new StringBuilder();
		for (int i = 2; i < elements.length; i++) {
			builder.append(elements[i]);
			builder.append("\n");
		}
		codeLines = builder.toString();
	}

	//region setter/getter
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getCodeLines() {
		return codeLines;
	}

	public void setCodeLines(String codeLines) {
		this.codeLines = codeLines;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Code code)) return false;
		if (!super.equals(o)) return false;

		if (!Objects.equals(language, code.language)) return false;
		if (!Objects.equals(caption, code.caption)) return false;
		return Objects.equals(codeLines, code.codeLines);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (language != null ? language.hashCode() : 0);
		result = 31 * result + (caption != null ? caption.hashCode() : 0);
		result = 31 * result + (codeLines != null ? codeLines.hashCode() : 0);
		return result;
	}
}
