package de.olivergeisel.materialgenerator.core.course;

import de.olivergeisel.materialgenerator.finalization.parts.CourseNavigation;
import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

/**
 * A MaterialOrderPart is an element of a structured Collection that represents Materials for a course.
 * <p>
 * It can be a Collection, of other {@link MaterialOrderPart}s or a {@link de.olivergeisel.materialgenerator.generation.material.Material}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @since 0.2.0
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class MaterialOrderPart {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID   id;
	private String name = "";

	protected MaterialOrderPart() {
	}

	protected MaterialOrderPart(String name) {
		this.name = name;
	}

	/**
	 * Find a part by its id. Must return null if not found.
	 *
	 * @param id id of the part
	 * @return the part or null if not found
	 */
	public abstract MaterialOrderPart find(UUID id);

//region setter/getter

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	public abstract boolean isValid();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSaveName() {
		return name.replaceAll(CourseNavigation.PATH_REPLACE_REGEX, "_");
	}

	public UUID getId() {
		return id;
	}
//endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MaterialOrderPart that)) return false;

		if (!Objects.equals(id, that.id)) return false;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return STR."MaterialOrderPart{name='\{name}', id=\{id}}";
	}
}

