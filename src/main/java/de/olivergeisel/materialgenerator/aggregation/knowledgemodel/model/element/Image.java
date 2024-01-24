package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;
import java.util.HashMap;

@Node
public class Image extends KnowledgeElement {

	private String imageName;
	private String imageDescription;
	private String headline;

	protected Image(String content, String id, String type,
			Collection<Relation> relations) {
		super(content, id, type, relations);
		HashMap<String, String> elements = new HashMap<>();
		for (String element : content.split(";")) {
			String[] parts = element.split(":");
			if (parts.length != 2)
				continue;
			elements.put(parts[0].trim(), parts[1].trim());
		}
		this.imageName = elements.getOrDefault("imageName", "");
		this.imageDescription = elements.getOrDefault("imageDescription", "");
		this.headline = elements.getOrDefault("headline", "");
	}

	protected Image(String content, String id, String type) {
		super(content, id, type);
	}

	//region setter/getter
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageDescription() {
		return imageDescription;
	}

	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}
//endregion
}
