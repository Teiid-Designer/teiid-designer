/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.file;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.eclipse.core.resources.IFile;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.Vdb.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Callback used by {@link VdbFileProcessor} for determining the version
 * of a vdb file by extracting it from the manifest.
 *
 * Note. Purposely avoids using JAXB as the vdb-deployed schema is version
 * dependent and we want to try and eliminate such exceptions occurring when
 * just trying to retrieve the version.
 */
public class ValidationVersionCallback implements IVdbFileCallback, VdbConstants {

    private final IFile vdbFile;

    private ITeiidServerVersion version;

    private Exception exception;

    /**
     * @param vdbFile
     */
    public ValidationVersionCallback(IFile vdbFile) {
        this.vdbFile = vdbFile;
    }

    @Override
    public IFile getVdb() {
        return vdbFile;
    }

    /**
     * @return the validation version of the vdb file
     */
    public ITeiidServerVersion getValidationVersion() {
        return version;
    }

    @Override
    public List<String> getFilesOfInterest() {
        return Collections.singletonList(MANIFEST);
    }

    @Override
    public void processStream(String fileName, InputStream inputStream) {
        CoreArgCheck.isNotNull(inputStream);

        /* Use DOM and XPath as these do not rely on the schema (which is version dependent) */
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            XPath xPath =  XPathFactory.newInstance().newXPath();
            String expression = "/vdb/property[@name='" + Vdb.Xml.VALIDATION_VERSION + "']"; //$NON-NLS-1$ //$NON-NLS-2$
            Node versionNode = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);
            if (versionNode == null)
                return;

            NamedNodeMap attributes = versionNode.getAttributes();
            if (attributes == null)
                return;

            Node valueAttribute = attributes.getNamedItem("value"); //$NON-NLS-1$
            if (valueAttribute == null)
                return;

            version = new TeiidServerVersion(valueAttribute.getNodeValue());

        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Override
    public void exceptionThrown(Exception ex) {
        this.exception = ex;
    }

    /**
     * @return true if an exception occurred
     */
    public boolean hasException() {
        return this.exception != null;
    }
}
