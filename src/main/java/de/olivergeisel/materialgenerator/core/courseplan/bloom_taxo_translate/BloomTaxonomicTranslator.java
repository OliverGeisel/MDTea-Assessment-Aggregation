package de.olivergeisel.materialgenerator.core.courseplan.bloom_taxo_translate;

import de.olivergeisel.materialgenerator.core.courseplan.CurriculumGoalExpression;

public class BloomTaxonomicTranslator {

	private BloomTaxonomicTranslator() {
	}

	public static CurriculumGoalExpression translate(String word) {
		return CurriculumGoalExpression.valueOf(word.toUpperCase().trim().replace(" ", "_"));
	}
}
