package de.olivergeisel.materialgenerator.finalization.parts;

public record MaterialHierarchy(String chapter, String group, String task, String material, int count, int size) {


	MaterialHierarchy() {
		this(null, null, null, null, 0, 0);
	}


}
