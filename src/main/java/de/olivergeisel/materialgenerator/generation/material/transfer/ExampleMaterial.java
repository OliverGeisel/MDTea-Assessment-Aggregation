package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class ExampleMaterial extends Material {

	private static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "bmp", "svg");

	private boolean imageExample;
	private String  imageName;

	public ExampleMaterial() {
		super(MaterialType.EXAMPLE);
	}

	public ExampleMaterial(String term, String termId, String structureId, String imageName) {
		super(MaterialType.EXAMPLE, TemplateType.EXAMPLE, term, termId, structureId);
		if (imageName == null || imageName.isBlank()) {
			throw new IllegalArgumentException("imageName must not be null or blank");
		}
		this.imageName = imageName;
		this.imageExample = true;
	}

	public ExampleMaterial(String term, String termId, String structureId) {
		super(MaterialType.EXAMPLE, TemplateType.EXAMPLE, term, termId, structureId);
		this.imageExample = false;
		imageName = "NO_IMAGE";
	}

	/**
	 * Change the example to a non image example. The imageName will be set to "NO_IMAGE"
	 *
	 * @return {@code true} if the example was changed to a non image example, {@code false} if the example was already a non image example
	 */
	public boolean setToNonImage() {
		if (!imageExample) {
			return false;
		}
		imageExample = false;
		imageName = "NO_IMAGE";
		return true;
	}

	//region setter/getter
	public boolean isImageExample() {
		return imageExample;
	}

	public String getImageName() {
		return imageName;
	}

	/**
	 * Sets the imageName. The imageName must contain a file extension. The file extension must be one of the following:
	 * jpg, jpeg, png, gif, bmp, svg
	 *
	 * @param imageName The imageName to set
	 * @throws IllegalArgumentException if the imageName is null or blank or does not contain a file extension
	 */
	public void setImageName(String imageName) throws IllegalArgumentException {
		if (imageName == null || imageName.isBlank()) {
			throw new IllegalArgumentException("imageName must not be null or blank");
		}
		var extension = imageName.split("\\.")[imageName.split("\\.").length - 1];
		if (!IMAGE_EXTENSIONS.contains(extension)) {
			throw new IllegalArgumentException("imageName must contain a file extension");
		}
		this.imageName = imageName;
	}
//endregion
}
