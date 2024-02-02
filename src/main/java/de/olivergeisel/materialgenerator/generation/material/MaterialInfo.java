package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import java.lang.reflect.Field;
import java.util.Arrays;


/**
 * A class that holds information about a specific {@link Material} class. It's used to get the information about the
 * fields of the class and the {@link TemplateType} of the {@link Material}.
 *
 * @param <T> The type of the {@link Material} class.
 */
public class MaterialInfo<T extends Material> {

	private final String[]     fields;
	private final Class<T>     materialClass;
	private final TemplateType templateType;
	private final MaterialType materialType;

	public MaterialInfo(Class<T> materialClass, TemplateType type, MaterialType materialType, String... fields) {
		this.materialClass = materialClass;
		this.materialType = materialType;
		this.fields = fields;
		this.templateType = type;
	}

	public MaterialInfo(Class<T> materialClass, TemplateType type, MaterialType materialType) {
		this.materialClass = materialClass;
		this.materialType = materialType;
		var fieldFromClass = Arrays.stream(materialClass.getDeclaredFields()).map(Field::getName);
		this.fields = fieldFromClass.toArray(String[]::new);
		this.templateType = type;
	}

	//region setter/getter
	public MaterialType getMaterialType() {
		return materialType;
	}

	public Class<T> getMaterialClass() {
		return materialClass;
	}

	public String[] getFields() {
		return fields;
	}

	public TemplateType getTemplateType() {
		return templateType;
	}
//endregion
}
