package de.olivergeisel.materialgenerator.finalization.export;

import de.olivergeisel.materialgenerator.finalization.export.opal.CourseOrganizerOPAL;
import de.olivergeisel.materialgenerator.finalization.export.opal.DefaultXMLWriter;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
import java.util.Random;


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

	private static final Logger logger = LoggerFactory.getLogger(OPAL_Exporter.class);
	private static final String CLASS  = "class";

	private CourseOrganizerOPAL organizer;

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
		// infos and general structure
		writeBasicDirectories(targetDirectory);
		// config
		writeCourseConfig(targetDirectory);
		var id = new Random().nextInt(100000); // Todo check if this is okay or works
		writeEditorTreeModel(targetDirectory, rawCourse, id);
		writeRunStructure(targetDirectory, rawCourse, id);
		// export directory
		var exportDirectory = new File(targetDirectory, "export");
		createExportFile(exportDirectory, "learninggroupexport");
		createExportFile(exportDirectory, "rightgroupexport");
		createRepoFile(exportDirectory, rawCourse);

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
		var encodedDescription = doc.createCDATASection(description);
		var descriptionElement = doc.createElement("Description");
		descriptionElement.appendChild(encodedDescription);
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

	private void writeRunStructure(File targetDirectory, RawCourse rawCourse, int id) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document doc = docBuilder.newDocument();
		// root element
		var rootElement = doc.createElement("org.olat.course.Structure");
		doc.appendChild(rootElement);
		rootElement.appendChild(
				createRootNode(doc, "rootNode", id, rawCourse.getMetadata().getName().orElse("new course")));
		rootElement.appendChild(elementWithText(doc, "latestPublishTimestamp", -1));
		rootElement.appendChild(elementWithText(doc, "version", 7));

		// write to file
		DefaultXMLWriter.write(doc, STR."\{targetDirectory.getAbsolutePath()}/runstructure.xml");
	}

	private Element elementWithText(Document doc, String name, String text) {
		var element = doc.createElement(name);
		element.appendChild(doc.createTextNode(text));
		return element;
	}

	private Element elementWithText(Document doc, String name, long value) {
		return elementWithText(doc, name, Long.toString(value));
	}

	private Element emptyElement(Document doc, String name) {
		return doc.createElement(name);
	}

	private void writeEditorTreeModel(File directory, RawCourse course, long id) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document doc = docBuilder.newDocument();
		// root element
		var rootElement = doc.createElement("org.olat.course.tree.CourseEditorTreeModel");
		doc.appendChild(rootElement);
		var rootNode = doc.createElement("rootNode");
		rootNode.setAttribute(CLASS, "org.olat.course.tree.CourseEditorTreeNode");
		rootElement.appendChild(rootNode);
		rootNode.appendChild(elementWithText(doc, "ident", id));
		rootNode.appendChild(elementWithText(doc, "accessible", true));
		rootNode.appendChild(elementWithText(doc, "selected", false));
		rootNode.appendChild(elementWithText(doc, "hrefPreferred", false));
		rootNode.appendChild(elementWithText(doc, "invisible", false)); // Todo maybe true for protection
		// cn object
		var cn = createRootNode(doc, "cn", id, course.getMetadata().getName().orElse("new course"));
		rootNode.appendChild(cn);
		var dirtry = elementWithText(doc, "dirty", false);
		var deleted = elementWithText(doc, "deleted", false);
		var newElement = elementWithText(doc, "newnode", false);
		rootNode.appendChild(dirtry);
		rootNode.appendChild(deleted);
		rootNode.appendChild(newElement);
		var latestPublishTimestamp = elementWithText(doc, "latestPublishTimestamp", -1);
		var highestNodeId = elementWithText(doc, "highestNodeId", 0);
		var version = elementWithText(doc, "version", 7);
		rootElement.appendChild(latestPublishTimestamp);
		rootElement.appendChild(highestNodeId);
		rootElement.appendChild(version);

		// write to file
		DefaultXMLWriter.write(doc, STR."\{directory.getAbsolutePath()}/editortreemodel.xml");
	}

	private Element createRootNode(Document doc, String tagType, long id, String title) {
		var node = doc.createElement(tagType);
		node.setAttribute(CLASS, "org.olat.course.nodes.RootCourseNode");
		node.appendChild(elementWithText(doc, "ident", id));
		node.appendChild(elementWithText(doc, "type", "root"));
		node.appendChild(elementWithText(doc, "shortTitle", title));
		node.appendChild(elementWithText(doc, "longTitle", title));
		node.appendChild(elementWithText(doc, "learningObjectives", ""));
		var moduleConfiguration = doc.createElement("moduleConfiguration");
		node.appendChild(moduleConfiguration);
		var config = doc.createElement("config");
		moduleConfiguration.appendChild(config);
		config.appendChild(createEntry(doc, "courseEnrolmentEnabled", false));
		config.appendChild(createEntry(doc, "columns", 1));
		config.appendChild(createEntry(doc, "courseEnrolmentMailSubject", ""));
		config.appendChild(createEntry(doc, "display", "peekview"));
		config.appendChild(createEntry(doc, "courseEnrolmentGroupId", 0));
		config.appendChild(createEntry(doc, "courseEnrolmentMailEnabled", false));
		config.appendChild(createEntry(doc, "courseEnrolmentMailBody", ""));
		config.appendChild(createEntry(doc, "configversion", 3));
		var additionalConditions = doc.createElement("additionalConditions");
		node.appendChild(additionalConditions);
		additionalConditions.appendChild(emptyElement(doc, "org.olat.course.condition.additionalconditions"
														   + ".PasswordCondition"));
		node.appendChild(elementWithText(doc, "evaluateAsSubcourse", false));
		return node;
	}

	private Node elementWithText(Document doc, String nodeName, boolean booleanValue) {
		return elementWithText(doc, nodeName, booleanValue ? "true" : "false");
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
		configuration.setAttribute(CLASS, "hashtable");
		rootElement.appendChild(configuration);
		// default entries
		for (var entry : getDefaultConfigEntries(doc)) {
			configuration.appendChild(entry);
		}
		// write to file
		DefaultXMLWriter.write(doc, STR."\{targetDirectory.getAbsolutePath()}/CourseConfig.xml");
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

	private Element createEntry(Document doc, String name, boolean booleanValue) {
		var entry = doc.createElement("entry");
		var wordform = doc.createElement("wordform");
		var booleanNode = doc.createElement("boolean");
		wordform.appendChild(doc.createTextNode(name));
		booleanNode.appendChild(doc.createTextNode(booleanValue ? "true" : "false"));
		entry.appendChild(wordform);
		entry.appendChild(booleanNode);
		return entry;
	}

	private Element createEntry(Document doc, String firstValue, String secondValue) {
		var entry = doc.createElement("entry");
		var wordform = doc.createElement("wordform");
		var secondWord = doc.createElement("wordform");
		wordform.appendChild(doc.createTextNode(firstValue));
		secondWord.appendChild(doc.createTextNode(secondValue));
		entry.appendChild(wordform);
		entry.appendChild(secondWord);
		return entry;
	}

	private Element createEntry(Document doc, String firstValue, int intVal) {
		var entry = doc.createElement("entry");
		var wordform = doc.createElement("wordform");
		var intNode = doc.createElement("int");
		wordform.appendChild(doc.createTextNode(firstValue));
		intNode.appendChild(doc.createTextNode(Integer.toString(intVal)));
		entry.appendChild(wordform);
		entry.appendChild(intNode);
		return entry;
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
}
