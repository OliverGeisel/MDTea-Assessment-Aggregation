package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

@Node
public class Term extends TermElement {

	private String termName;

	protected Term() {
		super();
		termName = "";
	}

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

	@Override
	public void setContent(String content) {
		super.setContent(content);
		termName = content;
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
