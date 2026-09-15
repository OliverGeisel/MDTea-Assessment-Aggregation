package de.olivergeisel.materialgenerator.ai.llm;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/ai")
public class ChatController {

	private final OllamaChatModel chatModel;
	private final LlmManager      llmManager;

	public ChatController(OllamaChatModel chatModel, LlmManager llmManager) {
		this.chatModel = chatModel;
		this.llmManager = llmManager;
	}

	@GetMapping("/generate")
	public Map<String, String> generate(
			@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		LlmProvider provider = new OllamaProvider();
		var prompt = this.llmManager.getPrompt(message, "", provider);
		return Map.of("generation",
				Objects.requireNonNull(llmManager.executePrompt(prompt).getResult().getOutput().getText()));
	}

	@PostMapping(value = "/generate", produces = "text/html; charset=UTF-8")
	public String generateForHtmx(
			@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		String generation = this.chatModel.call(message);
		String q = HtmlUtils.htmlEscape(message);
		String a = HtmlUtils.htmlEscape(generation);
		return "<pre class='chat-question'>" + q + "</pre>" +
			   "<pre class='chat-response'>" + a + "</pre>";
	}

	@GetMapping("/generateStream")
	public Flux<ChatResponse> generateStream(
			@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		Prompt prompt = new Prompt(new UserMessage(message));
		return this.chatModel.stream(prompt);
	}

}