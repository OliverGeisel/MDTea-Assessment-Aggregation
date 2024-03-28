package de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
class RelationTest {

	private Relation         relation;
	@Mock
	private KnowledgeElement fromElement;
	@Mock
	private KnowledgeElement toElement;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(fromElement.getId()).thenReturn("from");
		when(toElement.getId()).thenReturn("to");

		relation = new Relation("TestRelation", "from", "to", RelationType.HAS) {
		};

	}

	@Test
	void createTest() {
		assertNotNull(relation, "Relation must be created");
	}


	@Test
	void createNullTypeTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			new BasicRelation(null, "from", "to");
		}, "type must not be null");
	}

	@Test
	void createNullFromTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			new BasicRelation(RelationType.HAS, null, "to");
		}, "from must not be null");
	}

	@Test
	void createNullToTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			new BasicRelation(RelationType.HAS, "from", null);
		}, "to must not be null");
	}


	@Test
	void getFromIdTest() {
		assertEquals("from", relation.getFromId(), "fromId must be set in constructor");
	}

	@Test
	void getToIdTest() {
		assertEquals("to", relation.getToId(), "toId must be set in constructor");
	}

	@Test
	void getFromTest() {
		relation.setFrom(fromElement);
		assertEquals(fromElement, relation.getFrom(), "from must return correct Element");
	}

	@Test
	void setFromTest() {
		when(fromElement.getId()).thenReturn("from");
		relation.setFrom(fromElement);
		assertEquals(fromElement, relation.getFrom(), "from must return correct Element");
	}

	@Test
	void setFromNullTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			relation.setFrom(null);
		}, "from must throw IllegalArgumentException if set to null");
	}

	@Test
	void getNameTest() {
		assertEquals("TestRelation", relation.getName(), "name must be set in constructor");
	}

	@Test
	void getToOkayTest() {
		relation.setTo(toElement);
		assertEquals(toElement, relation.getTo(), "to must return correct Element");
	}

	@Test
	void getToNullTest() {
		assertThrows(IllegalStateException.class, () -> {
			relation.getTo();
		}, "to must throw IllegalStateException if not set");
	}

	@Test
	void setToTest() {
		when(toElement.getId()).thenReturn("to");
		relation.setTo(toElement);
		assertEquals(toElement, relation.getTo(), "to must return correct Element");
	}

	@Test
	void setToNullTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			relation.setTo(null);
		}, "to must throw IllegalArgumentException if set to null");
	}


	@Test
	void getTypeTest() {
		assertEquals(RelationType.HAS, relation.getType(), "type must be set in constructor");
	}

	@Test
	void hashCodeTest() {
		assertEquals(relation.hashCode(), relation.hashCode(), "hashCode must be equal for same object");
		assertEquals(relation.hashCode(), new Relation("TestRelation", "from", "to", RelationType.HAS) {
		}.hashCode(), "hashCode must be equal for equal objects");

	}

	@Test
	void equalsReflectiveTest() {
		assertEquals(relation, relation, "Relation must be equal to itself");
	}

	void equalsForContentTest() {
		assertEquals(relation, new Relation("TestRelation", "from", "to", RelationType.HAS) {
		}, "Relation must be equal to another Relation with same content");
	}


	@Test
	void equalsNullTest() {
		assertNotEquals(null, relation, "Relation must not be equal to null");
	}


	@Test
	void toStringTest() {
		assertEquals("Relation TestRelation: from → to", relation.toString(),
				"toString must return correct representation");
	}
}