package de.olivergeisel.materialgenerator.generation.material.assessment;

/**
 * The type of {@link ItemMaterial} in an assessment or {@link TestMaterial}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see TestMaterial
 * @see ItemMaterial
 * @since 1.1.0
 */
public enum ItemType {

	FILL_OUT_BLANKS("FILL_OUT_BLANKS"),
	SINGLE_CHOICE("SINGLE_CHOICE"),
	MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
	TRUE_FALSE("TRUE_FALSE"),
	ASSIGNMENT("ASSIGNMENT"),
	UNDEFINED("UNDEFINED"); // used when no type is defined or error occurred

	private final String type;

	ItemType(String type) {
		this.type = type;
	}

	/**
	 * Checks if the given type is equal to the current type. Ignores case.
	 *
	 * @param type The type to check
	 * @return true if the given type is equal to the current type. Ignores case.
	 */
	public boolean machtType(String type) {
		return this.type.equalsIgnoreCase(type);
	}

	//region setter/getter
	public String getType() {
		return type;
	}
//endregion
}
