package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

/**
 * The TemplateInfo class is the base class for all template information classes.
 * It contains the type of the template and the main term id.
 * All classes that extend this class have a FIELDS-set, which contains all names for the fields of the class.
 *
 * @since 1.0.0
 * @version 1.1.0
 * @author Oliver Geisel
 */
@Entity
public abstract class TemplateInfo {
	public static final Set<String>  FIELDS = Set.of("term", "termId", "structureId", "values", "templateType",
			"mainTermId");
	/**
	 * The type of the template. Specific template in the TemplateSet.
	 */
	@Embedded
	protected final     TemplateType templateType;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private             UUID         id;
	/**
	 * The name of the main term. the material is related to.
	 */
	private             UUID         mainTermId;

	protected TemplateInfo(TemplateType templateType, UUID mainTermId) {
		this.templateType = templateType;
		this.mainTermId = mainTermId;
	}

	protected TemplateInfo(TemplateType templateType) {
		this.templateType = templateType;
	}

	protected TemplateInfo() {
		this.templateType = null;
	}

	//region setter/getter
	public UUID getId() {
		return id;
	}

	public TemplateType getTemplateType() {
		return templateType;
	}

	public UUID getMainTermId() {
		return mainTermId;
	}

	public void setMainTermId(UUID mainTermId) {
		this.mainTermId = mainTermId;
	}
//endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TemplateInfo that)) return false;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return STR."TemplateInfo{templateType=\{templateType}, id=\{id}, mainTermId=\{mainTermId}}";
	}
}
