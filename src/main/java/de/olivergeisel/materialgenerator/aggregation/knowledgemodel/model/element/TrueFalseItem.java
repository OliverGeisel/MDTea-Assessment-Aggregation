package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import org.springframework.data.neo4j.core.schema.Node;

@Node
public class TrueFalseItem extends Item {

	private boolean correct;

	public TrueFalseItem() {
		super("TRUE_FALSE");
		this.correct = true;
	}

	public TrueFalseItem(String question, boolean correct, String id) {
		super(question, id, "TRUE_FALSE");
		this.correct = correct;
	}

//region setter/getter
	public boolean isCorrect() {
		return correct;
	}

	public String getQuestion() {
		return getContent();
	}
//endregion

}
