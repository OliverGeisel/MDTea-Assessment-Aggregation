package de.olivergeisel.materialgenerator;

import de.olivergeisel.materialgenerator.finalization.export.ImageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableNeo4jRepositories
public class MDTea_Aggregation {

	@Configuration
	public class WebMvcConfig implements WebMvcConfigurer {

		@Override
		public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
			configurer.defaultContentType(MediaType.APPLICATION_JSON);
		}
	}

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
}
