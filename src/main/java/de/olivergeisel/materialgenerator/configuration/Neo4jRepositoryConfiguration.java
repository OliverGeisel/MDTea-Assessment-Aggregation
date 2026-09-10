package de.olivergeisel.materialgenerator.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration
@EnableNeo4jRepositories(
		basePackages = {
				"de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.element",
				"de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.relation",
				"de.olivergeisel.materialgenerator.aggregation.knowledgemodel.model.structure"
		},
		transactionManagerRef = "neo4jTransactionManager"
)
public class Neo4jRepositoryConfiguration {
}