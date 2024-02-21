package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;

@Node
public class MultipleChoiceItem extends Item {

	private List<String> correctAnswers     = new ArrayList<>();
	private List<String> alternativeAnswers = new ArrayList<>();
	private boolean      shuffle;

	protected MultipleChoiceItem() {
		super("MULTIPLE_CHOICE");
	}

	public MultipleChoiceItem(String question, List<String> answers, int numberCorrect, boolean shuffle, String id) {
		super(question, id, "MULTIPLE_CHOICE");
		if (numberCorrect < 1) {
			throw new IllegalArgumentException("A multiple choice item must have at least one correct answer.");
		}
		if (numberCorrect > answers.size()) {
			throw new IllegalArgumentException("A multiple choice item cannot have more correct answers than answers.");
		}
		this.correctAnswers = answers.subList(0, numberCorrect);
		this.alternativeAnswers = answers.subList(numberCorrect, answers.size());
		this.shuffle = shuffle;
	}

	//region setter/getter
	public List<String> getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(List<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public List<String> getAlternativeAnswers() {
		return alternativeAnswers;
	}

	public void setAlternativeAnswers(List<String> alternativeAnswers) {
		this.alternativeAnswers = alternativeAnswers;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public int getNumberCorrect() {
		return correctAnswers.size();
	}

	public int getNumberAnswers() {
		return correctAnswers.size() + alternativeAnswers.size();
	}
//endregion
}
