package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.StorageFileNotFoundException;
import de.olivergeisel.materialgenerator.finalization.export.opal.OPAL_Exporter;
import de.olivergeisel.materialgenerator.finalization.parts.CourseNavigation;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialHierarchy;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * This class is responsible for exporting the course to the desired format.
 * It is the base class for all exporters.
 * <p>
 * The {@link #export} method is abstract and must be implemented by the subclasses. It needs a directory to save all
 * necessary files in this directory, which is given by the parameters.
 * The method is called by the {@link DownloadManager}
 * and is responsible for creating the final course.
 * The {@link DownloadManager} then creates a zip file or similar formats to enable the user to download the course.
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see HTML_Exporter
 * @see OPAL_Exporter
 * @see DownloadManager
 * @see RawCourse
 * @since 1.1.0
 */
@Service
public abstract class Exporter {

	private static final Logger logger = LoggerFactory.getLogger(Exporter.class);

	private final ImageService imageService;

	protected Exporter(ImageService imageService) {this.imageService = imageService;}

	/**
	 * Creates a MaterialHierarchy for the next group
	 *
	 * @param level      the current level
	 * @param navigation the navigation
	 * @param groups     the groups
	 * @param nextGroup  the next group
	 * @param i          the index
	 * @return the MaterialHierarchy for the wanted group
	 */
	protected static MaterialHierarchy getMaterialHierarchy(CourseNavigation.MaterialLevel level,
			CourseNavigation navigation, List<GroupOrder> groups, GroupOrder nextGroup, int i) {
		MaterialHierarchy next;
		if (nextGroup == null) {
			next = new MaterialHierarchy(navigation.getNextChapter(), null, null, null, navigation.getCount() + 1,
					navigation.getSize());
		} else {
			next = new MaterialHierarchy(level.getChapter(), level.getGroup(), null, null, i + 1, groups.size());
		}
		return next;
	}

	/**
	 * Exports the course to the desired format.
	 * <p>
	 * This method is called by the {@link DownloadManager} and is responsible for creating the final course.
	 *
	 * @param rawCourse       the course to be exported
	 * @param templateSetName the name of the template set to be used
	 * @param targetDirectory the directory where the course should be exported to. Normally a temporary directory.
	 * @throws IOException if an error occurs during the export
	 */
	public abstract void export(RawCourse rawCourse, String templateSetName, File targetDirectory) throws IOException;

	protected void loadImage(String image, File outputDir) {
		try {
			var imageFile = getImage(image);
			Files.copy(imageFile.getInputStream(), new File(outputDir, image).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException | StorageFileNotFoundException e) {
			logger.warn(e.toString());
		}
	}

	protected Resource getImage(String name) throws StorageFileNotFoundException {
		return imageService.loadAsResource(name);
	}

}
