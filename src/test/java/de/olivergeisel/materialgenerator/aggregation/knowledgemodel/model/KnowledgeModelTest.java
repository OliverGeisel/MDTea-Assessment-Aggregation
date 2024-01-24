package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model;


import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.BasicRelation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.Relation;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.KnowledgeLeaf;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure.RootStructureElement;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.old_version.KnowledgeModel;
import de.olivergeisel.materialgenerator.aggregation.source.KnowledgeSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
class KnowledgeModelTest {

	private KnowledgeModel knowledgeModel;

	@BeforeEach
	void setUp() {
		knowledgeModel = new KnowledgeModel();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void createKnowledgeModel() {
		var knowledgeModel = new KnowledgeModel();
	}

	@Test
	void createWithParameter() {
		var root = mock(RootStructureElement.class);
		var knowledgeModel = new KnowledgeModel(root);
	}

	@Test
	void creteWithRootIsNull() {
		assertThrows(IllegalArgumentException.class, () -> new KnowledgeModel(null));
	}

	@Test
	void addKnowledgeNewNodeTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		assertEquals(0, knowledgeModel.getElements().size());
		knowledgeModel.addKnowledge(newKnowledge);
		assertEquals(1, knowledgeModel.getElements().size());
	}

	@Test
	void addKnowledgeExistingNodeTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		knowledgeModel.addKnowledge(newKnowledge);
		assertEquals(1, knowledgeModel.getElements().size());
		knowledgeModel.addKnowledge(newKnowledge);
		assertEquals(1, knowledgeModel.getElements().size());
	}

	@Test
	void addKnowledgeNullTest() {
		KnowledgeElement newKnowledge = null;
		assertThrows(IllegalArgumentException.class, () -> knowledgeModel.addKnowledge(newKnowledge));
	}

	@Test
	void getElementsTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		assertEquals(0, knowledgeModel.getElements().size());
		knowledgeModel.addKnowledge(newKnowledge);
		assertEquals(1, knowledgeModel.getElements().size());
	}

	@Test
	void testAddAndLink() {
		var newKnowledge = mock(KnowledgeElement.class);
		var newKnowledge1 = mock(KnowledgeElement.class);
		var relation = mock(Relation.class);
		when(newKnowledge.getId()).thenReturn("1");
		when(newKnowledge1.getId()).thenReturn("2");
		when(relation.getFrom()).thenReturn(newKnowledge);
		when(relation.getTo()).thenReturn(newKnowledge1);
		when(relation.getFromId()).thenReturn("1");
		when(relation.getToId()).thenReturn("2");
		when(newKnowledge.getRelations()).thenReturn(Set.of(relation));
		when(relation.getType()).thenReturn(RelationType.RELATED);
		knowledgeModel.addAndLink(newKnowledge, newKnowledge1, relation);
		var foundById = knowledgeModel.get("1");
		assertEquals(2, knowledgeModel.getElements().size());
		assertEquals(1, foundById.getRelations().size());
	}


	@Test
	void testAddKnowledge() {
		assertEquals(0, knowledgeModel.getElements().size());
		knowledgeModel.addKnowledge(mock(KnowledgeElement.class));
		assertEquals(1, knowledgeModel.getElements().size());
	}

	@Test
	void addSource() {
		var source = mock(KnowledgeSource.class);
		assertTrue(knowledgeModel.addSource(source));
	}

	@Test
	void addStructureToNullParamasTest() {
		var fragment = mock(KnowledgeFragment.class);
		var object = mock(KnowledgeLeaf.class);
		assertAll(() -> assertFalse(knowledgeModel.addStructureTo(null, object)),
				() -> assertFalse(knowledgeModel.addStructureTo(fragment, null)),
				() -> assertFalse(knowledgeModel.addStructureTo(null, null))
		);
	}

	@Test
	void addStructureToNotContainingStructureTest() {
		var fragment = mock(KnowledgeFragment.class);
		var object = mock(KnowledgeLeaf.class);
		assertFalse(knowledgeModel.addStructureTo(fragment, object));
	}

	@Test
	void addStructureToTest() {
		var root = knowledgeModel.getRoot();
		var newStructure = new KnowledgeFragment("Fragment");
		var newStructure1 = mock(KnowledgeLeaf.class);
		when(newStructure1.getName()).thenReturn("S1-1");
		when(newStructure1.getIdUnified()).thenReturn("S1 1");
		assertTrue(knowledgeModel.addStructureTo(root, newStructure));
		assertTrue(knowledgeModel.addStructureTo(newStructure, newStructure1));
		assertTrue(newStructure.contains(newStructure1.getIdUnified()));
	}

	@Test
	void containsStringThrowsIllegalArgumentExceptionTest() {
		assertThrows(IllegalArgumentException.class, () -> knowledgeModel.contains((String) null));
	}

	@Test
	void containsStringTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		when(newKnowledge.getId()).thenReturn("1");
		knowledgeModel.addKnowledge(newKnowledge);
		assertTrue(knowledgeModel.contains("1"));
	}

	@Test
	void containsElementThrowsIllegalArgumentExceptionTest() {
		assertThrows(IllegalArgumentException.class, () -> knowledgeModel.contains((KnowledgeElement) null));
	}

	@Test
	void containsElementTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		when(newKnowledge.getId()).thenReturn("1");
		knowledgeModel.addKnowledge(newKnowledge);
		assertTrue(knowledgeModel.contains(newKnowledge));
	}

	@Test
	void findAllEmptyModelTest() {
		assertThrows(NoSuchElementException.class, () -> knowledgeModel.findAll("1"));
	}

	@Test
	void getInEmptyModelTest() {
		assertThrows(NoSuchElementException.class, () -> knowledgeModel.get("1"));
	}

	@Test
	void getElementNotPartTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		when(newKnowledge.getId()).thenReturn("1");
		knowledgeModel.addKnowledge(newKnowledge);
		assertThrows(NoSuchElementException.class, () -> knowledgeModel.get("2"));
	}

	@Test
	void getElementPartTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		when(newKnowledge.getId()).thenReturn("1");
		knowledgeModel.addKnowledge(newKnowledge);
		var foundById = knowledgeModel.get("1");
		assertEquals(newKnowledge, foundById);
	}

	@Test
	void hasUnfinishedRelationsAtStartTest() {
		assertFalse(knowledgeModel.hasUnfinishedRelations());
	}

	@Test
	void hasUnfinishedRelationsTrueAfterSingleElementAddTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		var newKnowledge1 = mock(KnowledgeElement.class);
		when(newKnowledge.getId()).thenReturn("1");
		when(newKnowledge1.getId()).thenReturn("2");
		var newRelation = new BasicRelation(RelationType.HAS, newKnowledge, newKnowledge1);
		when(newKnowledge.getRelations()).thenReturn(Set.of(newRelation));
		knowledgeModel.addKnowledge(newKnowledge);
		assertTrue(knowledgeModel.hasUnfinishedRelations());
	}


	@Test
	void getIDsAtBeginningTest() {
		assertEquals(0, knowledgeModel.getIDs().size());
	}

	@Test
	void getIDsAfterAddElementTest() {
		var newKnowledge = mock(KnowledgeElement.class);
		when(newKnowledge.getId()).thenReturn("12345");
		knowledgeModel.addKnowledge(newKnowledge);
		var ids = knowledgeModel.getIDs();
		assertEquals(1, ids.size());
		var first = ids.get(0);
		assertEquals("12345", first);
	}

	@Test
	void getNameNormalTest() {
		assertEquals("", knowledgeModel.getName());
	}

	@Test
	void getNameSpecificNameTest() {
		var knowledgeModel = new KnowledgeModel(mock(RootStructureElement.class), "0.0.0", "ModelName");
		assertEquals("ModelName", knowledgeModel.getName());
	}

	@Test
	void getVersionNormalTest() {
		assertEquals("0.0.0", knowledgeModel.getVersion());
	}

	@Test
	void getVersionSpecificVersionTest() {
		var knowledgeModel = new KnowledgeModel(mock(RootStructureElement.class), "1.2.3", "name");
		assertEquals("1.2.3", knowledgeModel.getVersion());
	}

	@Test
	void getElementsAfterCreationTest() {
		assertEquals(0, knowledgeModel.getElements().size());
	}
}