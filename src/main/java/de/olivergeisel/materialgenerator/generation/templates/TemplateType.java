package de.olivergeisel.materialgenerator.generation.templates;

import jakarta.persistence.Embeddable;

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

	private String type;

	public TemplateType(String type) {
		this.type = type;
	}

	public TemplateType() {
		this("TEXT");
	}

	public static TemplateType valueOf(String typeString) {
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
		return "TemplateType{" + "type='" + type + '\'' + '}';
	}
}
