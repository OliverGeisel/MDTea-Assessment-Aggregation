package de.olivergeisel.materialgenerator.core.courseplan.structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
class StructureElementTest {

	private StructureElement structureElement;

	@BeforeEach
	void setUp() {
		structureElement = new StructureElement() {
			@Override
			public Relevance updateRelevance() {
				return Relevance.TO_SET;
			}
		};
	}


	@Test
	void constructorWithParams() {
		var element = new StructureElement(null, Relevance.MANDATORY, "Element 1", null) {
			@Override
			public Relevance updateRelevance() {
				return Relevance.MANDATORY;
			}
		};
		assertEquals(Relevance.MANDATORY, element.getRelevance());
		assertEquals("Element 1", element.getName());
		assertEquals(0, element.getAlternatives().size());
		assertNull(element.getTopic());
	}

	@Test
	void addAlias() {
		structureElement.addAlias("test");
		assertEquals(1, structureElement.getAlternatives().size());
	}

	@Test
	void removeAliasOnEmpty() {
		assertEquals(0, structureElement.getAlternatives().size());
		assertFalse(structureElement.removeAlias("test"));
		assertEquals(0, structureElement.getAlternatives().size());
	}

	@Test
	void removeNotExistingAlias() {
		structureElement.addAlias("test");
		assertFalse(structureElement.removeAlias("notExisting"));
		assertEquals(1, structureElement.getAlternatives().size());
	}

	@Test
	void removeAlias() {
		structureElement.addAlias("test");
		assertEquals(1, structureElement.getAlternatives().size());
		structureElement.removeAlias("test");
		assertEquals(0, structureElement.getAlternatives().size());
	}

	@Test
	void moveUpAliasTop() {
		structureElement.addAlias("test");
		structureElement.addAlias("test2");
		structureElement.moveUpAlias("test");
		assertEquals("test", structureElement.getAlternatives().stream().findFirst().orElseThrow());
	}

	@Test
	void moveUpAlias() {
		structureElement.addAlias("test");
		structureElement.addAlias("test2");
		structureElement.moveUpAlias("test2");
		assertEquals("test2", structureElement.getAlternatives().stream().findFirst().orElseThrow());
	}

	@Test
	void moveUpAliasNotExisting() {
		structureElement.addAlias("test");
		structureElement.addAlias("test2");
		structureElement.moveUpAlias("notExisting");
		assertEquals("test", structureElement.getAlternatives().stream().findFirst().orElseThrow());
	}

	@Test
	void moveDownAliasTop() {
		structureElement.addAlias("test");
		structureElement.addAlias("test2");
		structureElement.moveDownAlias("test");
		assertEquals("test2", structureElement.getAlternatives().stream().findFirst().orElseThrow());
	}

	@Test
	void moveDownAliasLast() {
		structureElement.addAlias("test");
		structureElement.addAlias("test2");
		structureElement.moveDownAlias("test2");
		String last = null;
		for (var alias : structureElement.getAlternatives()) {
			last = alias;
		}
		assertEquals("test2", last);
	}

	@Test
	void getAliasOnEmptyConstructor() {
		assertEquals(0, structureElement.getAlternatives().size());
	}

	@Test
	void getAliasOnParamConstructor() {
		var element = new StructureElement(null, Relevance.MANDATORY, "Element 1", null) {
			@Override
			public Relevance updateRelevance() {
				return Relevance.MANDATORY;
			}
		};
		assertEquals(0, element.getAlternatives().size());
	}

	@Test
	void getAlternativesOnEmptyConstructor() {
		assertEquals(0, structureElement.getAlternatives().size());
	}

	@Test
	void getAlternativesOnParamConstructor() {
		var element = new StructureElement(null, Relevance.MANDATORY, "Element 1", null) {
			@Override
			public Relevance updateRelevance() {
				return Relevance.MANDATORY;
			}
		};
		assertEquals(0, element.getAlternatives().size());
	}

	@Test
	void isValidAtBeginningOfEmptyConstructorFalse() {
		assertFalse(structureElement.isValid());
	}
}