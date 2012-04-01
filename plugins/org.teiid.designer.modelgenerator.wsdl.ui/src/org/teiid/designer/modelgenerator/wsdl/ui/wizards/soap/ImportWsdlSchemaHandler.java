/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.impl.XSDElementDeclarationImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants.RuntimeTypeNames;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions.SOAPSchemaProcessor;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.BaseSchemaObject;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessingException;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessor;

public class ImportWsdlSchemaHandler {

	WSDLImportWizardManager importManager;
	OperationsDetailsPage operationsDetailsPage;
	SchemaTreeModel schemaTreeModel = null;
	SchemaNode rootnode = null;

	public ImportWsdlSchemaHandler(WSDLImportWizardManager manager,
			OperationsDetailsPage operationsDetailsPage) {
		super();
		this.importManager = manager;
		this.operationsDetailsPage = operationsDetailsPage;
	}

	public List<Object> getSchemaForSelectedOperation(final int type,
			ProcedureGenerator generator) {

		Model wsdlModel = null;
		Object elementDeclaration = null;

		try {
			wsdlModel = importManager.getWSDLModel();
		} catch (ModelGenerationException e) {
			throw new RuntimeException(e);
		}

		XSDSchema[] schemas = wsdlModel.getSchemas();

		Operation selectedOperation = generator.getOperation();
		String partElementName = null;
		Part[] partArray = null;

		if (type == ProcedureInfo.REQUEST) {
			if (selectedOperation.getInputMessage() != null) {
				partArray = selectedOperation.getInputMessage().getParts();
			}
		} else {
			if (selectedOperation.getOutputMessage() != null) {
				partArray = selectedOperation.getOutputMessage().getParts();
			}
		}

		List<Object> elementArrayList = new ArrayList<Object>();

		for (Part part : partArray) {
			partElementName = getPartElementName(part);
			elementDeclaration = null;

			boolean foundElement = false;

			for (XSDSchema schema : schemas) {
				EList<XSDTypeDefinition> types = schema.getTypeDefinitions();
				for (XSDTypeDefinition xsdType : types) {
					String elementName = xsdType.getName();
					if (elementName.equals(partElementName)) {
						elementDeclaration = xsdType;
						foundElement = true;
						if (type == ProcedureInfo.RESPONSE)
							try {
								describe(schema, elementName);
							} catch (ModelerCoreException ex) {
								throw new RuntimeException(ex);
							}
						elementArrayList.add(elementDeclaration);
						break;
					}
				}

				if (foundElement == true)
					continue;

				if (elementDeclaration == null) {

					EList<XSDElementDeclaration> elements = schema
							.getElementDeclarations();
					for (XSDElementDeclaration element : elements) {
						String elementName = element.getName();
						if (elementName.equals(partElementName)) {
							if (element.getTypeDefinition() instanceof XSDSimpleTypeDefinition) {
								elementDeclaration = element;
							} else {
								elementDeclaration = element
										.getTypeDefinition();
							}

							foundElement = true;
							if (type == ProcedureInfo.RESPONSE)
								try {
									describe(schema, elementName);
									displayXpathTest();
								} catch (ModelerCoreException ex) {
									throw new RuntimeException(ex);
								}
							elementArrayList.add(elementDeclaration);
							break;
						}
					}
				}

				// We already found our element. No need to look through anymore
				// schemas
				if (foundElement) {
					foundElement = false;
					break;
				}
			}

		}

		return elementArrayList;
	}
	
	private void displayXpathTest(){
		
		for (Object nodeKey:schemaTreeModel.getMapNode().keySet()){
			SchemaNode node = schemaTreeModel.getMapNode().get(nodeKey);
			String xpath = node.getParentXpath();
			String rootPath = schemaTreeModel.getRootNodeXpath();
			System.out.println(xpath+rootPath);
		}
		
		
	}

	private String getPartElementName(Part part) {
		String partElementName = null;

		partElementName = part.getTypeName();
		if (partElementName == null) {
			partElementName = part.getElementName();
		}

		return partElementName;
	}

	public String createRequestColumn(int type, IStructuredSelection selection,
			ProcedureInfo requestInfo) {

		Object obj = selection.getFirstElement();
		String name = null;
		String ns = null;

		if (obj instanceof XSDParticleImpl
				&& ((XSDParticleImpl) obj).getContent() instanceof XSDElementDeclarationImpl) {
			name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) obj)
					.getContent()).getName();
			ns = ((XSDElementDeclarationImpl) ((XSDParticleImpl) obj)
					.getContent()).getTargetNamespace();
			if (type == ProcedureInfo.TYPE_BODY) {
				requestInfo.addBodyColumn(
						requestInfo.getUniqueBodyColumnName(name), false,
						RuntimeTypeNames.STRING, null, ns);
			} else {
				requestInfo.addHeaderColumn(
						requestInfo.getUniqueHeaderColumnName(name), false,
						RuntimeTypeNames.STRING, null, ns);
			}
			operationsDetailsPage.notifyColumnDataChanged();
			return null;

		} else if (obj instanceof XSDElementDeclarationImpl) {
			name = ((XSDElementDeclarationImpl) obj).getName();
			ns = ((XSDElementDeclarationImpl) obj).getTargetNamespace();
			if (type == ProcedureInfo.TYPE_BODY) {
				requestInfo.addBodyColumn(
						requestInfo.getUniqueBodyColumnName(name), false,
						RuntimeTypeNames.STRING, null, ns);
			} else {
				requestInfo.addHeaderColumn(
						requestInfo.getUniqueHeaderColumnName(name), false,
						RuntimeTypeNames.STRING, null, ns);
			}
			operationsDetailsPage.notifyColumnDataChanged();
			return null;
		}

		return operationsDetailsPage.getSchemaLabelProvider().getText(obj);
	}

	public String createResponseColumn(int type,
			IStructuredSelection selection, ProcedureInfo responseInfo) {

		Object obj = selection.getFirstElement();
		if (obj instanceof XSDParticleImpl
				&& ((XSDParticleImpl) obj).getContent() instanceof XSDElementDeclarationImpl) {

			Model wsdlModel = null;
			SchemaModel schemaModel;
			XSDSchema[] schemas;

			try {
				wsdlModel = operationsDetailsPage.getImportManager()
						.getWSDLModel();
			} catch (ModelGenerationException e) {
				throw new RuntimeException(e);
			}

			SchemaProcessor processor = new SOAPSchemaProcessor(null);
			processor.representTypes(true);
			processor.setNamespaces(wsdlModel.getNamespaces());
			schemas = wsdlModel.getSchemas();
			try {
				processor.processSchemas(schemas);
			} catch (SchemaProcessingException e) {
				throw new RuntimeException(e);
			}
			schemaModel = processor.getSchemaModel();

			List<SchemaObject> elements = schemaModel.getElements();
			String name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) obj)
					.getContent()).getName();
			StringBuilder xpath = new StringBuilder();
			String namespace = null;
			String prefix = null;
			StringBuilder parentXpath = new StringBuilder();
			if (importManager.isMessageServiceMode()) {
				responseInfo.addNamespace(SqlConstants.ENVELOPE_NS_ALIAS,
						SqlConstants.ENVELOPE_NS);
			}
			getParentXpath(obj, parentXpath);
			//TODO: Determine Lowest Common Path programmatically instead
			//of just taking the first parent path of the first column the 
			//use picks
			if (this.schemaTreeModel.getRootPath()==null){
				this.schemaTreeModel.setRootPath(parentXpath.toString());
			}
			
			getRelativeXpath(obj, xpath);
			for (SchemaObject schemaObject : elements) {
				if (schemaObject.getName().equals(name)) {
					namespace = schemaObject.getNamespace();
					prefix = ((BaseSchemaObject) schemaObject)
							.getNamespacePrefix();
					if (namespace != null) {
						responseInfo.addNamespace(prefix, namespace);
					}
					responseInfo.setRootPath(this.schemaTreeModel.getRootPath());
					if (importManager.isMessageServiceMode()) {
						operationsDetailsPage.responseBodyColumnsInfoPanel.getRootPathText().setText(ResponseInfo.SOAPENVELOPE_ROOTPATH);
					} else {
						operationsDetailsPage.responseBodyColumnsInfoPanel.getRootPathText().setText(this.schemaTreeModel.getRootPath());
					}

					// TODO: Make sure Root Path is set in the
					// responseElementsInfoPanel on Refresh
					// operationsDetailsPage.responseElementsInfoPanel.getRootPathText().setText(parentXpath.toString());
				}
			}
			
			
			
			if (type == ProcedureInfo.TYPE_BODY) {
				String pathPrefix = ""; //$NON-NLS-1$
				if (importManager.isMessageServiceMode()) {
					pathPrefix = ResponseInfo.SOAPBODY_ROOTPATH;
					//Need to fully qualify xpath at the column level for BODY in MESSAGE mode
					xpath = new StringBuilder(this.schemaTreeModel.getRootPath()).append(xpath);
				}
				responseInfo.addBodyColumn(
						responseInfo.getUniqueBodyColumnName(name), false,
						RuntimeTypeNames.STRING, null, pathPrefix
								+ xpath.toString());
			} else {
				String pathPrefix = ""; //$NON-NLS-1$
				if (importManager.isMessageServiceMode()) {
					pathPrefix = ResponseInfo.SOAPHEADER_ROOTPATH;
				}
				responseInfo.addHeaderColumn(
						responseInfo.getUniqueHeaderColumnName(name), false,
						RuntimeTypeNames.STRING, null, pathPrefix
								+ xpath.toString());
			}
			operationsDetailsPage.notifyColumnDataChanged();
			return null;
		}

		return operationsDetailsPage.getSchemaLabelProvider().getText(obj);
	}

	// =================================================

	//Traverse Element to build parent-child relationships used
	//to determine XPath
	private void describe(XSDSchema schema, String element)
			throws ModelerCoreException {
		schemaTreeModel = new SchemaTreeModel();
		rootnode = schemaTreeModel.new SchemaNode();
		XSDElementDeclaration xed = schema.resolveElementDeclaration(element);
		XSDTypeDefinition xtd = xed.getTypeDefinition();

		if (xtd instanceof XSDComplexTypeDefinition) {
			System.out.println(xed.getName());
			rootnode.setElement(xed);
			rootnode.setRoot(true);
			XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) xtd;
			addComplexTypeDefToTree(complexType, rootnode, true);
		} else if (xtd instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) xtd;
			addSimpleTypeDefToTree(simpleType, rootnode);
		} else {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) xed
					.getType();
			addElementDeclarationToTree(xed, simpleType, rootnode);
		}
	}

	private void addElementDeclarationToTree(XSDElementDeclaration element,
			XSDSimpleTypeDefinition type, SchemaNode node)
			throws ModelerCoreException {
		String name = element.getName();
		if (null == name) {
			name = element.getAliasName();
		}
		schemaTreeModel.getMapNode().put(element, node);

	}

	private void addComplexTypeDefToTree(XSDComplexTypeDefinition complexType,
			SchemaNode node, boolean isRootNode) throws ModelerCoreException {
		XSDComplexTypeContent content = complexType.getContent();
		schemaTreeModel.getMapNode().put(complexType, node);
		if (content instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) content;
			addSimpleTypeDefToTree(simpleType, node);
		} else if (content instanceof XSDParticle) {
			XSDParticle particle = (XSDParticle) content;
			addXSDParticleToTree(particle, node, null);
		}
	}

	private void addXSDParticleToTree(XSDParticle particle, SchemaNode parent,
			SchemaNode node) throws ModelerCoreException {
		XSDParticleContent content = particle.getContent();
		if (content instanceof XSDWildcard) {
			// nothing
		} else if (content instanceof XSDModelGroup) {
			XSDModelGroup group = (XSDModelGroup) content;
			EList<XSDParticle> contents = group.getContents();
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToTree(xsdParticle, parent, node);
			}
		} else if (content instanceof XSDElementDeclaration) {
			XSDElementDeclaration element = (XSDElementDeclaration) content;
			System.out.println(element.getName());
			node = schemaTreeModel.new SchemaNode(particle, parent, null, false);
			if (parent!=null){
				parent.addChild(node);
			}
			schemaTreeModel.getMapNode().put(particle, node);
			if (element.isElementDeclarationReference()) {
				element = element.getResolvedElementDeclaration();
			}
			if (element.getType() instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition) element
						.getType();
				addElementDeclarationToTree(element, type, node);
			} else {
				XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) element
						.getType();
				String name = element.getName();
				if (null == name) {
					name = element.getAliasName();
				}
				
				addComplexTypeDefToTree(type, node, false);
			}
		} else if (content instanceof XSDModelGroupDefinition) {
			XSDModelGroupDefinition groupDefinition = (XSDModelGroupDefinition) content;
			XSDModelGroup group = groupDefinition.getModelGroup();
			EList<XSDParticle> contents = group.getContents();
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToTree(xsdParticle, node, null);
			}
		}
	}

	private void addSimpleTypeDefToTree(XSDSimpleTypeDefinition simpleType,
			SchemaNode node) throws ModelerCoreException {
		String name = simpleType.getName();
		if (null == name) {
			name = simpleType.getAliasName();
		}
	}

//	private void addSimpleTypeDefToTree(XSDSimpleTypeDefinition simpleType,
//			String name, SchemaNode node) throws ModelerCoreException {
//		SchemaNode childNode = schemaTreeModel.new SchemaNode();
//		node.addChild(childNode);
//		childNode.setElement(simpleType);
//		childNode.setParent(node);
//		schemaTreeModel.getMapNode().put(childNode.getElement(), childNode);
//		schemaTreeModel.getMapNode().put(node.getElement(), node);
//	}

	private void getParentXpath(Object element, StringBuilder parentXpath) {
		SchemaNode node = this.schemaTreeModel.getMapNode().get(element);
		String rootPath = this.schemaTreeModel.getRootNodeXpath();
		String parentXPath = node.getParentXpath();
		parentXpath.append(rootPath).append(parentXPath);
	}
	
	private void getRelativeXpath(Object element, StringBuilder xpath) {
		SchemaNode node = this.schemaTreeModel.getMapNode().get(element);
		String relativeXpath = node.getRelativeXpath();
		//We need to append the full path and then remove the root path.
		//This allows us to resolve nested complex type
		String parentXPath = node.getParentXpath();
		parentXPath = parentXPath.replace(this.schemaTreeModel.getRootPath(), ""); //$NON-NLS-1$
		xpath.append(parentXPath).append(relativeXpath);
	}
}
