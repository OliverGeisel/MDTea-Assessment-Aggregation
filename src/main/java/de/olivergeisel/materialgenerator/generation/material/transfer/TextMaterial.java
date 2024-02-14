package de.olivergeisel.materialgenerator.generation.material.transfer;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Text;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Represents a material that contains a text. Every text has a headline and a text.
 *
 * @version 0.2.0
 * @see Material
 * @see Text
 * @since 0.2.0
 */
@Entity
public class TextMaterial extends Material {

	@Column(length = 1_000)
	private String headline;
	@Column(length = 100_000)
	private String text;

	public TextMaterial(String headline, String text, TemplateType templateInfo) {
		super(MaterialType.WIKI, templateInfo);
		this.headline = headline;
		this.text = text;
	}

	public TextMaterial(Text text) {
		super("", text.getId(), text.getStructureId(), MaterialType.WIKI, TemplateType.TEXT);
		this.headline = text.getHeadline();
		this.text = text.getTextField();
	}

	protected TextMaterial() {
		super(MaterialType.WIKI);
	}

	//region setter/getter
	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
//endregion
}
