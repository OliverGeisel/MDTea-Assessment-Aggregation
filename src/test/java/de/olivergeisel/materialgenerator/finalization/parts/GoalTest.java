package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
class GoalTest {

	private Goal        goal;
	@Mock
	private ContentGoal contentGoal;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(contentGoal.getExpression()).thenReturn(ContentGoalExpression.FIRST_LOOK);
		when(contentGoal.getMasterKeyword()).thenReturn("MasterKeyword");
		when(contentGoal.getCompleteSentence()).thenReturn("Dies ist ein Testsatz.");
		when(contentGoal.getName()).thenReturn("Das ist eine Testüberschrift.");
		when(contentGoal.getContent()).thenReturn(List.of());
		goal = new Goal(contentGoal);
	}

	@Test
	void isSameOkayTest() {
		assertTrue(goal.isSame(contentGoal));
	}

	@Test
	void getExpressionOkayTest() {
		assertEquals(ContentGoalExpression.FIRST_LOOK, goal.getExpression());
	}

	@Test
	void getExpressionFalseTest() {
		assertNotEquals(ContentGoalExpression.COMMENT, goal.getExpression());
	}

	@Test
	void getMasterKeyword() {
		assertEquals("MasterKeyword", goal.getMasterKeyword());
	}

	@Test
	void getCompleteSentence() {
		assertEquals("Dies ist ein Testsatz.", goal.getCompleteSentence());
	}

	@Test
	void getName() {
		assertEquals("Das ist eine Testüberschrift.", goal.getName());
	}
}