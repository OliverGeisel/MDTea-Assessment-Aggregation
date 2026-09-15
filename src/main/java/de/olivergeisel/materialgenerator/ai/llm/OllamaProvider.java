package de.olivergeisel.materialgenerator.ai.llm;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaModel;

public class OllamaProvider implements LlmProvider {

	//region setter/getter
	@Override
	public ChatOptions getBuilder() {
		var back = OllamaChatOptions.builder();
		back.model(OllamaModel.GEMMA3)
			.lowVRAM(true)
			.disableThinking()
			.temperature(0.7)
			.topP(0.9);

		return back.build();
	}
//endregion
}
