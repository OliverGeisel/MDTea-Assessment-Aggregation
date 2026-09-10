package de.olivergeisel.materialgenerator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration for the storage of files
 * Set the upload location for a {@link CoursePlan} and the location for images.
 *
 * @author Oliver Geisel
 * @version 1.2.0
 * @see CoursePlan
 * @since 0.2.0
 */
@Getter
@Setter
@Configuration
@PropertySource("classpath:/application.properties")
@ConfigurationProperties("application")
public class StorageProperties {

	/**
	 * Folder location for storing {@link CoursePlan} files (default is 'upload-dir')
	 */
	@Value("${application.upload:upload-dir}")
	private String uploadLocation = "upload-dir";
	@Value("${application.images:images}")
	private String imageLocation  = "";
}