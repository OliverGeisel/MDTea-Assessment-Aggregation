package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Image;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Represents a material that contains an image.
 *
 * @version 0.2.0
 * @see Material
 * @see Image
 * @since 0.2.0
 */
@Entity
public class ImageMaterial extends Material {

	private                         String imageName;
	@Column(length = 5_000) private String imageDescription;
	private                         String headline;

	public ImageMaterial(Image image) {
		super(MaterialType.WIKI, TemplateType.IMAGE);
		this.imageName = image.getImageName();
		this.imageDescription = image.getImageDescription();
		this.headline = image.getHeadline();
	}

	public ImageMaterial(Image image, TemplateType templateType) {
		super(MaterialType.WIKI, templateType);
		this.imageName = image.getImageName();
		this.imageDescription = image.getImageDescription();
		this.headline = image.getHeadline();
	}

	public ImageMaterial(String imageName, String imageDescription, String headline) {
		super(MaterialType.WIKI, TemplateType.IMAGE);
		this.imageName = imageName;
		this.imageDescription = imageDescription;
		this.headline = headline;
	}


	protected ImageMaterial() {
		super(MaterialType.WIKI, TemplateType.IMAGE);
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
