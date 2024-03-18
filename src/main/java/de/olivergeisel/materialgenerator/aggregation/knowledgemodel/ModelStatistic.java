package de.olivergeisel.materialgenerator.aggregation.knowledgemodel;

public record ModelStatistic(long terms, long definitions, long examples, long items, long relations, long elements) {

	ModelStatistic() {
		this(0, 0, 0, 0, 0, 0);
	}

	public ModelStatistic(long terms, long definitions, long examples, long items, long relations) {
		this(terms, definitions, examples, items, relations, terms + definitions + examples + items);
	}

}
