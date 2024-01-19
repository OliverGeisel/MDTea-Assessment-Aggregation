package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.KnowledgeFragment;
import de.olivergeisel.materialgenerator.aggregation.model.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;

import java.util.List;

/**
 * A Negotiator is a class that extracts a list of {@link KnowledgeElement}s from a given source/{@link KnowledgeFragment}.
 * But the Elements <b>must not be</b> correct. The user must check the elements for correctness.
 * So the main purpose of a Negotiator is to extract the elements from a source/fragment and hold it until the user
 * is okay with it. Is all okay, the elements will be added to a {@link KnowledgeModel}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see KnowledgeElement
 * @see KnowledgeModel
 * @since 1.1.0
 */
public class ElementNegotiator implements Negotiator<KnowledgeElement> {


	@Override
	public List<KnowledgeElement> extract() {
		return null;
	}
}
