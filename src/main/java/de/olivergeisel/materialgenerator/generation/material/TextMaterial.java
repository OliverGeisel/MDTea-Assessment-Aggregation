package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Text;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TextTemplate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class TextMaterial extends Material {

	@Column(length = 1_000)
	private String headline;
	@Column(length = 100_000)
	private String textField;

	public TextMaterial(String headline, String textField, TextTemplate templateInfo) {
		super(MaterialType.WIKI, templateInfo);
		this.headline = headline;
		this.textField = textField;
	}

	public TextMaterial(Text text, TemplateInfo templateInfo) {
		super("", text.getId(), text.getStructureId(), MaterialType.WIKI, templateInfo);
		this.headline = text.getHeadline();
		this.textField = text.getTextField();
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

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}
//endregion
}
