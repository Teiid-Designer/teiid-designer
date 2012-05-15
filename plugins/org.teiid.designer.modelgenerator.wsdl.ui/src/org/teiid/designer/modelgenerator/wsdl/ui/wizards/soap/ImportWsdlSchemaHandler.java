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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.xsd.XSDAttributeUse;
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
import org.eclipse.xsd.impl.XSDAttributeUseImpl;
import org.eclipse.xsd.impl.XSDElementDeclarationImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants.RuntimeTypeNames;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions.SOAPSchemaProcessor;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
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
	int depth = 0;
	static int MAX_DEPTH = 1000;
	boolean circularSchemaWarningTriggered = false;

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
								describe(schema, elementName, (XSDElementDeclarationImpl)element);
							} catch (ModelerCoreException ex) {
								ErrorDialog.openError(this.operationsDetailsPage.getShell(), Messages.Error_GeneratingSchemaModelDueToCircularReferences_title, null, new Status(IStatus.WARNING, ModelGeneratorWsdlUiConstants.PLUGIN_ID, Messages.Error_GeneratingSchemaModelDueToCircularReferences));
							}
						elementArrayList.add(elementDeclaration);
						break;
					}
				}
				

				if (foundElement == true)
					break;

				if (elementDeclaration == null) {

					EList<XSDTypeDefinition> types = schema.getTypeDefinitions();
					for (XSDTypeDefinition xsdType : types) {
						String elementName = xsdType.getName();
						if (elementName.equals(partElementName)) {
							elementDeclaration = xsdType;
							foundElement = true;
							if (type == ProcedureInfo.RESPONSE)
								try {
									describe(schema, elementName, null);
								} catch (ModelerCoreException ex) {
									ErrorDialog.openError(this.operationsDetailsPage.getShell(), Messages.Error_GeneratingSchemaModelDueToCircularReferences_title, null, new Status(IStatus.WARNING, ModelGeneratorWsdlUiConstants.PLUGIN_ID, Messages.Error_GeneratingSchemaModelDueToCircularReferences));
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
			if (name==null){
				name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) obj)
						.getContent()).getResolvedElementDeclaration().getName();
			}
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
		} else if (obj instanceof XSDAttributeUseImpl) {
			XSDAttributeUseImpl attributeImpl = (XSDAttributeUseImpl)obj;
			name = attributeImpl.getAttributeDeclaration().getName();
			if( name == null ) {
				name = attributeImpl.getAttributeDeclaration().getAliasName();
			}
			ns = attributeImpl.getAttributeDeclaration().getTargetNamespace();
			if (type == ProcedureInfo.TYPE_BODY) {
				requestInfo.addBodyColumn(requestInfo.getUniqueBodyColumnName(name), false, RuntimeTypeNames.STRING, null, ns);
			} else {
				requestInfo.addHeaderColumn(requestInfo.getUniqueHeaderColumnName(name), false, RuntimeTypeNames.STRING, null, ns);
			}
			operationsDetailsPage.notifyColumnDataChanged();
			return null;
		}

		return operationsDetailsPage.getSchemaLabelProvider().getText(obj);
	}
	
	public static boolean shouldCreateResponseColumn(Object obj) {
		if (obj instanceof XSDParticleImpl ) {
			Object content = ((XSDParticleImpl) obj).getContent();
			if( content instanceof XSDElementDeclarationImpl ) {
				return ! (((XSDElementDeclaration )content).getType() instanceof XSDComplexTypeDefinition);
			} else if( content instanceof XSDModelGroup ) {
				return false;
			} else {
				return true;
			}
		} else if( obj instanceof XSDAttributeUseImpl ) {
			return true;
		}
		
		return false;
	}

	public String createResponseColumn(int type,
			IStructuredSelection selection, ProcedureInfo responseInfo) {

		Object obj = selection.getFirstElement();
		
		if( !shouldCreateResponseColumn(obj) ) {
			return operationsDetailsPage.getSchemaLabelProvider().getText(obj);
		}
		
		if (obj instanceof XSDParticleImpl 
				&& ((XSDParticleImpl) obj).getContent() instanceof XSDElementDeclarationImpl) {

			Model wsdlModel = null;
			SchemaModel schemaModel;
			XSDSchema[] schemas;

			try {
				wsdlModel = operationsDetailsPage.getImportManager().getWSDLModel();
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
			
			XSDElementDeclaration element = (XSDElementDeclaration)((XSDParticleImpl) obj).getContent();
			XSDTypeDefinition typeDefinition = element.getType();
			String name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) obj).getContent()).getName();
			
			if (name==null){
				name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) obj).getContent()).getResolvedElementDeclaration().getName();
			}
		
			StringBuilder xpath = new StringBuilder();
			String namespace = null;
			String prefix = null;
			StringBuilder parentXpath = new StringBuilder();
			if (importManager.isMessageServiceMode()) {
				responseInfo.addNamespace(SqlConstants.ENVELOPE_NS_ALIAS,
						SqlConstants.ENVELOPE_NS);
			}
			getParentXpath(obj, parentXpath);
			if (this.schemaTreeModel.getRootPath()==null || this.schemaTreeModel.getRootPath().isEmpty()){
				this.schemaTreeModel.setRootPath(this.schemaTreeModel.determineRootPath());
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
					String root = this.schemaTreeModel.getRootPath().replaceAll(ResponseInfo.SOAPENVELOPE_ROOTPATH, ""); //$NON-NLS-1$
					xpath = new StringBuilder(root).append(xpath);
				}
				String theType = RuntimeTypeNames.STRING;
				if( typeDefinition != null ) {
					theType = typeDefinition.getName();
				}
				responseInfo.addBodyColumn(
						responseInfo.getUniqueBodyColumnName(name), false,
						theType, null, pathPrefix
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
		} else if( obj instanceof XSDAttributeUseImpl ) {
			
			Model wsdlModel = null;
			SchemaModel schemaModel;
			XSDSchema[] schemas;

			try {
				wsdlModel = operationsDetailsPage.getImportManager().getWSDLModel();
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
			
			XSDAttributeUseImpl attributeImpl = (XSDAttributeUseImpl) obj;
			
			String name = attributeImpl.getAttributeDeclaration().getName();
			if( name == null ) {
				name = attributeImpl.getAttributeDeclaration().getAliasName();
			}
			
			String parentElementName = null;
			
			SchemaNode parentSchemaNode =  getParentElement(attributeImpl);
			if( parentSchemaNode != null ) {
				Object parentElement = parentSchemaNode.getElement();
				if (parentElement instanceof XSDParticleImpl 
						&& ((XSDParticleImpl) parentElement).getContent() instanceof XSDElementDeclarationImpl) {
					XSDElementDeclaration content = (XSDElementDeclaration)((XSDParticleImpl) parentElement).getContent();
	
					parentElementName = content.getName();
					
					if (parentElementName==null){
						parentElementName = ((XSDElementDeclarationImpl) ((XSDParticleImpl) parentElement).getContent()).getResolvedElementDeclaration().getName();
					}
				}
			}
			
			String dTypeString = getBaseTypeString(attributeImpl);
			
			StringBuilder xpath = new StringBuilder();
			String namespace = null;
			String prefix = null;
			StringBuilder parentXpath = new StringBuilder();

			getParentXpath(obj, parentXpath);
			
			if (this.schemaTreeModel.getRootPath()==null || this.schemaTreeModel.getRootPath().isEmpty()){
				this.schemaTreeModel.setRootPath(this.schemaTreeModel.determineRootPath());
			}
			
			getRelativeXpath(obj, xpath);
			xpath.append('/').append('@').append(name);
			
			if( parentElementName != null ) {
				for (SchemaObject schemaObject : elements) {
					if (schemaObject.getName().equals(parentElementName)) {
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
			}
			
			
			
			if (type == ProcedureInfo.TYPE_BODY) {
				String pathPrefix = ""; //$NON-NLS-1$
				if (importManager.isMessageServiceMode()) {
					pathPrefix = ResponseInfo.SOAPBODY_ROOTPATH;
					//Need to fully qualify xpath at the column level for BODY in MESSAGE mode
					String root = this.schemaTreeModel.getRootPath().replaceAll(ResponseInfo.SOAPENVELOPE_ROOTPATH, ""); //$NON-NLS-1$
					xpath = new StringBuilder(root).append(xpath);
				}
				responseInfo.addBodyColumn(
						responseInfo.getUniqueBodyColumnName(name), false,
						dTypeString, null, pathPrefix
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
	
	private String getBaseTypeString(XSDAttributeUseImpl attribute) {
		XSDTypeDefinition typeDef = attribute.getAttributeDeclaration().getType().getBaseType();
		while( typeDef.getBaseType() != null && 
				!typeDef.equals(typeDef.getBaseType()) && 
				!typeDef.getBaseType().getName().startsWith("any")) { //$NON-NLS-1$
			typeDef = typeDef.getBaseType();
		}
		
		return typeDef.getName();
	}

	// =================================================

	//Traverse Element to build parent-child relationships used
	//to determine XPath
	private void describe(XSDSchema schema, String element, XSDElementDeclaration elementDeclaration)
			throws ModelerCoreException {
		circularSchemaWarningTriggered = false;
		schemaTreeModel = new SchemaTreeModel();
		rootnode = schemaTreeModel.new SchemaNode();
		depth = 0;
		XSDElementDeclaration xed = (elementDeclaration == null ? schema.resolveElementDeclaration(element) : elementDeclaration);
		XSDTypeDefinition xtd = xed.getTypeDefinition();

		if (xtd instanceof XSDComplexTypeDefinition) {
			rootnode.setElement(xed);
			rootnode.setRoot(true);
			XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) xtd;
			addComplexTypeDefToTree(complexType, rootnode, true, depth++ );
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
		
		addToSchemaMap(element, node);
	}

	private void addComplexTypeDefToTree(XSDComplexTypeDefinition complexType,
			SchemaNode node, boolean isRootNode, int depth) throws ModelerCoreException {
		
		if (depth>MAX_DEPTH){
			throw new ModelerCoreException(Messages.Error_GeneratingSchemaModelDueToCircularReferences);
		}
		
		XSDComplexTypeContent content = complexType.getContent();
		addToSchemaMap(complexType, node);
		
		addAttributes(complexType, node);
		
		if (content instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) content;
			addSimpleTypeDefToTree(simpleType, node);
		} else if (content instanceof XSDParticle) {
			XSDParticle particle = (XSDParticle) content;
			addXSDParticleToTree(particle, node, null, depth++);
		}
	}

	private void addXSDParticleToTree(XSDParticle particle, SchemaNode parent,
			SchemaNode node, int depth) throws ModelerCoreException {
		XSDParticleContent content = particle.getContent();
		if (content instanceof XSDWildcard) {
			// nothing
		} else if (content instanceof XSDModelGroup) {
			XSDModelGroup group = (XSDModelGroup) content;
			EList<XSDParticle> contents = group.getContents();
			node = schemaTreeModel.new SchemaNode(content, parent, null, false);
			if (parent!=null){
				parent.addChild(node);
			}
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToTree(xsdParticle, parent, node, depth++);
			}
		} else if (content instanceof XSDElementDeclaration) {
			XSDElementDeclaration element = (XSDElementDeclaration) content;
			node = schemaTreeModel.new SchemaNode(particle, parent, null, false);
			if (parent!=null){
				parent.addChild(node);
			}
			
			addToSchemaMap(particle, node);
			
			addAttributes(element, node);
			
			if (element.isElementDeclarationReference()) {
				element = element.getResolvedElementDeclaration();
			}
			if (element.getType() instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition) element.getType();
				addElementDeclarationToTree(element, type, node);
			} else {
				XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) element.getType();
				String name = element.getName();
				if (null == name) {
					name = element.getAliasName();
				}
				
				addComplexTypeDefToTree(type, node, false, depth);
			}
		} else if (content instanceof XSDModelGroupDefinition) {
			XSDModelGroupDefinition groupDefinition = (XSDModelGroupDefinition) content;
			XSDModelGroup group = groupDefinition.getModelGroup();
			EList<XSDParticle> contents = group.getContents();
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToTree(xsdParticle, node, null, depth);
			}
		}
	}
	
	private void addAttributes(Object element, SchemaNode parent) {
		Object[] result = ModelUtilities.getModelContentProvider().getChildren(element);
		
		for( Object obj : result) {
			if( obj instanceof XSDAttributeUse ) {
				SchemaNode node = schemaTreeModel.new SchemaNode(obj, parent, null, false);
				addToSchemaMap(obj, node);
			}
		}
	}
	
	private void addToSchemaMap(Object key, SchemaNode node) {
		schemaTreeModel.getMapNode().put(key, node);
	}

	private void addSimpleTypeDefToTree(XSDSimpleTypeDefinition simpleType,
			SchemaNode node) throws ModelerCoreException {
		schemaTreeModel.getMapNode().put(simpleType, node);
	}

	private void getParentXpath(Object element, StringBuilder parentXpath) {
		SchemaNode node = this.schemaTreeModel.getMapNode().get(element);
		String rootPath = this.schemaTreeModel.getRootNodeXpath();
		String parentXPath = node == null ? "" : node.getParentXpath(); //$NON-NLS-1$
		parentXpath.append(rootPath).append(parentXPath);
	}
	
	private SchemaNode getParentElement(Object element) {
		SchemaNode node = this.schemaTreeModel.getMapNode().get(element);
		if( node != null ) {
			return node.getParent();
		}
		
		return null;
	}
	
	private void getRelativeXpath(Object element, StringBuilder xpath) {
		SchemaNode node = this.schemaTreeModel.getMapNode().get(element);
		String relativeXpath = node == null ? "" : node.getRelativeXpath(); //$NON-NLS-1$
		//We need to append the full path and then remove the root path.
		//This allows us to resolve nested complex types
		String parentXPath = node == null ? "" : node.getParentXpath(); //$NON-NLS-1$
		parentXPath = parentXPath.replace(this.schemaTreeModel.getRootPath(), ""); //$NON-NLS-1$
		xpath.append(parentXPath).append(relativeXpath);
	}
}
