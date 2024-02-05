package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a peace of source code of a specific programming-language.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @since 0.2.0
 */
@Node
public class Code extends SimpleElement {

	private String language;
	private String caption;
	private String codeLines;

	protected Code() {
		super();
	}

	public Code(String content, String id, String type) {
		this(content, id, type, (Collection<Relation>) null);
	}

	/**
	 * Creates a new Code element. This is a old version. Please use the new Constructor.
	 * <p>
	 * The <b>content</b> parameter is a string with the following structure:
	 * <ul>
	 *<li>language</li>
	 * <li>caption</li>
	 * <li>code lines</li>
	 * </ul>
	 * each part is separated by a newline character.
	 * </p>
	 * @param content the content of the element in the following structure: language\n caption\n code lines
	 * @param id the id of the element
	 * @param type the type of the element. Normally "CODE"
	 * @param relations the relations of the element
	 * @deprecated use the new constructor. Unhandy to use.
	 */
	@Deprecated(since = "1.1.0")
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

	/**
	 * Creates a new Code element.
	 * @param language the language of the code. E.g. Java, Python, C++
	 * @param caption the caption of the code
	 * @param codeLines the code lines
	 * @param id the id of the element
	 */
	public Code(String language, String caption, String codeLines, String id) {
		super(codeLines, id, "CODE");
		this.language = language;
		this.caption = caption;
		this.codeLines = codeLines;
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

		if (getId() == null || code.getId() == null) {
			if (!Objects.equals(language, code.language)) return false;
			if (!Objects.equals(caption, code.caption)) return false;
			return Objects.equals(codeLines, code.codeLines);
		}
		return getId().equals(code.getId());
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
