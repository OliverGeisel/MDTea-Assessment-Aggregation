package de.olivergeisel.materialgenerator.generation.templates;

import de.olivergeisel.materialgenerator.generation.material.Material;
import jakarta.persistence.Embeddable;

/**
 * This class represents the type of template. It is used to distinguish between different types of templates.
 * The type is used to determine which template should be used to generate the final document.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Material
 * @since 1.1.0
 */
@Embeddable
public class TemplateType {

	public static final TemplateType DEFINITION = new TemplateType("DEFINITION");
	public static final TemplateType EXERCISE   = new TemplateType("EXERCISE");
	public static final TemplateType SOLUTION   = new TemplateType("SOLUTION");
	public static final TemplateType TEXT       = new TemplateType("TEXT");
	public static final TemplateType IMAGE      = new TemplateType("IMAGE");
	public static final TemplateType CODE       = new TemplateType("CODE");
	public static final TemplateType SYNONYM    = new TemplateType("SYNONYM");
	public static final TemplateType ACRONYM    = new TemplateType("ACRONYM");
	public static final TemplateType LIST       = new TemplateType("LIST");
	public static final TemplateType EXAMPLE    = new TemplateType("EXAMPLE");
	public static final TemplateType PROOF      = new TemplateType("PROOF");
	public static final TemplateType TASK       = new TemplateType("TASK");
	public static final TemplateType SUMMARY = new TemplateType("SUMMARY");

	private String type;

	/**
	 * Constructor. The type will be set to the given String.
	 *
	 * @param type The type to set.
	 */
	public TemplateType(String type) {
		this.type = type;
	}

	/**
	 * Default constructor. TemplateType will be set to "TEXT".
	 */
	public TemplateType() {
		this("TEXT");
	}

	/**
	 * Find the TemplateType for the given String. Will return a new TemplateType if no match is found.
	 *
	 * @param typeString The String to find the TemplateType for.
	 * @return The TemplateType for the given String.
	 * @throws IllegalArgumentException If the given String is null or blank.
	 */
	public static TemplateType valueOf(String typeString) throws IllegalArgumentException {
		if (typeString == null || typeString.isBlank()) {
			throw new IllegalArgumentException("The typeString must not be null or blank.");
		}
		return switch (typeString) {
			case "DEFINITION" -> DEFINITION;
			case "EXERCISE" -> EXERCISE;
			case "SOLUTION" -> SOLUTION;
			case "TEXT" -> TEXT;
			case "LIST" -> LIST;
			case "EXAMPLE" -> EXAMPLE;
			case "ACRONYM" -> ACRONYM;
			case "SYNONYM" -> SYNONYM;
			case "PROOF" -> PROOF;
			case "TASK" -> TASK;
			case "SUMMARY" -> SUMMARY;
			default -> new TemplateType(typeString);
		};
	}

	//region setter/getter
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TemplateType that)) return false;

		return type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return STR."TemplateType{type='\{type}\{'\''}\{'}'}";
	}
}
