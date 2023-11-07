package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ImageTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("imageName");
		allFields.add("imageDescription");
		allFields.add("headline");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private String imageName;
	private String imageDescription;
	private String headline;

	public ImageTemplate(String imageName, String imageDescription, String headline) {
		super(TemplateType.IMAGE);
		this.imageName = imageName;
		this.imageDescription = imageDescription;
		this.headline = headline;
	}

	public ImageTemplate() {
		super(TemplateType.IMAGE);
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
