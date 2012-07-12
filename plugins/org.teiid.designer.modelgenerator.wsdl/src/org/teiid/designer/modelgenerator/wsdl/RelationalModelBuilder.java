/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.modelgenerator.wsdl.model.Message;
import org.teiid.designer.modelgenerator.wsdl.model.Model;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.model.Part;
import org.teiid.designer.modelgenerator.wsdl.model.Port;
import org.teiid.designer.modelgenerator.wsdl.model.Service;
import org.teiid.designer.modelgenerator.wsdl.schema.extensions.SOAPSchemaProcessor;
import org.teiid.designer.modelgenerator.xsd.procedures.ITraversalCtxFactory;
import org.teiid.designer.modelgenerator.xsd.procedures.ProcedureBuilder;
import org.teiid.designer.modelgenerator.xsd.procedures.RequestTraversalContextFactory;
import org.teiid.designer.modelgenerator.xsd.procedures.ResultTraversalContextFactory;
import org.teiid.designer.schema.tools.model.schema.SchemaModel;
import org.teiid.designer.schema.tools.model.schema.SchemaObject;
import org.teiid.designer.schema.tools.processing.SchemaProcessingException;
import org.teiid.designer.schema.tools.processing.SchemaProcessor;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * The WebService connection connects to one Endpoint URL, but a single WSDL can
 * contain many services that point to different endpoints. In order to minimize
 * the number of WS connections that we have to make in Teiid, this class
 * combines the all of the operations for an endpoint into one physical model,
 * and creates multiple physical models only in the case of multiple Endpoints
 * (as defined my Ports).
 * 
 * The virtual models to create and parse the XML are defined as Procedures and
 * are not linked to any particular WS operation. The elements defined in a WSDL
 * Schema are often used across operations, and binding them to an operation
 * would limit reuse or require duplication.
 */
public class RelationalModelBuilder {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RelationalModelBuilder.class);
    
    /**
     * Get the localized string text for the provided id
     */
    private static String getString( final String id ) {
        return ModelGeneratorWsdlPlugin.Util.getString(I18N_PREFIX + id);
    }
	public static final String XML = "XML"; //$NON-NLS-1$
	public static final String DOT_XMI = ".xmi"; //$NON-NLS-1$

	private static final String INVOKE = "invoke"; //$NON-NLS-1$

	private List<ModelResource> models;
	private Map<String, ModelResource> serviceNameToPhysicalMap;
	private Map<String, ModelResource> serviceNameToVirtualMap;
	private Model wsdlModel;
	private SchemaModel schemaModel;
	private IContainer folder;
	private RelationalFactory factory;
	private DatatypeManager datatypeManager = ModelerCore.getBuiltInTypesManager();
	private List<ProcedureBuilder> builders= new ArrayList<ProcedureBuilder>();
	private SOAPConnectionInfoProvider connProvider;
	private IConnectionProfile connectionProfile;
	private XSDSchema[] schemas;
	
	public RelationalModelBuilder(Model model, IConnectionProfile profile) throws SchemaProcessingException {
		wsdlModel = model;
		SchemaProcessor processor = new SOAPSchemaProcessor(null);
		processor.representTypes(true);
		processor.setNamespaces(wsdlModel.getNamespaces());
		schemas = wsdlModel.getSchemas();
		processor.processSchemas(schemas);
		schemaModel = processor.getSchemaModel();
		factory = org.teiid.designer.metamodels.relational.RelationalPackage.eINSTANCE
				.getRelationalFactory();
		connectionProfile = profile;
		connProvider = new SOAPConnectionInfoProvider();
	}

	public void modelOperations(List<Operation> operations, IContainer container)
			throws ModelBuildingException, ModelerCoreException {

		folder = container;
		models = new ArrayList<ModelResource>();
		serviceNameToPhysicalMap = new HashMap<String, ModelResource>();
		serviceNameToVirtualMap = new HashMap<String, ModelResource>();
		
		Map<String, Port> ports = new HashMap<String, Port>();
		for (Operation operation : operations) {
			Port port = operation.getBinding().getPort();
			ports.put(port.getName(), port);

			// all ops on a service/port are created in a single physical model
			ModelResource serviceVirtualModel;
			try {

				getPhysicalModelForService(operation);
				serviceVirtualModel = getVirtualModelForService(operation);

			} catch (CoreException e) {
				throw new ModelBuildingException(e);
			}

			Schema schema = factory.createSchema();
			serviceVirtualModel.getEmfResource().getContents().add(schema);
			schema.setName(operation.getName());
			schema.setNameInSource(operation.getName());
			
			ProcedureBuilder resultBuilder = new ProcedureBuilder(schema, serviceVirtualModel);
			builders.add(resultBuilder);
			modelInputMessage(operation, resultBuilder);

			ProcedureBuilder requestBuilder = new ProcedureBuilder(schema, serviceVirtualModel);
			builders.add(requestBuilder);
			modelOutputMessage(operation, requestBuilder);

			//operation.getFaults();

		}

		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

			@Override
			public void execute(final IProgressMonitor monitor) {
				for (ModelResource resource : models) {
					try {
						ModelUtilities.saveModelResource(resource, monitor, false, this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					for (ProcedureBuilder builder : builders) {
						builder.createTransformations();
					}
					
					try {
						ModelUtilities.saveModelResource(resource, monitor, false, this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		IProgressMonitor monitor = new NullProgressMonitor();
		try {
			operation.run(monitor);
		} catch (Exception ex) {
			if (ex instanceof InvocationTargetException) {
				throw new ModelBuildingException(
						((InvocationTargetException) ex).getTargetException());
			}
			throw new ModelBuildingException(ex);
		}

	}

	private void createPhysicalProcedure(Service service,
			ModelResource serviceModel) throws ModelerCoreException {

		Procedure procedure = factory.createProcedure();
		serviceModel.getEmfResource().getContents().add(procedure);
		procedure.setName(INVOKE);
		procedure.setNameInSource(INVOKE);

		ProcedureParameter param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName(getString("param.binding")); //$NON-NLS-1$
		param.setNameInSource(getString("param.binding")); //$NON-NLS-1$
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName(getString("param.action")); //$NON-NLS-1$
		param.setNameInSource(getString("param.action")); //$NON-NLS-1$
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName(getString("param.request")); //$NON-NLS-1$
		param.setNameInSource(getString("param.request")); //$NON-NLS-1$
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.XML_LITERAL));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName(getString("param.endpoint")); //$NON-NLS-1$
		param.setNameInSource(getString("param.endpoint")); //$NON-NLS-1$
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.OUT_LITERAL);
		param.setName(getString("param.result")); //$NON-NLS-1$
		param.setNameInSource(getString("param.result")); //$NON-NLS-1$
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.XML_LITERAL));

	}

	private void modelOutputMessage(Operation operation,
			ProcedureBuilder requestBuilder)
			throws ModelerCoreException {
		Message message = operation.getOutputMessage();
		
		if( message == null ) {
			return;
		}
		
		Part[] parts = message.getParts();
		for (int i = 0; i < parts.length; i++) {
			Part part = parts[i];
			String elementNamespace;
			String elementName;
			if (part.isElement()) {
				elementName = part.getElementName();
				elementNamespace = part.getElementNamespace();
			} else {
				elementName = part.getTypeName(); // get the name here also
				elementNamespace = part.getTypeNamespace();
			}

			ITraversalCtxFactory factory = new ResultTraversalContextFactory();
			QName qName = new QName(elementNamespace, elementName);
			SchemaObject sObject = schemaModel.getElement(qName);
			
			if(null != sObject) {
				requestBuilder.build(sObject, factory);
			} else {
				XSDSimpleTypeDefinition simpleType = XSDSchemaImpl.getSchemaForSchema(schemas[0].getSchemaForSchemaNamespace()).
					resolveSimpleTypeDefinition(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, elementName);
				requestBuilder.build(simpleType, part.getName(), factory);
			}
		}
		
	}


	private void modelInputMessage(Operation operation,
			ProcedureBuilder resultBuilder)
			throws ModelerCoreException {
		
		Message message = operation.getInputMessage();
		Part[] parts = message.getParts();
		for (int i = 0; i < parts.length; i++) {
			Part part = parts[i];
			String elementNamespace;
			String elementName;
			if (part.isElement()) {
				elementName = part.getElementName();
				elementNamespace = part.getElementNamespace();
			} else {
				elementName = part.getTypeName(); // get the name here also
				elementNamespace = part.getTypeNamespace();
			}
			
			ITraversalCtxFactory factory = new RequestTraversalContextFactory();
			QName qName = new QName(elementNamespace, elementName);
			SchemaObject sObject = schemaModel.getElement(qName);
			if(null != sObject) {
				resultBuilder.build(sObject, factory);
			} else {
				XSDSimpleTypeDefinition simpleType = XSDSchemaImpl.getSchemaForSchema(schemas[0].getSchemaForSchemaNamespace()).
					resolveSimpleTypeDefinition(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, elementName);
				resultBuilder.build(simpleType, part.getName(), factory);
			}
			
		}
	}

	public ModelResource getPhysicalModelForService(Operation operation)
			throws CoreException {

		Port port = operation.getBinding().getPort();
		ModelResource serviceModel;
		// we have the model for this port/service in our map
		if (serviceNameToPhysicalMap.containsKey(port.getService().getName())) {
			serviceModel = serviceNameToPhysicalMap.get(port.getService()
					.getName());
		} else {
			// we don't have it in out map, and it doesn't exist
			IFile iFile = createNewFile(port.getService().getName());
			serviceModel = createNewModelResource(iFile);
			serviceNameToPhysicalMap.put(port.getService().getName(),
					serviceModel);
			ModelAnnotation modelAnnotation = serviceModel.getModelAnnotation();
			modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);
			modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
			modelAnnotation.setNameInSource(port.getLocationURI());
			
			if( ! procedureExists(serviceModel, INVOKE)) {
				createPhysicalProcedure(port.getService(), serviceModel);
			}
			
			// Inject the connection profile properties into the physical model
			connProvider.setConnectionInfo(serviceModel, connectionProfile);
		}
		return serviceModel;
	}
	
    public boolean procedureExists(ModelResource modelResource, String procedureName) throws ModelWorkspaceException {
    	if( modelResource != null ) {
			for( Object obj : modelResource.getAllRootEObjects() ) {

                EObject eObj = (EObject)obj;
                if (eObj instanceof Procedure  && procedureName.equalsIgnoreCase(ModelObjectUtilities.getName(eObj)) ) {
                    return true;
                }
			}
    	}
    	
    	return false;
    }

	public ModelResource getVirtualModelForService(Operation operation)
			throws CoreException {

		Port port = operation.getBinding().getPort();
		ModelResource serviceModel;
		// we have the model for this port/service in our map
		if (serviceNameToVirtualMap.containsKey(port.getService().getName() + XML)) {
			serviceModel = serviceNameToVirtualMap.get(port.getService()
					.getName() + XML);
		} else {
			// we don't have it in out map, and it doesn't exist
			IFile iFile = createNewFile(port.getService().getName() + XML);
			serviceModel = createNewModelResource(iFile);
			serviceNameToVirtualMap.put(port.getService().getName(),
					serviceModel);
			ModelAnnotation modelAnnotation = serviceModel.getModelAnnotation();
			modelAnnotation.setModelType(ModelType.VIRTUAL_LITERAL);
			modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
		}
		return serviceModel;
	}

	private IFile createNewFile(String name) {
		Path modelPath = new Path(name + DOT_XMI);
		IFile iFile = folder.getFile(modelPath);
		return iFile;
	}

	private ModelResource createNewModelResource(IFile iFile) {
		ModelResource resource = ModelerCore.create(iFile);
		models.add(resource);
		return resource;
	}
}