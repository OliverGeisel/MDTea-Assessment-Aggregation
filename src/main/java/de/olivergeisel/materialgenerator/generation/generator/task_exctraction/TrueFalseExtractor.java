package de.olivergeisel.materialgenerator.generation.generator.task_exctraction;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;
import de.olivergeisel.materialgenerator.generation.material.assessment.TrueFalseQuestion;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Extract {@link TrueFalseQuestion}s from a {@link KnowledgeNode}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeNode
 * @see TrueFalseQuestion
 * @see Extractor
 * @since 1.1.0
 */
public class TrueFalseExtractor extends Extractor<TrueFalseQuestion> {

	@Override
	public List<MaterialAndMapping<TrueFalseQuestion>> extract(KnowledgeNode knowledgeNode,
			final TemplateType templateType) {
		var mainElement = knowledgeNode.getMainElement();
		knowledgeNode.getMasterKeyWord();
		var back = new LinkedList<MaterialAndMapping<TrueFalseQuestion>>();

		var allIsRelations = knowledgeNode.getWantedRelationsFrom(RelationType.IS);
		Random random = new Random();

		for (var relation : allIsRelations) {
			var trueStatement = random.nextBoolean(); // create random if the statement is true or false
			var correctSubject = relation.getFrom();
			var correctPredicate = relation.getTo();
			String subject = correctSubject.getContent();
			String predicate = correctPredicate.getContent();
			var newStatementParts = createStatementParts(trueStatement, subject, predicate, knowledgeNode);
			final var statement = STR."Ein(e) \{newStatementParts.subject} ist \{newStatementParts.predicate}.";
			var reason = createReason(knowledgeNode, RelationType.IS, trueStatement, newStatementParts,
					new StatementParts(correctSubject.getContent(), correctPredicate.getContent()));
			var question = new TrueFalseQuestion(statement, trueStatement, reason, templateType);
			var mappingEntry = new MaterialMappingEntry(question, correctSubject, correctPredicate);
			var mapping = new MaterialAndMapping<>(question, mappingEntry);
			back.add(mapping);
		}
		return back;
	}

	/**
	 * Creates the statement parts for the question. Depending on the trueStatement, the subject and predicate will
	 * be selected.
	 *
	 * @param trueStatement if the statement is true or false
	 * @param subject       the subject of the statement. Must be the correct subject at start.
	 * @param predicate     the predicate of the statement. Must be the correct predicate at start.
	 * @param knowledgeNode the {@link KnowledgeNode} where the statement is from. Needed if the
	 *                      statement is false, to create the wrong statement parts.
	 */
	private StatementParts createStatementParts(boolean trueStatement, String subject, String predicate,
			KnowledgeNode knowledgeNode) {
		if (trueStatement) {
			return new StatementParts(predicate, subject);
		}
		String newSubject = "";
		var terms = Arrays.stream(knowledgeNode.getRelatedElements())
						  .filter(it -> it.hasType(KnowledgeType.TERM) && !it.getContent().equals(predicate))
						  .toList();
		var termSize = terms.size();
		var random = new Random();
		var randomTerm = terms.get(random.nextInt(termSize));
		String newPredicate = randomTerm.getContent();
		return new StatementParts(newSubject, newPredicate);
	}


	private String createReason(KnowledgeNode knowledgeNode, RelationType relationType, boolean trueStatement,
			StatementParts statementParts, StatementParts correctStatementParts) {
		var allRelations = knowledgeNode.getWantedRelationsFrom(relationType);
		if (trueStatement) {
			return STR."Das Statement ist korrekt, weil ein(e) \{statementParts.subject} eine(e) \{statementParts.predicate} ist.";
		}
		return STR."Ein(e) \{statementParts.subject} ist keine(e) \{statementParts.predicate}, sondern eine(e) \{correctStatementParts.predicate}. Deshalb ist das Statement falsch.";
	}

	private record StatementParts(String subject, String predicate) {
	}

}

