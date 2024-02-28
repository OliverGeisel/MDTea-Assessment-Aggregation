package de.olivergeisel.materialgenerator.aggregation;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.*;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation.RelationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
class AggregationProcessTest {


	private AggregationProcess aggregationProcess;

	@Mock
	private Term       term;
	@Mock
	private Definition definition;
	@Mock
	private Code       code;
	@Mock
	private Item task;
	@Mock
	private Example    example;


	@BeforeEach
	void setUp() {
		aggregationProcess = new AggregationProcess();
		lenient().when(term.getType()).thenReturn(KnowledgeType.TERM);
		lenient().when(definition.getType()).thenReturn(KnowledgeType.DEFINITION);
		lenient().when(code.getType()).thenReturn(KnowledgeType.CODE);
		lenient().when(task.getType()).thenReturn(KnowledgeType.ITEM);
		lenient().when(example.getType()).thenReturn(KnowledgeType.EXAMPLE);
		lenient().when(term.getId()).thenReturn("termId");
		lenient().when(definition.getId()).thenReturn("definitionId");
		lenient().when(code.getId()).thenReturn("codeId");
		lenient().when(task.getId()).thenReturn("taskId");
		lenient().when(example.getId()).thenReturn("exampleId");
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void reset() {
	}

	@Test
	void setComplete() {
		assertFalse(aggregationProcess.isComplete(), "AggregationProcess should not be complete at the beginning");
		aggregationProcess.setComplete();
		assertTrue(aggregationProcess.isComplete(),
				"AggregationProcess should be complete after calling setComplete()");
	}

	@Test
	void nextStepAfterInitial() {
		assertEquals(0, aggregationProcess.getStepNumber(), "AggregationProcess should be at step 0 at the beginning");
		aggregationProcess.nextStep();
		assertEquals(1, aggregationProcess.getStepNumber(),
				"AggregationProcess should be at step 1 after calling nextStep()");
	}

	@Test
	void nextStepReachedEnd() {
		aggregationProcess.nextStep();
		aggregationProcess.nextStep();
		aggregationProcess.nextStep();
		aggregationProcess.nextStep();
		aggregationProcess.nextStep();
		aggregationProcess.nextStep();
		assertEquals(6, aggregationProcess.getStepNumber(), "AggregationProcess should be at step 6 after reaching "
															+ "the end");
		aggregationProcess.nextStep();
		assertEquals(6, aggregationProcess.getStepNumber(),
				"AggregationProcess should not be able to go beyond step 6");
	}


	@Test
	void addEmptyList() {
		var spy = mock(List.class);
		when(spy.isEmpty()).thenReturn(true);
		aggregationProcess.add(spy);
		verify(spy).isEmpty();
		verify(spy, atMost(0)).getFirst();
	}

	@Test
	void addNull() {
		assertThrows(IllegalArgumentException.class, () -> aggregationProcess.add(null));
	}

	@Test
	void addTerm() {
		aggregationProcess.add(List.of(term));
		assertEquals(1, aggregationProcess.getTerms().getAcceptedElements().size());
	}

	@Test
	void addDefinition() {
		aggregationProcess.add(List.of(definition));
		assertEquals(1, aggregationProcess.getDefinitions().getAcceptedElements().size());
	}

	@Test
	void addCode() {
		aggregationProcess.add(List.of(code));
		assertEquals(1, aggregationProcess.getCodes().getAcceptedElements().size());
	}

	@Test
	void addTask() {
		aggregationProcess.add(List.of(task));
		assertEquals(1, aggregationProcess.getTasks().getAcceptedElements().size());
	}

	@Test
	void addExample() {
		aggregationProcess.add(List.of(example));
		assertEquals(1, aggregationProcess.getExamples().getAcceptedElements().size());
	}

	@Test
	void containsTermElement() {
		aggregationProcess.add(List.of(term));
		assertTrue(aggregationProcess.containsElement(term.getId()));
	}

	@Test
	void containsDefinitionElement() {
		aggregationProcess.add(List.of(definition));
		assertTrue(aggregationProcess.containsElement(definition.getId()));
	}

	@Test
	void containsCodeElement() {
		aggregationProcess.add(List.of(code));
		assertTrue(aggregationProcess.containsElement(code.getId()));
	}

	@Test
	void containsTaskElement() {
		aggregationProcess.add(List.of(task));
		assertTrue(aggregationProcess.containsElement(task.getId()));
	}

	@Test
	void containsExampleElement() {
		aggregationProcess.add(List.of(example));
		assertTrue(aggregationProcess.containsElement(example.getId()));
	}


	@Test
	void suggestEmptyList() {
		var spy = mock(List.class);
		when(spy.isEmpty()).thenReturn(true);
		aggregationProcess.suggest(spy);
		verify(spy).isEmpty();
		verify(spy, atMost(0)).getFirst();
	}

	@Test
	void suggestNull() {
		assertThrows(IllegalArgumentException.class, () -> aggregationProcess.suggest(null));
	}


	@Test
	void suggestTerm() {
		aggregationProcess.suggest(List.of(term));
		assertEquals(1, aggregationProcess.getTerms().getSuggestedElements().size());
	}

	@Test
	void findByIdNull() {
		assertThrows(IllegalArgumentException.class, () -> aggregationProcess.findById(null));
	}

	@Test
	void findByIdBlank() {
		assertThrows(IllegalArgumentException.class, () -> aggregationProcess.findById(""));
	}

	@Test
	void findByIdNotFound() {
		assertThrows(NoSuchElementException.class, () -> aggregationProcess.findById("notFound"));
	}

	@Test
	void findByIdTerm() {
		aggregationProcess.add(List.of(term));
		assertEquals(term, aggregationProcess.findById("termId"));
	}

	@Test
	void removeById() {
		aggregationProcess.add(List.of(term));
		aggregationProcess.removeById("termId");
		assertFalse(aggregationProcess.getTerms().getAcceptedElements().contains(term));
	}

	@Test
	void approveById() {
	}

	@Test
	void rejectById() {
	}

	@Test
	void getRelationsByFromId() {
	}

	@Test
	void unlinkTermsFromDefinition() {
	}

	@Test
	void hasElementsInAcceptedTermTrue() {
		aggregationProcess.add(List.of(term));
		assertTrue(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInAcceptedDefinitionTrue() {
		aggregationProcess.add(List.of(definition));
		assertTrue(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInAcceptedCodeTrue() {
		aggregationProcess.add(List.of(code));
		assertTrue(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInAcceptedTaskTrue() {
		aggregationProcess.add(List.of(task));
		assertTrue(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInAcceptedExampleTrue() {
		aggregationProcess.add(List.of(example));
		assertTrue(aggregationProcess.hasElements());
	}


	@Test
	void hasElementsInSuggestedTermFalse() {
		aggregationProcess.suggest(List.of(term));
		assertFalse(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInSuggestedDefinitionFalse() {
		aggregationProcess.suggest(List.of(definition));
		assertFalse(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInSuggestedCodeFalse() {
		aggregationProcess.suggest(List.of(code));
		assertFalse(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInSuggestedTaskFalse() {
		aggregationProcess.suggest(List.of(task));
		assertFalse(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsInSuggestedExampleFalse() {
		aggregationProcess.suggest(List.of(example));
		assertFalse(aggregationProcess.hasElements());
	}


	@Test
	void hasRelationsFalse() {
		assertFalse(aggregationProcess.hasRelations(), "No relations should be present at the beginning");
	}

	@Test
	void hasRelationsTrue() {
		aggregationProcess.add(List.of(term, definition));
		aggregationProcess.link(term, definition, RelationType.DEFINED_BY);
		assertTrue(aggregationProcess.hasRelations(), "A relation should be present after linking");
	}

	@Test
	void hasElementsAndRelationsTrue() {
		assertFalse(aggregationProcess.hasElementsAndRelations(),
				"No elements and relations should be present at the beginning");
		aggregationProcess.add(List.of(term, definition));
		aggregationProcess.link(term, definition, RelationType.DEFINED_BY);
		assertTrue(aggregationProcess.hasElementsAndRelations());
	}

	@Test
	void hasElementsAndRelationsFalse() {
		assertFalse(aggregationProcess.hasElementsAndRelations(),
				"No elements and relations should be present at the beginning");
	}

	@Test
	void hasElementsAndRelationsFalseNoRelations() {
		aggregationProcess.add(List.of(term, definition));
		assertFalse(aggregationProcess.hasElementsAndRelations());
		assertTrue(aggregationProcess.hasElements());
	}

	@Test
	void hasElementsAndRelationsFalseNoElements() {
		aggregationProcess.add(List.of(term, definition));
		aggregationProcess.link(term, definition, RelationType.DEFINED_BY);
		aggregationProcess.removeById(term.getId());
		aggregationProcess.removeById(definition.getId());
		assertFalse(aggregationProcess.hasElementsAndRelations());
		assertTrue(aggregationProcess.hasRelations());
	}

	@Test
	void linkFromNull() {
		assertFalse(aggregationProcess.link(null, definition, RelationType.DEFINED_BY),
				"Linking from null should return false");
	}

	@Test
	void linkToNull() {
		assertFalse(aggregationProcess.link(term, null, RelationType.DEFINED_BY),
				"Linking to null should return false");
	}

	@Test
	void linkTypeNull() {
		assertFalse(aggregationProcess.link(term, definition, null), "Linking with null type should return false");
	}

	@Test
	void linkNotConatainFrom() {
		assertFalse(aggregationProcess.link(term, definition, RelationType.DEFINED_BY), "Linking should return false");
	}

	@Test
	void linkNotContainTo() {
		aggregationProcess.add(List.of(term));
		assertFalse(aggregationProcess.link(term, definition, RelationType.DEFINED_BY), "Linking should return false");
	}


	@Test
	void linkOkay() {
		aggregationProcess.add(List.of(term, definition));
		assertTrue(aggregationProcess.link(term, definition, RelationType.DEFINED_BY), "Linking should return true");
	}

	@Test
	void linkById() {
		aggregationProcess.add(List.of(term, definition));
		assertTrue(aggregationProcess.link(term.getId(), definition.getId(), RelationType.DEFINED_BY),
				"Linking should return true");
	}

	@Test
	void linkAlreadyLinked() {
		aggregationProcess.add(List.of(term, definition));
		assertTrue(aggregationProcess.link(term, definition, RelationType.DEFINED_BY));
		assertFalse(aggregationProcess.link(term, definition, RelationType.DEFINED_BY), "Linking should return false");
	}

	@Test
	void getStepNumberAtBeginning() {
		assertEquals(0, aggregationProcess.getStepNumber(), "AggregationProcess should be at step 0 at the beginning");
	}

	@Test
	void getStepNumberAfterOneStep() {
		aggregationProcess.nextStep();
		assertEquals(1, aggregationProcess.getStepNumber(), "AggregationProcess should be at step 1 after one step");
	}

	@Test
	void getStep() {
		assertEquals("INITIAL", aggregationProcess.getStep());
	}

	@Test
	void getSources() {
	}

	@Test
	void getCurrentFragment() {
	}

	@Test
	void getCodes() {
	}

	@Test
	void getTasks() {
	}

	@Test
	void setCurrentFragment() {
	}

	@Test
	void getModelParameters() {
	}

	@Test
	void setModelParameters() {
	}

	@Test
	void getAreaOfKnowledge() {
	}

	@Test
	void setAreaOfKnowledge() {
	}

	@Test
	void getModelLocation() {
	}

	@Test
	void setModelLocation() {
	}

	@Test
	void getApiKey() {
	}

	@Test
	void setApiKey() {
	}

	@Test
	void getUrl() {
	}

	@Test
	void setUrl() {
	}

	@Test
	void getModelName() {
	}

	@Test
	void setModelName() {
	}

	@Test
	void getTerms() {
	}

	@Test
	void getDefinitions() {
	}

	@Test
	void getExamples() {

	}

	@Test
	void getRelationsNotNul() {
		assertNotNull(aggregationProcess.getRelations());
	}

	@Test
	void isNotCompleteAtBeginning() {
		assertFalse(aggregationProcess.isComplete(), "AggregationProcess should not be complete at the beginning");
	}

	@Test
	void isCompleteAfterSetComplete() {
		aggregationProcess.setComplete();
		assertTrue(aggregationProcess.isComplete(),
				"AggregationProcess should be complete after calling setComplete()");
	}

	@Test
	void getStart() {
	}
}