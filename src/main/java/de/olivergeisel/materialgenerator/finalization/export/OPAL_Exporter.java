package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;


/**
 * Exports the course to the OPAL format.
 * <p>
 * The OPAL format is a format used by the OPAL learning platform.
 * OPAL is used at TU Dresden for example.
 * See <a href="https://bildungsportal.sachsen.de/opal/shiblogin">here</a> for more information.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.1.0
 * @see Exporter
 * @see RawCourse
 * @see DownloadManager
 * @since 1.1.0
 */
@Service
public class OPAL_Exporter extends Exporter {


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
	@Override
	public void export(RawCourse rawCourse, String templateSetName, File targetDirectory) throws IOException {
		// TODO: Implement this method
	}
}
