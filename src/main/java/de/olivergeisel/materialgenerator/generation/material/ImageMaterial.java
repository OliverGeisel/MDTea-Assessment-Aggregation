package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.Image;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class ImageMaterial extends Material {

	private String imageName;
	@Column(length = 5_000)
	private String imageDescription;
	private String headline;

	public ImageMaterial(Image image, TemplateInfo templateInfo) {
		super(MaterialType.WIKI, templateInfo);
		this.imageName = image.getImageName();
		this.imageDescription = image.getImageDescription();
		this.headline = image.getHeadline();
	}


	protected ImageMaterial() {
		super(MaterialType.WIKI);
	}

	//region setter/getter
	public String getImageName() {
		return imageName;
	}

	public String getImageDescription() {
		return imageDescription;
	}

	public String getHeadline() {
		return headline;
	}
//endregion
}
