package de.olivergeisel.materialgenerator.aggregation.model.element;


import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;

import java.util.Collection;
import java.util.HashMap;

public class Text extends KnowledgeElement {

	private String text;
	private String headline;

	public Text(String content, String id, String type,
			Collection<Relation> relations) {
		super(content, id, type, relations);
		HashMap<String, String> elements = new HashMap<>();
		for (String element : content.split(";")) {
			String[] parts = element.split(":");
			if (parts.length == 2) {
				elements.put(parts[0].trim(), parts[1].trim());
			}
		}
		this.text = elements.getOrDefault("text", "");
		this.headline = elements.getOrDefault("headline", "");
	}

	public Text(String content, String id, String type) {
		super(content, id, type);
	}

	//region setter/getter
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}
//endregion
}
