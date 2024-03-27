package de.olivergeisel.materialgenerator.core.courseplan.structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("Unit")
class StructureChapterTest {

	private StructureChapter structureChapter;

	@BeforeEach
	void setUp() {
		structureChapter = new StructureChapter();
	}

	@Test
	void constructorWithParams() {
		var chapter = new StructureChapter(null, Relevance.MANDATORY, "Chapter 1", 13.0, null);
		assertEquals(Relevance.MANDATORY, chapter.getRelevance());
		assertEquals(13.0, chapter.getWeight());
		assertEquals("Chapter 1", chapter.getName());
		assertEquals(0, chapter.numberOfTasks());
		assertNull(chapter.getTopic());
	}

	@Test
	void relevanceAtStart() {
		assertEquals(Relevance.TO_SET, structureChapter.getRelevance());
	}

	@Test
	void updateRelevanceAfterAddGroup() {
		var group = mock(StructureGroup.class);
		when(group.getRelevance()).thenReturn(Relevance.MANDATORY);
		structureChapter.add(group);
		structureChapter.updateRelevance();
		assertEquals(Relevance.MANDATORY, structureChapter.getRelevance());
	}

	@Test
	void findByAlias() {
		var group = mock(StructureGroup.class);
		when(group.getName()).thenReturn("Group 1");
		when(group.findByAlias("Group 1")).thenReturn(group);
		structureChapter.add(group);
		assertEquals(group, structureChapter.findByAlias("Group 1"));
	}

	@Test
	void addOkay() {
		assertEquals(0, structureChapter.size());
		var group = mock(StructureGroup.class);
		structureChapter.add(group);
		assertEquals(1, structureChapter.size());
	}

	@Test
	void addDuplicate() {
		var group = mock(StructureGroup.class);
		structureChapter.add(group);
		assertFalse(structureChapter.add(group));
	}

	@Test
	void containsFalse() {
		var group = mock(StructureGroup.class);
		assertFalse(structureChapter.contains(group));
	}

	@Test
	void containsTrue() {
		var group = mock(StructureGroup.class);
		structureChapter.add(group);
		assertTrue(structureChapter.contains(group));
	}

	@Test
	void sizeAtBeginning() {
		assertEquals(0, structureChapter.size());
	}

	@Test
	void getPartsAtBeginning() {
		assertTrue(structureChapter.getParts().isEmpty());
	}

	@Test
	void getWeightAtBeginning() {
		assertEquals(0.0, structureChapter.getWeight());
	}

}