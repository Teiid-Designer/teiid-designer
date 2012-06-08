/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.xsd.impl.XSDSimpleTypeDefinitionImpl;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants.RuntimeTypeNames;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.ModelGeneratorWsdlPlugin;
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

    private static final String WSDL_SCHEMA_HANDLER_RECURSIVE_DEPTH_PROPERTY = "WsdlSchemaHandlerRecursiveDepth"; //$NON-NLS-1$

    private static int MAX_DEPTH = 750;

    private WSDLImportWizardManager importManager;
    private OperationsDetailsPage operationsDetailsPage;
    private SchemaTreeModel requestSchemaTreeModel = null;
    private SchemaTreeModel responseSchemaTreeModel = null;
    private SchemaNode rootnode = null;
    private SchemaModel schemaModel;
    private int depth = 0;

    static {
        try {
            String value = System
                    .getProperty(WSDL_SCHEMA_HANDLER_RECURSIVE_DEPTH_PROPERTY);
            if (value != null) {
                MAX_DEPTH = Integer.parseInt(value);
            }
        }
        catch (Exception ex) {
            // Revert to the default but log the exception
            ModelGeneratorWsdlPlugin.Util.log(ex);
        }
    }

	boolean circularSchemaWarningTriggered = false;

	public ImportWsdlSchemaHandler(WSDLImportWizardManager manager,
			OperationsDetailsPage operationsDetailsPage) {
		super();
		this.importManager = manager;
		this.operationsDetailsPage = operationsDetailsPage;
	}

	public List<SchemaNode> getSchemaForSelectedOperation(final int type,
			ProcedureGenerator generator) {

		Model wsdlModel = null;
		Object elementDeclaration = null;
		if (type == ProcedureInfo.REQUEST) {
			this.requestSchemaTreeModel = new SchemaTreeModel();
		}else{
			this.responseSchemaTreeModel = new SchemaTreeModel();
			
		}
		
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
			String namespace = part.getElementNamespace();
			
			if (type == ProcedureInfo.REQUEST) {
				this.requestSchemaTreeModel.setDefaultNamespace(namespace);
			}else{
				this.responseSchemaTreeModel.setDefaultNamespace(namespace);
			}
			
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
                        try {
                            if (type == ProcedureInfo.REQUEST) {
                                this.requestSchemaTreeModel = describe(schema,
                                        elementName,
                                        (XSDElementDeclarationImpl) element,
                                        requestSchemaTreeModel);
                            }
                            else {
                                this.responseSchemaTreeModel = describe(schema,
                                        elementName,
                                        (XSDElementDeclarationImpl) element,
                                        responseSchemaTreeModel);
                            }
                        }
                        catch (ModelerCoreException ex) {
                            openErrorDialog(ex.getMessage());
                        }
                        catch (StackOverflowError e) {
                            /*
                             * Can occur if the depth threshold is set too high.
                             * Current value should be fine for most systems but
                             * just in case...
                             * 
                             * Eclipse will show a nasty dialog and offer to
                             * close the workbench which is confusing better to
                             * exit a little more gracefully.
                             */
                            String message = NLS
                                    .bind(Messages.Error_GeneratingSchemaModelCircularReferenceStackOverflow,
                                            MAX_DEPTH,
                                            WSDL_SCHEMA_HANDLER_RECURSIVE_DEPTH_PROPERTY);
                            openErrorDialog(message);
                            System.exit(1);
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
								try {
									if (type == ProcedureInfo.REQUEST){
										this.requestSchemaTreeModel = describe(schema, elementName, null, requestSchemaTreeModel);
									}else{
										this.responseSchemaTreeModel = describe(schema, elementName, null, responseSchemaTreeModel);
									}
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

		Collection<SchemaNode> nodeList = new ArrayList<SchemaNode>();
		if (type == ProcedureInfo.REQUEST){
			nodeList = this.requestSchemaTreeModel.getNodeList();
		}else{
			nodeList = this.responseSchemaTreeModel.getNodeList();
		}

		List<SchemaNode> elementsArray = new ArrayList<SchemaNode>();
		
		for (SchemaNode node : nodeList) {
			if (node.isRoot()){
				elementsArray.add(node);
			}
		}
		
		return	elementsArray;
	}
	
    private void openErrorDialog(String msg) {
        ErrorDialog
                .openError(
                        operationsDetailsPage.getShell(),
                        Messages.Error_GeneratingSchemaModelDueToCircularReferences_title,
                        null,
                        new Status(IStatus.WARNING,
                                ModelGeneratorWsdlUiConstants.PLUGIN_ID, msg));
    }

	private String getPartElementName(Part part) {
		String partElementName = null;

		partElementName = part.getTypeName();
		if (partElementName == null) {
			partElementName = part.getElementName();
		}

		return partElementName;
	}

    public SchemaTreeModel getResponseSchemaTreeModel() {
        return responseSchemaTreeModel;
    }

	public String createRequestColumn(int type, IStructuredSelection selection,
			ProcedureInfo requestInfo) {
		SchemaNode node = (SchemaNode)selection.getFirstElement();
		Object obj = node.getElement();
		requestInfo.setTreeModel(this.requestSchemaTreeModel);
		if (this.requestSchemaTreeModel.getDefaultNamespace()!=null){
			requestInfo.addNamespace(ResponseInfo.DEFAULT_NS, this.requestSchemaTreeModel.getDefaultNamespace());
		}
		
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
		}else if (obj instanceof XSDAttributeUseImpl) {
			XSDAttributeUseImpl attributeImpl = (XSDAttributeUseImpl)obj;
			name = attributeImpl.getAttributeDeclaration().getName();
			if( name == null ) {
				name = attributeImpl.getAttributeDeclaration().getAliasName();
			}
			ns = attributeImpl.getAttributeDeclaration().getTargetNamespace();
			
			ColumnInfo columnInfo = getColumnInfoForAttribute(type, requestInfo, attributeImpl, node);
			
			columnInfo.addAttributeInfo(columnInfo.getXmlElement(), columnInfo.getUniqueAttributeName(name));

			operationsDetailsPage.notifyColumnDataChanged();
			return null;
		}

		return operationsDetailsPage.getSchemaLabelProvider().getText(obj);
	}
	
	
	private ColumnInfo getColumnInfoForAttribute(int type, ProcedureInfo requestInfo, XSDAttributeUse attributeUse, SchemaNode parentNode) {
		SchemaNode parentSchemaNode =  getParentElement( parentNode);
		if( parentSchemaNode != null ) {
			Object parentElement = parentSchemaNode.getElement();
			// See if columnInfo already exists
			for( ColumnInfo info : requestInfo.getBodyColumnInfoList() ) {
				if( parentElement == info.getXmlElement() ) {
					return info;
				}
			}
			
			String name = null;
			String ns = null;
			ColumnInfo newInfo = null;
			
			if (parentElement instanceof XSDParticleImpl
					&& ((XSDParticleImpl) parentElement).getContent() instanceof XSDElementDeclarationImpl) {
				name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) parentElement)
						.getContent()).getName();
				if (name==null){
					name = ((XSDElementDeclarationImpl) ((XSDParticleImpl) parentElement)
							.getContent()).getResolvedElementDeclaration().getName();
				}
				ns = ((XSDElementDeclarationImpl) ((XSDParticleImpl) parentElement)
						.getContent()).getTargetNamespace();
				if (type == ProcedureInfo.TYPE_BODY) {
					newInfo = requestInfo.addBodyColumn(
							requestInfo.getUniqueBodyColumnName(name), false,
							RuntimeTypeNames.STRING, null, ns);
					newInfo.setXmlElement(parentElement);
				} else {
					newInfo = requestInfo.addHeaderColumn(
							requestInfo.getUniqueHeaderColumnName(name), false,
							RuntimeTypeNames.STRING, null, ns);
					newInfo.setXmlElement(parentElement);
				}		
				operationsDetailsPage.notifyColumnDataChanged();

			} else if (parentElement instanceof XSDElementDeclarationImpl) {
				name = ((XSDElementDeclarationImpl) parentElement).getName();
				ns = ((XSDElementDeclarationImpl) parentElement).getTargetNamespace();
				if (type == ProcedureInfo.TYPE_BODY) {
					newInfo = requestInfo.addBodyColumn(
							requestInfo.getUniqueBodyColumnName(name), false,
							RuntimeTypeNames.STRING, null, ns);
					newInfo.setXmlElement(parentElement);
				} else {
					newInfo = requestInfo.addHeaderColumn(
							requestInfo.getUniqueHeaderColumnName(name), false,
							RuntimeTypeNames.STRING, null, ns);
					newInfo.setXmlElement(parentElement);
				}
				operationsDetailsPage.notifyColumnDataChanged();
			}
			
			return newInfo;
		}
		
		return null;
	}
	
	public static boolean shouldCreateRequestColumn(Object obj) {
		if (obj instanceof XSDParticleImpl ) {
			Object content = ((XSDParticleImpl) obj).getContent();
			if( content instanceof XSDElementDeclarationImpl ) {
				return ! (((XSDElementDeclaration )content).getType() instanceof XSDComplexTypeDefinition);
			} else if( content instanceof XSDModelGroup ) {
				return false;
			} else {
				return true;
			}
		} else if (obj instanceof XSDElementDeclarationImpl ) {
			Object type = ((XSDElementDeclarationImpl) obj).getTypeDefinition();
			if( type instanceof XSDSimpleTypeDefinitionImpl ) {
				return true;
			}
		} else if( obj instanceof XSDAttributeUseImpl ) {
			return true;
		}
		
		return false;
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
		} else if (obj instanceof XSDElementDeclarationImpl ) {
			Object type = ((XSDElementDeclarationImpl) obj).getTypeDefinition();
			if( type instanceof XSDSimpleTypeDefinitionImpl ) {
				return true;
			}
		} else if( obj instanceof XSDAttributeUseImpl ) {
			return true;
		}
		
		return false;
	}

	public String createResponseColumn(int type,
			IStructuredSelection selection, ProcedureInfo responseInfo) {

		SchemaNode node = (SchemaNode)selection.getFirstElement();
		Object obj = ((SchemaNode)selection.getFirstElement()).getElement();
		responseInfo.setTreeModel(this.responseSchemaTreeModel);
		
		if( !shouldCreateResponseColumn(obj) ) {
			return operationsDetailsPage.getSchemaLabelProvider().getText(obj);
		}
		
		if (obj instanceof XSDParticleImpl 
				&& ((XSDParticleImpl) obj).getContent() instanceof XSDElementDeclarationImpl) {

			Model wsdlModel = null;
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
			//Add the default namespace.
			responseInfo.addNamespace(ResponseInfo.DEFAULT_NS, this.responseSchemaTreeModel.getDefaultNamespace());
			getParentXpath(node, parentXpath, this.responseSchemaTreeModel);
			 
			for (SchemaObject schemaObject : elements) {
				if (schemaObject.getName().equals(name)) {
					namespace = schemaObject.getNamespace();
					prefix = ((BaseSchemaObject) schemaObject)
							.getNamespacePrefix();
					if (namespace != null) {
						responseInfo.addNamespace(prefix, namespace);
					}
					// TODO: Make sure Root Path is set in the
					// responseElementsInfoPanel on Refresh
					// operationsDetailsPage.responseElementsInfoPanel.getRootPathText().setText(parentXpath.toString());
					break;
				}
			}
						
			//Set namespace map on schemaTreeModel. This will be used for determining ns alias names to use for the root path and column paths 
			this.responseSchemaTreeModel.setNamespaceMap(responseInfo.getNamespaceMap());
			
			if (this.responseSchemaTreeModel.getRootPath()==null || this.responseSchemaTreeModel.getRootPath().isEmpty()){
				this.responseSchemaTreeModel.setRootPath(this.responseSchemaTreeModel.determineRootPath());
			}
			
			if (importManager.isMessageServiceMode()) {
				operationsDetailsPage.responseBodyColumnsInfoPanel.getRootPathText().setText(ResponseInfo.SOAPENVELOPE_ROOTPATH);
			} else {
				operationsDetailsPage.responseBodyColumnsInfoPanel.getRootPathText().setText(this.responseSchemaTreeModel.getRootPath());
			}
			
			responseInfo.setRootPath(this.responseSchemaTreeModel.getRootPath());
			getRelativeXpath(node, xpath, this.responseSchemaTreeModel);
			
			if (type == ProcedureInfo.TYPE_BODY) {
				String pathPrefix = ""; //$NON-NLS-1$
				if (importManager.isMessageServiceMode()) {
					pathPrefix = ResponseInfo.SOAPBODY_ROOTPATH;
					//Need to fully qualify xpath at the column level for BODY in MESSAGE mode
					String root = this.responseSchemaTreeModel.getRootPath().replaceAll(ResponseInfo.SOAPENVELOPE_ROOTPATH, ""); //$NON-NLS-1$
					xpath = new StringBuilder(root).append(xpath);
				}
				String theType = RuntimeTypeNames.STRING;
				if( typeDefinition != null && typeDefinition.getName() != null ) {
					theType = typeDefinition.getName();
				}
				String runtimeTypeName = theType;
				try {
					runtimeTypeName = DatatypeUtilities.getRuntimeTypeName(theType);
				} catch (ModelerCoreException ex) {
					ex.printStackTrace();
				}
				
				if( runtimeTypeName != null && !runtimeTypeName.equalsIgnoreCase(theType) ) {
					theType = runtimeTypeName;
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

			//Add the default namespace.
			responseInfo.addNamespace(ResponseInfo.DEFAULT_NS, this.operationsDetailsPage.getProcedureGenerator().getNamespaceURI()); 
			
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
			
			XSDAttributeUse attributeUse = (XSDAttributeUse) obj;
			
			String name = attributeUse.getAttributeDeclaration().getName();
			if( name == null ) {
				name = attributeUse.getAttributeDeclaration().getAliasName();
			}
			
			String parentElementName = null;
			
			SchemaNode parentSchemaNode =  getParentElement(node);
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
			
			String dTypeString = attributeUse.getAttributeDeclaration().getType().getAliasName();
			String runtimeTypeName = dTypeString;
			try {
				runtimeTypeName = DatatypeUtilities.getRuntimeTypeName(dTypeString);
			} catch (ModelerCoreException ex) {
				ex.printStackTrace();
			}
			
			if( runtimeTypeName != null && !runtimeTypeName.equalsIgnoreCase(dTypeString) ) {
				dTypeString = runtimeTypeName;
			}
			
			StringBuilder xpath = new StringBuilder();
			String namespace = null;
			String prefix = null;
			StringBuilder parentXpath = new StringBuilder();

			getParentXpath(node, parentXpath, this.responseSchemaTreeModel);
			
			if (this.responseSchemaTreeModel.getRootPath()==null || this.responseSchemaTreeModel.getRootPath().isEmpty()){
				this.responseSchemaTreeModel.setRootPath(this.responseSchemaTreeModel.determineRootPath());
			}
			
			getRelativeXpath(node, xpath,this.responseSchemaTreeModel);
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
						responseInfo.setRootPath(this.responseSchemaTreeModel.getRootPath());
						if (importManager.isMessageServiceMode()) {
							operationsDetailsPage.responseBodyColumnsInfoPanel.getRootPathText().setText(ResponseInfo.SOAPENVELOPE_ROOTPATH);
						} else {
							operationsDetailsPage.responseBodyColumnsInfoPanel.getRootPathText().setText(this.responseSchemaTreeModel.getRootPath());
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
					String root = this.responseSchemaTreeModel.getRootPath().replaceAll(ResponseInfo.SOAPENVELOPE_ROOTPATH, ""); //$NON-NLS-1$
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
	
	public static String getBaseTypeString(XSDAttributeUse attribute) {
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
	private SchemaTreeModel describe(XSDSchema schema, String element, XSDElementDeclaration elementDeclaration, SchemaTreeModel schemaTreeModel)
	
			throws ModelerCoreException {
		circularSchemaWarningTriggered = false;
		rootnode = schemaTreeModel.new SchemaNode();
		depth = 0;
		XSDElementDeclaration xed = (elementDeclaration == null ? schema.resolveElementDeclaration(element) : elementDeclaration);
		XSDTypeDefinition xtd = xed.getTypeDefinition();

		if (xtd instanceof XSDComplexTypeDefinition) {
			rootnode.setElement(xed);
			rootnode.setRoot(true);
			XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) xtd;
			addComplexTypeDefToTree(complexType, rootnode, true, depth++, schemaTreeModel);
		} else if (xtd instanceof XSDSimpleTypeDefinition) {
			rootnode.setElement(xed);
			rootnode.setRoot(true);
			addAttributes(xtd, rootnode, schemaTreeModel);
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) xtd;
			addSimpleTypeDefToTree(simpleType, rootnode, schemaTreeModel);
		} else {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) xed
					.getType();
			addElementDeclarationToTree(xed, simpleType, rootnode, schemaTreeModel);
		}
		
		return schemaTreeModel;
	}

	private void addElementDeclarationToTree(XSDElementDeclaration element,
			XSDSimpleTypeDefinition type, SchemaNode node, SchemaTreeModel schemaTreeModel)
			throws ModelerCoreException {
		String name = element.getName();
		if (null == name) {
			name = element.getAliasName();
		}
		
		addToSchemaMap(node, schemaTreeModel);
	}

	private void addComplexTypeDefToTree(XSDComplexTypeDefinition complexType,
			SchemaNode node, boolean isRootNode, int depth, SchemaTreeModel schemaTreeModel) throws ModelerCoreException {
        if (depth > MAX_DEPTH) {
            String message = NLS
                    .bind(Messages.Error_GeneratingSchemaModelDueToCircularReferences,
                            MAX_DEPTH,
                            WSDL_SCHEMA_HANDLER_RECURSIVE_DEPTH_PROPERTY);
            throw new ModelerCoreException(message);
		}
		
		XSDComplexTypeContent content = complexType.getContent();
		addToSchemaMap(node, schemaTreeModel);
		
		addAttributes(complexType, node, schemaTreeModel);
		
		if (content instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) content;
			addSimpleTypeDefToTree(simpleType, node, schemaTreeModel);
		} else if (content instanceof XSDParticle) {
			XSDParticle particle = (XSDParticle) content;
			addXSDParticleToTree(particle, node, null, depth++, schemaTreeModel);
		}
	}

	private void addXSDParticleToTree(XSDParticle particle, SchemaNode parent,
			SchemaNode node, int depth, SchemaTreeModel schemaTreeModel ) throws ModelerCoreException {
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
			addToSchemaMap(node, schemaTreeModel);
			parent=node;
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToTree(xsdParticle, parent, node, depth++, schemaTreeModel);
			}
		} else if (content instanceof XSDElementDeclaration) {
			XSDElementDeclaration element = (XSDElementDeclaration) content;
			node = schemaTreeModel.new SchemaNode(particle, parent, null, false);
			if (parent!=null){
				parent.addChild(node);
			}
			
			addToSchemaMap(node, schemaTreeModel);
			
			addAttributes(element, node, schemaTreeModel);
			
			if (element.isElementDeclarationReference()) {
				element = element.getResolvedElementDeclaration();
			}
			if (element.getType() instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition) element.getType();
				addElementDeclarationToTree(element, type, node, schemaTreeModel);
			} else {
				XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) element.getType();
				String name = element.getName();
				if (null == name) {
					name = element.getAliasName();
				}
				
				addComplexTypeDefToTree(type, node, false, depth, schemaTreeModel);
			}
		} else if (content instanceof XSDModelGroupDefinition) {
			XSDModelGroupDefinition groupDefinition = (XSDModelGroupDefinition) content;
			XSDModelGroup group = groupDefinition.getModelGroup();
			EList<XSDParticle> contents = group.getContents();
			for (XSDParticle xsdParticle : contents) {
				addXSDParticleToTree(xsdParticle, node, null, depth, schemaTreeModel);
			}
		}
	}
	
	private void addAttributes(Object element, SchemaNode parent, SchemaTreeModel schemaTreeModel) {
		Object[] result = new Object[0];
		
		if( element instanceof XSDComplexTypeDefinition ) {
			result = ((XSDComplexTypeDefinition)element).getAttributeUses().toArray();
		} else {
			result = ModelUtilities.getModelContentProvider().getChildren(element);
		}
		
		for( Object obj : result) {
			if( obj instanceof XSDAttributeUse ) {
				SchemaNode node = schemaTreeModel.new SchemaNode(obj, parent, null, false);
				if (parent!=null){
					parent.addChild(node);
				}
				addToSchemaMap(node, schemaTreeModel);
			}
		}
	}
	
	private void addToSchemaMap(SchemaNode node, SchemaTreeModel schemaTreeModel) {
		schemaTreeModel.getNodeList().add(node);
	}

	private void addSimpleTypeDefToTree(XSDSimpleTypeDefinition simpleType,
			SchemaNode node, SchemaTreeModel schemaTreeModel) throws ModelerCoreException {
		    addToSchemaMap(node, schemaTreeModel);
	}

	private void getParentXpath(SchemaNode node, StringBuilder parentXpath, SchemaTreeModel schemaTreeModel) {
		String rootPath = schemaTreeModel.getRootNodeXpath();
		String parentXPath = node == null ? "" : node.getParentXpath(); //$NON-NLS-1$
		parentXpath.append(rootPath).append(parentXPath);
	}
	
	private SchemaNode getParentElement(SchemaNode node) {
		if( node != null ) {
			return node.getParent();
		}
		
		return null;
	}
	
	private void getRelativeXpath(SchemaNode node, StringBuilder xpath, SchemaTreeModel schemaTreeModel) {
		Object element = node.getElement();
		Map<String, String> nsMap = this.operationsDetailsPage.getProcedureGenerator().getResponseInfo().getNamespaceMap();
		String relativeXpath = node == null ? "" : node.getRelativeXpath(); //$NON-NLS-1$
		//We need to append the full path and then remove the root path.
		//This allows us to resolve nested complex types
		String parentXPath = node == null ? "" : node.getParentXpath(); //$NON-NLS-1$
		
		parentXPath = parentXPath.replace(schemaTreeModel.getRootPath(), ""); //$NON-NLS-1$
		xpath.append(parentXPath).append(relativeXpath);
	}
	
	
}
