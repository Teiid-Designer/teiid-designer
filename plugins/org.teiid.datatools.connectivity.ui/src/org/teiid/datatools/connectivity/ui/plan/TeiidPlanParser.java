package org.teiid.datatools.connectivity.ui.plan;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.sqltools.plan.IExecutionPlanDocument;
import org.eclipse.datatools.sqltools.plan.IPlanParser;
import org.eclipse.datatools.sqltools.plan.treeplan.TreeExecutionPlanDocument;
import org.eclipse.datatools.sqltools.plan.treeplan.TreePlanNodeComponent;
import org.eclipse.datatools.sqltools.plan.treeplan.TreePlanNodeComposite;
import org.eclipse.datatools.sqltools.plan.treeplan.TreePlanNodeLeaf;
import org.teiid.datatools.connectivity.ui.Activator;
import org.teiid.datatools.connectivity.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TeiidPlanParser implements IPlanParser {
	
	private Transformer transformer;

	public TeiidPlanParser() {
		try {
			InputStream stylesheetStream = FileLocator.openStream(Activator.getDefault().getBundle(),new Path("PlanToHTML.xsl"), true);
			Source xsltSource = new StreamSource(stylesheetStream);
			TransformerFactory transFact = 
			       TransformerFactory.newInstance();
			    transformer = transFact.newTransformer(xsltSource);

		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		Messages.getString("TeiidPlanSupportRunnable.errorCreatingParser"), e);
            Activator.getDefault().getLog().log(status);
		}
	}

	@Override
	public IExecutionPlanDocument[] parsePlan(String rawPlan) {
		TreeExecutionPlanDocument result;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document d;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource( new StringReader( rawPlan ) );
			d = builder.parse( is );
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		Messages.getString("TeiidPlanSupportRunnable.errorParsingPlan"), e);
            Activator.getDefault().getLog().log(status);
			return null;
		}
		
		Element docRoot = d.getDocumentElement();
		TreePlanNodeComponent rootNode = parsePlan(docRoot);
		
		String planName = "Teiid Plan"; //$NON-NLS-1$
		result = new TreeExecutionPlanDocument(rootNode, planName, rawPlan);
		return new TreeExecutionPlanDocument[]{result};
	}
	
	private TreePlanNodeComponent parsePlan(Element elem) {
		TreePlanNodeComponent rootNode;
		if(elem.getElementsByTagName("node").getLength() == 0) {
			rootNode = new TreePlanNodeLeaf();
		} else {
			rootNode = new TreePlanNodeComposite();
		}
		
		rootNode.setName(elem.getAttribute("name"));
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(new DOMSource(elem), result);
		} catch (TransformerException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		Messages.getString("TeiidPlanSupportRunnable.errorParsingPlan"), e);
            Activator.getDefault().getLog().log(status);
		}
		rootNode.setDetail(writer.toString());
		
		NodeList nodes = elem.getElementsByTagName("node");
		for(int i = 0; i<nodes.getLength(); i++) {
			Element childElement = (Element) nodes.item(i);
			handleNode(childElement, rootNode);
		}
		return rootNode;
	}

	private void handleNode(Element elem, TreePlanNodeComponent parentNode) {
		TreePlanNodeComponent node;
		if(elem.getElementsByTagName("node").getLength() == 0) {
			node = new TreePlanNodeLeaf();
		} else {
			node = new TreePlanNodeComposite();
		}
		
		node.setName(elem.getAttribute("name"));
		node.setParent(parentNode);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(new DOMSource(elem), result);
		} catch (TransformerException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		Messages.getString("TeiidPlanSupportRunnable.errorParsingPlan"), e);
            Activator.getDefault().getLog().log(status);
		}
		parentNode.setDetail(writer.toString());
		
		NodeList nodes = elem.getElementsByTagName("node");
		for(int i = 0; i<nodes.getLength(); i++) {
			Element childNode = (Element) nodes.item(i);
			handleNode(childNode, parentNode);
		}
	}

}
