/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

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
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;
import com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions.SOAPSchemaProcessor;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.ITraversalCtxFactory;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.ProcedureBuilder;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.RequestTraversalContextFactory;
import com.metamatrix.modeler.modelgenerator.xsd.procedures.ResultTraversalContextFactory;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessingException;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessor;

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

	public static final String XML = "XML";

	private static final String INVOKE = "invoke";

	private List<ModelResource> models;
	private Map<String, ModelResource> serviceNameToPhysicalMap;
	private Map<String, ModelResource> serviceNameToVirtualMap;
	private Model wsdlModel;
	private SchemaModel schemaModel;
	private IContainer folder;
	private RelationalFactory factory;
	private DatatypeManager datatypeManager = ModelerCore.getBuiltInTypesManager();
	private List<ProcedureBuilder> builders= new ArrayList<ProcedureBuilder>();
	
	public RelationalModelBuilder(Model model) throws SchemaProcessingException {
		wsdlModel = model;
		SchemaProcessor processor = new SOAPSchemaProcessor(null);
		processor.representTypes(true);
		processor.setNamespaces(wsdlModel.getNamespaces());
		processor.processSchemas(wsdlModel.getSchemas());
		schemaModel = processor.getSchemaModel();
		factory = com.metamatrix.metamodels.relational.RelationalPackage.eINSTANCE
				.getRelationalFactory();
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
			ModelResource servicePhysicalModel, serviceVirtualModel;
			try {

				servicePhysicalModel = getPhysicalModelForService(operation);
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
			public void execute(final IProgressMonitor monitor)
					throws CoreException {
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
		param.setName("binding");
		param.setNameInSource("binding");
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName("action");
		param.setNameInSource("action");
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName("request");
		param.setNameInSource("request");
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.XML_LITERAL));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.IN_LITERAL);
		param.setName("endpoint");
		param.setNameInSource("endpoint");
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));

		param = factory.createProcedureParameter();
		procedure.getParameters().add(param);
		param.setDirection(DirectionKind.OUT_LITERAL);
		param.setName("result");
		param.setNameInSource("result");
		param.setNullable(NullableType.NULLABLE_LITERAL);
		param.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.XML_LITERAL));

	}

	private void modelOutputMessage(Operation operation,
			ProcedureBuilder requestBuilder)
			throws ModelerCoreException {
		Message message = operation.getOutputMessage();
		
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

			QName qName = new QName(elementNamespace, elementName);
			SchemaObject sObject = schemaModel.getElement(qName);
			ITraversalCtxFactory factory = new ResultTraversalContextFactory();
			requestBuilder.build(sObject, factory);
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

			QName qName = new QName(elementNamespace, elementName);
			SchemaObject sObject = schemaModel.getElement(qName);
			ITraversalCtxFactory factory = new RequestTraversalContextFactory();
			resultBuilder.build(sObject, factory);
		}
	}

	public ModelResource getPhysicalModelForService(Operation operation)
			throws CoreException, ModelBuildingException {

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
			createPhysicalProcedure(port.getService(), serviceModel);
		}
		return serviceModel;
	}

	public ModelResource getVirtualModelForService(Operation operation)
			throws CoreException, ModelBuildingException {

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
		Path modelPath = new Path(name + ".xmi");
		IFile iFile = folder.getFile(modelPath);
		return iFile;
	}

	private ModelResource createNewModelResource(IFile iFile) {
		ModelResource resource = ModelerCore.create(iFile);
		models.add(resource);
		return resource;
	}
}