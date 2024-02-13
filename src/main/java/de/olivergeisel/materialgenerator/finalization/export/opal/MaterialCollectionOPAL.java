package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;

import java.io.File;
import java.util.List;

interface MaterialCollectionOPAL<T extends MaterialOrderCollection, E extends MaterialOrderPart> {
	void createMaterials(File targetDirectory);

//region setter/getter
	long getNodeId();

	List<E> getMaterialOrder();

	T getOriginalCollection();

	String getName();
//endregion
}
