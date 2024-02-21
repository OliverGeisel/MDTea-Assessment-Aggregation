package de.olivergeisel.materialgenerator.finalization.export.opal.test;


import java.io.File;

import static de.olivergeisel.materialgenerator.finalization.export.opal.BasicElementsOPAL.newDocument;

public class TestWriter {

	public void writeTest(OPALItemMaterialInfo test, File targetDirectory) {
		var document = newDocument();
		var root = document.createElement("assessmentTest");
		root.setAttribute("xmlns", "http://www.imsglobal.org/xsd/imsqti_v2p1");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation", "http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal"
												+ ".org/xsd/qti/qtiv2p1/imsqti_v2p1p1.xsd");
		root.setAttribute("identifier", "");
		root.setAttribute("title", "");
		document.appendChild(root);

	}
}
