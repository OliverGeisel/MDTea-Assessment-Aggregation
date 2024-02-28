package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementNegotiatorTest {

	private ElementNegotiator<Term> elementNegotiator;


	@BeforeEach
	void setUp() {
		elementNegotiator = new ElementNegotiator<Term>(null);
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void approveFalse() {
		Term term = mock(Term.class);
		var result = elementNegotiator.approve(term);
		assertFalse(result);
	}

	@Test
	void approveTrue() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		elementNegotiator.suggest(term);
		var result = elementNegotiator.approve(term);
		assertTrue(result);
	}

	@Test
	void hasNoElementsAtBeginning() {
		assertTrue(elementNegotiator.isEmpty());
		assertFalse(elementNegotiator.hasElements());
	}

	@Test
	void hasSuggestedElements() {
		elementNegotiator.suggest(mock(Term.class));
		assertTrue(elementNegotiator.hasElements());
		assertTrue(elementNegotiator.hasSuggestedElements());
	}

	@Test
	void findByIdNothingEmpty() {
		assertNull(elementNegotiator.findById("1"));
	}

	@Test
	void findByIdSuggested() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		elementNegotiator.suggest(term);
		assertEquals(term, elementNegotiator.findById("1"));
	}

	@Test
	void findByIdAccepted() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		elementNegotiator.add(term);
		assertEquals(term, elementNegotiator.findById("1"));
	}


	@Test
	void approveByIdTrue() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		elementNegotiator.suggest(term);
		assertTrue(elementNegotiator.approveById("1"));
	}

	@Test
	void approveByIdFalse() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		assertFalse(elementNegotiator.approveById("1"));
	}

	@Test
	void reject() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		elementNegotiator.add(term);
		assertTrue(elementNegotiator.reject(term));
	}

	@Test
	void rejectByIdTrue() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		elementNegotiator.add(term);
		assertTrue(elementNegotiator.rejectById("1"));
	}

	@Test
	void rejectByIdFalse() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		assertFalse(elementNegotiator.rejectById("1"));
	}

	@Test
	void add() {
		Term term = mock(Term.class);
		assertFalse(elementNegotiator.hasElements());
		elementNegotiator.add(term);
		assertTrue(elementNegotiator.hasElements());
		assertTrue(elementNegotiator.getAcceptedElements().contains(term));
	}

	@Test
	void addAll() {
		var term1 = mock(Term.class);
		var term2 = mock(Term.class);
		assertFalse(elementNegotiator.hasElements());
		elementNegotiator.addAll(List.of(term1, term2));
		assertTrue(elementNegotiator.hasElements());
		assertTrue(elementNegotiator.getAcceptedElements().contains(term1));
	}

	@Test
	void suggest() {
		Term term = mock(Term.class);
		assertFalse(elementNegotiator.hasElements());
		elementNegotiator.suggest(term);
		assertTrue(elementNegotiator.hasElements());
		assertTrue(elementNegotiator.getSuggestedElements().contains(term));
		assertFalse(elementNegotiator.getAcceptedElements().contains(term));
	}

	@Test
	void removeAfterAdd() {
		Term term = mock(Term.class);
		elementNegotiator.add(term);
		assertTrue(elementNegotiator.hasElements());
		elementNegotiator.remove(term);
		assertFalse(elementNegotiator.hasElements());
	}

	@Test
	void removeAfterSuggest() {
		Term term = mock(Term.class);
		elementNegotiator.suggest(term);
		assertTrue(elementNegotiator.hasElements());
		elementNegotiator.remove(term);
		assertFalse(elementNegotiator.hasElements());
	}

	@Test
	void removeOnEmpty() {
		Term term = mock(Term.class);
		assertFalse(elementNegotiator.hasElements());
		assertFalse(elementNegotiator.remove(term));
	}


	@Test
	void removeById() {
		Term term = mock(Term.class);
		when(term.getId()).thenReturn("1");
		elementNegotiator.add(term);
		assertTrue(elementNegotiator.hasElements());
		assertTrue(elementNegotiator.removeById("1"));
		assertFalse(elementNegotiator.hasElements());
	}

	@Test
	@Disabled
	void extract() {
		// not used by ElementNegotiator yet
	}

	@Test
	void isEmptyAtStart() {
		assertTrue(elementNegotiator.isEmpty());
	}

	@Test
	void isNotEmptyAfterAdd() {
		Term term = mock(Term.class);
		elementNegotiator.add(term);
		assertFalse(elementNegotiator.isEmpty());
	}

	@Test
	void getFragment() {
		assertNull(elementNegotiator.getFragment());
	}

	@Test
	void getSuggestedElementsIsNotNull() {
		assertNotNull(elementNegotiator.getSuggestedElements());
	}

	@Test
	void getAcceptedElementsIsNotNull() {
		assertNotNull(elementNegotiator.getAcceptedElements());
	}
}