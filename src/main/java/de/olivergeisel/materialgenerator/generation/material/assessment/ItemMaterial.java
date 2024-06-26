package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Item;
import de.olivergeisel.materialgenerator.generation.configuration.ItemConfiguration;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Entity;


/**
 * Basic Material class for {@link Material} that are used for {@link Item}s or is a Material extracted from
 * a part of a {@link KnowledgeModel}.
 */
@Entity
public abstract class ItemMaterial extends Material {

	private ItemType itemType;

	protected ItemMaterial() {
		super(MaterialType.ITEM);
	}

	protected ItemMaterial(ItemConfiguration itemConfiguration, TemplateType templateType) {
		super(MaterialType.ITEM, templateType);
		itemType = itemConfiguration.getForItemType();
	}

	@Override
	public boolean isIdentical(Material material) {
		if (!(material instanceof ItemMaterial item)) {
			return false;
		}
		if (!super.isIdentical(material)) {
			return false;
		}
		return this.itemType.equals(item.itemType);
	}

	//region setter/getter
	public ItemType getItemType() {
		return itemType;
	}

	public abstract ItemConfiguration getItemConfiguration();
//endregion


}
