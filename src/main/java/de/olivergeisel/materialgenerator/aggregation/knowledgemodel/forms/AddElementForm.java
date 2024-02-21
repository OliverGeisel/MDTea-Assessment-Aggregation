package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.forms;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeType;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Depending on what type of element you want to add to the knowledge model, you have to fill out different fields.
 * The fields can have different meanings. If you not need a field, you can leave it empty.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeType
 * @see KnowledgeElement
 * @since 1.1.0
 */
@Setter
@Getter
public class AddElementForm {

	@NotBlank
	private String content;

	private String description;
	@NotBlank
	private String structureId;

	private String        headline;
	private String        language;
	private KnowledgeType type;
	boolean isTrue;
	private ItemType itemType;

	public AddElementForm() {
	}

	public AddElementForm(String content, String description, String structureId, KnowledgeType type,
			ItemType itemType, String isTrue) {
		this.content = content;
		this.description = description;
		this.structureId = structureId;
		this.type = type;
		this.itemType = itemType;
		this.isTrue = Boolean.parseBoolean(isTrue);
	}
}
