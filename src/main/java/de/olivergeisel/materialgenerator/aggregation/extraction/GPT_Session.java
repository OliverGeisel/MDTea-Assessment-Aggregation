package de.olivergeisel.materialgenerator.aggregation.extraction;

import de.olivergeisel.materialgenerator.aggregation.extraction.elementtype_prompts.PromptAnswer;
import de.olivergeisel.materialgenerator.aggregation.model.element.KnowledgeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GPT_Session {

	private static final Logger logger = LoggerFactory.getLogger(GPT_Session.class);

	public Process run() {
		logger.info("open connection to GPT");
		var workingDir = System.getProperty("user.dir");
		ProcessBuilder pb = new ProcessBuilder("python", STR."\{workingDir}/gpt-connection/main.py");
		Process process;
		int exitCode = -1;
		try {
			process = pb.start();


			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			// Warte auf das Ende des Prozesses
			exitCode = process.waitFor();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			System.out.println("Python-Programm beendet mit Exit-Code: " + exitCode);
		}
		return process;


	}

	private void prepare() {
		;
	}

	private String requestLocalModel() {
		logger.info("open connection to GPT");
		var workingDir = System.getProperty("user.dir");
		ProcessBuilder pb = new ProcessBuilder("python", STR."\{workingDir}/gpt-connection/main.py");
		Process process;
		int exitCode = -1;
		StringBuilder answer = new StringBuilder();
		try {
			process = pb.start();


			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}

			// Warte auf das Ende des Prozesses
			exitCode = process.waitFor();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			logger.info(STR."Connection-(Python) was closed with code: \{exitCode}");
		}
		return answer.toString();


	}

	private String requestRemoteModel() {
		return null;
	}


	/**
	 * Reqest the prompt in the {@link GPT_Request} and set+return the answer.
	 *
	 * @param request The request to be sent to the GPT-model
	 * @param <T>     The type of the {@link KnowledgeElement} this request is for.
	 * @return The answer to the request
	 */
	public <T extends KnowledgeElement> PromptAnswer<T> request(GPT_Request<T> request) {
		prepare();
		String response;
		if (request.getModelLocation() == GPT_Request.ModelLocation.LOCAL) {
			response = requestLocalModel();
		} else {
			response = requestRemoteModel();
		}
		request.getAnswer().setAnswer(response);
		return request.getAnswer();
	}
}
