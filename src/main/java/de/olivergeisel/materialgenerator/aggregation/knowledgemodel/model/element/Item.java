package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collection;

/**
 * Represents a task in the knowledge base.
 * A {@link Item} is the base class for all items (Questions or similar) in the knowledge base. It contains the task
 * type. Every subclass has as KnowledgeType {@link KnowledgeType#ITEM}. But the itemType is different.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see ItemType
 * @see KnowledgeType
 * @see KnowledgeElement
 * @since 1.1.0
 */
@Node
public abstract class Item extends KnowledgeElement {

	private ItemType itemType;

	protected Item() {
		super();
		this.itemType = ItemType.UNDEFINED;
	}

	protected Item(String itemType) {
		super();
		this.itemType = ItemType.valueOf(itemType.toUpperCase());
	}

	protected Item(String content, String id, String itemType) {
		super(content, id, KnowledgeType.ITEM.name());
		this.itemType = ItemType.valueOf(itemType.toUpperCase());
	}

	protected Item(String content, String id, String itemType, Collection<Relation> relations) {
		super(content, id, KnowledgeType.TEXT.name(), relations);
		this.itemType = ItemType.valueOf(itemType.toUpperCase());
	}

	//region setter/getter
	public ItemType getItemType() {
		return itemType;
	}
//endregion


}
