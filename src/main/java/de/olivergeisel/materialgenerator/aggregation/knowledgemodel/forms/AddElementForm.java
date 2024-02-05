package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.forms;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeType;
import jakarta.validation.constraints.NotBlank;

public class AddElementForm {

	@NotBlank
	private String content;

	private String description;
	@NotBlank
	private String structureId;

	private String        headline;
	private String        language;
	private KnowledgeType type;

	public AddElementForm() {
	}

	public AddElementForm(String content, String description, String structureId, KnowledgeType type) {
		this.content = content;
		this.description = description;
		this.structureId = structureId;
		this.type = type;
	}

	//region setter/getter
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStructureId() {
		return structureId;
	}

	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}

	public KnowledgeType getType() {
		return type;
	}

	public void setType(
			KnowledgeType type) {
		this.type = type;
	}
//endregion
}
