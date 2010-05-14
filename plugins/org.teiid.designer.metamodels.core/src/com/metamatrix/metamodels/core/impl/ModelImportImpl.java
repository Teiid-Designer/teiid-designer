/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.core.CoreMetamodelPlugin;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Model Import</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelImportImpl#getName <em>Name</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelImportImpl#getPath <em>Path</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelImportImpl#getModelLocation <em>Model Location</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelImportImpl#getUuid <em>Uuid</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelImportImpl#getModelType <em>Model Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelImportImpl#getPrimaryMetamodelUri <em>Primary Metamodel Uri</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelImportImpl#getModel <em>Model</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ModelImportImpl extends EObjectImpl implements ModelImport {

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getPath() <em>Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getPath()
     * @generated
     * @ordered
     */
    protected static final String PATH_EDEFAULT = null;

    /**
     * The default value of the '{@link #getModelLocation() <em>Model Location</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getModelLocation()
     * @generated
     * @ordered
     */
    protected static final String MODEL_LOCATION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getModelLocation() <em>Model Location</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getModelLocation()
     * @generated
     * @ordered
     */
    protected String modelLocation = MODEL_LOCATION_EDEFAULT;

    /**
     * The default value of the '{@link #getUuid() <em>Uuid</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUuid()
     * @generated
     * @ordered
     */
    protected static final String UUID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUuid() <em>Uuid</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUuid()
     * @generated
     * @ordered
     */
    protected String uuid = UUID_EDEFAULT;

    /**
     * The default value of the '{@link #getModelType() <em>Model Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getModelType()
     * @generated
     * @ordered
     */
    protected static final ModelType MODEL_TYPE_EDEFAULT = ModelType.UNKNOWN_LITERAL;

    /**
     * The cached value of the '{@link #getModelType() <em>Model Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getModelType()
     * @generated
     * @ordered
     */
    protected ModelType modelType = MODEL_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getPrimaryMetamodelUri() <em>Primary Metamodel Uri</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getPrimaryMetamodelUri()
     * @generated
     * @ordered
     */
    protected static final String PRIMARY_METAMODEL_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPrimaryMetamodelUri() <em>Primary Metamodel Uri</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getPrimaryMetamodelUri()
     * @generated
     * @ordered
     */
    protected String primaryMetamodelUri = PRIMARY_METAMODEL_URI_EDEFAULT;

    /**
     * The cached Eclipse workspace.
     * 
     * @generated NOT
     */
    protected static IWorkspaceRoot workspaceRoot = EcorePlugin.getWorkspaceRoot();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ModelImportImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return CorePackage.eINSTANCE.getModelImport();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setName( String newName ) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.MODEL_IMPORT__NAME,
                                                                   oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String getPath() {
        if (this.modelLocation != null) {

            // If the modelLocation value represents a logical location of a built-in resource
            // then simply return this value as the path
            if (this.modelLocation.startsWith("http") || //$NON-NLS-1$
                this.modelLocation.startsWith("mtkplugin:") || //$NON-NLS-1$
                this.modelLocation.startsWith("pathmap://UML2_METAMODELS/")) { //$NON-NLS-1$
                return modelLocation;
            }

            // If the modelLocation value represents a file location relative
            // to the resource with this ModelImport then return the path
            // relative to the Eclipse workspace root
            // ECLIPSE-DEPEND-BEGIN
            if (workspaceRoot != null && eResource() != null) {
                URI baseLocationURI = eResource().getURI();
                // If the base resource URI was created as a file URI then it's path is encoded so before we
                // resolve the referenced resource we need to encode it's relative path
                URI modelLocationURI = (baseLocationURI.isFile() ? URI.createURI(this.modelLocation, false) : URI.createURI(this.modelLocation));
                if (baseLocationURI.isHierarchical() && !baseLocationURI.isRelative() && modelLocationURI.isRelative()) {
                    modelLocationURI = modelLocationURI.resolve(baseLocationURI);
                }

                // Match the file system location of the model to an IFile in the workspace
                IFile iFile = matchUriToIFile(modelLocationURI);
                if (iFile != null) {
                    return iFile.getFullPath().toString();
                }
            }
        }
        return null;
    }

    /**
     * Match the file URI to an IFile in the workspace
     * 
     * @param uri
     * @return
     * @since 4.3
     */
    protected IFile matchUriToIFile( final URI uri ) {
        // Match the file system location of the model to an IFile in the workspace
        if (uri != null && uri.isFile()) {
            File f = new File(URI.decode(uri.toFileString()));

            // Verify its existence on the file system
            if (f.exists()) {
                String modelPath = new Path(f.getAbsolutePath()).toPortableString();

                // Iterate through all the open IProjects in the workspace ...
                IProject[] projects = workspaceRoot.getProjects();
                for (int i = 0; i != projects.length; ++i) {
                    IProject iProj = projects[i];

                    // Match the name in the file URI to one of the IProject names
                    if (iProj.isOpen() && modelPath.startsWith(iProj.getLocation().toPortableString())) {
                        List iFiles = new ArrayList();
                        collectIFiles(iProj, iFiles);

                        // Match the name in the file URI to one of the IFile names in this IProject
                        for (Iterator j = iFiles.iterator(); j.hasNext();) {
                            IFile iFile = (IFile)j.next();
                            if (modelPath.equals(iFile.getLocation().toPortableString())) {
                                return iFile;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Add all IFile instances found within the specified IContainer to the result list
     * 
     * @param iContainer
     * @param result
     * @since 4.3
     */
    protected void collectIFiles( final IContainer iContainer,
                                  final List result ) {
        if (iContainer != null) {
            try {
                IResource[] iResources = iContainer.members();
                for (int i = 0; i != iResources.length; ++i) {
                    IResource r = iResources[i];
                    if (r.exists()) {
                        if (r.getType() == IResource.FILE) {
                            result.add(r);
                        } else if (r.getType() == IResource.FOLDER) {
                            collectIFiles((IContainer)r, result);
                        }
                    }
                }
            } catch (CoreException e) {
                CoreMetamodelPlugin.Util.log(e);
            }
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getModelLocation() {
        return modelLocation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setModelLocation( String newModelLocation ) {
        String oldModelLocation = modelLocation;
        modelLocation = newModelLocation;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_IMPORT__MODEL_LOCATION, oldModelLocation,
                                                                   modelLocation));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setUuid( String newUuid ) {
        String oldUuid = uuid;
        uuid = newUuid;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.MODEL_IMPORT__UUID,
                                                                   oldUuid, uuid));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ModelType getModelType() {
        return modelType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setModelType( ModelType newModelType ) {
        ModelType oldModelType = modelType;
        modelType = newModelType == null ? MODEL_TYPE_EDEFAULT : newModelType;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.MODEL_IMPORT__MODEL_TYPE,
                                                                   oldModelType, modelType));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getPrimaryMetamodelUri() {
        return primaryMetamodelUri;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setPrimaryMetamodelUri( String newPrimaryMetamodelUri ) {
        String oldPrimaryMetamodelUri = primaryMetamodelUri;
        primaryMetamodelUri = newPrimaryMetamodelUri;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_IMPORT__PRIMARY_METAMODEL_URI,
                                                                   oldPrimaryMetamodelUri, primaryMetamodelUri));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ModelAnnotation getModel() {
        if (eContainerFeatureID != CorePackage.MODEL_IMPORT__MODEL) return null;
        return (ModelAnnotation)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setModel( ModelAnnotation newModel ) {
        if (newModel != eContainer || (eContainerFeatureID != CorePackage.MODEL_IMPORT__MODEL && newModel != null)) {
            if (EcoreUtil.isAncestor(this, newModel)) throw new IllegalArgumentException(
                                                                                         "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newModel != null) msgs = ((InternalEObject)newModel).eInverseAdd(this,
                                                                                 CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS,
                                                                                 ModelAnnotation.class,
                                                                                 msgs);
            msgs = eBasicSetContainer((InternalEObject)newModel, CorePackage.MODEL_IMPORT__MODEL, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          CorePackage.MODEL_IMPORT__MODEL, newModel, newModel));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( InternalEObject otherEnd,
                                          int featureID,
                                          Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.MODEL_IMPORT__MODEL:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, CorePackage.MODEL_IMPORT__MODEL, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( InternalEObject otherEnd,
                                             int featureID,
                                             Class baseClass,
                                             NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.MODEL_IMPORT__MODEL:
                    return eBasicSetContainer(null, CorePackage.MODEL_IMPORT__MODEL, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case CorePackage.MODEL_IMPORT__MODEL:
                    return eContainer.eInverseRemove(this,
                                                     CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS,
                                                     ModelAnnotation.class,
                                                     msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet( EStructuralFeature eFeature,
                        boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.MODEL_IMPORT__NAME:
                return getName();
            case CorePackage.MODEL_IMPORT__PATH:
                return getPath();
            case CorePackage.MODEL_IMPORT__MODEL_LOCATION:
                return getModelLocation();
            case CorePackage.MODEL_IMPORT__UUID:
                return getUuid();
            case CorePackage.MODEL_IMPORT__MODEL_TYPE:
                return getModelType();
            case CorePackage.MODEL_IMPORT__PRIMARY_METAMODEL_URI:
                return getPrimaryMetamodelUri();
            case CorePackage.MODEL_IMPORT__MODEL:
                return getModel();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet( EStructuralFeature eFeature,
                      Object newValue ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.MODEL_IMPORT__NAME:
                setName((String)newValue);
                return;
            case CorePackage.MODEL_IMPORT__MODEL_LOCATION:
                setModelLocation((String)newValue);
                return;
            case CorePackage.MODEL_IMPORT__UUID:
                setUuid((String)newValue);
                return;
            case CorePackage.MODEL_IMPORT__MODEL_TYPE:
                setModelType((ModelType)newValue);
                return;
            case CorePackage.MODEL_IMPORT__PRIMARY_METAMODEL_URI:
                setPrimaryMetamodelUri((String)newValue);
                return;
            case CorePackage.MODEL_IMPORT__MODEL:
                setModel((ModelAnnotation)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.MODEL_IMPORT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case CorePackage.MODEL_IMPORT__MODEL_LOCATION:
                setModelLocation(MODEL_LOCATION_EDEFAULT);
                return;
            case CorePackage.MODEL_IMPORT__UUID:
                setUuid(UUID_EDEFAULT);
                return;
            case CorePackage.MODEL_IMPORT__MODEL_TYPE:
                setModelType(MODEL_TYPE_EDEFAULT);
                return;
            case CorePackage.MODEL_IMPORT__PRIMARY_METAMODEL_URI:
                setPrimaryMetamodelUri(PRIMARY_METAMODEL_URI_EDEFAULT);
                return;
            case CorePackage.MODEL_IMPORT__MODEL:
                setModel((ModelAnnotation)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.MODEL_IMPORT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case CorePackage.MODEL_IMPORT__PATH:
                return PATH_EDEFAULT == null ? getPath() != null : !PATH_EDEFAULT.equals(getPath());
            case CorePackage.MODEL_IMPORT__MODEL_LOCATION:
                return MODEL_LOCATION_EDEFAULT == null ? modelLocation != null : !MODEL_LOCATION_EDEFAULT.equals(modelLocation);
            case CorePackage.MODEL_IMPORT__UUID:
                return UUID_EDEFAULT == null ? uuid != null : !UUID_EDEFAULT.equals(uuid);
            case CorePackage.MODEL_IMPORT__MODEL_TYPE:
                return modelType != MODEL_TYPE_EDEFAULT;
            case CorePackage.MODEL_IMPORT__PRIMARY_METAMODEL_URI:
                return PRIMARY_METAMODEL_URI_EDEFAULT == null ? primaryMetamodelUri != null : !PRIMARY_METAMODEL_URI_EDEFAULT.equals(primaryMetamodelUri);
            case CorePackage.MODEL_IMPORT__MODEL:
                return getModel() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", modelLocation: "); //$NON-NLS-1$
        result.append(modelLocation);
        result.append(", uuid: "); //$NON-NLS-1$
        result.append(uuid);
        result.append(", modelType: "); //$NON-NLS-1$
        result.append(modelType);
        result.append(", primaryMetamodelUri: "); //$NON-NLS-1$
        result.append(primaryMetamodelUri);
        result.append(')');
        return result.toString();
    }

} // ModelImportImpl
