/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.teiid.core.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Exports resources to a .zip file
 */
public class ModelerZipExporter {

	private ZipOutputStream outputStream;
	private boolean useCompression = true;
	private boolean clearSourceConnectionInfo = true;

	// constants

	/**
	 * Create an instance of this class.
	 * 
	 * @param filename
	 *            java.lang.String
	 * @param compress
	 *            boolean
	 * @param includeManifestFile
	 *            boolean
	 * @param clearSourceConnectionInfo
	 *            boolean
	 * @exception java.io.IOException
	 */
	public ModelerZipExporter(String filename, boolean compress,
			boolean clearSourceConnectionInfo) throws IOException {
		this.outputStream = new ZipOutputStream(new FileOutputStream(filename));
		this.useCompression = compress;
		this.clearSourceConnectionInfo = clearSourceConnectionInfo;
	}

	/**
	 * Do all required cleanup now that we're finished with the currently-open
	 * .zip
	 * 
	 * @exception java.io.IOException
	 */
	public void finished() throws IOException {
		outputStream.close();
	}

	/**
	 * Create a new ZipEntry with the passed pathname and contents, and write it
	 * to the current archive
	 * 
	 * @param pathname
	 *            java.lang.String
	 * @param contents
	 *            byte[]
	 * @exception java.io.IOException
	 */
	protected void write(String pathname, byte[] contents) throws IOException {
		ZipEntry newEntry = new ZipEntry(pathname);

		// if the contents are being compressed then we get the below for free.
		if (!useCompression) {
			newEntry.setMethod(ZipEntry.STORED);
			newEntry.setSize(contents.length);
			CRC32 checksumCalculator = new CRC32();
			checksumCalculator.update(contents);
			newEntry.setCrc(checksumCalculator.getValue());
		}

		outputStream.putNextEntry(newEntry);
		outputStream.write(contents);
		outputStream.closeEntry();
	}

	/**
	 * Write the passed resource to the current archive
	 * 
	 * @param resource
	 *            org.eclipse.core.resources.IFile
	 * @param destinationPath
	 *            java.lang.String
	 * @exception java.io.IOException
	 * @exception org.eclipse.core.runtime.CoreException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws TransformerConfigurationException
	 */
	public void write(IFile resource, String destinationPath)
			throws IOException, CoreException, SAXException,
			ParserConfigurationException, TransformerConfigurationException,
			TransformerException, TransformerFactoryConfigurationError {
		ByteArrayOutputStream output = null;
		InputStream contentStream = null;
		Document doc = null;
		// If the user wants to remove the connection info during export and
		// this is a model file, look for "tags" with key values that start 
		// with "connection" and remove the node
		if (this.clearSourceConnectionInfo
				&& resource.getFileExtension().equals("xmi")) { //$NON-NLS-1$
			contentStream = resource.getContents(false);
			doc = readXml(contentStream);
			removeAll(doc, Node.ELEMENT_NODE, "tags", "connection"); //$NON-NLS-1$ //$NON-NLS-2$
			doc.normalize();
			contentStream = convertDocToInputStream(doc);

		}

		try {
			output = new ByteArrayOutputStream();
			if (contentStream == null)
				contentStream = resource.getContents(false);
			int chunkSize = contentStream.available();
			byte[] readBuffer = new byte[chunkSize];
			int n = contentStream.read(readBuffer);

			while (n > 0) {
				output.write(readBuffer);
				n = contentStream.read(readBuffer);
			}
		} finally {
			if (output != null)
				output.close();
			if (contentStream != null)
				contentStream.close();
		}

		write(destinationPath, output.toByteArray());
	}

	// This method walks the document and removes all nodes
	// of the specified type and specified name that satisfy the filter condition.
	public static void removeAll(Node node, short nodeType, String name, String filter) {
	    if (node.getNodeType() == nodeType && node.getNodeName().equals(name) 
	    		&& node.getAttributes().getNamedItem("key").getTextContent().startsWith(filter)) { //$NON-NLS-1$
	    	node.getParentNode().removeChild(node);
	    } else {
	        // Visit the children
	        NodeList list = node.getChildNodes();
	        for (int i=0; i<list.getLength(); i++) {
	            removeAll(list.item(i), nodeType, name, filter);
	        }
	    }
	}

	/**
	 * Read XML as DOM.
	 */
	public Document readXml(InputStream is) throws SAXException, IOException,
			ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setValidating(false);
		dbf.setIgnoringComments(false);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setNamespaceAware(true);

		DocumentBuilder db = null;
		db = dbf.newDocumentBuilder();
		db.setEntityResolver(new NullResolver());

		return db.parse(is);
	}

	public InputStream convertDocToInputStream(Document doc)
			throws TransformerConfigurationException, TransformerException,
			TransformerFactoryConfigurationError {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(doc);
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource,
				outputTarget);
		InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
		return is;
	}

	class NullResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			return new InputSource(new StringReader(
					StringUtil.Constants.EMPTY_STRING));
		}
	}
}
