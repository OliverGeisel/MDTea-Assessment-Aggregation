package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.generation.configuration.FillOutBlanksConfiguration;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A FillOutBlanksItemMaterial is a Material that contains a text with blanks that have to be filled out.
 * <p>
 *
 * <h3>Example</h3>
 * "MDTea has the four phases: <b>Aggregation</b>, |_____________|, <b>Finalization</b> and <b>Synchronization</b>"
 * <br>
 * In the blank field the correct answer hast to be filled in.
 *
 * @version 1.1.0
 * @see ItemMaterial
 * @since 1.1.0
 */
@Entity
public class FillOutBlanksItemMaterial extends ItemMaterial {

	public static final String BLANK = "<_____>";
	private static final FillOutBlanksConfiguration DEFAULT_CONFIGURATION = new FillOutBlanksConfiguration();


	private String      body;
	@ElementCollection
	private List<Blank> blanks;
	@Embedded
	private FillOutBlanksConfiguration itemConfiguration;

	protected FillOutBlanksItemMaterial() {
		super(DEFAULT_CONFIGURATION, TemplateType.FILL_OUT_BLANKS);
		itemConfiguration = DEFAULT_CONFIGURATION;
		blanks = new ArrayList<>();
	}

	public FillOutBlanksItemMaterial(String body, List<Blank> blanks) {
		super(DEFAULT_CONFIGURATION, TemplateType.FILL_OUT_BLANKS);
		this.body = body;
		this.blanks = new ArrayList<>(blanks);
		this.itemConfiguration = DEFAULT_CONFIGURATION;
	}

	public FillOutBlanksItemMaterial(String body, List<Blank> blanks, FillOutBlanksConfiguration configuration) {
		super(configuration, TemplateType.FILL_OUT_BLANKS);
		this.body = body;
		this.blanks = new ArrayList<>(blanks);
		this.itemConfiguration = configuration;
	}

	//region setter/getter
	@Override
	public FillOutBlanksConfiguration getItemConfiguration() {
		return itemConfiguration;
	}

	public String getBody() {
		return body;
	}

	public List<Blank> getBlanks() {
		return blanks;
	}
//endregion

	/**
	 * A blank in the text that has to be filled out.
	 */
	@Embeddable
	public static class Blank {
		private boolean      caseSensitive;
		private String       correctAnswer;
		private int          error;
		private List<String> alternativeAnswers;

		protected Blank() {
		}

		public Blank(boolean caseSensitive, String correctAnswer, int error, List<String> alternativeAnswers) {
			this.caseSensitive = caseSensitive;
			this.correctAnswer = correctAnswer;
			this.error = error;
			this.alternativeAnswers = alternativeAnswers;
		}

		//region setter/getter
		public boolean isCaseSensitive() {
			return caseSensitive;
		}

		public String getCorrectAnswer() {
			return correctAnswer;
		}

		public int getError() {
			return error;
		}

		public List<String> getAlternativeAnswers() {
			return alternativeAnswers;
		}
//endregion
	}
}
