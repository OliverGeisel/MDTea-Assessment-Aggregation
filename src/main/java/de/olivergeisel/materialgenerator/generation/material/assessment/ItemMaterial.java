package de.olivergeisel.materialgenerator.generation.material.assessment;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeModel;
import de.olivergeisel.materialgenerator.generation.configuration.ItemConfiguration;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialType;
import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;


/**
 * Basic Material class for {@link Material} that are used for {@link Item}s or is a Material extracted from
 * a part of a {@link KnowledgeModel}.
 */
@Entity
public abstract class ItemMaterial extends Material {

	private ItemType          itemType;
	@Embedded
	private ItemConfiguration itemConfiguration;

	protected ItemMaterial() {
		super();
	}

	protected ItemMaterial(ItemConfiguration itemConfiguration, TemplateType templateType) {
		super(MaterialType.ITEM, templateType);
		this.itemConfiguration = itemConfiguration;
		itemType = itemConfiguration.getForItemType();
	}



}