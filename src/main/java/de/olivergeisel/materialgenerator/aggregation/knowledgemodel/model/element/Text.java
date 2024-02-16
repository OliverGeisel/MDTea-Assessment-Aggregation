package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import jakarta.persistence.Column;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;
import java.util.HashMap;

/**
 * A Text is a long text with a headline.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @since 0.2.0
 */
@Node
public class Text extends KnowledgeElement {

	@Column(length = 10_000)
	private String textField;
	private String headline;

	protected Text() {
		super();
	}

	public Text(String textField, String id, String type,
			Collection<Relation> relations) {
		super(textField, id, type, relations);
		HashMap<String, String> elements = new HashMap<>();
		for (String element : textField.split(";")) {
			String[] parts = element.split(":");
			if (parts.length == 2) {
				elements.put(parts[0].trim(), parts[1].trim());
			}
		}
		this.textField = elements.getOrDefault("text", "");
		this.headline = elements.getOrDefault("headline", "");
	}

	public Text(String textField, String headline, String id) {
		super(textField, id, "TEXT");
		this.textField = textField;
		this.headline = headline;
	}

	protected Text(String textField, String headline, String id, String type) {
		super(textField, id, type);
		this.textField = textField;
		this.headline = headline;
	}

	public Text(String textField, String id) {
		super(textField, id, "TEXT");
	}

	//region setter/getter
	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}
//endregion
}
