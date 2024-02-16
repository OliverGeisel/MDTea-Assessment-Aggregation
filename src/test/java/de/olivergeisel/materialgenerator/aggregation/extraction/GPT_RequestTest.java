package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.ElementPrompt;
import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.TermPromptAnswer;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.Term;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
class GPT_RequestTest {

	private final String URL = "localhost:8080";

	private GPT_Request gpt_request;


	@Mock
	private ElementPrompt<Term> prompt;
	@Mock
	private TermPromptAnswer    answer;

	@BeforeEach
	void setUp() {
		gpt_request =
				new GPT_Request<Term, TermPromptAnswer>(prompt, answer, URL, "gpt-3.5", GPT_Request.ModelLocation.LOCAL,
						100, 0.7, 0.9, 0.2,
						1);
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getUrl() {
		assertEquals(URL, gpt_request.getUrl().get());
	}

	@Test
	void setUrl() {
		gpt_request.setUrl(Optional.of("localhost:8081"));
		assertEquals("localhost:8081", gpt_request.getUrl().get());
	}

	@Test
	void getPromptParameters() {
		var parameter = gpt_request.getPromptParameters();
		assertEquals(100, parameter.maxTokens());
		assertEquals(0.7, parameter.temperature());
		assertEquals(0.9, parameter.topP());
		assertEquals(1, parameter.retries());
	}

	@Test
	void getPrompt() {
	}

	@Test
	void setPrompt() {
	}

	@Test
	void getModelLocation() {
	}

	@Test
	void setModelLocation() {
	}

	@Test
	void getModelName() {
	}

	@Test
	void setModelName() {
	}

	@Test
	void getApiKey() {
	}

	@Test
	void setApiKey() {
	}

	@Test
	void getRetries() {
	}

	@Test
	void setRetries() {
	}

	@Test
	void getMaxTokens() {
	}

	@Test
	void setMaxTokens() {
	}

	@Test
	void getTemperature() {
	}

	@Test
	void setTemperature() {
	}

	@Test
	void getTopP() {
	}

	@Test
	void setTopP() {
	}

	@Test
	void getFrequencyPenalty() {
	}

	@Test
	void setFrequencyPenalty() {
	}

	@Test
	void getAnswer() {
	}

	@Test
	void setAnswer() {
	}
}