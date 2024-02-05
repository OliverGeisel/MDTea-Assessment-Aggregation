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

	protected Image() {
		super();
	}

	/**
	 * Creates a new Image element. This is a old version. Please use the new Constructor.
	 *
	 * @param content   the content of the element in the following structure: imageName: name; imageDescription: description; headline: headline
	 * @param id        the id of the element
	 * @param type      the type of the element. Normally "IMAGE"
	 * @param relations the relations of the element
	 */
	@Deprecated(since = "1.1.0")
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

	/**
	 * Creates a new Image element.
	 *
	 * @param imageName        the name of the image
	 * @param imageDescription the description of the image
	 * @param headline         the headline of the image
	 * @param id               the id of the element
	 */
	public Image(String imageName, String imageDescription, String headline, String id) {
		super(STR."\{imageName}; imageDescription: \{imageDescription}; headline: \{headline}", id, "IMAGE");
		this.imageName = imageName;
		this.imageDescription = imageDescription;
		this.headline = headline;
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
