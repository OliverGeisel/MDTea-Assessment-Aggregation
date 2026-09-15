package de.olivergeisel.materialgenerator.ai.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

@Slf4j
public class LlmSession {

	private final ChatModel chatModel;

	public LlmSession(ChatModel chatModel) {
		this.chatModel = chatModel;
	}

	public String requestOllama(Prompt prompt) {
		String back;
		var result = chatModel.call(prompt);
		back = result.getResult().getOutput().toString();

		return back;
	}


}
