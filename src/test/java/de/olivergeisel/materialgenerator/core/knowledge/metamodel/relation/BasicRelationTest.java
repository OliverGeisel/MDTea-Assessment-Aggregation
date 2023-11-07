package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
class BasicRelationTest {


	private BasicRelation relation;

	@BeforeEach
	void setUp() {
	}

	@Test
	void idFromNameOkay() {
		var result = BasicRelation.idFromName("type", "from", "to");
		assertEquals("from_TYPE_to", result);
	}

	@Test
	void idFromNameNullType() {
		assertThrows(IllegalArgumentException.class, () -> {
			BasicRelation.idFromName((RelationType) null, "from", "to");
		}, "type must not be null");
	}

	@Test
	void idFromNameNullFrom() {
		assertThrows(IllegalArgumentException.class, () -> {
			BasicRelation.idFromName("type", null, "to");
		}, "from must not be null");
	}

	@Test
	void idFromNameNullTo() {
		assertThrows(IllegalArgumentException.class, () -> {
			BasicRelation.idFromName("type", "from", null);
		}, "to must not be null");
	}
}