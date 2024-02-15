package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.TaskConfiguration;
import de.olivergeisel.materialgenerator.generation.configuration.TrueFalseConfiguration;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;

@Entity
public class TrueFalseQuestion extends ItemMaterial {

	private static final ItemConfiguration DEFAULT_CONFIGURATION = new TrueFalseConfiguration();

	private final boolean trueStatement;
	private       String  statement;
	private       String  reason;


	protected TrueFalseQuestion() {
		this.statement = "";
		this.trueStatement = true;
		this.reason = "";
	}

	/**
	 * Creates a new TrueFalseQuestion.
	 *
	 * @param statement     The question to ask.
	 * @param trueStatement The correct answer to the question.
	 * @param reason        The reason why the statement is correct or wrong. Can be empty. (Never null)
	 * @throws IllegalArgumentException If the statement is null or blank.
	 */
	public TrueFalseQuestion(String statement, boolean trueStatement, String reason, TemplateType templateType)
			throws IllegalArgumentException {
		super(DEFAULT_CONFIGURATION, "", templateType);
		if (statement == null || statement.isBlank())
			throw new IllegalArgumentException("The statement must not be null or blank.");
		this.statement = statement;
		this.trueStatement = trueStatement;
		this.reason = reason != null ? reason : "";
	}

	/**
	 * Creates a new TrueFalseQuestion with the statement is correct.
	 *
	 * @param statement The question to ask.
	 * @param reason    The reason why the statement is correct. Can be empty. (Never null)
	 * @throws IllegalArgumentException If the statement is null or blank.
	 */
	public TrueFalseQuestion(String statement, String reason, TemplateType templateType)
			throws IllegalArgumentException {
		this(statement, true, reason, templateType);
	}

	public TrueFalseQuestion(String statement, boolean trueStatement, String reason, ItemConfiguration configuration)
			throws IllegalArgumentException {
		super(configuration, "", null);
		if (statement == null || statement.isBlank())
			throw new IllegalArgumentException("The statement must not be null or blank.");
		this.statement = statement;
		this.trueStatement = trueStatement;
		this.reason = reason != null ? reason : "";
	}

//region setter/getter
	public String getStatement() {
		return statement;
	}

	public boolean isTrueStatement() {
		return trueStatement;
	}

	public String getReason() {
		return reason;
	}
//endregion
}
