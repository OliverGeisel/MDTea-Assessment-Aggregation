package de.olivergeisel.materialgenerator.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
		basePackages = {
				"de.olivergeisel.materialgenerator.finalization.parts",
				"de.olivergeisel.materialgenerator.core.courseplan",
				"de.olivergeisel.materialgenerator.generation.*",
		}
)
public class JpaRepositoryConfiguration {
}