package de.olivergeisel.materialgenerator.generation.material.assessment;

public enum TaskType {

	FILL_OUT_BLANKS("FILL_OUT_BLANKS"),
	SINGLE_CHOICE("SINGLE_CHOICE"),
	MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
	TRUE_FALSE("TRUE_FALSE"),
	ASSIGNMENT("ASSIGNMENT"),
	UNDEFINED("UNDEFINED"); // used when no type is defined or error occurred

	private final String type;

	TaskType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	/**
	 * Checks if the given type is equal to the current type. Ignores case.
	 * @param type The type to check
	 * @return true if the given type is equal to the current type. Ignores case.
	 */
	public boolean machtType(String type) {
		return type.equalsIgnoreCase(type);
	}
}
