package de.olivergeisel.materialgenerator;

import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Cleans the Neo4J database on start if the --CLEAN argument is passed.
 *
 * @author Oliver Geisel
 * @version 1.2
 * @since 1.0.0
 */
@Component
@Order(1)
public class Neo4JCleanOnStart implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4JCleanOnStart.class);

	private final Driver neo4jDriver;

	public Neo4JCleanOnStart(Driver neo4jDriver) {
		this.neo4jDriver = neo4jDriver;
	}

	@Override
	public void run(String... args) throws Exception {
		if (Arrays.stream(args).toList().contains("--CLEAN")) {
			LOGGER.warn("Cleaning Neo4J database");
			try (var session = neo4jDriver.session()) {
				session.run("MATCH (n)-[r]-() DETACH DELETE r");
				session.run("MATCH (n) DELETE n");
			}
			LOGGER.warn("Neo4J database cleaned");
		}
	}
}
