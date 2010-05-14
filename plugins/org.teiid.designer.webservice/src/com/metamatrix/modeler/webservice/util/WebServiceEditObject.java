/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.webservice.WebServicePlugin;

/**
 * The WebServiceEditObject is a business object which works with WebService Models, interfaces and operations.
 */
public class WebServiceEditObject {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(WebServiceEditObject.class);

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static final String INVALID_MODELNAME_MESSAGE = getString("invalidModelName.message"); //$NON-NLS-1$
    private static final String INVALID_INTERFACENAME_MESSAGE = getString("invalidInterfaceName.message"); //$NON-NLS-1$
    private static final String INVALID_OPERATIONNAME_MESSAGE = getString("invalidOperationName.message"); //$NON-NLS-1$
    private static final String OPERATIONNAME_ALREADY_EXISTS_MESSAGE = getString("operationNameAlreadyExists.message"); //$NON-NLS-1$
    private static final String INVALID_OPERATION_OUTPUTNAME_MESSAGE = getString("invalidOperationOutputMesgName.message"); //$NON-NLS-1$
    private static final String INVALID_OPERATION_INPUTNAME_MESSAGE = getString("invalidOperationInputMesgName.message"); //$NON-NLS-1$
    private static final String OPERATION_INPUT_AND_OUTPUT_SAMENAME_MESSAGE = getString("operationInputOutputSameName.message"); //$NON-NLS-1$
    private static final String SELECT_MODEL_MESSAGE = getString("selectModel.message"); //$NON-NLS-1$
    private static final String SELECT_INTERFACE_MESSAGE = getString("selectInterface.message"); //$NON-NLS-1$
    private static final String ENTER_OPERATION_MESSAGE = getString("enterOperation.message"); //$NON-NLS-1$
    private static final String ENTER_OPERATION_INPUTNAME_MESSAGE = getString("enterOperationInputName.message"); //$NON-NLS-1$
    private static final String ENTER_OPERATION_INPUTELEMENT_MESSAGE = getString("enterOperationInputElement.message"); //$NON-NLS-1$
    private static final String ENTER_OPERATION_OUTPUTNAME_MESSAGE = getString("enterOperationOutputName.message"); //$NON-NLS-1$
    private static final String CLICK_FINISH_MESSAGE = getString("clickFinish.message"); //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    // fields for holding the current "working" values
    private ModelResource currentModel;
    private ModelProject currentProject;
    private Interface currentInterface;
    private String currentOperationName;
    private String currentOperationInputMessageName = EMPTY_STRING;
    private XSDElementDeclaration currentOperationInputMessageElem;
    private String currentOperationOutputMessageName = EMPTY_STRING;
    private XSDElementDeclaration currentOperationOutputMessageElem;
    private XmlDocument currentXmlDoc;

    // fields for holding the current 'edited' values which have not yet been applied
    private String editedModelName;
    private String editedInterfaceName;

    // default values which were the initial settings
    private String defaultModelName;
    private String defaultInterfaceName;
    private String defaultOperationName;
    private String defaultOperationInputMessageName = EMPTY_STRING;
    private String defaultOperationOutputMessageName = EMPTY_STRING;
    private XSDElementDeclaration defaultOperationOutputMessageElem;

    // Maintain a map of models in the current project
    private HashMap currentProjectModelsMap = new HashMap();
    // Maintain a List of interfaces for the current model
    private List currentModelInterfacesList = new ArrayList();
    // Maintain a List of operations for the current interface
    private List currentInterfaceOperationsList = new ArrayList();

    // Validator for entity names
    private StringNameValidator nameValidator = new StringNameValidator();

    // Needed to process location when creating a New Web Service model
    private IContainer locationContainer;
    private boolean useLocationContainer = false;

    /**
     * Default constructor
     */
    public WebServiceEditObject() {
    }

    /**
     * Contructor
     * 
     * @param model the model to use for initialization.
     */
    public WebServiceEditObject( ModelResource model ) {
        this.currentModel = model;
        setCurrentProject(this.currentModel);
    }

    /**
     * Contructor
     * 
     * @param project the project to use for initialization.
     */
    public WebServiceEditObject( ModelProject modelProject ) {
        this.currentProject = modelProject;
        if (this.currentProject != null) {
            this.currentProjectModelsMap.clear();
            List wsModels = getWebServiceModelsForProject(this.currentProject);
            Iterator iter = wsModels.iterator();
            while (iter.hasNext()) {
                ModelResource mr = (ModelResource)iter.next();
                String modelName = ModelerCore.getModelEditor().getModelName(mr);
                this.currentProjectModelsMap.put(modelName, mr);
            }
        } else {
            this.currentProjectModelsMap.clear();
        }
        setCurrentWebServiceModel(null);
    }

    /**
     * Set the current WebService model to work on. The provided object can be a ModelResource or a String. If it's a String, the
     * string value is the proposed modelName for creation.
     * 
     * @param model the model (or proposed model name).
     */
    public void setCurrentWebServiceModel( Object model ) {
        // If a ModelResource, then update currentModel
        if (model instanceof ModelResource) {
            this.currentModel = (ModelResource)model;
            this.editedModelName = null;
            setCurrentProject(this.currentModel);

            // Update the Interfaces and set to the first one
            this.updateCurrentInterfaceList();
            setCurrentInterfaceName(this.editedInterfaceName);

            // If a String, then the model name has been edited
        } else if (model instanceof String) {
            if (modelExistsInCurrentProject((String)model)) {
                ModelResource mr = (ModelResource)this.currentProjectModelsMap.get(model);
                this.currentModel = mr;
                this.editedModelName = null;
                setCurrentProject(this.currentModel);

                // Update the Interfaces and set to the first one
                this.updateCurrentInterfaceList();
                setCurrentInterfaceName(this.editedInterfaceName);
            } else {
                // Update the Model name, dont update the project
                this.currentModel = null;
                this.editedModelName = (String)model;

                // Clear everything under the model
                this.currentInterface = null;
                this.currentModelInterfacesList.clear();
                this.currentInterfaceOperationsList.clear();
            }

            // Model being set null, clear it
        } else if (model == null) {
            this.currentInterface = null;
            this.currentModel = null;
            this.editedModelName = null;
            this.editedInterfaceName = null;
            this.currentModelInterfacesList.clear();
            this.currentInterfaceOperationsList.clear();
        }
    }

    /**
     * Set the current interface to work on. The provided object can be an interface or a String. If it's a String, the string
     * value is the proposed interface name to be created.
     * 
     * @param theInterface the interface (or proposed interface name).
     */
    private void setCurrentInterface( Object theInterface ) {
        // If an Interface, then update accordingly
        if (theInterface instanceof Interface) {
            // Set the Model - this will also reset project and lists, etc if necessary
            ModelResource newModel = null;
            try {
                newModel = ModelUtil.getModel(theInterface);
            } catch (ModelWorkspaceException err) {
            }
            this.currentModel = newModel;
            this.editedModelName = null;
            setCurrentProject(this.currentModel);

            // Update the Interfaces and set to the supplied interface
            this.updateCurrentInterfaceList();
            this.currentInterface = (Interface)theInterface;
            this.editedInterfaceName = null;

            // Update the Operations add set to the first one
            this.updateCurrentOperationList();
            // If a String, then the interface name has been edited
        } else if (theInterface instanceof String) {
            // Update the Model name, dont update the project
            this.currentInterface = null;
            this.editedInterfaceName = (String)theInterface;

            // Clear everything under the interface
            this.currentInterfaceOperationsList.clear();

            // Interface being set null, clear it
        } else if (theInterface == null) {
            this.currentInterface = null;
            this.editedInterfaceName = null;
            this.currentInterfaceOperationsList.clear();
        }
    }

    /**
     * Set the current Interface Name. If the name matches the name of an existing interface, the existing interface is selected,
     * otherwise the name is set as the working interface
     * 
     * @param interfaceName the name of the interface
     */
    public void setCurrentInterfaceName( String interfaceName ) {
        if (interfaceName != null && interfaceName.trim().length() > 0) {
            if (this.currentModel != null || (this.editedModelName != null && this.editedModelName.length() > 0)) {
                // Look for matching interface in the current list
                boolean matchFound = false;
                Iterator iter = this.currentModelInterfacesList.iterator();
                while (iter.hasNext()) {
                    Interface interf = (Interface)iter.next();
                    if (interf.getName().equalsIgnoreCase(interfaceName)) {
                        matchFound = true;
                        setCurrentInterface(interf);
                        break;
                    }
                }
                // if match not found, set to the supplied name
                if (!matchFound) {
                    setCurrentInterface(interfaceName);
                }
            }
        } else {
            setCurrentInterface(null);
        }
    }

    /**
     * Set the current working Object
     * 
     * @param object the object to edit
     */
    public void setCurrent( Object object ) {
        if (object instanceof ModelResource) {
            setCurrentWebServiceModel(object);
        } else if (object instanceof Interface) {
            setCurrentInterface(object);
        }
    }

    /**
     * Set the current Operation Name. If the name matches the name of an existing operation, the existing operation is selected,
     * otherwise the name is set as the working operation.
     * 
     * @param operationName the name of the operation
     */
    public void setCurrentOperationName( String operationName ) {
        this.currentOperationName = operationName;
    }

    /**
     * Set the current Operation InputMessage Name.
     * 
     * @param name the name of the input message
     */
    public void setOperationInputMessageName( String name ) {
        this.currentOperationInputMessageName = name;
    }

    /**
     * Set the current Operation OutputMessage Name.
     * 
     * @param name the name of the output message
     */
    public void setOperationOutputMessageName( String name ) {
        this.currentOperationOutputMessageName = name;
    }

    /**
     * Set the current Operation Input Message Element.
     * 
     * @param elem the operation input element.
     */
    public void setOperationInputMessageElem( XSDElementDeclaration elem ) {
        this.currentOperationInputMessageElem = elem;
    }

    /**
     * Set the current project from the provided ModelResource.
     * 
     * @param model the ModelResource
     */
    private void setCurrentProject( ModelResource model ) {
        if (model != null) {
            ModelProject modelProject = model.getModelProject();
            this.currentProject = modelProject;
            if (this.currentProject != null) {
                this.currentProjectModelsMap.clear();
                List wsModels = getWebServiceModelsForProject(this.currentProject);
                Iterator iter = wsModels.iterator();
                while (iter.hasNext()) {
                    ModelResource mr = (ModelResource)iter.next();
                    String modelName = ModelerCore.getModelEditor().getModelName(mr);
                    this.currentProjectModelsMap.put(modelName, mr);
                }
            } else {
                this.currentProjectModelsMap.clear();
            }
        } else {
            this.currentProject = null;
            this.currentProjectModelsMap.clear();
        }
    }

    /**
     * Update the current interface list based on the current model
     */
    private void updateCurrentInterfaceList() {
        // Reset the interfaces Map to the current model interfaces
        if (this.currentModel != null) {
            try {
                this.currentModelInterfacesList = com.metamatrix.metamodels.webservice.util.WebServiceUtil.findInterfaces(this.currentModel.getEmfResource(),
                                                                                                                          ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelWorkspaceException err) {
                this.currentModelInterfacesList = Collections.EMPTY_LIST;
            }
        } else {
            this.currentModelInterfacesList = Collections.EMPTY_LIST;
        }

    }

    /**
     * Update the current operation list based on the current interface
     */
    private void updateCurrentOperationList() {
        // Set the operations List to the current interface operations
        if (this.currentInterface != null) {
            this.currentInterfaceOperationsList = com.metamatrix.metamodels.webservice.util.WebServiceUtil.findOperations(this.currentInterface,
                                                                                                                          ModelVisitorProcessor.DEPTH_INFINITE);
        } else {
            this.currentInterfaceOperationsList = Collections.EMPTY_LIST;
        }
    }

    /**
     * Get the current ModelProject
     * 
     * @return the current project
     */
    public ModelProject getCurrentProject() {
        return this.currentProject;
    }

    /**
     * Get the name of the current Model
     * 
     * @return the current modelName
     */
    public String getCurrentWebServiceModelName() {
        if (this.currentModel != null) {
            String modelName = ModelerCore.getModelEditor().getModelName(this.currentModel);
            return modelName;
        } else if (this.editedModelName != null && this.editedModelName.length() > 0) {
            return this.editedModelName;
        }
        return EMPTY_STRING;
    }

    /**
     * Get the name of the current Interface
     * 
     * @return the current interfaceName
     */
    public String getCurrentInterfaceName() {
        if (this.currentInterface != null) {
            return this.currentInterface.getName();
        } else if (this.editedInterfaceName != null && this.editedInterfaceName.length() > 0) {
            return this.editedInterfaceName;
        }
        return EMPTY_STRING;
    }

    /**
     * Get the name of the current Interface
     * 
     * @return the current interfaceName
     */
    public String getDefaultInterfaceName() {
        if (this.defaultInterfaceName != null) {
            return this.defaultInterfaceName;
        }
        return EMPTY_STRING;
    }

    /**
     * Get the name of the current Operation
     * 
     * @return the current operation Name
     */
    public String getCurrentOperationName() {
        return this.currentOperationName;
    }

    /**
     * Determine if the ModelName has any outstanding edits
     * 
     * @return 'true' if the model name has been edited, 'false' if not.
     */
    public boolean hasModelNameEdits() {
        if (this.editedModelName != null && this.editedModelName.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the InterfaceName has any outstanding edits
     * 
     * @return 'true' if the interface name has been edited, 'false' if not.
     */
    public boolean hasInterfaceNameEdits() {
        if (this.editedInterfaceName != null && this.editedInterfaceName.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Get the List of WebService Models for the provided project
     * 
     * @param project the model project
     * @return the List of WebService Models in the project
     */
    private List getWebServiceModelsForProject( ModelProject project ) {
        List allWebServiceModels = new ArrayList();
        ModelWorkspaceItem[] workspaceItems = null;
        try {
            workspaceItems = project.getChildren();
        } catch (ModelWorkspaceException err) {
        }
        if (workspaceItems != null) {
            for (int i = 0; i < workspaceItems.length; i++) {
                IResource resource = null;
                try {
                    resource = workspaceItems[i].getCorrespondingResource();
                } catch (ModelWorkspaceException err) {
                }
                ModelResource mr = null;
                if (resource instanceof IFile) {
                    try {
                        mr = ModelerCore.getModelEditor().findModelResource((IFile)resource);
                    } catch (ModelWorkspaceException e) {
                        e.printStackTrace();
                    }
                    if (mr != null && WebServiceUtil.isWebServiceModelResource(mr)) {
                        allWebServiceModels.add(mr);
                    }
                }
            }
        }
        return allWebServiceModels;
    }

    /**
     * Get the Model Names in the current project
     * 
     * @param the collection of modelNames
     */
    public Collection getModelNamesForCurrentProject() {
        return this.currentProjectModelsMap.keySet();
    }

    /**
     * Get the Interface Names in the current model
     * 
     * @param the collection of InterfaceNames
     */
    public String[] getInterfaceNamesForCurrentModel() {
        if (this.currentModelInterfacesList != null && !this.currentModelInterfacesList.isEmpty()) {
            String[] interfaceNames = new String[this.currentModelInterfacesList.size()];

            int index = 0;
            Iterator iter = this.currentModelInterfacesList.iterator();
            while (iter.hasNext()) {
                String interfaceName = ((Interface)iter.next()).getName();
                interfaceNames[index] = interfaceName;
                index++;
            }
            return interfaceNames;
        }

        return new String[0];
    }

    /**
     * Get the Interface Names in the current model, include the default generated if it exists
     * 
     * @param the collection of InterfaceNames
     */
    public String[] getInterfaceNamesForCurrentModelIncludeDefault() {
        if (this.currentModelInterfacesList != null && !this.currentModelInterfacesList.isEmpty()) {
            int arrayLength = this.currentModelInterfacesList.size();
            if (this.defaultInterfaceName != null && this.defaultInterfaceName.length() > 0) {
                arrayLength++;
            }
            String[] interfaceNames = new String[arrayLength];

            int index = 0;
            Iterator iter = this.currentModelInterfacesList.iterator();
            while (iter.hasNext()) {
                String interfaceName = ((Interface)iter.next()).getName();
                interfaceNames[index] = interfaceName;
                index++;
            }
            if (this.defaultInterfaceName != null && this.defaultInterfaceName.length() > 0) {
                interfaceNames[index] = defaultInterfaceName;
            }
            return interfaceNames;
        }

        return new String[0];
    }

    /**
     * Get the Operation Names in the current interface
     * 
     * @param the collection of Operation Names
     */
    public String[] getOperationNamesForCurrentInterface() {
        if (this.currentInterfaceOperationsList != null && !this.currentInterfaceOperationsList.isEmpty()) {
            String[] operationNames = new String[this.currentInterfaceOperationsList.size()];

            int index = 0;
            Iterator iter = this.currentInterfaceOperationsList.iterator();
            while (iter.hasNext()) {
                String operationName = ((Operation)iter.next()).getName();
                operationNames[index] = operationName;
                index++;
            }
            return operationNames;
        }

        return new String[0];
    }

    /**
     * Determine if a WebServiceModel with the supplied name can be created in the current Project. The name must be valid and
     * must not already exist in the project.
     * 
     * @param modelName the supplied ModelName
     * @return 'true' if the model can be created, 'false' if not.
     */
    public boolean canCreateWebServiceModel( String modelName ) {
        if (modelName != null && modelName.trim().length() > 0) {
            // Validate name (chars, etc)

            // If current Project is set, get its models and see if we can add
            if (this.currentProject != null) {
                Collection modelNames = getModelNamesForCurrentProject();
                if (modelNames != null && !containsName(modelName, modelNames)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if an interface with the supplied name can be created in the current model. The name must be valid and must not
     * already exist in the model.
     * 
     * @param interfaceName the supplied interface Name
     * @return 'true' if the interface can be created, 'false' if not.
     */
    public boolean canCreateInterface( String interfaceName ) {
        if (interfaceName != null && interfaceName.trim().length() > 0) {
            // Validate name (chars, etc)

            // If current Model is set, get its interfaces and see if we can add
            if (this.currentModel != null) {
                String[] interfaceNames = getInterfaceNamesForCurrentModel();
                if (interfaceNames != null) {
                    if (!containsName(interfaceName, Arrays.asList(interfaceNames))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determine if an operation with the supplied name can be created in the current Interface. The name must be valid and must
     * not already exist in the interface.
     * 
     * @param operationName the supplied Operation Name
     * @return 'true' if the operation can be created, 'false' if not.
     */
    public boolean canCreateOperation( String operationName ) {
        if (operationName != null && operationName.trim().length() > 0) {
            // Validate name (chars, etc)

            // If current Interface is set, get its operations and see if we can add
            if (this.currentInterface != null) {
                String[] operationNames = getOperationNamesForCurrentInterface();
                if (operationNames != null) {
                    if (!containsName(operationName, Arrays.asList(operationNames))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Helper method to determine if a string is in a collection of strings.
     * 
     * @param name the string to look for
     * @param nameList the collection of names to search
     * @return 'true' if the nameList contains the name, 'false' if not.
     */
    private boolean containsName( String name,
                                  Collection nameList ) {
        boolean containsName = false;
        Iterator iter = nameList.iterator();
        while (iter.hasNext()) {
            String listName = (String)iter.next();
            if (listName.equalsIgnoreCase(name)) {
                containsName = true;
                break;
            }
        }
        return containsName;
    }

    /**
     * Determine if a WebServiceModel with the supplied name exists in the current Project.
     * 
     * @param modelName the supplied ModelName
     * @return 'true' if the model exists, 'false' if not.
     */
    public boolean modelExistsInCurrentProject( String modelName ) {
        if (modelName != null && modelName.trim().length() > 0) {
            // Validate name (chars, etc)

            // check current models map
            Set currentModelNames = this.currentProjectModelsMap.keySet();

            if (containsName(modelName, currentModelNames)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if an interface with the supplied name exists in the current model.
     * 
     * @param interfaceName the supplied interface Name
     * @return 'true' if the interface exists, 'false' if not.
     */
    public boolean interfaceExistsInCurrentModel( String interfaceName ) {
        if (interfaceName != null && interfaceName.trim().length() > 0) {
            // check current interfaces list
            if (this.currentModel != null) {
                // check current InterfacesList
                String[] currentInterfaceNames = getInterfaceNamesForCurrentModel();
                if (containsName(interfaceName, Arrays.asList(currentInterfaceNames))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if an operation with the supplied name exists in the current Interface.
     * 
     * @param operationName the supplied Operation Name
     * @return 'true' if the operation exists, 'false' if not.
     */
    public boolean operationExistsInCurrentInterface( String operationName ) {
        if (operationName != null && operationName.trim().length() > 0) {
            // check current operations list
            if (this.currentInterface != null) {
                // check current OperationsList
                String[] currentOperationNames = getOperationNamesForCurrentInterface();
                if (containsName(operationName, Arrays.asList(currentOperationNames))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if a WebServiceModel with the supplied name exists in the current Project.
     * 
     * @param modelName the supplied ModelName
     * @return 'true' if the model exists, 'false' if not.
     */
    public ModelResource getModelFromCurrentProject( String modelName ) {
        return (ModelResource)this.currentProjectModelsMap.get(modelName);
    }

    /**
     * Get interface with the supplied name.
     * 
     * @param interfaceName the supplied Interface name
     * @return the Interface object
     */
    public Object getInterfaceFromCurrentModel( String interfaceName ) {
        Object result = null;
        Iterator iter = this.currentModelInterfacesList.iterator();
        while (iter.hasNext()) {
            Interface interf = (Interface)iter.next();
            if (interf.getName().equalsIgnoreCase(interfaceName)) {
                result = interf;
                break;
            }
        }
        return result;
    }

    /**
     * Get operation with the supplied name.
     * 
     * @param operationName the supplied Operation name
     * @return the Operation object
     */
    public Object getOperationFromCurrentInterface( String operationName ) {
        Object result = null;
        Iterator iter = this.currentInterfaceOperationsList.iterator();
        while (iter.hasNext()) {
            Operation operation = (Operation)iter.next();
            if (operation.getName().equalsIgnoreCase(operationName)) {
                result = operation;
                break;
            }
        }
        return result;
    }

    /**
     * Set the wizard page defaults using the supplied XmlRoot as a starting point
     * 
     * @param xmlRoot the xmlRoot to generate names from
     */
    public void setDefaultsUsingXmlRoot( XmlRoot xmlRoot ) {
        if (xmlRoot != null) {
            String xmlRootName = xmlRoot.getName();
            Object rootParent = xmlRoot.eContainer();
            if (rootParent != null && rootParent instanceof XmlDocument) {
                this.currentXmlDoc = (XmlDocument)rootParent;
            }

            XSDComponent xsdComp = xmlRoot.getXsdComponent();
            String modelName = xmlRootName + "_WS"; //$NON-NLS-1$

            // Set the current WebService Model
            this.defaultModelName = modelName;
            setCurrentWebServiceModel(this.defaultModelName);

            // Set the current interface
            this.defaultInterfaceName = xmlRootName;
            setCurrentInterfaceName(this.defaultInterfaceName);

            // Create the operation (based on the xmlRootName) under the new interface
            this.defaultOperationName = "get" + xmlRootName; //$NON-NLS-1$
            setCurrentOperationName(this.defaultOperationName);

            String outputMessageName = xmlRootName;
            this.currentOperationInputMessageName = this.defaultOperationInputMessageName;
            this.defaultOperationOutputMessageName = outputMessageName;
            this.currentOperationOutputMessageName = this.defaultOperationOutputMessageName;
            if (xsdComp != null && xsdComp instanceof XSDElementDeclaration) {
                this.defaultOperationOutputMessageElem = (XSDElementDeclaration)xsdComp;
                this.currentOperationOutputMessageElem = this.defaultOperationOutputMessageElem;
            }

        }
    }

    /**
     * Get the WebService build options object based on the current wizard settings.
     * 
     * @return the WebService build options
     */
    public WebServiceBuildOptions getWebServiceBuildOptions() {
        // ----------------------------------------------------------------
        // Create Options object and populate it with current selections
        // ----------------------------------------------------------------
        WebServiceBuildOptions buildOptions = new WebServiceBuildOptions();
        buildOptions.setCurrentProject(this.currentProject);
        if (this.currentModel != null) {
            buildOptions.setModel(this.currentModel);
        } else {
            buildOptions.setModel(this.editedModelName);
        }
        if (this.currentInterface != null) {
            buildOptions.setInterface(this.currentInterface);
        } else {
            buildOptions.setInterface(this.editedInterfaceName);
        }
        buildOptions.setOperationName(this.currentOperationName);
        buildOptions.setOperationInputMessageName(this.currentOperationInputMessageName);
        buildOptions.setOperationInputMessageElem(this.currentOperationInputMessageElem);
        buildOptions.setOperationOutputMessageName(this.currentOperationOutputMessageName);
        buildOptions.setOperationOutputMessageElem(this.currentOperationOutputMessageElem);
        buildOptions.setOperationOutputXmlDoc(this.currentXmlDoc);
        buildOptions.setUseLocationContainer(this.useLocationContainer);
        buildOptions.setLocationContainer(this.locationContainer);
        return buildOptions;
    }

    /**
     * Get the current operation input message Name
     * 
     * @return the input message name
     */
    public String getCurrentOperationInputName() {
        return this.currentOperationInputMessageName;
    }

    /**
     * Get the current operation input message Content Element
     * 
     * @return the input message content element
     */
    public XSDElementDeclaration getCurrentOperationInputElem() {
        return this.currentOperationInputMessageElem;
    }

    /**
     * Get the current operation output message Name
     * 
     * @return the output message name
     */
    public String getCurrentOperationOutputName() {
        return this.currentOperationOutputMessageName;
    }

    /**
     * Get the current operation output message Content Element
     * 
     * @return the output message content element
     */
    public XSDElementDeclaration getCurrentOperationOutputElem() {
        return this.currentOperationOutputMessageElem;
    }

    /**
     * Get the validation status for the current entries
     * 
     * @return the validation status
     */
    public IStatus validate() {
        // Check the model entry
        IStatus modelStatus = validateModelSelection();
        if (modelStatus.getSeverity() == IStatus.ERROR) {
            return modelStatus;
        }

        // Check the interface entry
        IStatus interfaceStatus = validateInterfaceSelection();
        if (interfaceStatus.getSeverity() == IStatus.ERROR) {
            return interfaceStatus;
        }

        // Check the operation entry
        IStatus operationStatus = validateOperationSelection();
        if (operationStatus.getSeverity() == IStatus.ERROR) {
            return operationStatus;
        }

        // Check the operation Input Message Element
        IStatus operationInputMessageElemStatus = validateOperationInputMessageElem();
        if (operationInputMessageElemStatus.getSeverity() == IStatus.ERROR) {
            return operationInputMessageElemStatus;
        }

        // Check the operation Input Message name
        IStatus operationInputMessageNameStatus = validateOperationInputMessageName();
        if (operationInputMessageNameStatus.getSeverity() == IStatus.ERROR) {
            return operationInputMessageNameStatus;
        }

        // Check the operation Output Message name
        IStatus operationOutputMessageNameStatus = validateOperationOutputMessageName();
        if (operationOutputMessageNameStatus.getSeverity() == IStatus.ERROR) {
            return operationOutputMessageNameStatus;
        }

        // Check the operation Output Message name
        IStatus overallStatus = validateOverallStatus();
        if (overallStatus.getSeverity() == IStatus.ERROR) {
            return overallStatus;
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, CLICK_FINISH_MESSAGE, null);
    }

    /**
     * Get the validation status for the current model selection
     * 
     * @return the validation status
     */
    public IStatus validateModelSelection() {
        // If Model Name has been edited, check validity
        if (this.editedModelName != null && this.editedModelName.length() > 0) {
            // Validate the typed name
            boolean isValidName = nameValidator.isValidName(this.editedModelName);
            if (!isValidName) {
                String message = INVALID_MODELNAME_MESSAGE;
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
            }
            return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
        }

        // Model Name not edited, check model selection
        if (this.currentModel == null) {
            String message = SELECT_MODEL_MESSAGE;
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
    }

    /**
     * Get the validation status for the current interface selection
     * 
     * @return the validation status
     */
    public IStatus validateInterfaceSelection() {
        // If Interface Name has been edited, check validity
        if (this.editedInterfaceName != null && this.editedInterfaceName.length() > 0) {
            // Validate the typed name
            boolean isValidName = nameValidator.isValidName(this.editedInterfaceName);
            if (!isValidName) {
                String message = INVALID_INTERFACENAME_MESSAGE;
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
            }
            return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
        }

        // Check Interface Selection
        if (this.currentInterface == null) {
            String message = SELECT_INTERFACE_MESSAGE;
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
    }

    /**
     * Get the validation status for the current operation selection
     * 
     * @return the validation status
     */
    public IStatus validateOperationSelection() {
        // Check the operation name
        if (this.currentOperationName == null || this.currentOperationName.length() == 0) {
            String message = ENTER_OPERATION_MESSAGE;
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
        }

        if (this.currentOperationName.length() > 0) {
            // Validate the typed name
            boolean isValidName = nameValidator.isValidName(this.currentOperationName);
            if (!isValidName) {
                String message = INVALID_OPERATIONNAME_MESSAGE;
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
            }

            // Check whether the typed name already exists in the current interface
            if (this.currentInterface != null) {
                String[] operationNames = getOperationNamesForCurrentInterface();
                if (operationNames != null) {
                    if (containsName(this.currentOperationName, Arrays.asList(operationNames))) {
                        String message = OPERATIONNAME_ALREADY_EXISTS_MESSAGE;
                        return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
                    }
                }
            }
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
    }

    /**
     * Get the validation status for the current operation Input Message element
     * 
     * @return the validation status
     */
    public IStatus validateOperationInputMessageElem() {
        // Check the input message element
        if (this.currentOperationInputMessageElem == null) {
            String message = ENTER_OPERATION_INPUTELEMENT_MESSAGE;
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
    }

    /**
     * Get the validation status for the current operation Input Message name
     * 
     * @return the validation status
     */
    public IStatus validateOperationInputMessageName() {
        // Check the operation name
        if (this.currentOperationInputMessageName == null || this.currentOperationInputMessageName.length() == 0) {
            String message = ENTER_OPERATION_INPUTNAME_MESSAGE;
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
        }

        if (this.currentOperationInputMessageName.length() > 0) {
            // Validate the typed name
            boolean isValidName = nameValidator.isValidName(this.currentOperationInputMessageName);
            if (!isValidName) {
                String message = INVALID_OPERATION_INPUTNAME_MESSAGE;
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
            }
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
    }

    /**
     * Get the validation status for the current operation Output Message name
     * 
     * @return the validation status
     */
    public IStatus validateOperationOutputMessageName() {
        // Check the operation name
        if (this.currentOperationOutputMessageName == null || this.currentOperationOutputMessageName.length() == 0) {
            String message = ENTER_OPERATION_OUTPUTNAME_MESSAGE;
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
        }

        if (this.currentOperationOutputMessageName.length() > 0) {
            // Validate the typed name
            boolean isValidName = nameValidator.isValidName(this.currentOperationOutputMessageName);
            if (!isValidName) {
                String message = INVALID_OPERATION_OUTPUTNAME_MESSAGE;
                return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
            }
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
    }

    /**
     * Do additional validation after all of the individual fields pass
     * 
     * @return the validation status
     */
    public IStatus validateOverallStatus() {
        // Input and Output Message names cannot be the same
        String inputMessageName = this.currentOperationInputMessageName;
        String outputMessageName = this.currentOperationOutputMessageName;
        if (inputMessageName != null && inputMessageName.equalsIgnoreCase(outputMessageName)) {
            String message = OPERATION_INPUT_AND_OUTPUT_SAMENAME_MESSAGE;
            return new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, null);
        }

        return new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, EMPTY_STRING, null);
    }

    // /**
    // * Validate the Interface EObject by applying all the validation rules applicable to it.
    // */
    // public static IStatus validateInterface(final Interface theInterface, XmlRoot xmlRoot) {
    // // create validation context
    // ValidationContext context = ModelBuildUtil.createValidationContext();
    // try {
    // Container modelContainer = ModelerCore.getModelContainer();
    // Resource modelResource = ModelerCore.getModelEditor().findResource(modelContainer, xmlRoot);
    //
    // context.setResourceContainer(modelContainer);
    // Resource[] resourcesInScope = new Resource[] {modelResource};
    // context.setResourcesInScope(resourcesInScope);
    // } catch (CoreException theException) {
    // ModelerCore.Util.log(theException);
    // }
    //
    // // Validate the Interface
    // Validator.validateObject(null, theInterface, context);
    // ValidationResult result = context.getLastResult();
    // if(result != null) {
    // ValidationProblem[] problems = result.getProblems();
    // if(problems != null) {
    // return problems[0].getStatus();
    // }
    // }
    //        
    // return null;
    // }

    /**
     * Helper method to determine if an object is an XmlDocument
     * 
     * @param object the object to test
     * @return 'true' if the object is an xmlDocument, 'false' if not.
     */
    public boolean isXmlDocument( Object object ) {
        if (object instanceof XmlDocument) {
            return true;
        }
        return false;
    }

    /**
     * Helper method to determine if an object is an XmlRoot
     * 
     * @param object the object to test
     * @return 'true' if the object is an xmlRoot, 'false' if not.
     */
    public boolean isXmlRoot( Object object ) {
        if (object instanceof XmlRoot) {
            return true;
        }
        return false;
    }

    /**
     * Utility to get localized text.
     * 
     * @param theKey the key whose value is being localized
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return WebServicePlugin.Util.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    public IContainer getLocationContainer() {
        return this.locationContainer;
    }

    public void setLocationContainer( IContainer theLocationContainer ) {
        this.locationContainer = theLocationContainer;
    }

    public boolean shouldLocationContainer() {
        return this.useLocationContainer;
    }

    public void setUseLocationContainer( boolean theUseLocationContainer ) {
        this.useLocationContainer = theUseLocationContainer;
    }

}
