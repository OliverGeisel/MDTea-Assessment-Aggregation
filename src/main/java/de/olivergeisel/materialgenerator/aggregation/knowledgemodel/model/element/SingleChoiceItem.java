package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import jakarta.persistence.ElementCollection;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node
public class SingleChoiceItem extends Item {

	private String       correctAnswer;
	@ElementCollection
	private List<String> alternativeAnswers;

	protected SingleChoiceItem() {
		super("SINGLE_CHOICE");
	}

	public SingleChoiceItem(String question, List<String> answers, String id) throws IllegalArgumentException {
		super(question, id, "SINGLE_CHOICE");
		if (answers.size() < 2) {
			throw new IllegalArgumentException("A single choice item must have at least two answers.");
		}
		this.correctAnswer = answers.getFirst();
		this.alternativeAnswers = answers.subList(1, answers.size());
	}

//region setter/getter
	public String getQuestion() {
		return getContent();
	}
	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public List<String> getAlternativeAnswers() {
		return alternativeAnswers;
	}

	public void setAlternativeAnswers(List<String> alternativeAnswers) {
		this.alternativeAnswers = alternativeAnswers;
	}
//endregion
}
