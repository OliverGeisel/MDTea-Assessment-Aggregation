package de.olivergeisel.materialgenerator.ai.llm;


import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class LlmManager {


	private final ChatModel chatModel;

	public <T extends LlmProvider> LlmManager(ChatModel chatModel) {this.chatModel = chatModel;}

	public Prompt getPrompt(String promptText, String systemMessage, LlmProvider provider) {
		var prompt = new Prompt(promptText, provider.getBuilder());
		prompt.augmentSystemMessage(systemMessage);
		return prompt;
	}

	public ChatResponse executePrompt(Prompt prompt) {
		return chatModel.call(prompt);
	}

//region setter/getter
	public ChatModel getModel() {
		return chatModel;
	}
//endregion
}
