package de.olivergeisel.materialgenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/application.properties")
@ConfigurationProperties("application")
@Primary
public class StorageProperties {

	/**
	 * Folder location for storing files (courseplans)
	 */
	@Value("${application.upload}")
	private String uploadLocation = "upload-dir";
	@Value("${application.images}")
	private String imageLocation  = "";

	//region setter/getter
	public String getImageLocation() {
		return imageLocation;
	}

	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}

	public String getUploadLocation() {
		return uploadLocation;
	}

	public void setUploadLocation(String uploadLocation) {
		this.uploadLocation = uploadLocation;
	}
//endregion

}