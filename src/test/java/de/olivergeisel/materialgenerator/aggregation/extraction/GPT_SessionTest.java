package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.TermPrompt;
import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.TermPromptAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Unit")
class GPT_SessionTest {

	@InjectMocks
	private GPT_Session      gpt_session;
	@Mock
	private ProcessBuilder   processBuilder;
	@Mock
	private TermPrompt       termPrompt;
	@Mock
	private TermPromptAnswer termPromptAnswer;
	@Mock
	private GPT_Request      gpt_request;


	@BeforeEach
	void setUp() {
		gpt_session = new GPT_Session();
	}

	@Test
	void request() {
	}

	@Test
	void requestLocalModel() {
	}

	@Test
	void requestRemoteModel() {
		var parameters = new PromptParameters(1, 100, 0.7, 0.9);
		when(gpt_request.getPromptParameters()).thenReturn(parameters);
		when(gpt_request.getModelLocation()).thenReturn(GPT_Request.ModelLocation.REMOTE);
		when(gpt_request.getModelName()).thenReturn("gpt-3.5");
		when(gpt_request.getUrl()).thenReturn(Optional.of("localhost:8080"));
		when(gpt_request.getApiKey()).thenReturn(Optional.of("1234567890"));
		when(gpt_request.getPrompt()).thenReturn(termPrompt);
		try {
			gpt_session.requestRemoteModel(gpt_request);
		} catch (TimeoutException e) {
			fail();
		}
	}
}