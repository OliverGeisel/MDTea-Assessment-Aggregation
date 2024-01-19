package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A material is a part of a course. It can be a text, an example, a proof, a definition and so on. It contains all
 * values for the specific Materialtype and TemplateInfo. It is used to generate the final material. The TemplateInfo
 * has all information for the template. Specific Materials have more Information about the Material.  @see
 * TemplateInfo for more information.
 * <p>
 * The MaterialType is a general type of the material. It is only a classification from MDTea.
 *
 * @author Oliver Geisel
 * @version 1.0
 * @see de.olivergeisel.materialgenerator.core.course.MaterialOrderPart
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
	private final Map<String, String> values = new HashMap<>();
	/**
	 * The term name of the material, which is used in the template
	 */
	private       String              term;
	/**
	 * The unique term id of the material, which is used in the template
	 */
	private       String              termId;
	/**
	 * Part in the structure of the knowledge base
	 */
	private       String              structureId;
	@Enumerated(EnumType.ORDINAL)
	private       MaterialType        type;
	@ManyToOne(cascade = CascadeType.ALL)
	private       TemplateInfo        templateInfo;

	protected Material() {

	}

	protected Material(MaterialType type) {
		this(type, (TemplateInfo) null);
	}

	protected Material(MaterialType type, TemplateInfo templateInfo) {
		this.type = type;
		this.templateInfo = templateInfo;
	}

	public Material(MaterialType type, String term, String termId, String structureId) {
		this("Material for %s".formatted(term), type, term, termId, structureId);
	}

	public Material(String name, MaterialType type, String term, String termId, String structureId) {
		super(name);
		this.type = type;
		this.term = term;
		this.termId = termId;
		this.structureId = structureId;
		templateInfo = null;
	}

	/**
	 * Create a Material from a KnowledgeElement. The KnowledgeElement must not be null.
	 * <p>
	 * term will be the content of the KnowledgeElement termId will be the id of the KnowledgeElement structureId will
	 * be the structureId of the KnowledgeElement. Should be used with care. When content is lage term is misused.
	 * Use ist only for Terms.
	 *
	 * @param type    The MaterialType
	 * @param element The KnowledgeElement
	 * @throws IllegalArgumentException if element is null
	 */
	public Material(MaterialType type, KnowledgeElement element) throws IllegalArgumentException {
		this.type = type;
		templateInfo = null;
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
		this.termId = element.getId();
		this.term = element.getContent();
		this.structureId = element.getStructureId();
	}

	protected Material(String term, String termId, String structureId, MaterialType type, TemplateInfo templateInfo) {
		this.term = term;
		this.termId = termId;
		this.structureId = structureId;
		this.type = type;
		this.templateInfo = templateInfo;
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

	public TemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(TemplateInfo templateInfo) {
		this.templateInfo = templateInfo;
	}
//endregion

	@Override
	public String toString() {
		return "Material{" + "term='" + term + '\'' + ", structureId='" + structureId + '\''
			   + ", type=" + type + ", template=" + templateInfo + ", values=" + values + '}';
	}
}
