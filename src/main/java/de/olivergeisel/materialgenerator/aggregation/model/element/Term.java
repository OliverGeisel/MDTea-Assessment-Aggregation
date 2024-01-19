package de.olivergeisel.materialgenerator.aggregation.model.element;

import de.olivergeisel.materialgenerator.aggregation.model.relation.Relation;

import java.util.Collection;

public class Term extends TermElement {
	private final String termName;

	public Term(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
		termName = content;
	}

	public Term(String content, String id, String type) {
		super(content, id, type);
		termName = content;
	}

	//region setter/getter
	public String getTermName() {
		return termName;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Term term)) return false;
		if (!super.equals(o)) return false;

		return termName.equals(term.termName);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + termName.hashCode();
		return result;
	}
}
