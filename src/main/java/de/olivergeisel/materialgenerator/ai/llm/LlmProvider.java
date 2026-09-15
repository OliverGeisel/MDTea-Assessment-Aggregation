package de.olivergeisel.materialgenerator.ai.llm;

import org.springframework.ai.chat.prompt.ChatOptions;

public interface LlmProvider {

//region setter/getter
	ChatOptions getBuilder();
//endregion
}
