package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import org.springframework.data.neo4j.core.schema.Node;

@Node
public class TrueFalseItem extends Item {

//region setter/getter

	private boolean correct;

	public TrueFalseItem() {
		super("TRUE_FALSE");
		this.correct = true;
	}

	public TrueFalseItem(String question, boolean correct, String id) {
		super(question, id, "TRUE_FALSE");
		this.correct = correct;
	}
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	public boolean isCorrect() {
		return correct;
	}

	public String getQuestion() {
		return getContent();
	}
//endregion

}
