package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
class RelationGeneratorTest {


	@Test
	void generateRelation_HAS_Test() {
		var result = RelationGenerator.create("HAS");
		assertEquals(RelationType.HAS, result.getType());
		assertEquals("UNKNOWN", result.getFromId());
		assertEquals("UNKNOWN", result.getToId());
	}

	@Test
	void generateRelation_IS_Test() {
		var result = RelationGenerator.create("IS");
		assertEquals(RelationType.IS, result.getType());
	}

	@Test
	void generateRelation_DEFINES_Test() {
		var result = RelationGenerator.create("DEFINES");
		assertEquals(RelationType.DEFINES, result.getType());
	}

	@Test
	void generateRelation_CUSTOM_Test() {
		var result = RelationGenerator.create("RANDOM");
		assertEquals(RelationType.CUSTOM, result.getType());
		assertEquals("RANDOM", result.getName());
	}

	@Test
	void generateRelation_NULL_Test() {
		assertThrows(IllegalArgumentException.class, () -> {
			RelationGenerator.create(null);
		}, "type must not be null");
	}

	@Test
	void generateRelation_EMPTY_Test() {
		assertThrows(IllegalArgumentException.class, () -> {
			RelationGenerator.create("");
		}, "type must not be empty");
	}

	@Test
	void generateRelation_WHITESPACE_Test() {
		assertThrows(IllegalArgumentException.class, () -> {
			RelationGenerator.create(" ");
		}, "type must not be empty");
	}

	@Test
	void createWithFromAndToTest() {
		var result = RelationGenerator.create("HAS", "from", "to");
		assertEquals(RelationType.HAS, result.getType());
		assertEquals("from", result.getFromId());
		assertEquals("to", result.getToId());
	}

	@Test
	void createWithFromAndToNullTypeTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			RelationGenerator.create(null, "from", "to");
		}, "type must not be null");
	}

	@Test
	void createWithFromAndToNullFromTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			RelationGenerator.create("HAS", null, "to");
		}, "fromId must not be null");
	}

	@Test
	void createWithFromAndToNullToTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			RelationGenerator.create("HAS", "from", null);
		}, "toId must not be null");
	}

}