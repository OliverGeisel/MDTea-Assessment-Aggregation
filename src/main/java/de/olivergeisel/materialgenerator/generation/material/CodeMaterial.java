package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class CodeMaterial extends Material {

	private String language;
	@Column(length = 100_000)
	private String code;
	private String title;


	/**
	 * Creates a new CodeMaterial.
	 *
	 * @param language programming language the code is written
	 * @param code     complete sourcecode
	 * @param element  the related {@link KnowledgeElement} for the code
	 * @throws IllegalArgumentException if language or code is null
	 */
	public CodeMaterial(String language, String code, KnowledgeElement element) throws IllegalArgumentException {
		this(language, code, "", element);
	}

	/**
	 * @param language programming language the code is written
	 * @param code     complete sourcecode
	 * @param title    title of the code
	 * @param element  the related {@link KnowledgeElement} for the code
	 * @throws IllegalArgumentException if language or code is null
	 * @throws NullPointerException     if element is null
	 */
	public CodeMaterial(String language, String code, String title, KnowledgeElement element)
			throws IllegalArgumentException, NullPointerException {
		super(MaterialType.WIKI, title, element.getId(), element.getStructureId());
		if (language == null) {
			throw new IllegalArgumentException("language must not be null!");
		}
		if (code == null) {
			throw new IllegalArgumentException("code must not be null!");
		}
		this.language = language;
		this.code = code.replace("\n", "<br>").replace("\\t", "\t").replace("\\\\t", "\t");
		this.title = title;
	}

	protected CodeMaterial() {
		super(MaterialType.WIKI);
	}

	@Override
	public String shortName() {
		return "Code: " + title;
	}

	//region setter/getter
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
//endregion

	@Override
	public String toString() {
		var length = Math.min(code.length(), 10);
		return "CodeMaterial{" +
			   "title='" + title + '\'' +
			   ", code='" + code.substring(0, 10) + "...'" +
			   ", language='" + language + '\'' +
			   '}';
	}
}
