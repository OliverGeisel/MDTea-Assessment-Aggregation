package de.olivergeisel.materialgenerator.finalization.export.opal.test;


import de.olivergeisel.materialgenerator.finalization.export.ZipService;
import de.olivergeisel.materialgenerator.finalization.export.opal.DefaultXMLWriter;
import de.olivergeisel.materialgenerator.finalization.export.opal.OPALComplexMaterialInfo;
import de.olivergeisel.materialgenerator.finalization.export.opal.OPALMaterialInfo;
import de.olivergeisel.materialgenerator.generation.material.assessment.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.UUID;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.*;

/**
 * Management for exporting a test to the OPAL platform.
 */
@Service
public class OPALTestExport {

	private final ZipService zipService;


	public OPALTestExport(ZipService zipService) {this.zipService = zipService;}


	/**
	 * Writes the test to the file system.
	 *
	 * @param test            The test to export
	 * @param targetDirectory The directory to write the test to
	 */
	public void createTestSingle(OPALComplexMaterialInfo<TestMaterial, OPALItemMaterialInfo> test,
			File targetDirectory) {
		if (!targetDirectory.exists()) {
			targetDirectory.mkdirs();
		}
		// manifest
		writeManifest(test, targetDirectory);
		// write mainFile xml (test-xml)
		writeTestXml(test, targetDirectory);
		// items
		for (var item : test.getMaterialInfos()) {
			writeItemXml(item, targetDirectory);
		}
	}

	private void createZip(OPALComplexMaterialInfo<TestMaterial, OPALItemMaterialInfo> test, File directory) {
		File dir;
		try {
			dir = Files.createTempDirectory("repo").toFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// manifest
		writeManifest(test, dir);
		// write mainFile xml (test-xml)
		writeTestXml(test, dir);
		// items
		for (var item : test.getMaterialInfos()) {
			writeItemXml(item, dir);
		}
		try {
			var zipFile = zipService.createZipArchive("repo.zip", directory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeTestXml(OPALComplexMaterialInfo<TestMaterial, OPALItemMaterialInfo> test, File dir) {
		var document = newDocument();
		var root = document.createElement("assessmentTest");
		root.setAttribute("xmlns", "http://www.imsglobal.org/xsd/imsqti_v2p1");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation",
				"http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_v2p1p1.xsd");
		root.setAttribute("identifier", Long.toString(test.getNodeId()));
		root.setAttribute("title", test.getName());
		document.appendChild(root);
		// outcome score
		var outcomeDeclaration = document.createElement("outcomeDeclaration");
		root.appendChild(outcomeDeclaration);
		outcomeDeclaration.setAttribute("identifier", "SCORE");
		outcomeDeclaration.setAttribute("cardinality", "single");
		outcomeDeclaration.setAttribute("baseType", "float");
		var defaultValue = document.createElement("defaultValue");
		defaultValue.appendChild(elementWithText(document, "value", "0"));
		outcomeDeclaration.appendChild(defaultValue);
		// max score
		var maxScore = document.createElement("outcomeDeclaration");
		root.appendChild(maxScore);
		maxScore.setAttribute("identifier", "MAXSCORE");
		maxScore.setAttribute("cardinality", "single");
		maxScore.setAttribute("baseType", "float");
		var maxScoreValue = document.createElement("defaultValue");
		maxScoreValue.appendChild(elementWithText(document, "value", "1")); // Todo calculate bases on items
		maxScore.appendChild(maxScoreValue);
		// min score
		var minScore = document.createElement("outcomeDeclaration");
		root.appendChild(minScore);
		minScore.setAttribute("identifier", "MINSCORE");
		minScore.setAttribute("cardinality", "single");
		minScore.setAttribute("baseType", "float");
		var minScoreValue = document.createElement("defaultValue");
		minScoreValue.appendChild(elementWithText(document, "value", "0"));
		minScore.appendChild(minScoreValue);
		// testpart
		var testPart = document.createElement("testPart");
		root.appendChild(testPart);
		// create a "new" id for this test to match the standard - ToDO think about a better solution
		var newId = UUID.randomUUID().toString();
		testPart.setAttribute("identifier", newId);
		testPart.setAttribute("navigationMode", "nonlinear");
		testPart.setAttribute("submissionMode", "individual");
		var itemSessionControl = document.createElement("itemSessionControl");
		itemSessionControl.setAttribute("maxAttempts", "1");
		itemSessionControl.setAttribute("allowComment", "false");
		testPart.appendChild(itemSessionControl);
		// assessmentSection parts - TOdo is alway only one section in this state of the project
		var assessmentSection = document.createElement("assessmentSection");
		testPart.appendChild(assessmentSection);
		var sectionId = UUID.randomUUID().toString();
		assessmentSection.setAttribute("identifier", sectionId);
		assessmentSection.setAttribute("title", "Test-Sektion");
		assessmentSection.setAttribute("visible", "true");
		assessmentSection.setAttribute("fixed", "false");
		assessmentSection.appendChild(createElementWithAttribute(document, "rubricBlock", "view", "candidate"));
		for (var item : test.getMaterialInfos()) {
			assessmentSection.appendChild(createAssessmentItemRef(document, item));
		}
		// outcome processing
		var outcomeProcessing = document.createElement("outcomeProcessing");
		root.appendChild(outcomeProcessing);
		var setOutcomeValue = createElementWithAttribute(document, "setOutcomeValue", "identifier", "SCORE");
		outcomeProcessing.appendChild(setOutcomeValue);
		var sum = document.createElement("sum");
		setOutcomeValue.appendChild(sum);
		sum.appendChild(createElementWithAttribute(document, "testVariables", "variableIdentifier", "SCORE"));

		// write
		var filePath = new File(dir, STR."\{test.getNodeId()}.xml").getAbsolutePath();
		DefaultXMLWriter.write(document, filePath);
	}

	private Element createAssessmentItemRef(Document document, OPALMaterialInfo item) {
		var assessmentItemRef = document.createElement("assessmentItemRef");
		assessmentItemRef.setAttribute("identifier", Long.toString(item.getNodeId()));
		assessmentItemRef.setAttribute("href", STR."\{item.getNodeId()}.xml");
		assessmentItemRef.setAttribute("fixed", "false");
		return assessmentItemRef;
	}

	private void writeManifest(OPALComplexMaterialInfo<TestMaterial, OPALItemMaterialInfo> test, File dir) {
		var document = newDocument();
		var filePath = new File(dir, "imsmanifest.xml").getAbsolutePath();
		var root = document.createElement("manifest");
		document.appendChild(root);
		root.setAttribute("xmlns", "http://www.imsglobal.org/xsd/imscp_v1p1");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation", """
				http://www.imsglobal.org/xsd/imscp_v1p1 http://www.imsglobal.org/xsd/qti/qtiv2p1/qtiv2p1_imscpv1p2_v1p0.xsd
				http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_v2p1p1.xsd
				http://www.imsglobal.org/xsd/imsqti_metadata_v2p1 http://www.imsglobal.org/xsd/qti/qtiv2p1/imsqti_metadata_v2p1p1.xsd
				http://ltsc.ieee.org/xsd/LOM http://www.imsglobal.org/xsd/imsmd_loose_v1p3p2.xsd
				http://www.w3.org/1998/Math/MathML http://www.w3.org/Math/XMLSchema/mathml2/mathml2.xsd""");
		root.setAttribute("identifier", STR."\{test.getNodeId()}_mainfest");
		var metadata = document.createElement("metadata");
		root.appendChild(metadata);
		metadata.appendChild(elementWithText(document, "schema", "QTIv2.1 Package"));
		metadata.appendChild(elementWithText(document, "schemaversion", "1.0.0"));
		root.appendChild(emptyElement(document, "organizations"));
		var resources = document.createElement("resources");
		root.appendChild(resources);
		// items
		var items = new LinkedList<Element>();
		for (var item : test.getMaterialInfos()) {
			var itemElement = document.createElement("resource");
			itemElement.setAttribute("identifier", Long.toString(item.getNodeId()));
			itemElement.setAttribute("type", "imsqti_item_xmlv2p1");
			itemElement.setAttribute("href", STR."\{item.getNodeId()}.xml");
			itemElement.appendChild(
					createElementWithAttribute(document, "file", "href", STR."\{item.getNodeId()}.xml"));
			items.add(itemElement);
		}
		// main resource
		var mainResource = document.createElement("resource");
		mainResource.setAttribute("identifier", Long.toString(test.getNodeId()));
		mainResource.setAttribute("type", "imsqti_test_xmlv2p1");
		mainResource.setAttribute("href", test.getNodeId() + ".xml");
		metadata = document.createElement("metadata");
		mainResource.appendChild(metadata);
		// lom
		var lom = createElementWithAttribute(document, "lom", "xmlns", "http://ltsc.ieee.org/xsd/LOM");
		metadata.appendChild(lom);
		var general = document.createElement("general");
		var identifier = document.createElement("identifier");
		identifier.appendChild(elementWithText(document, "entry", test.getNodeId()));
		general.appendChild(identifier);
		var title = document.createElement("title");
		title.appendChild(elementWithText(document, "string", test.getName()));
		general.appendChild(title);
		var description = document.createElement("description");
		var string = document.createElement("string");
		string.setAttribute("language", "de");
		string.appendChild(
				document.createCDATASection(test.getDescription()));
		description.appendChild(string);
		lom.appendChild(general);
		var lifecycle = document.createElement("lifeCycle");
		lom.appendChild(lifecycle);
		var contribute = document.createElement("contribute");
		lifecycle.appendChild(contribute);
		var entity = document.createElement("entity");
		contribute.appendChild(entity);
		var card = URLEncoder.encode(createVCardString(), StandardCharsets.UTF_8);
		entity.appendChild(document.createCDATASection(card));
		var date = document.createElement("date");
		date.appendChild(elementWithText(document, "dateTime",
				LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
		contribute.appendChild(date);
		var role = document.createElement("role");
		role.appendChild(elementWithText(document, "source", "LOMv1.0"));
		role.appendChild(elementWithText(document, "value", "creator"));
		contribute.appendChild(role);
		var secondContribute = document.createElement("contribute");
		lifecycle.appendChild(secondContribute);
		var secondEntity = document.createElement("entity");
		secondContribute.appendChild(secondEntity);
		secondEntity.appendChild(document.createCDATASection(card));
		var secondDate = document.createElement("date");
		secondDate.appendChild(elementWithText(document, "dateTime",
				LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
		secondContribute.appendChild(secondDate);
		var secondRole = document.createElement("role");
		secondRole.appendChild(elementWithText(document, "source", "LOMv1.0"));
		secondRole.appendChild(elementWithText(document, "value", "author"));
		secondContribute.appendChild(secondRole);
		var educational = document.createElement("educational");
		lom.appendChild(educational);
		var learningResourceType = document.createElement("learningResourceType");
		learningResourceType.appendChild(elementWithText(document, "source", "LOMv1.0"));
		learningResourceType.appendChild(elementWithText(document, "value", "ExaminationTest"));
		educational.appendChild(learningResourceType);
		var qtiMeta = document.createElement("qtiMetadata");
		qtiMeta.setAttribute("xmlns", "http://www.imsglobal.org/xsd/imsqti_metadata_v2p1");
		qtiMeta.appendChild(elementWithText(document, "toolName", "MDTea-Prototype"));
		qtiMeta.appendChild(elementWithText(document, "toolVersion", "1.1.0"));
		qtiMeta.appendChild(elementWithText(document, "toolVendor", "MDTea"));
		educational.appendChild(qtiMeta);
		var technical = document.createElement("technical");
		technical.appendChild(elementWithText(document, "location", ""));
		lom.appendChild(technical);

		var file = createElementWithAttribute(document, "file", "href", STR."\{test.getNodeId()}.xml");
		mainResource.appendChild(file);
		for (var item : test.getMaterialInfos()) {
			var dependency =
					createElementWithAttribute(document, "dependency", "identifierref",
							Long.toString(item.getNodeId()));
			mainResource.appendChild(dependency);
		}
		// add to resources
		resources.appendChild(mainResource);
		for (var item : items) {
			resources.appendChild(item);
		}

		// write
		DefaultXMLWriter.write(document, filePath);
	}

	/**
	 * Writes the item to the file system.
	 *
	 * @param item The item to write
	 * @param dir  The directory to write the item to
	 * @throws IllegalArgumentException If the item type is unknown
	 */
	private <T extends ItemMaterial> void writeItemXml(OPALItemMaterialInfo<T> item, File dir)
			throws IllegalArgumentException {
		var itemType = item.getOriginalMaterial().getTemplateType();
		switch (itemType.getType()) {
			case "SINGLE_CHOICE" ->
					new SingleChoiceItemWriter().writeItem((OPALItemMaterialInfo<SingleChoiceItemMaterial>) item, dir);
			case "MULTIPLE_CHOICE" ->
					new MultipleChoiceItemWriter().writeItem((OPALItemMaterialInfo<MultipleChoiceItemMaterial>) item,
							dir);
			case "TRUE_FALSE" ->
					new TrueFalseItemWriter().writeItem((OPALItemMaterialInfo<TrueFalseItemMaterial>) item, dir);
			case "FILL_OUT_BLANKS" ->
					new FillOutBlanksItemWriter().writeItem((OPALItemMaterialInfo<FillOutBlanksItemMaterial>) item,
							dir);
			default -> throw new IllegalArgumentException(STR."Unknown item type: \{itemType}");
		}
	}


	private void createRepoXml(OPALComplexMaterialInfo test, File targetDirectory) {
		var document = newDocument();
		var filePath = new File(targetDirectory, "repo.xml").getAbsolutePath();
		var fileName = STR."\{test.getNodeId()}.zip";
		var root = document.createElement("RepositoryEntryProperties");
		document.appendChild(root);
		root.appendChild(elementWithText(document, "Softkey", "myolat_1_123456789"));
		root.appendChild(elementWithText(document, "ResourceName", fileName));
		root.appendChild(elementWithText(document, "DisplayName", test.getName()));
		var description = document.createElement("Description");
		description.appendChild(
				document.createCDATASection(test.getCourseOrganizer().getMeta().getDescription().orElse("")));
		root.appendChild(description);
		root.appendChild(elementWithText(document, "InitialAuthor",
				test.getCourseOrganizer().getMeta().getOtherInfos().getOrDefault("author", "")));
		var meta = document.createElement("Metadata");
		meta.appendChild(document.createCDATASection(metaString(document, test)));
		root.appendChild(meta);
		DefaultXMLWriter.write(document, filePath);
	}

	private String createVCardString() {
		return """
				BEGIN:VCARD
				VERSION:4.0
				PRODID:MDTea-Prototype 1.1.0
				N;LANGUAGE=de:UNKNOWN;;;;
				FN;UNKNOWN;;;
				EMAIL:UNKNOWN
				END:VCARD""";
	}

	private String metaString(Document doc, OPALComplexMaterialInfo test) {
		var metadata = doc.createElement("Metadata");
		final var creationDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		var time = creationDate.format(formatter);
		var meta = test.getCourseOrganizer().getMeta();
		var otherInfos = meta.getOtherInfos();
		var timeShort = creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		var timeShortLetter = creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		var version = otherInfos.getOrDefault("version", "0");
		var author = otherInfos.getOrDefault("author", "");
		var description = doc.createCDATASection(meta.getDescription().orElse(""));
		var id = test.getNodeId();
		var testName = test.getName();
		var fileName = STR."\{test.getNodeId()}.zip"; // todo normally Test.zip
		var license = "";
		var link = "";
		return STR."""
				<list>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>test_type</name>
				<value>TEST_TYPE_ONYX</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>RESSOURCE_TYPE</name>
				<value>TEST</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version> <name>PRODUCER</name>
				<value>MDTea-Prototype</value> <repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement> <org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>FORMAT</name>
				<value>IMS QTI 2.1</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement> <org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version> <name>LAST_ACCESS</name>
				<value>\{timeShortLetter}</value> <repoKey>0</repoKey>
				<timevalue class="sql-timestamp">\{timeShort}</timevalue>
				</org.olat.repository.MetaDataElement> <org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version> <name>CREATION_DATE</name>
				<value>\{timeShortLetter}</value> <repoKey>0</repoKey>
				<timevalue class="sql-timestamp">\{timeShort}</timevalue>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>LAST_MODIFIED</name>
				<value>\{timeShortLetter}</value>
				<repoKey>0</repoKey>
				<timevalue class="sql-timestamp">\{timeShort}</timevalue>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>AUTHOR</name>
				<value>\{author}</value> <repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>VISIBILITY</name>
				<value>1</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version> <name>FAVOURITE</name>
				<value>false</value> <repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version> <name>DESCRIPTION</name>
				<value>\{description}</value>
				<repoKey>0</repoKey> </org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement> <creationDate
				class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>VERSION</name>
				<value>1.1.0</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>IDENTIFIER</name>
				<value>\{id}</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement> <creationDate
				class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>LICENSE</name>
				<value>\{license}</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>FILENAME</name>
				<value>\{fileName}</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>TOOL</name>
				<value>MDTea-Generator</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>LINK</name>
				<value>\{link}</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>ITEM_TYPE</name>
				<value>MULTIPLE_CHOICE</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>NAME</name>
				<value>\{testName}</value>
				<repoKey>0</repoKey>
				</org.olat.repository.MetaDataElement>
				<org.olat.repository.MetaDataElement>
				<creationDate class="sql-timestamp">\{time}</creationDate>
				<version>0</version>
				<name>NAME</name>
				<value>\{testName}</value>
				<repoKey>0</repoKey>
				<metagroup>NAME</metagroup>
				</org.olat.repository.MetaDataElement>
				</list>""";
	}

}
