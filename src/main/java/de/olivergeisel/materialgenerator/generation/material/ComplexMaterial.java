package de.olivergeisel.materialgenerator.generation.material;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

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

	@OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private final List<Material> parts = new ArrayList<>();

	protected ComplexMaterial() {
		super(MaterialType.COMPLEX);
	}

	protected ComplexMaterial(List<Material> parts) {
		super(MaterialType.COMPLEX);
		this.parts.addAll(parts);
	}

	protected ComplexMaterial(MaterialType type) {
		super(type);
	}

	protected ComplexMaterial(MaterialType type, List<Material> parts) {
		super(type);
		this.parts.addAll(parts);
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


	//region setter/getter
	public List<Material> getParts() {
		return parts;
	}
//endregion
}
