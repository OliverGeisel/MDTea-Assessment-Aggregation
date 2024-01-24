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
	private String text;

	public TextMaterial(String headline, String text, TextTemplate templateInfo) {
		super(MaterialType.WIKI, templateInfo);
		this.headline = headline;
		this.text = text;
	}

	public TextMaterial(Text text, TemplateInfo templateInfo) {
		super("", text.getId(), text.getStructureId(), MaterialType.WIKI, templateInfo);
		this.headline = text.getHeadline();
		this.text = text.getText();
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
