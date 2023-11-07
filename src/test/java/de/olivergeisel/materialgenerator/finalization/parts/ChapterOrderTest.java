package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter;
import de.olivergeisel.materialgenerator.finalization.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
class ChapterOrderTest {


	private ChapterOrder     chapterOrder;
	@Mock
	private Goal             goal;
	@Mock
	private GroupOrder       groupOrder;
	@Mock
	private StructureChapter structureChapter;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(goal.getName()).thenReturn("TestGoal");
		when(groupOrder.getTaskOrder()).thenReturn(List.of(mock(TaskOrder.class)));
		when(groupOrder.getRelevance()).thenReturn(Relevance.IMPORTANT);

		chapterOrder = new ChapterOrder(structureChapter, Set.of(goal));
	}

	@Test
	void createTest() {
		assertNotNull(chapterOrder, "ChapterOrder must be created");
	}

	@Test
	void createNullChapterTest() {
		var goals = Set.of(goal);
		assertThrows(IllegalArgumentException.class, () -> {
			new ChapterOrder(null, goals);
		}, "chapter must not be null");
	}
}