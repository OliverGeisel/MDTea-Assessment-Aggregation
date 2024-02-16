package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A material is a part of a course. It can be a text, an example, a proof, a definition and so on. It contains all
 * values for the specific Materialtype and AbstractTemplateCategory. It is used to generate the final material.
 * The AbstractTemplateCategory has all information for the template. Specific Materials have more information about the there content. This is
 * stored in a {@link TemporalType}.
 * <p>
 * The {@link MaterialType} is a general type of the material. It is only a classification from MDTea.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.0
 * @see MaterialOrderPart
 * @see TemplateType
 * @see MaterialType
 * @since 0.2.0
 */
@Entity
public class Material extends MaterialOrderPart {
	/**
	 * The values of the material, which are used in the template
	 */
	@ElementCollection
	@CollectionTable(name = "material_entity_map", joinColumns = @JoinColumn(name = "entity_id"))
	@MapKeyColumn(name = "key_column")
	@Column(name = "value_column", length = 2_000)
	private final Map<String, String> values      = new HashMap<>();
	/**
	 * The term name of the material, which is used in the template
	 */
	private       String              term        = "";
	/**
	 * The unique term id of the material, which is used in the template
	 */
	private       String              termId      = "";
	/**
	 * Part in the structure of the knowledge base
	 */
	private       String              structureId = "";
	@Enumerated(EnumType.ORDINAL)
	private       MaterialType        type;
	@Enumerated(EnumType.ORDINAL)
	@AttributeOverride(name = "type", column = @Column(name = "template_type"))
	private       TemplateType        templateType;

	/**
	 * DON'T USE IT! ONLY FOR JPA
	 */
	protected Material() {}

	protected Material(MaterialType type) {
		this(type, (TemplateType) null);
	}

	protected Material(MaterialType type, TemplateType templateType) {
		this.type = type;
		this.templateType = templateType;
	}

	/**
	 * Create a Material from a KnowledgeElement. The KnowledgeElement must not be null.
	 * <p>
	 * term will be the content of the KnowledgeElement termId will be the id of the KnowledgeElement structureId will
	 * be the structureId of the KnowledgeElement. Should be used with care. When content is lage term is misused.
	 * Use it only for {@link Term}s.
	 *
	 * @param type    The MaterialType
	 * @param element The KnowledgeElement
	 * @throws IllegalArgumentException if element is null
	 */
	public Material(MaterialType type, KnowledgeElement element) throws IllegalArgumentException {
		this.type = type;
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
		templateType = parseTemplateType(element);
		this.termId = element.getId();
		this.term = element.getContent();
		this.structureId = element.getStructureId();
	}

	public Material(MaterialType type, TemplateType templateType, String term, String termId, String structureId) {
		this("Material for %s".formatted(term), type, templateType, term, termId, structureId);
	}

	protected Material(String term, String termId, String structureId, MaterialType type, TemplateType templateType) {
		this.term = term;
		this.termId = termId;
		this.structureId = structureId;
		this.type = type;
		this.templateType = templateType;
	}

	/**
	 * Create a Material with a name. The name is used for the {@link MaterialOrderPart} and the {@link MaterialInfo}.
	 *
	 * @param name         the name of the material
	 * @param type         the type of the material
	 * @param templateType the template type of the material
	 * @param term         term name the material is related to
	 * @param termId       id of the term in the knowledge base
	 * @param structureId  id of the structure in the knowledge base
	 */
	public Material(String name, MaterialType type, TemplateType templateType, String term, String termId,
			String structureId) {
		super(name);
		this.type = type;
		this.term = term;
		this.termId = termId;
		this.structureId = structureId;
		this.templateType = templateType;
	}

	private static TemplateType parseTemplateType(KnowledgeElement element) {
		return TemplateType.valueOf(element.getClass().getSimpleName().toUpperCase());
	}

	public boolean addValue(String key, String value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("key and value must not be null");
		}
		return values.put(key, value) != null;
	}

	public boolean removeValue(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		}
		return values.remove(key) != null;
	}

	@Override
	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return null;
	}

	public String shortName() {
		return getName();
	}

	public boolean isIdentical(Material material) {
		if (material == null) {
			throw new IllegalArgumentException("material must not be null");
		}
		if (this == material) return true;
		return type.equals(material.getType())
			   && values.equals(material.getValues())
			   && term.equals(material.getTerm())
			   && structureId.equals(material.getStructureId());
	}

	//region setter/getter
	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	public MaterialInfo<Material> getMaterialInfo() {
		return new MaterialInfo<>(Material.class, templateType, type);
	}

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid() {
		return true;
	}

	public String getStructureId() {
		return structureId;
	}

	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public Map<String, String> getValues() {
		return values;
	}

	/**
	 * Set the values of the material. Deletes all existing values and replaces them with the new ones.
	 *
	 * @param values the values to set (must not be null)
	 */
	public void setValues(Map<String, String> values) {
		if (values == null) {
			throw new IllegalArgumentException("values must not be null");
		}
		this.values.clear();
		this.values.putAll(values);
	}

	public MaterialType getType() {
		return type;
	}

	public void setType(MaterialType type) {
		this.type = type;
	}
//endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Material material)) return false;
		if (!super.equals(o)) return false;

		if (!values.equals(material.values)) return false;
		if (!Objects.equals(term, material.term)) return false;
		if (!Objects.equals(termId, material.termId)) return false;
		if (!Objects.equals(structureId, material.structureId))
			return false;
		if (type != material.type) return false;
		return Objects.equals(templateType, material.templateType);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + values.hashCode();
		result = 31 * result + (term != null ? term.hashCode() : 0);
		result = 31 * result + (termId != null ? termId.hashCode() : 0);
		result = 31 * result + (structureId != null ? structureId.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (templateType != null ? templateType.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return STR."Material{term='\{term}', structureId='\{structureId}', type=\{type}, template=\{templateType}, values=\{values}}";
	}
}
