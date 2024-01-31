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
 */
@Component
@Order(1)
public class Neo4J_Clean_On_Start implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4J_Clean_On_Start.class);

	private Driver neo4jDriver;

	@Override
	public void run(String... args) throws Exception {
		if (Arrays.stream(args).toList().contains("--CLEAN")) {
			LOGGER.info("Cleaning Neo4J database");
			try (var session = neo4jDriver.session()) {
				session.run("MATCH (n)-[r]-() DETACH DELETE r");
				session.run("MATCH (n) DELETE n");
			}
			LOGGER.info("Neo4J database cleaned");
		}
	}
}
