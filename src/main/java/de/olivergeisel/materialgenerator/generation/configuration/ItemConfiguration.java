package de.olivergeisel.materialgenerator.generation.configuration;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Item;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemMaterial;
import de.olivergeisel.materialgenerator.generation.material.assessment.ItemType;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

/**
 * A Configuration for a {@link ItemMaterial}. the ItemType say which {@link Item} is used.
 * The {@link TestParameters} are optional and can be used to configure the material for a {@link TestMaterial}.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Item
 * @see Material
 * @see ItemMaterial
 * @since 1.1.0
 */
@Embeddable
public class ItemConfiguration {

	private ItemType forItemType;
	@Embedded
	private TestParameters testParameters = TestParameters.DEFAULT;

	protected ItemConfiguration(ItemType forItemType) {
		this(forItemType, null);
	}

	protected ItemConfiguration(ItemType itemType, TestParameters testParameters) {
		this.forItemType = itemType;
		this.testParameters = testParameters == null ? TestParameters.DEFAULT : testParameters;
	}

	protected ItemConfiguration() {

	}

	@Override
	public ItemConfiguration clone() {
		return new ItemConfiguration(forItemType, testParameters.clone());
	}

	//region setter/getter
//endregion
	public TestParameters getTestParameters() {
		return testParameters;
	}

	public ItemType getForItemType() {
		return forItemType;
	}

	@Override
	public String toString() {
		return STR."ItemConfiguration{forItemType=\{forItemType}, testParameters=\{testParameters}}";
	}


}
