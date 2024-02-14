package de.olivergeisel.materialgenerator.core.courseplan.content;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopicStructureAliasMappingsTest {

	private TopicStructureAliasMappings mapping;

	@BeforeEach
	void setUp() {
		mapping = new TopicStructureAliasMappings();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getAliasesFor() {
	}

	@Test
	void addAlias() {
	}

	@Test
	void removeAlias() {
	}

	@Test
	void removeStructure() {
	}

	@Test
	void clear() {
	}

	@Test
	void isEmptyAtBeginning() {
		assertTrue(mapping.isEmpty(), "Mapping should be empty at beginning");
	}


	@Test
	void containsStructureFalseAtBeginning() {
		assertFalse(mapping.containsStructure("structure"), "Mapping should not contain structure at beginning");
	}

	@Test
	void entrySet() {
	}

	@Test
	void keySet() {
	}

	@Test
	void values() {
	}

	@Test
	void aliasMappings() {
	}
}