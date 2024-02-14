package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.PromptAnswer;
import de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element.KnowledgeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class GPT_Session {

	private static final Logger logger = LoggerFactory.getLogger(GPT_Session.class);


	private static <T extends KnowledgeElement, A extends PromptAnswer<T>> RequestParameters createParameters(
			GPT_Request<T, A> request) {
		return new RequestParameters(request);
	}

	private static <T extends KnowledgeElement, A extends PromptAnswer<T>> StringBuilder runConnection(
			GPT_Request<T, A> request)
			throws ServerNotAvailableException, TimeoutException {
		final var workingDir = System.getProperty("user.dir");
		var parameters = createParameters(request);
		var prompt = STR."\"\{request.getPrompt().getPrompt()}\""; // " are needed for the python script
		List<String> programArgs = new LinkedList<>(List.of("python", STR."\{workingDir}/gpt-connection/main.py",
				STR."\{prompt}"));
		programArgs.addAll(parameters.toList());
		ProcessBuilder pb = new ProcessBuilder(programArgs);
		int exitCode = -1;
		Process process;
		StringBuilder answer = new StringBuilder();
		Thread timerThread = null;
		try {
			process = pb.start();
			timerThread = new Thread(() -> {
				try {
					Thread.sleep(10 * 60 * 1_000);
					process.destroy();
				} catch (InterruptedException e) {
					// do nothing
				}
			});
			timerThread.start();

			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}
			exitCode = process.waitFor();
			if (exitCode == 3) { // server isn't running
				logger.error("The server isn't running. Please start the server and try again.");
				var url = request.getUrl().orElse("localhost:4891/v1");
				url = url.isBlank() ? "localhost:4891/v1" : url;
				throw new ServerNotAvailableException(STR."""
				The server behind "\{url}" is not responding.""");
			}
			if (timerThread.isInterrupted()) {
				throw new TimeoutException("The connection timed out.");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			logger.info("Connection was interrupted.");
		} finally {
			if (timerThread != null) {
				timerThread.interrupt();
			}
			logger.info(STR."Connection-(Python) was closed with code: \{exitCode}");
		}
		return answer;
	}

	private <T extends KnowledgeElement, A extends PromptAnswer<T>> String requestLocalModel(
			GPT_Request<T, A> request) throws TimeoutException {
		logger.info("open connection to a local GPT-Model. Model-Name: {} - Parameters: {}", request.getModelName(),
				request.getPromptParameters());
		StringBuilder answer = runConnection(request);
		return answer.toString();
	}

	private <T extends KnowledgeElement, A extends PromptAnswer<T>> String requestRemoteModel(GPT_Request<T, A> request)
			throws ServerNotAvailableException, TimeoutException {
		logger.info("open connection to a GPT/Model-Server. Model-Name: {} - Parameters: {}", request.getModelName(),
				request.getPromptParameters());
		StringBuilder answer = runConnection(request);
		return answer.toString();
	}

	/**
	 * Request the prompt in the {@link GPT_Request} and set+return the answer.
	 *
	 * @param request The request to be sent to the GPT-model
	 * @param <T>     The type of the {@link KnowledgeElement} this request is for.
	 * @return The answer to the request
	 */
	public <T extends KnowledgeElement, A extends PromptAnswer<T>> A request(GPT_Request<T, A> request)
			throws ServerNotAvailableException, TimeoutException {
		String response;
		if (request.getModelLocation() == GPT_Request.ModelLocation.LOCAL) {
			response = requestLocalModel(request);
		} else {
			response = requestRemoteModel(request);
		}
		request.getAnswer().setAnswer(response);
		return request.getAnswer();
	}
}
