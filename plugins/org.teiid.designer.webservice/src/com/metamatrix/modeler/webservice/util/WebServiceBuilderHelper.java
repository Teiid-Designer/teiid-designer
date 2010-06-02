/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.teiid.core.types.DataTypeManager;
import org.teiid.query.sql.lang.Query;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.webservice.aspects.sql.InputAspect;
import com.metamatrix.metamodels.webservice.aspects.sql.InterfaceAspect;
import com.metamatrix.metamodels.webservice.aspects.sql.OperationAspect;
import com.metamatrix.metamodels.webservice.aspects.sql.OutputAspect;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlSequence;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.NewModelObjectHelperManager;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.modeler.webservice.procedure.XsdInstanceNode;

/**
 * Build XSD models from a set of Relational Entities.
 */
public class WebServiceBuilderHelper {

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(WebServiceBuilderHelper.class);

    private static final String FILE_EXT = ".xmi"; //$NON-NLS-1$

    // Used to enable Unit Testing.
    public static boolean HEADLESS = false;

    private static org.eclipse.emf.common.command.Command getInterfaceDescriptor( final Resource resource ) {
        org.eclipse.emf.common.command.Command interfaceDescriptor = null;
        // ------------------------------------------------
        // Get the Descriptor for InterfaceAspect
        // ------------------------------------------------
        // Get the valid descriptors that can be added under the targetEObject
        Collection descriptors = null;
        try {
            descriptors = ModelerCore.getModelEditor().getNewRootObjectCommands(resource);
        } catch (final ModelerCoreException e) {
            final String message = getString("getInterfaceDescriptor.errMsg"); //$NON-NLS-1$
            WebServicePlugin.Util.log(IStatus.ERROR, e, message);
            return null;
        }

        // Use the first InterfaceAspect found
        final Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            interfaceDescriptor = (org.eclipse.emf.common.command.Command)iter.next();
            final EObject eObj = (EObject)interfaceDescriptor.getResult().iterator().next();
            // If the descriptor is a ColumnAspect, stop
            final SqlAspect aspect = SqlAspectHelper.getSqlAspect(eObj);
            if (aspect instanceof InterfaceAspect) break;
        }
        return interfaceDescriptor;
    }

    private static org.eclipse.emf.common.command.Command getOperationDescriptor( final EObject targetEObject ) {
        org.eclipse.emf.common.command.Command operationDescriptor = null;
        // ------------------------------------------------
        // Get the Descriptor for OperationAspect
        // ------------------------------------------------
        // Get the valid descriptors that can be added under the targetEObject
        Collection descriptors = null;
        try {
            descriptors = ModelerCore.getModelEditor().getNewChildCommands(targetEObject);
        } catch (final ModelerCoreException e) {
            final String message = getString("getOperationDescriptor.errMsg"); //$NON-NLS-1$
            WebServicePlugin.Util.log(IStatus.ERROR, e, message);
            return null;
        }

        // Use the first InterfaceAspect found
        final Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            operationDescriptor = (org.eclipse.emf.common.command.Command)iter.next();
            final EObject eObj = (EObject)operationDescriptor.getResult().iterator().next();
            // If the descriptor is a ColumnAspect, stop
            final SqlAspect aspect = SqlAspectHelper.getSqlAspect(eObj);
            if (aspect instanceof OperationAspect) break;
        }
        return operationDescriptor;
    }

    private static org.eclipse.emf.common.command.Command getOperationInputDescriptor( final EObject targetEObject ) {
        org.eclipse.emf.common.command.Command operationInputDescriptor = null;
        // ------------------------------------------------
        // Get the Descriptor for OperationAspect
        // ------------------------------------------------
        // Get the valid descriptors that can be added under the targetEObject
        Collection descriptors = null;
        try {
            descriptors = ModelerCore.getModelEditor().getNewChildCommands(targetEObject);
        } catch (final ModelerCoreException e) {
            final String message = getString("getOperationInputDescriptor.errMsg"); //$NON-NLS-1$
            WebServicePlugin.Util.log(IStatus.ERROR, e, message);
            return null;
        }

        // Use the first InterfaceAspect found
        final Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            operationInputDescriptor = (org.eclipse.emf.common.command.Command)iter.next();
            final EObject eObj = (EObject)operationInputDescriptor.getResult().iterator().next();
            // If the descriptor is a ColumnAspect, stop
            final SqlAspect aspect = SqlAspectHelper.getSqlAspect(eObj);
            if (aspect instanceof InputAspect) break;
        }
        return operationInputDescriptor;
    }

    private static org.eclipse.emf.common.command.Command getOperationOutputDescriptor( final EObject targetEObject ) {
        org.eclipse.emf.common.command.Command operationOutputDescriptor = null;
        // ------------------------------------------------
        // Get the Descriptor for OperationAspect
        // ------------------------------------------------
        // Get the valid descriptors that can be added under the targetEObject
        Collection descriptors = null;
        try {
            descriptors = ModelerCore.getModelEditor().getNewChildCommands(targetEObject);
        } catch (final ModelerCoreException e) {
            final String message = getString("getOperationOutputDescriptor.errMsg"); //$NON-NLS-1$
            WebServicePlugin.Util.log(IStatus.ERROR, e, message);
            return null;
        }

        // Use the first InterfaceAspect found
        final Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            operationOutputDescriptor = (org.eclipse.emf.common.command.Command)iter.next();
            final EObject eObj = (EObject)operationOutputDescriptor.getResult().iterator().next();
            // If the descriptor is a ColumnAspect, stop
            final SqlAspect aspect = SqlAspectHelper.getSqlAspect(eObj);
            if (aspect instanceof OutputAspect) break;
        }
        return operationOutputDescriptor;
    }

    /**
     * Utility to get localized text.
     * 
     * @param theKey the key whose value is being localized
     * @return the localized text
     */
    private static String getString( final String theKey ) {
        return WebServicePlugin.Util.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Determine if the proposed model name is valid, and return an error message if it is not.
     * 
     * @param proposedName
     * @return null if the name is valid, or an error message if it is not.
     */
    public static String validateModelName( String proposedName,
                                            final String fileExtension ) {
        boolean removedValidExtension = false;
        if (proposedName.endsWith(fileExtension)) {
            proposedName = proposedName.substring(0, proposedName.lastIndexOf(fileExtension));
            removedValidExtension = true;
        }

        if (proposedName.indexOf('.') != -1) if (!removedValidExtension) return WebServicePlugin.Util.getString("ModelUtilities.illegalExtensionMessage", fileExtension); //$NON-NLS-1$

        final ValidationResultImpl result = new ValidationResultImpl(proposedName);
        CoreValidationRulesUtil.validateStringNameChars(result, proposedName, null);
        if (result.hasProblems()) return result.getProblems()[0].getMessage();
        return null;
    }

    private final PluginUtil Util = WebServicePlugin.Util;
    private MultiStatus result;

    private String parentPath;

    private ModelResource wsModel;

    private Collection transformationsToFinish;

    private boolean createMultipleWebServices = false;

    private boolean useLocationContainer;

    private IContainer locationContainer;

    /**
     * Public constructor
     */
    public WebServiceBuilderHelper() {
        super();
        this.result = new MultiStatus(WebServicePlugin.PLUGIN_ID, 0, Util.getString("WebServiceBuilderHelper.result"), null);//$NON-NLS-1$
    }

    private void addStatus( final String msg,
                            final int severity,
                            final Throwable err ) {
        if (this.result == null) this.result = new MultiStatus(WebServicePlugin.PLUGIN_ID, 0,
                                                               Util.getString("WebServiceBuilderHelper.result"), null);//$NON-NLS-1$

        final Status status = new Status(severity, WebServicePlugin.PLUGIN_ID, 0, msg, err);
        result.add(status);
    }

    /**
     * Method for creating a new WebService Model interface
     * 
     * @param model - the model to create the interface in
     * @param interfaceName - the name of the interface to create
     * @return the created Interface may be NULL.
     */
    private Object createInterface( final Resource resource,
                                    final String interfaceName,
                                    IProgressMonitor monitor ) {
        EObject newInterface = null;
        monitor = monitor == null ? new NullProgressMonitor() : monitor;

        // first, see if this interface already exists
        for (final Object element : resource.getContents()) {
            final EObject obj = (EObject)element;
            if (obj instanceof Interface) if (interfaceName.equals(((Interface)obj).getName())) return obj;
        }

        // Intialize Txn if required
        final String txnDescr = getString("createInterface.txnDescr"); //$NON-NLS-1$

        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false, false, txnDescr, WebServiceBuilderHelper.this);

        try {
            // Get the descriptor for a SqlColumn from the target
            final org.eclipse.emf.common.command.Command interfaceToCreate = getInterfaceDescriptor(resource);

            if (interfaceToCreate != null) // Create a new Interface with the specified name
            try {
                newInterface = ModelerCore.getModelEditor().createNewRootObjectFromCommand(resource, interfaceToCreate);
                ModelerCore.getModelEditor().rename(newInterface, interfaceName);
            } catch (final ModelerCoreException theException) {
                final String message = getString("createInterface.errMsg"); //$NON-NLS-1$
                WebServicePlugin.Util.log(IStatus.ERROR, theException, message);
            }

        } catch (final Exception e) {
            final String message = getString("createInterface.errMsg"); //$NON-NLS-1$
            addStatus(message, IStatus.ERROR, e);
        } finally {
            // This Txn is not undoable and not significant. Always commit.
            if (startedTxn) ModelerCore.commitTxn();
        }

        return newInterface;

    }

    /**
     * Method for creating a new WebService Model
     * 
     * @param modelName - name of the model to create
     * @param monitor - Progress Monitor
     * @return the created Model may be NULL.
     */
    private Object createModel( final ModelProject project,
                                final String modelName,
                                IProgressMonitor monitor ) {
        Object newModel = null;
        monitor = monitor == null ? new NullProgressMonitor() : monitor;

        // Intialize Txn if required
        final String txnDescr = getString("createModel.txnDescr"); //$NON-NLS-1$

        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false, false, txnDescr, WebServiceBuilderHelper.this);

        try {
            boolean foundExisting = false;
            // First see if we can find an existing model in the supplied project
            // Get all of the webService models in the project
            final List wsModels = getWebServiceModelsForProject(project);
            final Iterator iter = wsModels.iterator();
            while (iter.hasNext()) {
                final Object mr = iter.next();
                String mName = null;
                if (mr instanceof ModelResource) mName = ModelerCore.getModelEditor().getModelName((ModelResource)mr);
                else if (mr instanceof Resource) mName = ModelerCore.getModelEditor().getModelName((Resource)mr);

                if (mName != null && mName.equalsIgnoreCase(modelName)) {
                    newModel = mr;
                    foundExisting = true;
                    break;
                }
            }

            // If existing model not found, go ahead and create
            if (!foundExisting) if (HEADLESS) newModel = this.createWebServiceModel(null, modelName);
            else {
                final IResource targetResource = project.getResource();

                if (this.isValidWebServiceModelName(targetResource, modelName)) newModel = this.createWebServiceModel(targetResource,
                                                                                                                      modelName);
            }

        } catch (final Exception e) {
            final String err = getString("createModel.errMsg"); //$NON-NLS-1$
            addStatus(err, IStatus.ERROR, e);
        } finally {
            // This Txn is not undoable and not significant. Always commit.
            if (startedTxn) ModelerCore.commitTxn();
        }

        return newModel;

    }

    /**
     * Method for creating a new WebService Model interface
     * 
     * @param model - the model to create the interface in
     * @param interfaceName - the name of the interface to create
     * @return the created Interface may be NULL.
     */
    private Object createOperation( final EObject theInterface,
                                    String operationName,
                                    final String inputName,
                                    final String outputName,
                                    IProgressMonitor monitor ) {
        EObject newOperation = null;
        monitor = monitor == null ? new NullProgressMonitor() : monitor;

        // dis-ambiguate the name as necessary
        final Interface i = (Interface)theInterface;
        final ArrayList opNames = new ArrayList(i.getOperations().size());
        for (final Iterator iter = i.getOperations().iterator(); iter.hasNext();)
            opNames.add(((Operation)iter.next()).getName());

        String newName = operationName;
        final Collection siblings = i.getOperations();
        if (siblings != null) {
            final Set siblingNames = new HashSet();
            for (final Iterator it = siblings.iterator(); it.hasNext();)
                siblingNames.add(((Operation)it.next()).getName());
            boolean foundUniqueName = false;
            int index = 1;
            while (!foundUniqueName)
                if (siblingNames.contains(newName)) newName = operationName + String.valueOf(index++);
                else foundUniqueName = true;
        }
        operationName = newName;

        // Intialize Txn if required
        final String txnDescr = getString("createOperation.txnDescr"); //$NON-NLS-1$

        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false, false, txnDescr, WebServiceBuilderHelper.this);

        try {
            // Get the descriptor for a SqlColumn from the target
            final org.eclipse.emf.common.command.Command operationToCreate = getOperationDescriptor(theInterface);

            if (operationToCreate != null) // Create a new Operation with the specified name
            try {
                newOperation = ModelerCore.getModelEditor().createNewChildFromCommand(theInterface, operationToCreate);
                ModelerCore.getModelEditor().rename(newOperation, operationName);

                if (inputName != null) {
                    final org.eclipse.emf.common.command.Command operationInputToCreate = getOperationInputDescriptor(newOperation);
                    final EObject newInputMessage = ModelerCore.getModelEditor().createNewChildFromCommand(newOperation,
                                                                                                           operationInputToCreate);
                    ModelerCore.getModelEditor().rename(newInputMessage, inputName);
                }

                final org.eclipse.emf.common.command.Command operationOutputToCreate = getOperationOutputDescriptor(newOperation);
                final EObject newOutputMessage = ModelerCore.getModelEditor().createNewChildFromCommand(newOperation,
                                                                                                        operationOutputToCreate);
                ModelerCore.getModelEditor().rename(newOutputMessage, outputName);

                if (newOperation != null) NewModelObjectHelperManager.helpCreate(newOperation, null);
            } catch (final ModelerCoreException theException) {
                final String message = getString("createOperation.errMsg"); //$NON-NLS-1$
                WebServicePlugin.Util.log(IStatus.ERROR, theException, message);
            }

        } catch (final Exception e) {
            final String message = getString("createOperation.errMsg"); //$NON-NLS-1$
            addStatus(message, IStatus.ERROR, e);
        } finally {
            // This Txn is not undoable and not significant. Always commit.
            if (startedTxn) ModelerCore.commitTxn();
        }

        return newOperation;

    }

    /**
     * Create a SQL assignment statement using the supplied info
     * 
     * @param elem the supplied XSDElementDeclaration.
     * @return the assignment statement
     */
    private String createSqlAssignment( final Operation operation,
                                        final XSDElementDeclaration elem ) {
        final StringBuffer sbuffer = new StringBuffer();
        if (operation != null && elem != null) {
            final String operationInputName = getSqlFullName(operation.getInput());
            final String name = elem.getName();
            sbuffer.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
            sbuffer.append(name.toUpperCase());
            sbuffer.append(" = xPathValue("); //$NON-NLS-1$
            sbuffer.append(operationInputName);
            sbuffer.append(", '"); //$NON-NLS-1$
            // Create XSD instance node tree and find element within it
            final XsdInstanceNode node = findNode(new XsdInstanceNode(operation.getInput().getContentElement()), elem);
            // Create XPath for element using its node
            sbuffer.append(WebServiceUtil.createXPath(node));
            sbuffer.append("');"); //$NON-NLS-1$
        }
        return sbuffer.toString();
    }

    /**
     * Create a SQL assignment statement using the supplied info
     * 
     * @param elem the supplied XSDElementDeclaration.
     * @return the assignment statement
     */
    private String createSqlCriteria( final XmlElement docElem,
                                      final XSDElementDeclaration elem ) {
        XmlElement docInstanceElem = docElem;
        // Navigate one level down to get instance element
        final List elementChildren = docElem.eContents();
        final Iterator iter = elementChildren.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (obj instanceof XmlSequence) {
                final XmlSequence seq = (XmlSequence)obj;
                final List seqElems = seq.getEntities();
                final Object seqElemObj = seqElems.get(0);
                if (seqElemObj instanceof XmlElement) {
                    docInstanceElem = (XmlElement)seqElemObj;
                    break;
                }
            }
        }

        final StringBuffer sbuffer = new StringBuffer();
        if (docInstanceElem != null && elem != null) {
            final String docStr = getSqlFullName(docInstanceElem);
            final String inpName = elem.getName();

            if (docStr != null) {
                final Container cntr = ModelerCore.getContainer(docElem);
                final String runtimeType = cntr.getDatatypeManager().getRuntimeTypeName(elem.getType());

                sbuffer.append(docStr);
                sbuffer.append("."); //$NON-NLS-1$
                sbuffer.append(inpName);
                if (runtimeType != null && !runtimeType.equals(DataTypeManager.DefaultDataTypes.STRING)) {
                    sbuffer.append(" = convert("); //$NON-NLS-1$
                    sbuffer.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
                    sbuffer.append(inpName.toUpperCase());
                    sbuffer.append(", ");//$NON-NLS-1$
                    sbuffer.append(runtimeType);
                    sbuffer.append(")");//$NON-NLS-1$
                } else {
                    sbuffer.append(" = "); //$NON-NLS-1$
                    sbuffer.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
                    sbuffer.append(inpName.toUpperCase());
                }
            } else {
                sbuffer.append("DocElement = "); //$NON-NLS-1$
                sbuffer.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
                sbuffer.append(inpName.toUpperCase());
            }
        }
        return sbuffer.toString();

    }

    /**
     * Create a SQL variable declaration statement using the supplied XSDElementDeclaration
     * 
     * @param elem the supplied XSDElementDeclaration.
     * @return the variable declaration statement
     */
    private String createSqlDeclaration( final XSDElementDeclaration elem ) {
        final StringBuffer sbuffer = new StringBuffer();
        if (elem != null) {
            sbuffer.append("DECLARE string " + WebServiceUtil.INPUT_VARIABLE_PREFIX); //$NON-NLS-1$
            sbuffer.append(elem.getName().toUpperCase());
            sbuffer.append(";"); //$NON-NLS-1$
        }
        return sbuffer.toString();
    }

    /**
     * generate a Webservice model / interface from the supplied buildOptions
     * 
     * @param buildOptions the builder options
     * @param autoGenSQL flag indicating that the buildOptions are being passed from the end-to-end wizard - there for we can
     *        auto-generate the SQL using the input and output message elements.
     * @param saveModel flag indicating whether to save the Model. If this method is being called multiple times, the flag is set to
     *        false - since save will kick of workspace build and collide with indexing.
     * @param monitor the progress monitor
     * @return result MultiStatus
     */
    public MultiStatus createWebService( final WebServiceBuildOptions buildOptions,
                                         final boolean autoGenSQL,
                                         final boolean saveModel,
                                         final IProgressMonitor monitor ) {
        this.useLocationContainer = buildOptions.shouldUseLocationContainer();
        this.locationContainer = buildOptions.getLocationContainer();

        // Intialize Txn if required
        final String txnDescr = getString("createWebService.txnDescr"); //$NON-NLS-1$

        // Detmine TXN status and start one if required.
        final boolean startedTxn = ModelerCore.startTxn(false, false, txnDescr, WebServiceBuilderHelper.this);

        try {
            // -------------------------------------------
            // Get the project to create model within
            // -------------------------------------------
            final ModelProject project = buildOptions.getCurrentProject();
            ModelResource model = null;
            Resource resource = null;

            // -------------------------------------------------
            // Create model or use existing model if selected
            // -------------------------------------------------
            // Determine if a new model needs to be create
            final Object modelObj = buildOptions.getModel();
            if (modelObj != null && modelObj instanceof ModelResource) {
                model = (ModelResource)modelObj;
                resource = model.getEmfResource();
            } else if (modelObj instanceof String) {
                final String modelName = (String)modelObj;
                final Object returnedObj = createModel(project, modelName, monitor);
                if (returnedObj instanceof ModelResource) {
                    model = (ModelResource)returnedObj;
                    wsModel = model;
                    resource = model.getEmfResource();
                } else if (HEADLESS && returnedObj instanceof Resource) resource = (Resource)returnedObj;
            }

            // ---------------------------------------------------------
            // Create interface or use existing interface if selected
            // ---------------------------------------------------------
            Interface theInterface = null;
            // Now create the interface if necessary
            if (resource != null && result.getSeverity() < IStatus.ERROR) {
                final Object interfaceObj = buildOptions.getInterface();
                if (interfaceObj != null && interfaceObj instanceof Interface) theInterface = (Interface)interfaceObj;
                else if (interfaceObj instanceof String) {
                    final String interfaceName = (String)interfaceObj;
                    final Object returnedObj = createInterface(resource, interfaceName, monitor);
                    if (returnedObj instanceof Interface) theInterface = (Interface)returnedObj;
                }
            }

            // ---------------------------------------------------------
            // Create operation
            // ---------------------------------------------------------
            Operation theOperation = null;
            // Now create the operation if necessary
            if (theInterface != null && result.getSeverity() < IStatus.ERROR) {
                final String operationName = buildOptions.getOperationName();
                final String operationInputMessageName = buildOptions.getOperationInputMessageName();
                final String operationOutputMessageName = buildOptions.getOperationOutputMessageName();

                final Object returnedObj = createOperation(theInterface,
                                                           operationName,
                                                           operationInputMessageName,
                                                           operationOutputMessageName,
                                                           monitor);
                if (returnedObj instanceof Operation) theOperation = (Operation)returnedObj;
            }

            // ---------------------------------------------------------
            // Set the operation messages and properties
            // ---------------------------------------------------------
            XmlDocument outputDoc = null;
            if (theOperation != null && result.getSeverity() < IStatus.ERROR) {
                final XSDElementDeclaration inputElem = buildOptions.getOperationInputMessageElem();
                final XSDElementDeclaration outputElem = buildOptions.getOperationOutputMessageElem();
                outputDoc = buildOptions.getOperationOutputXmlDoc();
                final Input opInput = theOperation.getInput();
                if (opInput != null) opInput.setContentElement(inputElem);
                final Output opOutput = theOperation.getOutput();
                if (opOutput != null) {
                    opOutput.setContentElement(outputElem);
                    opOutput.setXmlDocument(outputDoc);
                }
            }

            // ---------------------------------------------------------
            // Force indexing of model - fixes problem where not all
            // of the result queries were validating...
            // ---------------------------------------------------------
            if (!HEADLESS) {
                // ---------------------------------------------------------
                // Generate SQL and set it on the mappingRoot
                // ---------------------------------------------------------
                final TransformationFinisher finisher = new TransformationFinisher(model, outputDoc, theOperation, autoGenSQL);
                if (!createMultipleWebServices) finisher.finish();
                else transformationsToFinish.add(finisher);
            }

            try {
                ModelBuildUtil.rebuildImports(model.getEmfResource(), this, false);
            } catch (final ModelWorkspaceException e) {
                Util.log(e);
            }

            // ---------------------------------------------------------
            // Save Model
            // ---------------------------------------------------------
            if (saveModel && model != null) try {

                model.save(monitor, true);
            } catch (final Exception e) {
                final String saveError = getString("createWebService.errSave"); //$NON-NLS-1$
                addStatus(saveError, IStatus.ERROR, e);
            }

        } catch (final Exception e) {
            final String err = getString("createWebService.errMsg"); //$NON-NLS-1$
            addStatus(err, IStatus.ERROR, e);
        } finally {
            // This Txn is not undoable and not significant. Always commit.
            if (startedTxn) ModelerCore.commitTxn();
        }

        return result;
    }

    /**
     * Create a Relationships Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    private Object createWebServiceModel( final IResource targetRes,
                                          final String modelName ) {
        String fileName = getFileName(modelName);
        if (HEADLESS) {
            if (parentPath != null) fileName = parentPath + File.separator + modelName;
            final URI uri = URI.createURI(fileName);
            try {
                final Resource rsrc = ModelerCore.getModelContainer().createResource(uri);
                final ModelContents mc = ModelerCore.getModelEditor().getModelContents(rsrc);
                mc.getModelAnnotation().setPrimaryMetamodelUri(WebServicePackage.eNS_URI);
                mc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
                return rsrc;
            } catch (final CoreException err) {
                // Do nothing, return null;
                return null;
            }
        }

        IFile modelFile = null;
        ModelResource resrc = null;
        boolean exists = false;
        if (parentPath == null) {
            IPath relativeModelPath = null;
            if (useLocationContainer) {
                final IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
                relativeModelPath = this.locationContainer.getFullPath();
                modelFile = wsroot.getFileForLocation(wsroot.getRawLocation().append(relativeModelPath).append(fileName));
            } else {
                relativeModelPath = targetRes.getProjectRelativePath().append(fileName);
                modelFile = targetRes.getProject().getFile(relativeModelPath);
            }

        } else {
            // case 4133: Add logic to create new WS model at a given parent path rather than always
            // as a root model.
            final IPath path = new Path(parentPath + File.separator + fileName);
            final IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
            modelFile = wsroot.getFileForLocation(path);
            exists = new File(path.toString()).exists();
        }

        if (modelFile != null) {
            resrc = ModelerCore.create(modelFile);
            if (!exists) // Found this working 4133... don't always reset the attributes. If the model
            // already exists, no need to reset these attributes
            try {
                resrc.getModelAnnotation().setPrimaryMetamodelUri(WebServicePackage.eNS_URI);
                resrc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
                try {
                    final String msg = Util.getString("WebServiceBuilderHelper.newModel", modelFile.getName()); //$NON-NLS-1$
                    addStatus(msg, IStatus.INFO, null);
                    resrc.save(null, false);
                } catch (final ModelWorkspaceException e) {
                    WebServicePlugin.Util.log(e);
                }
            } catch (final ModelWorkspaceException e) {
                e.printStackTrace();
            }
        }

        return resrc;
    }

    /**
     * generate Webservices from the supplied buildOptions collection
     * 
     * @param buildOptions a collection of WebServiceBuildOption objects
     * @param autoGenSQL flag indicating that the buildOptions are being passed from the end-to-end wizard - there for we can
     *        auto-generate the SQL using the input and output message elements.
     * @param monitor the progress monitor
     * @return messages string buffer
     */
    public MultiStatus createWebServices( final Collection buildOptions,
                                          final boolean autoGenSQL,
                                          final MultiStatus result,
                                          final IProgressMonitor monitor ) {
        // Intialize Txn if required
        if (result != null) this.result = result;
        createMultipleWebServices = true;

        final String txnDescr = getString("createWebServices.txnDescr"); //$NON-NLS-1$

        // Detmine TXN status and start one if required.
        final boolean startedTxn = ModelerCore.startTxn(false, false, txnDescr, WebServiceBuilderHelper.this);

        try {
            if (buildOptions != null && buildOptions.size() > 0) {

                transformationsToFinish = new ArrayList(buildOptions.size());

                final Iterator iter = buildOptions.iterator();
                while (iter.hasNext()) {
                    final WebServiceBuildOptions option = (WebServiceBuildOptions)iter.next();
                    createWebService(option, autoGenSQL, false, monitor);
                }
                // Find models to index
                final Collection modelsToIndex = new HashSet();
                for (final Iterator iter2 = transformationsToFinish.iterator(); iter2.hasNext();) {
                    final TransformationFinisher finisher = (TransformationFinisher)iter2.next();
                    modelsToIndex.add(finisher.model);
                }
                if (!modelsToIndex.isEmpty()) {
                    final Collection iResources = new HashSet(modelsToIndex.size());
                    for (final Iterator iter2 = modelsToIndex.iterator(); iter2.hasNext();) {
                        final ModelResource mr = (ModelResource)iter2.next();
                        final IResource resrc = mr.getCorrespondingResource();
                        if (resrc != null) iResources.add(resrc);
                    }
                    ModelBuildUtil.indexResources(null, iResources);
                }
                // Now we finish the transformations

                for (final Iterator iter2 = transformationsToFinish.iterator(); iter2.hasNext();) {
                    final TransformationFinisher finisher = (TransformationFinisher)iter2.next();
                    finisher.finish();
                }

                saveModels(buildOptions, monitor);
            }
        } catch (final Exception e) {
            final String err = getString("createWebServices.errMsg"); //$NON-NLS-1$
            addStatus(err, IStatus.ERROR, e);
        } finally {
            // This Txn is not undoable and not significant. Always commit.
            if (startedTxn) ModelerCore.commitTxn();
        }
        createMultipleWebServices = false;
        return result;
    }

    /**
     * Create a WHERE clause using the supplied list of criteria
     * 
     * @param sqlCriteria the supplied list of criteria
     * @return the WHERE statement string
     */
    private String createWhereClause( final List sqlCriteria ) {
        final StringBuffer sbuffer = new StringBuffer();
        if (sqlCriteria != null && !sqlCriteria.isEmpty()) {
            sbuffer.append(" WHERE "); //$NON-NLS-1$
            final Iterator criteriaIter = sqlCriteria.iterator();
            while (criteriaIter.hasNext()) {
                sbuffer.append(" ("); //$NON-NLS-1$
                sbuffer.append((String)criteriaIter.next());
                sbuffer.append(") "); //$NON-NLS-1$
                if (criteriaIter.hasNext()) sbuffer.append(" AND "); //$NON-NLS-1$
            }
        }
        return sbuffer.toString();
    }

    private XsdInstanceNode findNode( XsdInstanceNode node,
                                      final XSDElementDeclaration element ) {
        if (node.getResolvedXsdComponent() == element) return node;
        final XsdInstanceNode[] nodes = node.getChildren();
        for (int ndx = nodes.length; --ndx >= 0;) {
            node = findNode(nodes[ndx], element);
            if (node != null) return node;
        } // for
        return null;
    }

    /**
     * generate default sql procedure. This SQL is strictly a starting point that the user can edit as desired. It contains an
     * example variable declaration and assignment. It is up to the user to add the sql criteria.
     * 
     * @param outputDoc the xml output document
     * @param operation the webservice operation, used to get the input message
     * @return the default SQL String
     */
    String generateDefaultSQL( final XmlDocument outputDoc,
                               final Operation operation ) {
        final StringBuffer sbuffer = new StringBuffer();

        // Start the procedure
        sbuffer.append("CREATE VIRTUAL PROCEDURE "); //$NON-NLS-1$
        sbuffer.append("BEGIN "); //$NON-NLS-1$

        // Create the default SELECT * FROM query using the output document
        final Query qry = TransformationSqlHelper.createDefaultQuery(outputDoc);
        if (qry != null) {
            sbuffer.append(qry.toString());
            sbuffer.append(";"); //$NON-NLS-1$
        }

        // End the procedure
        sbuffer.append(" END"); //$NON-NLS-1$

        return sbuffer.toString();
    }

    /**
     * generate SQL using the input and output message elements from the operation. This method can be used on operations generated
     * by the WebService Generation Wizard only. There are assumptions made about the input element type, etc. Very difficult to
     * handle generically.
     * 
     * @param outputDoc the xml output document
     * @param operation the webservice operation, used to get the input message
     * @return the default SQL String
     */
    String generateSQLUsingMessageElements( final XmlDocument outputDoc,
                                            final Operation operation ) {
        if (HEADLESS) return generateDefaultSQL(outputDoc, operation);

        final StringBuffer sbuffer = new StringBuffer();

        // Start the procedure
        sbuffer.append("CREATE VIRTUAL PROCEDURE "); //$NON-NLS-1$
        sbuffer.append("BEGIN "); //$NON-NLS-1$

        // Add variable declarations based on operation input elements
        final List inputElems = WebServiceUtil.getInputElements(operation, true);
        if (!inputElems.isEmpty()) {
            final Iterator inpIter = inputElems.iterator();
            while (inpIter.hasNext()) {
                final String sqlDeclare = createSqlDeclaration((XSDElementDeclaration)inpIter.next());
                sbuffer.append(sqlDeclare);
            }
        } else {
            sbuffer.append("DECLARE string "); //$NON-NLS-1$
            sbuffer.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
            sbuffer.append("exampleVar ;"); //$NON-NLS-1$
        }

        // Add the variable assignments
        // InputElements supplied - build the assignments from them.
        if (!inputElems.isEmpty()) {
            final Iterator inpIter = inputElems.iterator();
            while (inpIter.hasNext()) {
                final String sqlAssign = createSqlAssignment(operation, (XSDElementDeclaration)inpIter.next());
                sbuffer.append(sqlAssign);
            }
            // No InputElements supplied - use default example assignment
        } else {
            String operationInputName = "inputMessage"; //$NON-NLS-1$ 
            if (operation != null) operationInputName = getSqlFullName(operation.getInput());
            sbuffer.append(WebServiceUtil.INPUT_VARIABLE_PREFIX);
            sbuffer.append("exampleVar = xPathValue("); //$NON-NLS-1$
            sbuffer.append(operationInputName);
            sbuffer.append(", '//*[local-name()=\"exampleName\"]');"); //$NON-NLS-1$
        }

        // Create the default SELECT * FROM query using the output document
        final Query qry = TransformationSqlHelper.createDefaultQuery(outputDoc);

        // Add query to the procedure
        if (qry != null) {
            // append default query
            sbuffer.append(qry.toString());

            // Create Criteria if elements are available
            final List criteriaList = new ArrayList();
            if (!inputElems.isEmpty()) {
                // Find the document element matching the operation output message element
                final XmlElement docElement = getDocElemMatchingOperationOutputElem(outputDoc, operation);

                // create the individual criteria and add to list
                final Iterator inpIter = inputElems.iterator();
                while (inpIter.hasNext()) {
                    final String criteria = createSqlCriteria(docElement, (XSDElementDeclaration)inpIter.next());
                    criteriaList.add(criteria);
                }
            }

            // If criteria were created, create the compound where and add it to the query
            if (!criteriaList.isEmpty()) {
                final String whereClause = createWhereClause(criteriaList);
                sbuffer.append(whereClause);
            }

            // End the query statement
            sbuffer.append(";"); //$NON-NLS-1$
        }

        // End the procedure
        sbuffer.append(" END"); //$NON-NLS-1$

        return sbuffer.toString();
    }

    /**
     * This method will get the XmlElement from the supplied XmlDocument, which has the supplied XsdComponent property.
     * 
     * @param xmlDoc - the XmlDocument to search
     * @param operation - the webservice operation
     * @return the xmlDocument element whose xsdComponent matches the operation output.
     */
    private XmlElement getDocElemMatchingOperationOutputElem( final XmlDocument xmlDoc,
                                                              final Operation operation ) {
        XmlElement docElement = null;

        if (xmlDoc != null && operation != null) {
            final Output outp = operation.getOutput();
            final XSDElementDeclaration outputElem = outp.getContentElement();
            final Iterator docIter = xmlDoc.eAllContents();
            while (docIter.hasNext()) {
                final Object elem = docIter.next();
                if (elem instanceof XmlElement) {
                    final XmlElement xmlElem = (XmlElement)elem;
                    final XSDComponent xsdComp = xmlElem.getXsdComponent();
                    if (xsdComp != null && xsdComp.equals(outputElem)) {
                        docElement = xmlElem;
                        break;
                    }
                }
            }
        }

        return docElement;
    }

    /**
     * get the full file name, given a modelName string
     * 
     * @param modelName the model name
     * @return the full model name, including extension
     */
    private String getFileName( final String modelName ) {
        String result = modelName.trim();
        if (!result.endsWith(FILE_EXT)) result += FILE_EXT;
        return result;
    }

    /**
     * @return Returns the parentPath.
     * @since 4.3
     */
    public String getParentPath() {
        return this.parentPath;
    }

    /**
     * Get the Sql FullName for the supplied EObject
     * 
     * @param eObj the supplied EObject.
     * @return the full name of the EObject.
     */
    private String getSqlFullName( final EObject eObj ) {
        String fullName = null;
        if (eObj != null) {
            // Get the full name from Sql aspect
            final SqlAspect aspect = SqlAspectHelper.getSqlAspect(eObj);
            if (aspect != null) fullName = aspect.getFullName(eObj);
        }
        return fullName;
    }

    public ModelResource getWebServiceModel() {
        return this.wsModel;
    }

    /**
     * Get the List of WebService Models for the provided project
     * 
     * @param project the model project
     * @return the List of WebService Models in the project
     */
    private List getWebServiceModelsForProject( final ModelProject project ) {
        if (HEADLESS) {
            final List result = new ArrayList();
            try {
                final Iterator resources = ModelerCore.getModelContainer().getResources().iterator();
                while (resources.hasNext()) {
                    final Resource next = (Resource)resources.next();
                    if (next instanceof MtkXmiResourceImpl) {
                        final ModelContents contents = ModelerCore.getModelEditor().getModelContents(next);
                        final ModelAnnotation ma = contents.getModelAnnotation();
                        if (WebServicePackage.eNS_URI.equals(ma.getPrimaryMetamodelUri())) result.add(next);
                    }
                }
            } catch (final Exception err) {
                // Do nothing, just return the result
            }
            return result;
        }

        final List allWebServiceModels = new ArrayList();
        ModelWorkspaceItem[] workspaceItems = null;
        try {
            workspaceItems = project.getChildren();
        } catch (final ModelWorkspaceException err) {
        }
        if (workspaceItems != null) for (final ModelWorkspaceItem workspaceItem : workspaceItems) {
            IResource resource = null;
            try {
                resource = workspaceItem.getCorrespondingResource();
            } catch (final ModelWorkspaceException err) {
            }
            ModelResource mr = null;
            if (resource instanceof IFile) {
                try {
                    mr = ModelerCore.getModelEditor().findModelResource((IFile)resource);
                } catch (final ModelWorkspaceException e) {
                    e.printStackTrace();
                }
                if (mr != null && WebServiceUtil.isWebServiceModelResource(mr)) allWebServiceModels.add(mr);
            }
        }
        return allWebServiceModels;
    }

    /**
     * test whether the supplied modelName is valid
     * 
     * @param modelName the model name to test
     * @return 'true' if the name is valid, 'false' if not.
     */
    private boolean isValidWebServiceModelName( final IResource targetResource,
                                                final String modelName ) {
        // Check for null or zero-length
        if (modelName == null || modelName.length() == 0) return false;
        // Check for valid model name
        final String fileNameMessage = validateModelName(modelName, FILE_EXT);
        if (fileNameMessage != null) return false;
        // Check if already exists
        final String fileName = getFileName(modelName);
        IPath modelRelativePath = null;
        if (targetResource != null) modelRelativePath = targetResource.getProjectRelativePath().append(fileName);

        if (targetResource != null && targetResource.getProject().exists(modelRelativePath)) return false;
        // success
        return true;
    }

    /**
     * save the models specified in the options list.
     * 
     * @param webServiceBuildOptions the list of webService build options
     * @param monitor - Progress Monitor
     */
    private void saveModels( final Collection webServiceBuildOptions,
                             IProgressMonitor monitor ) {
        monitor = monitor == null ? new NullProgressMonitor() : monitor;

        // Intialize Txn if required
        final String txnDescr = getString("saveModels.txnDescr"); //$NON-NLS-1$

        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false, false, txnDescr, WebServiceBuilderHelper.this);

        // keep track of already saved models - no need to save them twice.
        final List savedModels = new ArrayList();

        try {
            final Iterator wsOptionsIter = webServiceBuildOptions.iterator();
            while (wsOptionsIter.hasNext()) {
                final WebServiceBuildOptions wsOption = (WebServiceBuildOptions)wsOptionsIter.next();
                final Object model = wsOption.getModel();
                // Supplied Model is a ModelResource
                if (model instanceof ModelResource) {
                    final ModelResource modelRes = (ModelResource)model;
                    if (!savedModels.contains(modelRes)) {
                        modelRes.save(monitor, true);
                        savedModels.add(modelRes);
                    }
                    // Supplied Model is a name String
                } else if (model instanceof String) {
                    final String modelName = (String)model;
                    final ModelProject project = wsOption.getCurrentProject();
                    // Lookup model with the supplied same and save it if found
                    final List wsModels = getWebServiceModelsForProject(project);
                    final Iterator iter = wsModels.iterator();
                    while (iter.hasNext()) {
                        final Object tmp = iter.next();
                        String mName = null;
                        ModelResource mr = null;
                        if (tmp instanceof ModelResource) {
                            mr = (ModelResource)tmp;
                            mName = ModelerCore.getModelEditor().getModelName(mr);
                        } else if (tmp instanceof Resource) mName = ModelerCore.getModelEditor().getModelName((Resource)tmp);

                        if (mName != null && mName.equalsIgnoreCase(modelName) && mr != null) {
                            if (!savedModels.contains(mr)) {
                                mr.save(monitor, true);
                                savedModels.add(mr);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (final Exception e) {
            final String err = getString("saveModels.errMsg"); //$NON-NLS-1$
            addStatus(err, IStatus.ERROR, e);
        } finally {
            // This Txn is not undoable and not significant. Always commit.
            if (startedTxn) ModelerCore.commitTxn();
        }
    }

    /**
     * @param parentPath The parentPath to set.
     * @since 4.3
     */
    public void setParentPath( final String parentPath ) {
        this.parentPath = parentPath;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // TransformationFinisher INNER CLASS
    // Provides means to delay setting sql text strings until after all WS models are completed
    // This way, indexing can be done on the complete model first, then query resolution can be
    // accomplished without error.
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private class TransformationFinisher {
        ModelResource model;
        boolean autoGenSql;
        XmlDocument xmlOutputDoc;
        Operation operation;

        public TransformationFinisher( final ModelResource theModel,
                                       final XmlDocument theXmlDoc,
                                       final Operation theOperation,
                                       final boolean theAutoGenSql ) {
            super();
            model = theModel;
            xmlOutputDoc = theXmlDoc;
            operation = theOperation;
            autoGenSql = theAutoGenSql;
        }

        protected void finish() {
            // ---------------------------------------------------------
            // Generate SQL and set it on the mappingRoot
            // ---------------------------------------------------------
            String sqlString = null;
            if (autoGenSql) sqlString = generateSQLUsingMessageElements(xmlOutputDoc, operation);
            else sqlString = generateDefaultSQL(xmlOutputDoc, operation);

            // Set the SQL on the mapping root for the operation
            final EObject mappingRoot = TransformationHelper.getMappingRoot(operation);

            TransformationHelper.setSelectSqlString(mappingRoot, sqlString, false, null);
            // reconcile mappings
            TransformationMappingHelper.reconcileMappingsOnSqlChange(mappingRoot, null);
        }
    }
}
