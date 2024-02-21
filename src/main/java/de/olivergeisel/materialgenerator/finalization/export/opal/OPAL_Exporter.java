package de.olivergeisel.materialgenerator.finalization.export.opal;

import de.olivergeisel.materialgenerator.finalization.export.DownloadManager;
import de.olivergeisel.materialgenerator.finalization.export.Exporter;
import de.olivergeisel.materialgenerator.finalization.export.ImageService;
import de.olivergeisel.materialgenerator.finalization.export.opal.test.OPALTestExport;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.generation.material.assessment.TestMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.*;


/**
 * Exports the course to the OPAL format.
 * <p>
 * The OPAL format is a format used by the OPAL learning platform.
 * OPAL is used at TU Dresden for example.
 * See <a href="https://bildungsportal.sachsen.de/opal/shiblogin">here</a> for more information.
 * </p>
 * <p>
 * The OPAL course has at least 3 directories:
 *     <ul>
 *         <li>coursefolder - materials for every node (images, html, ...)</li>
 *         <li>export - 3 files for export. 2 are always the same 3rt (repo.xml) has metadata</li>
 *         <li>layout - mostly empty</li>
 *     </ul>
 *     in the root files two files are important. "editortreemodel.xml" and "runstructure.xml".
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

	private static final Logger logger = LoggerFactory.getLogger(OPAL_Exporter.class);
	private final OPALTestExport testExport;

	public OPAL_Exporter(ImageService imageService, OPALTestExport testExport) {
		super(imageService);
		this.testExport = testExport;
	}

	/**
	 * Exports the course to the desired format.
	 * <p>
	 * This method is called by the {@link DownloadManager} and is responsible for creating the final course.
	 *
	 * @param rawCourse       the course to be exported
	 * @param templateSetName the name of the template set to be used
	 * @param targetDirectory the directory where the course should be exported to. Normally a temporary directory.
	 * @throws IOException              if an error occurs during the export
	 * @throws IllegalArgumentException if the rawCourse, templateSetName, or targetDirectory is null
	 */
	@Override
	public void export(RawCourse rawCourse, String templateSetName, File targetDirectory)
			throws IOException, IllegalArgumentException {
		if (rawCourse == null) {
			throw new IllegalArgumentException("RawCourse is null");
		}
		if (templateSetName == null) {
			throw new IllegalArgumentException("TemplateSetName is null");
		}
		if (targetDirectory == null) {
			throw new IllegalArgumentException("TargetDirectory is null");
		}
		CourseOrganizerOPAL organizer = new CourseOrganizerOPAL(rawCourse);
		// infos and general structure
		writeBasicDirectories(targetDirectory);
		// materials
		var courseDir = new File(targetDirectory, "coursefolder");
		organizer.creatMaterials(courseDir);
		// config
		writeCourseConfig(targetDirectory);
		writeEditorTreeModel(targetDirectory, organizer);
		writeRunStructure(targetDirectory, organizer);
		// export directory
		var exportDirectory = new File(targetDirectory, "export");
		createExportFile(exportDirectory, "learninggroupexport");
		createExportFile(exportDirectory, "rightgroupexport");
		createRepoFile(exportDirectory, rawCourse);
	}

	/**
	 * Exports the test to the desired format.
	 *
	 * @param testMaterial the test to be exported
	 * @param tempDir      the directory where the test should be exported to. Normally a temporary directory.
	 * @return the zip file containing the test
	 */
	public void exportTest(TestMaterial testMaterial, File tempDir) {
		var testInfo = new OPAlTestMaterialInfo(testMaterial);
		testExport.createTestSingle(testInfo, tempDir);
	}

	private void writeBasicDirectories(File directory) {
		File courseDir = new File(directory, "coursefolder");
		courseDir.mkdir();
		File exportDir = new File(directory, "export");
		exportDir.mkdir();
		File layoutDir = new File(directory, "layout");
		layoutDir.mkdir();
		// default files
		backUpContext(directory);
		emptyCourseRulesXML(directory);
	}

	private void writeCourseConfig(File targetDirectory) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document doc = docBuilder.newDocument();
		// root element
		var rootElement = doc.createElement("org.olat.course.config.CourseConfig");
		doc.appendChild(rootElement);
		// children
		var version = doc.createElement("version");
		version.appendChild(doc.createTextNode("12"));
		rootElement.appendChild(version);
		var configuration = doc.createElement("configuration");
		configuration.setAttribute("class", "hashtable");
		rootElement.appendChild(configuration);
		// default entries
		for (var entry : getDefaultConfigEntries(doc)) {
			configuration.appendChild(entry);
		}
		// write to file
		DefaultXMLWriter.write(doc, STR."\{targetDirectory.getAbsolutePath()}/CourseConfig.xml");
	}

	private void writeEditorTreeModel(File directory, CourseOrganizerOPAL course) {
		Document doc = newDocument();
		// root element
		var root = doc.createElement("org.olat.course.tree.CourseEditorTreeModel");
		doc.appendChild(root);
		var rootNode = new RootOPAL().createRootOPAL(course, FileCreationType.TREE, doc);
		root.appendChild(rootNode);
		root.appendChild(elementWithText(doc, "latestPublishTimestamp", -1));
		root.appendChild(elementWithText(doc, "highestNodeId", 0));
		root.appendChild(elementWithText(doc, "version", 7));
		// write to file
		DefaultXMLWriter.write(doc, STR."\{directory.getAbsolutePath()}/editortreemodel.xml");
	}

	private void writeRunStructure(File targetDirectory, CourseOrganizerOPAL course) {
		Document doc = newDocument();
		// root element
		var rootElement = doc.createElement("org.olat.course.Structure");
		doc.appendChild(rootElement);
		var rootNode = new RootOPAL().createRootOPAL(course, FileCreationType.RUN, doc);
		rootElement.appendChild(rootNode);
		rootElement.appendChild(elementWithText(doc, "latestPublishTimestamp", -1));
		rootElement.appendChild(elementWithText(doc, "version", 7));

		// write to file
		DefaultXMLWriter.write(doc, STR."\{targetDirectory.getAbsolutePath()}/runstructure.xml");
	}

	private void createExportFile(File exportDirectory, String fileName) {
		var file = new File(exportDirectory, STR."\{fileName}.xml");
		try (var writer = new FileWriter(file)) {
			writer.write("""
					<?xml version="1.0" encoding="UTF-8"?>
					     
					<OLATGroupExport>
					  <AreaCollection/>
					  <GroupCollection/>
					</OLATGroupExport>
					""");
		} catch (IOException e) {
			logger.error(STR."Error while writing \{fileName}");
		}
	}

	private void createRepoFile(File exportDirectory, RawCourse rawCourse) {
		var DocFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = DocFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document doc = docBuilder.newDocument();
		// root element
		var rootElement = doc.createElement("RepositoryEntryProperties");
		doc.appendChild(rootElement);
		// children
		rootElement.appendChild(elementWithText(doc, "Softkey", "myolat_1_1234567890123456"));
		rootElement.appendChild(elementWithText(doc, "ResourceName",
				rawCourse.getMetadata().getName().orElse("new course").replace(" ", "_")));
		rootElement.appendChild(elementWithText(doc, "DisplayName",
				rawCourse.getMetadata().getName().orElse("new course")));
		var description = rawCourse.getMetadata().getDescription().orElse("");
		var encodedDescription = escapeTags(description);
		var descriptionElement = doc.createElement("Description");
		descriptionElement.appendChild(doc.createTextNode(encodedDescription));
		rootElement.appendChild(descriptionElement);
		rootElement.appendChild(elementWithText(doc, "InitialAuthor", "MDTea-System")); // Todo Use a metadata field
		// metadata
		var metadata = doc.createElement("Metadata");
		final var creationDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		var time = creationDate.format(formatter);
		var otherInfos = rawCourse.getMetadata().getOtherInfos();
		var moduleNumber = otherInfos.getOrDefault("moduleNumber", "new module");
		var moduleName = rawCourse.getMetadata().getName().orElse("new module");
		var version = otherInfos.getOrDefault("version", "0");
		var level = rawCourse.getMetadata().getLevel().orElse("0");
		var moduleTime = otherInfos.getOrDefault("moduleTime", "");
		var ownerAccess = creationDate.toEpochSecond(ZoneOffset.UTC) * 1000;
		// Todo replace with xml system or something else
		var content = STR."""
<list>
<org.olat.repository.MetaDataElement>
<creationDate class="sql-timestamp">\{time}</creationDate>
<version>0</version>
<name>modulenumber</name>
<value>\{moduleNumber}</value>
<repoKey>0</repoKey>
</org.olat.repository.MetaDataElement>
<org.olat.repository.MetaDataElement>
<creationDate class="sql-timestamp">\{time}</creationDate>
<version>0</version>
<name>modulename</name>
<value>\{moduleName}</value>
<repoKey>0</repoKey>
</org.olat.repository.MetaDataElement>
<org.olat.repository.MetaDataElement>
<creationDate class="sql-timestamp">\{time}</creationDate>
<version>0</version>
<name>moduleversion</name>
<value>\{version}</value>
<repoKey>0</repoKey>
</org.olat.repository.MetaDataElement>
<org.olat.repository.MetaDataElement>
<creationDate class="sql-timestamp">\{time}</creationDate>
<version>0</version>
<name>modulelevel</name>
<value>\{level}</value>
<repoKey>0</repoKey>
</org.olat.repository.MetaDataElement>
<org.olat.repository.MetaDataElement>
<creationDate class="sql-timestamp">\{time}</creationDate>
<version>0</version>
<name>moduleduration</name>
<value>\{moduleTime}</value>
<repoKey>0</repoKey>
</org.olat.repository.MetaDataElement>
<org.olat.repository.MetaDataElement>
<creationDate class="sql-timestamp">\{time}</creationDate>
<version>0</version>
<name>olatlinkinteresting</name>
<value></value>
<repoKey>0</repoKey>
</org.olat.repository.MetaDataElement>
<org.olat.repository.MetaDataElement>
<creationDate class="sql-timestamp">\{time}</creationDate>
<version>0</version>
<name>lastOwnerAccess</name>
<value>\{ownerAccess}</value>
<repoKey>0</repoKey>
</org.olat.repository.MetaDataElement>
</list>""";


		var encoded = doc.createCDATASection(content);
		metadata.appendChild(encoded);
		rootElement.appendChild(metadata);
		// write to file
		DefaultXMLWriter.write(doc, STR."\{exportDirectory.getAbsolutePath()}/repo.xml");
	}

	private void backUpContext(File directory) {
		var file = new File(directory, "__backup_context__.json");
		try (var writer = new FileWriter(file)) {
			writer.write("""
					{
					  "data": {}
					}""");
		} catch (IOException e) {
			logger.error("Error while writing __backup_context__.json");
		}
	}

	/**
	 * Default empty course_rules.xml
	 *
	 * @param directory the directory to write the file to
	 */
	private void emptyCourseRulesXML(File directory) {
		var courseRules = new File(directory, "course_rules.xml");
		try (var writer = new FileWriter(courseRules)) {
			writer.write("<list/>");
		} catch (IOException e) {
			logger.error("Error while writing course_rules.xml");
		}
	}


	private List<Element> getDefaultConfigEntries(Document doc) {
		return List.of(
				createEntry(doc, "KEY_CALENDAR_ENABLED", false),
				createEntry(doc, "SHAREDFOLDER_SOFTKEY", "sf.notconfigured"),
				createEntry(doc, "FORBID_EXTENSIONS", ""),
				createEntry(doc, "KEY_DOWNLOAD_CERTIFICATE_ENABLED", false),
				createEntry(doc, "KEY_CERTIFICATE_ENABLED", false),
				createEntry(doc, "CSS_FILEREF", "form.layout.setsystemcss"),
				createEntry(doc, "KEY_EFFICENCY_RULES_ENABLED", true),
				createEntry(doc, "KEY_EFFICENCY_ENABLED", true));
	}
}
