package de.olivergeisel.materialgenerator;

import de.olivergeisel.materialgenerator.finalization.export.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;


/**
 * Boot point. General Config
 */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class MDTea_Aggregation {

	public static void main(String[] args) {
		SpringApplication.run(MDTea_Aggregation.class, args);
	}

	@Bean
	CommandLineRunner initFiles(FileSystemStorageService storageService) {
		return args -> storageService.init();
	}

	@Bean
	CommandLineRunner initImages(ImageService storageService) {
		return args -> storageService.init();
	}

	@Component
	@Order(0)
	public static class Neo4jConnectionTest {

		public Neo4jConnectionTest(Driver driver) {
			try {
				driver.executableQuery("RETURN 1 AS value")
					  .withConfig(QueryConfig.builder()
											 .withDatabase("neo4j")
											 .build())
					  .execute();

			} catch (Exception e) {
				log.error(Arrays.toString(e.getStackTrace()));
				log.error("Failed to connect to Neo4j database. Please check your configuration.");
				System.exit(3);
			}
		}
	}

	@Configuration
	public static class WebMvcConfig implements WebMvcConfigurer {

		@Override
		public void configureContentNegotiation(
				ContentNegotiationConfigurer configurer) {

			configurer.defaultContentType(MediaType.APPLICATION_JSON);
		}
	}
}