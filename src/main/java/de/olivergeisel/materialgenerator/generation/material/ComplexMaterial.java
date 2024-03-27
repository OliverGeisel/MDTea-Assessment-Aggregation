package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A complex material is a material that consists of multiple other {@link Material}s.
 * Generally, it's a simple list of materials. The order of the materials is <b>important</b>.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @since 0.2.0
 */
@Entity
public abstract class ComplexMaterial extends Material {

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "complex_material_material",
			joinColumns = @JoinColumn(name = "complex_material_id"),
			inverseJoinColumns = @JoinColumn(name = "material_id"))
	private final List<Material> parts       = new ArrayList<>();
	private       String         description = "";

	protected ComplexMaterial() {
		super(MaterialType.COMPLEX);
	}

	protected ComplexMaterial(List<Material> parts) {
		super(MaterialType.COMPLEX);
		this.parts.addAll(parts);
	}

	protected ComplexMaterial(MaterialType type, TemplateType templateType, Term term) {
		super(type, templateType, term.getContent(), term.getId(), term.getStructureId());
	}

	protected ComplexMaterial(MaterialType type, TemplateType templateType) {
		super(type, templateType);
	}

	protected ComplexMaterial(MaterialType type, TemplateType templateType, List<Material> parts) {
		super(type, templateType);
		if (parts != null) {
			this.parts.addAll(parts);
		}
	}

	protected ComplexMaterial(MaterialType type, TemplateType templateType, Term term, List<Material> parts) {
		super(type, templateType, term.getContent(), term.getId(), term.getStructureId());
		this.parts.addAll(parts);
	}

	public int size() {
		return parts.size();
	}

	public boolean move(Material part, int index) {
		if (parts.contains(part)) {
			parts.remove(part);
			parts.add(index, part);
			return true;
		}
		return false;
	}

	public boolean append(Material part) {
		return parts.add(part);
	}

	public boolean remove(Material part) {
		return parts.remove(part);
	}

	public boolean remove(int index) {
		if (index >= 0 && index < parts.size()) {
			parts.remove(index);
			return true;
		}
		return false;
	}

	public boolean replace(Material oldPart, Material newPart) {
		if (parts.contains(oldPart)) {
			parts.remove(oldPart);
			parts.add(newPart);
			return true;
		}
		return false;
	}

	public boolean moveUp(Material part) {
		if (parts.contains(part)) {
			int index = parts.indexOf(part);
			if (index > 0) {
				parts.remove(part);
				parts.add(index - 1, part);
				return true;
			}
		}
		return false;
	}

	public boolean moveDown(Material part) {
		if (parts.contains(part)) {
			int index = parts.indexOf(part);
			if (index < parts.size() - 1) {
				parts.remove(part);
				parts.add(index + 1, part);
				return true;
			}
		}
		return false;
	}

	public boolean moveTop(Material part) {
		if (parts.contains(part)) {
			parts.remove(part);
			parts.add(0, part);
			return true;
		}
		return false;
	}

	public boolean moveBottom(Material part) {
		if (parts.contains(part)) {
			parts.remove(part);
			parts.add(part);
			return true;
		}
		return false;
	}

	public int indexOf(Material part) {
		return parts.indexOf(part);
	}


	@Override
	public boolean isIdentical(Material material) {
		if (!(super.isIdentical(material))) {
			return false;
		}
		if (material instanceof ComplexMaterial complexMaterial) {
			if (complexMaterial.size() == size()) {
				for (int i = 0; i < size(); i++) {
					if (!parts.get(i).isIdentical(complexMaterial.parts.get(i))) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}


	//region setter/getter
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Material> getParts() {
		return parts;
	}
//endregion
}
