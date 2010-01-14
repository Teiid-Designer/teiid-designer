/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDPackage;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * This Utility class is designed to provide access to specific model information from any Modeler UI plugin If at all possible,
 * the method calls to access <code>ModelType</code> and primary metamodel URI attempt to use the <code>XmiHeader</code>
 * information via an <code>IResource</code>. This insures a light-weight check so <code>ModelResource</code>s don't get loaded
 * needlessly/repeatedly. See <code>PdeTestModelIdentifier</code>
 * 
 * @since 5.0
 */
public abstract class ModelIdentifier implements UiConstants {

    // UNKNOWN
    public static final int UNKNOWN_MODEL_ID = -1;
    public static final int UNKNOWN_MODEL_TYPE_ID = ModelType.UNKNOWN;
    public static final ModelType UNKNOWN_MODEL_TYPE = ModelType.UNKNOWN_LITERAL;
    public static final String UNKNOWN_MODEL_STRING = "Unknown"; //$NON-NLS-1$
    public static final String UNKNOWN_MODEL_IMAGE_ID = PluginConstants.Images.MODEL;

    // Relational Source Model Constants
    public static final int RELATIONAL_SOURCE_MODEL_ID = 0;
    public static final int RELATIONAL_SOURCE_MODEL_TYPE_ID = ModelType.PHYSICAL;
    public static final ModelType RELATIONAL_SOURCE_MODEL_TYPE = ModelType.PHYSICAL_LITERAL;
    public static final String RELATIONAL_SOURCE_MODEL_URI = RelationalPackage.eNS_URI;
    public static final String RELATIONAL_SOURCE_MODEL_STRING = "Source"; //$NON-NLS-1$
    public static final String RELATIONAL_SOURCE_MODEL_IMAGE_ID = PluginConstants.Images.MODEL;

    // Extension Model Constants
    public static final int EXTENSION_MODEL_ID = 1;
    public static final int EXTENSION_MODEL_TYPE_ID = ModelType.EXTENSION;
    public static final ModelType EXTENSION_MODEL_TYPE = ModelType.EXTENSION_LITERAL;
    public static final String EXTENSION_MODEL_URI = ExtensionPackage.eNS_URI;
    public static final String EXTENSION_MODEL_STRING = ModelType.EXTENSION_LITERAL.getName();
    public static final String EXTENSION_MODEL_IMAGE_ID = PluginConstants.Images.EXTENSION_MODEL;

    // Function Model Constants
    public static final int FUNCTION_MODEL_ID = 2;
    public static final int FUNCTION_MODEL_TYPE_ID = ModelType.FUNCTION;
    public static final ModelType FUNCTION_MODEL_TYPE = ModelType.FUNCTION_LITERAL;
    public static final String FUNCTION_MODEL_URI = "http://www.metamatrix.com/metamodels/MetaMatrixFunction"; //$NON-NLS-1$
    public static final String FUNCTION_MODEL_STRING = ModelType.FUNCTION_LITERAL.getName();
    public static final String FUNCTION_MODEL_IMAGE_ID = PluginConstants.Images.FUNCTION_MODEL;

    // Logical Model Constants
    public static final int LOGICAL_MODEL_ID = 3;
    public static final int LOGICAL_MODEL_TYPE_ID = ModelType.LOGICAL;
    public static final ModelType LOGICAL_MODEL_TYPE = ModelType.LOGICAL_LITERAL;
    public static final String LOGICAL_MODEL_URI = "http://www.metamatrix.com/metamodels/Relationship"; //$NON-NLS-1$
    public static final String LOGICAL_MODEL_STRING = ModelType.LOGICAL_LITERAL.getName();
    public static final String LOGICAL_MODEL_IMAGE_ID = PluginConstants.Images.LOGICAL_MODEL;

    // Relational View Model Constants
    public static final int RELATIONAL_VIEW_MODEL_ID = 4;
    public static final int RELATIONAL_VIEW_MODEL_TYPE_ID = ModelType.VIRTUAL;
    public static final ModelType RELATIONAL_VIEW_MODEL_TYPE = ModelType.VIRTUAL_LITERAL;
    public static final String RELATIONAL_VIEW_MODEL_URI = RelationalPackage.eNS_URI;
    public static final String RELATIONAL_VIEW_MODEL_STRING = "View"; //$NON-NLS-1$
    public static final String RELATIONAL_VIEW_MODEL_IMAGE_ID = PluginConstants.Images.VIEW_MODEL;

    // XML View Model Constants
    public static final int XML_VIEW_MODEL_ID = 5;
    public static final int XML_VIEW_MODEL_TYPE_ID = ModelType.VIRTUAL;
    public static final ModelType XML_VIEW_MODEL_TYPE = ModelType.VIRTUAL_LITERAL;
    public static final String XML_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$
    public static final String XML_VIEW_MODEL_STRING = "Xml View"; //$NON-NLS-1$
    public static final String XML_VIEW_MODEL_IMAGE_ID = PluginConstants.Images.XML_VIEW_MODEL;

    // Web Service Model Constants
    public static final int WEB_SERVICES_VIEW_MODEL_ID = 6;
    public static final int WEB_SERVICES_VIEW_MODEL_TYPE_ID = ModelType.VIRTUAL;
    public static final ModelType WEB_SERVICES_MODEL_TYPE = ModelType.VIRTUAL_LITERAL;
    public static final String WEB_SERVICES_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
    public static final String WEB_SERVICES_VIEW_MODEL_STRING = "Web Services View"; //$NON-NLS-1$
    public static final String WEB_SERVICES_VIEW_MODEL_IMAGE_ID = PluginConstants.Images.WEB_SERVICE_VIEW_MODEL;

    // XML Schema Model Constants
    public static final int XML_SCHEMA_MODEL_ID = 7;
    public static final int XML_SCHEMA_MODEL_TYPE_ID = ModelType.TYPE;
    public static final ModelType XML_SCHEMA_MODEL_TYPE = ModelType.TYPE_LITERAL;
    public static final String XML_SCHEMA_MODEL_URI = XSDPackage.eNS_URI;
    public static final String XML_SCHEMA_MODEL_STRING = "Xml Schema"; //$NON-NLS-1$
    public static final String XML_SCHEMA_MODEL_IMAGE_ID = PluginConstants.Images.XSD_MODEL;

    // XML Service Source Model Constants
    public static final int XML_SERVICE_SOURCE_MODEL_ID = 8;
    public static final int XML_SERVICE_SOURCE_MODEL_TYPE_ID = ModelType.PHYSICAL;
    public static final ModelType XML_SERVICE_SOURCE_MODEL_TYPE = ModelType.PHYSICAL_LITERAL;
    public static final String XML_SERVICE_SOURCE_MODEL_URI = "http://www.metamatrix.com/metamodels/XmlService"; //$NON-NLS-1$
    public static final String XML_SERVICE_SOURCE_MODEL_STRING = "Xml Service"; //$NON-NLS-1$
    public static final String XML_SERVICE_SOURCE_MODEL_IMAGE_ID = PluginConstants.Images.XML_SERVICE_SOURCE_MODEL;

    // XML Service View Model Constants
    public static final int XML_SERVICE_VIEW_MODEL_ID = 9;
    public static final int XML_SERVICE_VIEW_MODEL_TYPE_ID = ModelType.VIRTUAL;
    public static final ModelType XML_SERVICE_VIEW_MODEL_TYPE = ModelType.VIRTUAL_LITERAL;
    public static final String XML_SERVICE_VIEW_MODEL_URI = "http://www.metamatrix.com/metamodels/XmlService"; //$NON-NLS-1$
    public static final String XML_SERVICE_VIEW_MODEL_STRING = "Xml Service View"; //$NON-NLS-1$
    public static final String XML_SERVICE_VIEW_MODEL_IMAGE_ID = PluginConstants.Images.XML_SERVICE_VIEW_MODEL;

    // Relationship Model Constants
    public static final int RELATIONSHIP_MODEL_ID = 10;
    public static final int RELATIONSHIP_MODEL_TYPE_ID = ModelType.LOGICAL;
    public static final ModelType RELATIONSHIP_MODEL_TYPE = ModelType.LOGICAL_LITERAL;
    public static final String RELATIONSHIP_MODEL_URI = "http://www.metamatrix.com/metamodels/Relationship"; //$NON-NLS-1$
    public static final String RELATIONSHIP_MODEL_STRING = ModelType.LOGICAL_LITERAL.getName();
    public static final String RELATIONSHIP_MODEL_IMAGE_ID = PluginConstants.Images.RELATIONSHIP_MODEL;

    // People Model Constants
    public static final int PEOPLE_MODEL_ID = 11;
    public static final int PEOPLE_MODEL_TYPE_ID = ModelType.LOGICAL;
    public static final ModelType PEOPLE_MODEL_TYPE = ModelType.LOGICAL_LITERAL;
    public static final String PEOPLE_MODEL_URI = "http://www.metamatrix.com/metamodels/People"; //$NON-NLS-1$
    public static final String PEOPLE_MODEL_STRING = "People"; //$NON-NLS-1$
    public static final String PEOPLE_MODEL_IMAGE_ID = PluginConstants.Images.PEOPLE_MODEL;

    // UML Model Constants
    public static final int UML_MODEL_ID = 12;
    public static final int UML_MODEL_TYPE_ID = ModelType.LOGICAL;
    public static final ModelType UML_MODEL_TYPE = ModelType.LOGICAL_LITERAL;
    public static final String UML_MODEL_URI = "http://www.eclipse.org/uml2/3.0.0/UML"; //$NON-NLS-1$
    public static final String UML_MODEL_STRING = "UML"; //$NON-NLS-1$
    public static final String UML_MODEL_IMAGE_ID = PluginConstants.Images.UML_MODEL;

    // XML Message Structure Model Constants
    public static final int XML_MESSAGE_STRUCTURE_MODEL_ID = 13;
    public static final int XML_MESSAGE_STRUCTURE_MODEL_TYPE_ID = ModelType.LOGICAL;
    public static final ModelType XML_MESSAGE_STRUCTURE_MODEL_TYPE = ModelType.LOGICAL_LITERAL;
    public static final String XML_MESSAGE_STRUCTURE_MODEL_URI = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$
    public static final String XML_MESSAGE_STRUCTURE_MODEL_STRING = "Xml Message Structure"; //$NON-NLS-1$
    public static final String XML_MESSAGE_STRUCTURE_MODEL_IMAGE_ID = PluginConstants.Images.XML_MESSAGE_STRUCTURE_MODEL;

    /**
     * Indicates if the specified <code>IResource</code> is a model with the specified type and metamodel URI.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @param theModelType the model type
     * @param thePrimaryMetamodelUri the URI
     * @return <code>true</code> if the model matches the criteria; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isModel( IResource theResource,
                                   ModelType theModelType,
                                   String thePrimaryMetamodelUri ) {
        boolean result = false;
        // Resource may have been deleted or in an unloaded initial state. Need to check if exists (Defect 22660)
        if ((theResource != null) && (theResource instanceof IFile) && theResource.exists()) {
            if (ModelUtilities.isModelFile(theResource)) {
                final IPath path = theResource.getLocation();
                if (path != null) {
                    File resourceFile = path.toFile();
                    if (resourceFile.exists()) {
                        if (ModelUtil.isXsdFile(theResource)
                            && ((thePrimaryMetamodelUri == null) || thePrimaryMetamodelUri.equals(XSDPackage.eNS_URI))) {
                            result = true;
                        } else {
                            XMIHeader header = ModelUtil.getXmiHeader(resourceFile);
                            if (header != null) {
                                if (theModelType != null && header.getModelType() != null) {
                                    if (header.getModelType().equals(theModelType.getName())) {
                                        String theURI = header.getPrimaryMetamodelURI();

                                        if (theURI != null && theURI.equals(thePrimaryMetamodelUri)) {
                                            result = true;
                                        }
                                    }
                                } else {
                                    if ((header.getPrimaryMetamodelURI() != null)
                                        && (header.getPrimaryMetamodelURI().equals(thePrimaryMetamodelUri))) {
                                        result = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * @param modelResource
     * @param wrapInTransaction
     * @param source
     * @return
     * @since 5.0
     */
    public static String getPrimaryMetamodelURIFromModelResource( ModelResource modelResource,
                                                                  boolean wrapInTransaction,
                                                                  Object source ) {
        if (modelResource == null || !modelResource.exists()) {
            return null;
        }
        String thePMURI = null;
        if (wrapInTransaction) {

            boolean requiredStart = ModelerCore.startTxn(false, false, "Get Primary Metamodel URI", source); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                thePMURI = modelResource.getPrimaryMetamodelUri();
                succeeded = true;
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        } else {
            try {
                thePMURI = modelResource.getPrimaryMetamodelUri();
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            }
        }

        return thePMURI;
    }

    /**
     * @param modelResource
     * @param wrapInTransaction
     * @param source
     * @return
     * @since 5.0
     */
    public static ModelType getModelTypeFromModelResource( ModelResource modelResource,
                                                           boolean wrapInTransaction,
                                                           Object source ) {
        if (modelResource == null || !modelResource.exists()) {
            return null;
        }
        ModelType modelType = null;
        if (wrapInTransaction) {

            boolean requiredStart = ModelerCore.startTxn(false, false, "Get Primary Metamodel URI", source); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                modelType = modelResource.getModelType();
                succeeded = true;
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        } else {
            try {
                modelType = modelResource.getModelType();
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            }
        }

        return modelType;
    }

    /**
     * @param someResource
     * @return
     * @since 5.0
     */
    public static String getPrimaryMetamodelURI( Object someResource ) {
        if (someResource == null) {
            return null;
        }
        String thePMURI = null;
        boolean isXsdFile = false;
        File fileOnFileSystem = null;

        if (someResource instanceof IResource && someResource instanceof IFile) {
            if (ModelUtilities.isModelFile((IResource)someResource)) {
                final IPath path = ((IResource)someResource).getLocation();
                if (path != null) {
                    fileOnFileSystem = path.toFile();
                }

            }
            isXsdFile = isSchemaModel((IResource)someResource);
        } else if (someResource instanceof ModelResource) {
            IResource theIResource = null;

            try {
                theIResource = ((ModelResource)someResource).getCorrespondingResource();
            } catch (ModelWorkspaceException theException) {
                UiConstants.Util.log(theException);
            }
            if (theIResource != null) {
                final IPath path = theIResource.getLocation();
                if (path != null) {
                    fileOnFileSystem = path.toFile();
                }
            }
            isXsdFile = isSchemaModel((ModelResource)someResource);
        } else if (someResource instanceof File) {
            fileOnFileSystem = (File)someResource;
            isXsdFile = fileOnFileSystem.getName().endsWith(ModelUtil.EXTENSION_XSD);
        }

        if (fileOnFileSystem != null && fileOnFileSystem.exists()) {
            if (isXsdFile) {
                thePMURI = XML_SCHEMA_MODEL_URI;
            } else {
                XMIHeader header = ModelUtil.getXmiHeader(fileOnFileSystem);
                if (header != null) {
                    thePMURI = header.getPrimaryMetamodelURI();
                }
            }
        }

        return thePMURI;
    }

    /**
     * @param someResource
     * @return
     * @since 5.0
     */
    public static String getModelType( Object someResource ) {
        if (someResource == null) {
            return null;
        }
        String theModelType = null;
        boolean isXsdFile = false;
        File fileOnFileSystem = null;

        if (someResource instanceof IResource && someResource instanceof IFile) {
            if (ModelUtilities.isModelFile((IResource)someResource)) {
                final IPath path = ((IResource)someResource).getLocation();
                if (path != null) {
                    fileOnFileSystem = path.toFile();
                }

            }
            isXsdFile = isSchemaModel((IResource)someResource);
        } else if (someResource instanceof ModelResource) {
            IResource theIResource = null;

            try {
                theIResource = ((ModelResource)someResource).getCorrespondingResource();
            } catch (ModelWorkspaceException theException) {
                UiConstants.Util.log(theException);
            }
            if (theIResource != null) {
                final IPath path = theIResource.getLocation();
                if (path != null) {
                    fileOnFileSystem = path.toFile();
                }
            }
            isXsdFile = isSchemaModel((ModelResource)someResource);
        } else if (someResource instanceof File) {
            fileOnFileSystem = (File)someResource;
        }

        if (fileOnFileSystem != null && fileOnFileSystem.exists()) {
            if (isXsdFile) {
                theModelType = XML_SCHEMA_MODEL_TYPE.toString();
            } else {
                XMIHeader header = ModelUtil.getXmiHeader(fileOnFileSystem);
                if (header != null) {
                    theModelType = header.getModelType();
                }
            }
        }

        return theModelType;
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a model with the specified type and metamodel URI.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @param theModelType the model type
     * @param thePrimaryMetamodelUri the URI
     * @return <code>true</code> if the model matches the criteria; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isModel( ModelResource modelResource,
                                   ModelType theModelType,
                                   String thePrimaryMetamodelUri ) {
        boolean result = false;
        // Resource may have been deleted or in an unloaded initial state. Need to check if exists (Defect 22660)
        if ((modelResource != null) && modelResource.exists()) {
            try {
                // Let's just use the header info (IResource) method to keep this as light a call as possible
                // (i.e. should not result in ModelAnnotations getting loaded/created, etc...)

                if (modelResource.getUnderlyingResource() != null) {
                    return isModel(modelResource.getUnderlyingResource(), theModelType, thePrimaryMetamodelUri);
                }

                // Now if we really have to, we use the model resource info
                if (ModelUtil.isXsdFile(modelResource.getUnderlyingResource())) {
                    result = true;
                } else {
                    if (theModelType != null) {
                        if (modelResource.getModelType().equals(theModelType)
                            && modelResource.getPrimaryMetamodelDescriptor() != null
                            && (modelResource.getPrimaryMetamodelDescriptor().getNamespaceURI() != null)
                            && (modelResource.getPrimaryMetamodelDescriptor().getNamespaceURI().equals(thePrimaryMetamodelUri))) {
                            result = true;
                        }
                    } else {
                        if (modelResource.getPrimaryMetamodelDescriptor() != null
                            && (modelResource.getPrimaryMetamodelDescriptor().getNamespaceURI() != null)
                            && (modelResource.getPrimaryMetamodelDescriptor().getNamespaceURI().equals(thePrimaryMetamodelUri))) {
                            result = true;
                        }
                    }
                }
            } catch (ModelWorkspaceException theException) {
                UiConstants.Util.log(theException);
            }
        }

        return result;
    }

    /**
     * Indicates if the specified <code>IResource</code> is an extension model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a extension model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isExtensionModel( IResource theResource ) {
        return isModel(theResource, EXTENSION_MODEL_TYPE, EXTENSION_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is an extension model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a extension model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isExtensionModel( ModelResource modelResource ) {
        return isModel(modelResource, EXTENSION_MODEL_TYPE, EXTENSION_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a schema model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a schema model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isSchemaModel( IResource theResource ) {
        return isModel(theResource, XML_SCHEMA_MODEL_TYPE, XML_SCHEMA_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a schema model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a schema model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isSchemaModel( ModelResource modelResource ) {
        return isModel(modelResource, XML_SCHEMA_MODEL_TYPE, XML_SCHEMA_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a source model. A source model is a relational, physical model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a source model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isRelationalSourceModel( IResource theResource ) {
        return isModel(theResource, RELATIONAL_SOURCE_MODEL_TYPE, RELATIONAL_SOURCE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a source model. A source model is a relational, physical model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a source model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isRelationalSourceModel( ModelResource modelResource ) {
        return isModel(modelResource, RELATIONAL_SOURCE_MODEL_TYPE, RELATIONAL_SOURCE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a relational view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isRelationalViewModel( IResource theResource ) {
        return isModel(theResource, RELATIONAL_VIEW_MODEL_TYPE, RELATIONAL_VIEW_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a relational view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isRelationalViewModel( ModelResource modelResource ) {
        return isModel(modelResource, RELATIONAL_VIEW_MODEL_TYPE, RELATIONAL_VIEW_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a xml view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlViewModel( IResource theResource ) {
        return isModel(theResource, XML_VIEW_MODEL_TYPE, XML_VIEW_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a xml view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlViewModel( ModelResource modelResource ) {
        return isModel(modelResource, XML_VIEW_MODEL_TYPE, XML_VIEW_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a web services view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a web services view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isWebServicesViewModel( IResource theResource ) {
        return isModel(theResource, WEB_SERVICES_MODEL_TYPE, WEB_SERVICES_VIEW_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a web services view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a web services view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isWebServicesViewModel( ModelResource modelResource ) {
        return isModel(modelResource, WEB_SERVICES_MODEL_TYPE, WEB_SERVICES_VIEW_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a logical view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a logical model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isLogicalModel( IResource theResource ) {
        if ((theResource != null) && (theResource instanceof IFile) && theResource.exists()) {
            if (ModelUtilities.isModelFile(theResource)) {

                if (isUmlModel(theResource)) {
                    return true;
                } else if (isExtensionModel(theResource)) {
                    return true;
                } else if (isRelationshipModel(theResource)) {
                    return true;
                } else if (isFunctionModel(theResource)) {
                    return true;
                } else if (isPeopleModel(theResource)) {
                    return true;
                } else if (isXmlMessageStructureModel(theResource)) {
                    return true;
                }

                // Now if we have to, let's check the ModelType == LOGICAL_MODEL_TYPE by itself
                final IPath path = theResource.getLocation();
                if (path != null) {
                    File resourceFile = path.toFile();
                    if (resourceFile.exists()) {
                        XMIHeader header = ModelUtil.getXmiHeader(resourceFile);
                        if (header != null && header.getModelType() != null) {
                            return header.getModelType().equals(LOGICAL_MODEL_TYPE.getName());
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a logical view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a logical model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isLogicalModel( ModelResource modelResource ) {
        if (isModel(modelResource, LOGICAL_MODEL_TYPE, LOGICAL_MODEL_URI)) {
            return true;
        } else if (isUmlModel(modelResource)) {
            return true;
        } else if (isExtensionModel(modelResource)) {
            return true;
        } else if (isRelationshipModel(modelResource)) {
            return true;
        } else if (isFunctionModel(modelResource)) {
            return true;
        } else if (isPeopleModel(modelResource)) {
            return true;
        } else if (isXmlMessageStructureModel(modelResource)) {
            return true;
        }

        return false;
    }

    /**
     * Indicates if the specified <code>IResource</code> is a relationship view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a relationship model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isRelationshipModel( IResource theResource ) {
        return isModel(theResource, LOGICAL_MODEL_TYPE, RELATIONSHIP_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a relationship model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a relationship model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isRelationshipModel( ModelResource modelResource ) {
        return isModel(modelResource, LOGICAL_MODEL_TYPE, RELATIONSHIP_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a people model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a people model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isPeopleModel( IResource theResource ) {
        return isModel(theResource, LOGICAL_MODEL_TYPE, PEOPLE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a people model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a people model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isUmlModel( ModelResource modelResource ) {
        return isModel(modelResource, LOGICAL_MODEL_TYPE, UML_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a people model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a people model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isUmlModel( IResource theResource ) {
        return isModel(theResource, LOGICAL_MODEL_TYPE, UML_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a people model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a people model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isPeopleModel( ModelResource modelResource ) {
        return isModel(modelResource, LOGICAL_MODEL_TYPE, PEOPLE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a function model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a function model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isFunctionModel( IResource theResource ) {
        // Function Models have NO ModelType
        return isModel(theResource, null, FUNCTION_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a function view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a function model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isFunctionModel( ModelResource modelResource ) {
        return isModel(modelResource, null, FUNCTION_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a xml service source model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml service source model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlServiceSourceModel( IResource theResource ) {
        // Function Models have NO ModelType
        return isModel(theResource, XML_SERVICE_SOURCE_MODEL_TYPE, XML_SERVICE_SOURCE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a xml service source model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml service source model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlServiceSourceModel( ModelResource modelResource ) {
        return isModel(modelResource, XML_SERVICE_SOURCE_MODEL_TYPE, XML_SERVICE_SOURCE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a xml service view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml service view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlServiceViewModel( IResource theResource ) {
        // Function Models have NO ModelType
        return isModel(theResource, XML_SERVICE_VIEW_MODEL_TYPE, XML_SERVICE_SOURCE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a xml service view model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml service view model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlServiceViewModel( ModelResource modelResource ) {
        return isModel(modelResource, XML_SERVICE_VIEW_MODEL_TYPE, XML_SERVICE_SOURCE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>IResource</code> is a xml message structure model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml message structure model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlMessageStructureModel( IResource theResource ) {
        // Function Models have NO ModelType
        return isModel(theResource, XML_MESSAGE_STRUCTURE_MODEL_TYPE, XML_MESSAGE_STRUCTURE_MODEL_URI);
    }

    /**
     * Indicates if the specified <code>ModelResource</code> is a xml message structure model.
     * 
     * @param theResource the resource being checked (may not be <code>null</code>)
     * @return <code>true</code> if a xml message structure model; <code>false</code> otherwise.
     * @since 5.0
     */
    public static boolean isXmlMessageStructureModel( ModelResource modelResource ) {
        return isModel(modelResource, XML_MESSAGE_STRUCTURE_MODEL_TYPE, XML_MESSAGE_STRUCTURE_MODEL_URI);
    }

    /**
     * Returns the <code>ModelType</code> for a given <code>IResource</code>
     * 
     * @param theResource
     * @return <code>ModelType</code>
     * @since 5.0
     */
    public static ModelType getModelType( IResource theResource ) {
        // Resource may have been deleted or in an unloaded initial state. Need to check if exists (Defect 22660)
        if ((theResource != null) && (theResource instanceof IFile) && theResource.exists()) {
            final IPath path = theResource.getLocation();
            if (path != null) {
                File resourceFile = path.toFile();
                if (resourceFile.exists()) {
                    XMIHeader header = ModelUtil.getXmiHeader(resourceFile);
                    if (header != null && header.getModelType() != null) {
                        String theModelType = header.getModelType();
                        return ModelType.get(theModelType);
                    } else if (ModelUtil.isXsdFile(theResource)) {
                        return XML_SCHEMA_MODEL_TYPE;
                    }
                }
            }
        }
        return ModelType.UNKNOWN_LITERAL;
    }

    /**
     * Returns the <code>ModelType</code> for a given <code>ModelResource</code>
     * 
     * @param theResource
     * @return <code>ModelType</code>
     * @since 5.0
     */
    public static ModelType getModelType( ModelResource modelResource ) {
        // Resource may have been deleted or in an unloaded initial state. Need to check if exists (Defect 22660)
        if (modelResource != null && modelResource.exists()) {
            try {
                // Let's just use the header info (IResource) method to keep this as light a call as possible
                // (i.e. should not result in ModelAnnotations getting loaded/created, etc...)

                if (modelResource.getUnderlyingResource() != null) {
                    return getModelType(modelResource.getUnderlyingResource());
                }

                // Now if we really have to, we use the model resource info
                if (modelResource.getModelType() != null) {
                    return modelResource.getModelType();
                }
            } catch (ModelWorkspaceException theException) {
                UiConstants.Util.log(theException);
            }
        }
        return ModelType.UNKNOWN_LITERAL;
    }

    public static boolean isVirtualModelType( ModelResource modelResource ) {
        return getModelType(modelResource).equals(ModelType.VIRTUAL_LITERAL);
    }

    public static boolean isPhysicalModelType( ModelResource modelResource ) {
        return getModelType(modelResource).equals(ModelType.PHYSICAL_LITERAL);
    }

    public static boolean isLogicalModelType( ModelResource modelResource ) {
        return getModelType(modelResource).equals(ModelType.LOGICAL_LITERAL);
    }

    public static boolean isVirtualModelType( IResource theResource ) {
        return getModelType(theResource).equals(ModelType.VIRTUAL_LITERAL);
    }

    public static boolean isPhysicalModelType( IResource theResource ) {
        return getModelType(theResource).equals(ModelType.PHYSICAL_LITERAL);
    }

    public static boolean isLogicalModelType( IResource theResource ) {
        return getModelType(theResource).equals(ModelType.LOGICAL_LITERAL);
    }

    public static Image getModelImage( ModelResource modelResource ) {
        // Check for XSD file first
        if (modelResource != null && modelResource.exists()) {
            try {
                if (ModelUtil.isXsdFile(modelResource.getCorrespondingResource())) {
                    return UiPlugin.getDefault().getImage(XML_SCHEMA_MODEL_IMAGE_ID);
                }
            } catch (ModelWorkspaceException theException) {
                UiConstants.Util.log(theException);
            }
        }

        int modelID = getModelID(modelResource);

        return getImage(modelID);
    }

    public static Image getModelImage( IResource theResource ) {
        if (theResource != null && theResource.exists()) {
            // Check for XSD file first since the header reader won't work for it.
            if (ModelUtil.isXsdFile(theResource)) {
                return UiPlugin.getDefault().getImage(XML_SCHEMA_MODEL_IMAGE_ID);
            }
        }
        int modelID = getModelID(theResource);

        return getImage(modelID);
    }

    public static Image getImage( int modelID ) {
        Image modelImage = null;
        switch (modelID) {
            case RELATIONAL_SOURCE_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(RELATIONAL_SOURCE_MODEL_IMAGE_ID);
            }
                break;

            case RELATIONAL_VIEW_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(RELATIONAL_VIEW_MODEL_IMAGE_ID);
            }
                break;

            case XML_VIEW_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(XML_VIEW_MODEL_IMAGE_ID);
            }
                break;

            case EXTENSION_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(EXTENSION_MODEL_IMAGE_ID);
            }
                break;

            case FUNCTION_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(FUNCTION_MODEL_IMAGE_ID);
            }
                break;

            case XML_SCHEMA_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(XML_SCHEMA_MODEL_IMAGE_ID);
            }
                break;

            case LOGICAL_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(LOGICAL_MODEL_IMAGE_ID);
            }
                break;

            case WEB_SERVICES_VIEW_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(WEB_SERVICES_VIEW_MODEL_IMAGE_ID);
            }
                break;

            case XML_SERVICE_SOURCE_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(XML_SERVICE_SOURCE_MODEL_IMAGE_ID);
            }
                break;

            case XML_SERVICE_VIEW_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(XML_SERVICE_VIEW_MODEL_IMAGE_ID);
            }
                break;

            case RELATIONSHIP_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(RELATIONSHIP_MODEL_IMAGE_ID);
            }
                break;

            case PEOPLE_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(PEOPLE_MODEL_IMAGE_ID);
            }
                break;

            case UML_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(UML_MODEL_IMAGE_ID);
            }
                break;

            case XML_MESSAGE_STRUCTURE_MODEL_ID: {
                modelImage = UiPlugin.getDefault().getImage(XML_MESSAGE_STRUCTURE_MODEL_IMAGE_ID);
            }
                break;

            default: {
                modelImage = UiPlugin.getDefault().getImage(RELATIONAL_SOURCE_MODEL_IMAGE_ID);
            }
                break;
        }
        return modelImage;
    }

    private static int getModelID( IResource theResource ) {
        // Resource may have been deleted or in an unloaded initial state. Need to check if exists (Defect 22660)
        if ((theResource != null) && (theResource instanceof IFile) && theResource.exists()) {
            if (isSchemaModel(theResource)) {
                return XML_SCHEMA_MODEL_ID;
            }
            if (isRelationalSourceModel(theResource)) {
                return RELATIONAL_SOURCE_MODEL_ID;
            }
            if (isExtensionModel(theResource)) {
                return EXTENSION_MODEL_ID;
            }
            if (isRelationalViewModel(theResource)) {
                return RELATIONAL_VIEW_MODEL_ID;
            }
            if (isXmlViewModel(theResource)) {
                return XML_VIEW_MODEL_ID;
            }
            if (isFunctionModel(theResource)) {
                return FUNCTION_MODEL_ID;
            }
            if (isWebServicesViewModel(theResource)) {
                return WEB_SERVICES_VIEW_MODEL_ID;
            }
            if (isXmlServiceSourceModel(theResource)) {
                return XML_SERVICE_SOURCE_MODEL_ID;
            }
            if (isXmlServiceViewModel(theResource)) {
                return XML_SERVICE_VIEW_MODEL_ID;
            }
            if (isUmlModel(theResource)) {
                return UML_MODEL_ID;
            }
            if (isRelationshipModel(theResource)) {
                return RELATIONSHIP_MODEL_ID;
            }
            if (isPeopleModel(theResource)) {
                return PEOPLE_MODEL_ID;
            }
            if (isXmlMessageStructureModel(theResource)) {
                return XML_MESSAGE_STRUCTURE_MODEL_ID;
            }
            if (isLogicalModel(theResource)) {
                return LOGICAL_MODEL_ID;
            }
        }

        return UNKNOWN_MODEL_ID;
    }

    private static int getModelID( ModelResource modelResource ) {
        // Resource may have been deleted or in an unloaded initial state. Need to check if exists (Defect 22660)
        if ((modelResource != null) && modelResource.exists()) {
            if (isSchemaModel(modelResource)) {
                return XML_SCHEMA_MODEL_ID;
            }
            if (isRelationalSourceModel(modelResource)) {
                return RELATIONAL_SOURCE_MODEL_ID;
            }
            if (isExtensionModel(modelResource)) {
                return EXTENSION_MODEL_ID;
            }
            if (isRelationalViewModel(modelResource)) {
                return RELATIONAL_VIEW_MODEL_ID;
            }
            if (isXmlViewModel(modelResource)) {
                return XML_VIEW_MODEL_ID;
            }
            if (isFunctionModel(modelResource)) {
                return FUNCTION_MODEL_ID;
            }
            if (isWebServicesViewModel(modelResource)) {
                return WEB_SERVICES_VIEW_MODEL_ID;
            }
            if (isXmlServiceSourceModel(modelResource)) {
                return XML_SERVICE_SOURCE_MODEL_ID;
            }
            if (isXmlServiceViewModel(modelResource)) {
                return XML_SERVICE_VIEW_MODEL_ID;
            }
            if (isUmlModel(modelResource)) {
                return UML_MODEL_ID;
            }
            if (isRelationshipModel(modelResource)) {
                return RELATIONSHIP_MODEL_ID;
            }
            if (isPeopleModel(modelResource)) {
                return PEOPLE_MODEL_ID;
            }
            if (isXmlMessageStructureModel(modelResource)) {
                return XML_MESSAGE_STRUCTURE_MODEL_ID;
            }
            if (isLogicalModel(modelResource)) {
                return LOGICAL_MODEL_ID;
            }
        }

        return UNKNOWN_MODEL_ID;
    }
}
