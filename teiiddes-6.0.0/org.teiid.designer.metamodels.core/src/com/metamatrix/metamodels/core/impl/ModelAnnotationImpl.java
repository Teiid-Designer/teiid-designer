/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.metamodels.core.CoreMetamodelPlugin;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.core.util.UriValidator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Model Annotation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getDescription <em>Description</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getNameInSource <em>Name In Source</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getPrimaryMetamodelUri <em>Primary Metamodel Uri</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getModelType <em>Model Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getMaxSetSize <em>Max Set Size</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#isVisible <em>Visible</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#isSupportsDistinct <em>Supports Distinct</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#isSupportsJoin <em>Supports Join</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#isSupportsOrderBy <em>Supports Order By</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#isSupportsOuterJoin <em>Supports Outer Join</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#isSupportsWhereAll <em>Supports Where All</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getTags <em>Tags</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getNamespaceUri <em>Namespace Uri</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getProducerName <em>Producer Name</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getProducerVersion <em>Producer Version</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getModelImports <em>Model Imports</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl#getExtensionPackage <em>Extension Package</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ModelAnnotationImpl extends EObjectImpl implements ModelAnnotation {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

    /**
     * The default value of the '{@link #getNameInSource() <em>Name In Source</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getNameInSource()
     * @generated
     * @ordered
     */
    protected static final String NAME_IN_SOURCE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNameInSource() <em>Name In Source</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getNameInSource()
     * @generated
     * @ordered
     */
    protected String nameInSource = NAME_IN_SOURCE_EDEFAULT;

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
     * The default value of the '{@link #getMaxSetSize() <em>Max Set Size</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getMaxSetSize()
     * @generated
     * @ordered
     */
    protected static final int MAX_SET_SIZE_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getMaxSetSize() <em>Max Set Size</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getMaxSetSize()
     * @generated
     * @ordered
     */
    protected int maxSetSize = MAX_SET_SIZE_EDEFAULT;

    /**
     * The default value of the '{@link #isVisible() <em>Visible</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isVisible()
     * @generated
     * @ordered
     */
    protected static final boolean VISIBLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isVisible() <em>Visible</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isVisible()
     * @generated
     * @ordered
     */
    protected boolean visible = VISIBLE_EDEFAULT;

    /**
     * The default value of the '{@link #isSupportsDistinct() <em>Supports Distinct</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isSupportsDistinct()
     * @generated
     * @ordered
     */
    protected static final boolean SUPPORTS_DISTINCT_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSupportsDistinct() <em>Supports Distinct</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isSupportsDistinct()
     * @generated
     * @ordered
     */
    protected boolean supportsDistinct = SUPPORTS_DISTINCT_EDEFAULT;

    /**
     * The default value of the '{@link #isSupportsJoin() <em>Supports Join</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isSupportsJoin()
     * @generated
     * @ordered
     */
    protected static final boolean SUPPORTS_JOIN_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSupportsJoin() <em>Supports Join</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isSupportsJoin()
     * @generated
     * @ordered
     */
    protected boolean supportsJoin = SUPPORTS_JOIN_EDEFAULT;

    /**
     * The default value of the '{@link #isSupportsOrderBy() <em>Supports Order By</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isSupportsOrderBy()
     * @generated
     * @ordered
     */
    protected static final boolean SUPPORTS_ORDER_BY_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSupportsOrderBy() <em>Supports Order By</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isSupportsOrderBy()
     * @generated
     * @ordered
     */
    protected boolean supportsOrderBy = SUPPORTS_ORDER_BY_EDEFAULT;

    /**
     * The default value of the '{@link #isSupportsOuterJoin() <em>Supports Outer Join</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSupportsOuterJoin()
     * @generated
     * @ordered
     */
    protected static final boolean SUPPORTS_OUTER_JOIN_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSupportsOuterJoin() <em>Supports Outer Join</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSupportsOuterJoin()
     * @generated
     * @ordered
     */
    protected boolean supportsOuterJoin = SUPPORTS_OUTER_JOIN_EDEFAULT;

    /**
     * The default value of the '{@link #isSupportsWhereAll() <em>Supports Where All</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSupportsWhereAll()
     * @generated
     * @ordered
     */
    protected static final boolean SUPPORTS_WHERE_ALL_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSupportsWhereAll() <em>Supports Where All</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isSupportsWhereAll()
     * @generated
     * @ordered
     */
    protected boolean supportsWhereAll = SUPPORTS_WHERE_ALL_EDEFAULT;

    /**
     * The cached value of the '{@link #getTags() <em>Tags</em>}' map. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getTags()
     * @generated
     * @ordered
     */
    protected EMap tags = null;

    /**
     * The default value of the '{@link #getNamespaceUri() <em>Namespace Uri</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getNamespaceUri()
     * @generated
     * @ordered
     */
    protected static final String NAMESPACE_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNamespaceUri() <em>Namespace Uri</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getNamespaceUri()
     * @generated
     * @ordered
     */
    protected String namespaceUri = NAMESPACE_URI_EDEFAULT;

    /**
     * The default value of the '{@link #getProducerName() <em>Producer Name</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getProducerName()
     * @generated
     * @ordered
     */
    protected static final String PRODUCER_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProducerName() <em>Producer Name</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getProducerName()
     * @generated
     * @ordered
     */
    protected String producerName = PRODUCER_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getProducerVersion() <em>Producer Version</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getProducerVersion()
     * @generated
     * @ordered
     */
    protected static final String PRODUCER_VERSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProducerVersion() <em>Producer Version</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getProducerVersion()
     * @generated
     * @ordered
     */
    protected String producerVersion = PRODUCER_VERSION_EDEFAULT;

    /**
     * The cached value of the '{@link #getModelImports() <em>Model Imports</em>}' containment reference list. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getModelImports()
     * @generated
     * @ordered
     */
    protected EList modelImports = null;

    /**
     * The cached value of the '{@link #getExtensionPackage() <em>Extension Package</em>}' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getExtensionPackage()
     * @generated
     * @ordered
     */
    protected XPackage extensionPackage = null;

    /**
     * The map of path string to ModelImport object in {@link #getModelImports()}.
     */
    protected Map modelImportsByPath = new HashMap();

    /**
     * The map of UUID string to ModelImport object in {@link #getModelImports()}.
     */
    protected Map modelImportsByUuid = new HashMap();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ModelAnnotationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return CorePackage.eINSTANCE.getModelAnnotation();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setDescription( String newDescription ) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__DESCRIPTION, oldDescription,
                                                                   description));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getNameInSource() {
        return nameInSource;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setNameInSource( String newNameInSource ) {
        String oldNameInSource = nameInSource;
        nameInSource = newNameInSource;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__NAME_IN_SOURCE, oldNameInSource,
                                                                   nameInSource));
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
                                                                   CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI,
                                                                   oldPrimaryMetamodelUri, primaryMetamodelUri));
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
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__MODEL_TYPE, oldModelType,
                                                                   modelType));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public int getMaxSetSize() {
        return maxSetSize;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMaxSetSize( int newMaxSetSize ) {
        int oldMaxSetSize = maxSetSize;
        maxSetSize = newMaxSetSize;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__MAX_SET_SIZE, oldMaxSetSize,
                                                                   maxSetSize));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setVisible( boolean newVisible ) {
        boolean oldVisible = visible;
        visible = newVisible;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.MODEL_ANNOTATION__VISIBLE,
                                                                   oldVisible, visible));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isSupportsDistinct() {
        return supportsDistinct;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSupportsDistinct( boolean newSupportsDistinct ) {
        boolean oldSupportsDistinct = supportsDistinct;
        supportsDistinct = newSupportsDistinct;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__SUPPORTS_DISTINCT,
                                                                   oldSupportsDistinct, supportsDistinct));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isSupportsJoin() {
        return supportsJoin;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSupportsJoin( boolean newSupportsJoin ) {
        boolean oldSupportsJoin = supportsJoin;
        supportsJoin = newSupportsJoin;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__SUPPORTS_JOIN, oldSupportsJoin,
                                                                   supportsJoin));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isSupportsOrderBy() {
        return supportsOrderBy;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSupportsOrderBy( boolean newSupportsOrderBy ) {
        boolean oldSupportsOrderBy = supportsOrderBy;
        supportsOrderBy = newSupportsOrderBy;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__SUPPORTS_ORDER_BY,
                                                                   oldSupportsOrderBy, supportsOrderBy));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isSupportsOuterJoin() {
        return supportsOuterJoin;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSupportsOuterJoin( boolean newSupportsOuterJoin ) {
        boolean oldSupportsOuterJoin = supportsOuterJoin;
        supportsOuterJoin = newSupportsOuterJoin;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN,
                                                                   oldSupportsOuterJoin, supportsOuterJoin));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isSupportsWhereAll() {
        return supportsWhereAll;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSupportsWhereAll( boolean newSupportsWhereAll ) {
        boolean oldSupportsWhereAll = supportsWhereAll;
        supportsWhereAll = newSupportsWhereAll;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__SUPPORTS_WHERE_ALL,
                                                                   oldSupportsWhereAll, supportsWhereAll));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EMap getTags() {
        if (tags == null) {
            tags = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this,
                                 CorePackage.MODEL_ANNOTATION__TAGS);
        }
        return tags;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * Sets the namespace URI to the specified value. The {@link UriValidator} is used to validate the proposed value.
     * 
     * @param theNewNamespaceUri the proposed value
     * @throws MetaMatrixRuntimeException if the proposed value is not valid
     * @generated NOT
     */
    public void setNamespaceUri( String theNewNamespaceUri ) throws MetaMatrixRuntimeException {
        try {
            IStatus status = UriValidator.validate(theNewNamespaceUri);

            if (status.getSeverity() == IStatus.ERROR) {
                throw new MetaMatrixRuntimeException(status.getMessage());
            }
        } catch (RuntimeException theException) {
            String msg = CoreMetamodelPlugin.Util.getString("ModelAnnotationImpl.invalidNamespaceUriMsg", //$NON-NLS-1$
                                                            new Object[] {theNewNamespaceUri, theException.getLocalizedMessage()});
            throw new MetaMatrixRuntimeException(theException, msg);
        }

        setNamespaceUriGen(theNewNamespaceUri);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setNamespaceUriGen( String newNamespaceUri ) {
        String oldNamespaceUri = namespaceUri;
        namespaceUri = newNamespaceUri;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__NAMESPACE_URI, oldNamespaceUri,
                                                                   namespaceUri));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getProducerName() {
        return producerName;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setProducerName( String newProducerName ) {
        String oldProducerName = producerName;
        producerName = newProducerName;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__PRODUCER_NAME, oldProducerName,
                                                                   producerName));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getProducerVersion() {
        return producerVersion;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setProducerVersion( String newProducerVersion ) {
        String oldProducerVersion = producerVersion;
        producerVersion = newProducerVersion;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__PRODUCER_VERSION,
                                                                   oldProducerVersion, producerVersion));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getModelImports() {
        if (modelImports == null) {
            modelImports = new EObjectContainmentWithInverseEList(ModelImport.class, this,
                                                                  CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS,
                                                                  CorePackage.MODEL_IMPORT__MODEL);
        }
        return modelImports;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XPackage getExtensionPackage() {
        if (extensionPackage != null && extensionPackage.eIsProxy()) {
            XPackage oldExtensionPackage = extensionPackage;
            extensionPackage = (XPackage)eResolveProxy((InternalEObject)extensionPackage);
            if (extensionPackage != oldExtensionPackage) {
                if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                                                                           CorePackage.MODEL_ANNOTATION__EXTENSION_PACKAGE,
                                                                           oldExtensionPackage, extensionPackage));
            }
        }
        return extensionPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XPackage basicGetExtensionPackage() {
        return extensionPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setExtensionPackage( XPackage newExtensionPackage ) {
        XPackage oldExtensionPackage = extensionPackage;
        extensionPackage = newExtensionPackage;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   CorePackage.MODEL_ANNOTATION__EXTENSION_PACKAGE,
                                                                   oldExtensionPackage, extensionPackage));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public NotificationChain eInverseAdd( InternalEObject otherEnd,
                                          int featureID,
                                          Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                    // Start customized code
                    // This method should do the normal work ...
                    final NotificationChain result = ((InternalEList)getModelImports()).basicAdd(otherEnd, msgs);
                    // And then add to the maps
                    if (otherEnd instanceof ModelImport) {
                        final ModelImport modelImport = (ModelImport)otherEnd;
                        final String path = modelImport.getPath();
                        if (path != null) {
                            this.modelImportsByPath.put(path, otherEnd);
                        }
                        final String uuid = modelImport.getUuid();
                        if (uuid != null) {
                            this.modelImportsByUuid.put(uuid, otherEnd);
                        }
                    }
                    // And return the result of the original work
                    return result;
                    // End customized code
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
    public NotificationChain eInverseAddGen( InternalEObject otherEnd,
                                             int featureID,
                                             Class baseClass,
                                             NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                    return ((InternalEList)getModelImports()).basicAdd(otherEnd, msgs);
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
                case CorePackage.MODEL_ANNOTATION__TAGS:
                    return ((InternalEList)getTags()).basicRemove(otherEnd, msgs);
                case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                    return ((InternalEList)getModelImports()).basicRemove(otherEnd, msgs);
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
    public Object eGet( EStructuralFeature eFeature,
                        boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.MODEL_ANNOTATION__DESCRIPTION:
                return getDescription();
            case CorePackage.MODEL_ANNOTATION__NAME_IN_SOURCE:
                return getNameInSource();
            case CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI:
                return getPrimaryMetamodelUri();
            case CorePackage.MODEL_ANNOTATION__MODEL_TYPE:
                return getModelType();
            case CorePackage.MODEL_ANNOTATION__MAX_SET_SIZE:
                return new Integer(getMaxSetSize());
            case CorePackage.MODEL_ANNOTATION__VISIBLE:
                return isVisible() ? Boolean.TRUE : Boolean.FALSE;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_DISTINCT:
                return isSupportsDistinct() ? Boolean.TRUE : Boolean.FALSE;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_JOIN:
                return isSupportsJoin() ? Boolean.TRUE : Boolean.FALSE;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_ORDER_BY:
                return isSupportsOrderBy() ? Boolean.TRUE : Boolean.FALSE;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN:
                return isSupportsOuterJoin() ? Boolean.TRUE : Boolean.FALSE;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_WHERE_ALL:
                return isSupportsWhereAll() ? Boolean.TRUE : Boolean.FALSE;
            case CorePackage.MODEL_ANNOTATION__TAGS:
                return getTags();
            case CorePackage.MODEL_ANNOTATION__NAMESPACE_URI:
                return getNamespaceUri();
            case CorePackage.MODEL_ANNOTATION__PRODUCER_NAME:
                return getProducerName();
            case CorePackage.MODEL_ANNOTATION__PRODUCER_VERSION:
                return getProducerVersion();
            case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                return getModelImports();
            case CorePackage.MODEL_ANNOTATION__EXTENSION_PACKAGE:
                if (resolve) return getExtensionPackage();
                return basicGetExtensionPackage();
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
            case CorePackage.MODEL_ANNOTATION__DESCRIPTION:
                setDescription((String)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI:
                setPrimaryMetamodelUri((String)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__MODEL_TYPE:
                setModelType((ModelType)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__MAX_SET_SIZE:
                setMaxSetSize(((Integer)newValue).intValue());
                return;
            case CorePackage.MODEL_ANNOTATION__VISIBLE:
                setVisible(((Boolean)newValue).booleanValue());
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_DISTINCT:
                setSupportsDistinct(((Boolean)newValue).booleanValue());
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_JOIN:
                setSupportsJoin(((Boolean)newValue).booleanValue());
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_ORDER_BY:
                setSupportsOrderBy(((Boolean)newValue).booleanValue());
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN:
                setSupportsOuterJoin(((Boolean)newValue).booleanValue());
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_WHERE_ALL:
                setSupportsWhereAll(((Boolean)newValue).booleanValue());
                return;
            case CorePackage.MODEL_ANNOTATION__TAGS:
                getTags().clear();
                getTags().addAll((Collection)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__NAMESPACE_URI:
                setNamespaceUri((String)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__PRODUCER_NAME:
                setProducerName((String)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__PRODUCER_VERSION:
                setProducerVersion((String)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                getModelImports().clear();
                getModelImports().addAll((Collection)newValue);
                return;
            case CorePackage.MODEL_ANNOTATION__EXTENSION_PACKAGE:
                setExtensionPackage((XPackage)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public void eUnset( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.MODEL_ANNOTATION__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI:
                // setPrimaryMetamodelUri(PRIMARY_METAMODEL_URI_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__MODEL_TYPE:
                // setModelType(MODEL_TYPE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__MAX_SET_SIZE:
                setMaxSetSize(MAX_SET_SIZE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__VISIBLE:
                setVisible(VISIBLE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_DISTINCT:
                setSupportsDistinct(SUPPORTS_DISTINCT_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_JOIN:
                setSupportsJoin(SUPPORTS_JOIN_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_ORDER_BY:
                setSupportsOrderBy(SUPPORTS_ORDER_BY_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN:
                setSupportsOuterJoin(SUPPORTS_OUTER_JOIN_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_WHERE_ALL:
                setSupportsWhereAll(SUPPORTS_WHERE_ALL_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__TAGS:
                getTags().clear();
                return;
            case CorePackage.MODEL_ANNOTATION__NAMESPACE_URI:
                setNamespaceUri(NAMESPACE_URI_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__PRODUCER_NAME:
                setProducerName(PRODUCER_NAME_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__PRODUCER_VERSION:
                setProducerVersion(PRODUCER_VERSION_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                getModelImports().clear();
                return;
            case CorePackage.MODEL_ANNOTATION__EXTENSION_PACKAGE:
                setExtensionPackage((XPackage)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void eUnsetGen( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.MODEL_ANNOTATION__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI:
                setPrimaryMetamodelUri(PRIMARY_METAMODEL_URI_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__MODEL_TYPE:
                setModelType(MODEL_TYPE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__MAX_SET_SIZE:
                setMaxSetSize(MAX_SET_SIZE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__VISIBLE:
                setVisible(VISIBLE_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_DISTINCT:
                setSupportsDistinct(SUPPORTS_DISTINCT_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_JOIN:
                setSupportsJoin(SUPPORTS_JOIN_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_ORDER_BY:
                setSupportsOrderBy(SUPPORTS_ORDER_BY_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN:
                setSupportsOuterJoin(SUPPORTS_OUTER_JOIN_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_WHERE_ALL:
                setSupportsWhereAll(SUPPORTS_WHERE_ALL_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__TAGS:
                getTags().clear();
                return;
            case CorePackage.MODEL_ANNOTATION__NAMESPACE_URI:
                setNamespaceUri(NAMESPACE_URI_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__PRODUCER_NAME:
                setProducerName(PRODUCER_NAME_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__PRODUCER_VERSION:
                setProducerVersion(PRODUCER_VERSION_EDEFAULT);
                return;
            case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                getModelImports().clear();
                return;
            case CorePackage.MODEL_ANNOTATION__EXTENSION_PACKAGE:
                setExtensionPackage((XPackage)null);
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
            case CorePackage.MODEL_ANNOTATION__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
            case CorePackage.MODEL_ANNOTATION__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI:
                return PRIMARY_METAMODEL_URI_EDEFAULT == null ? primaryMetamodelUri != null : !PRIMARY_METAMODEL_URI_EDEFAULT.equals(primaryMetamodelUri);
            case CorePackage.MODEL_ANNOTATION__MODEL_TYPE:
                return modelType != MODEL_TYPE_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__MAX_SET_SIZE:
                return maxSetSize != MAX_SET_SIZE_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__VISIBLE:
                return visible != VISIBLE_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_DISTINCT:
                return supportsDistinct != SUPPORTS_DISTINCT_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_JOIN:
                return supportsJoin != SUPPORTS_JOIN_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_ORDER_BY:
                return supportsOrderBy != SUPPORTS_ORDER_BY_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN:
                return supportsOuterJoin != SUPPORTS_OUTER_JOIN_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_WHERE_ALL:
                return supportsWhereAll != SUPPORTS_WHERE_ALL_EDEFAULT;
            case CorePackage.MODEL_ANNOTATION__TAGS:
                return tags != null && !tags.isEmpty();
            case CorePackage.MODEL_ANNOTATION__NAMESPACE_URI:
                return NAMESPACE_URI_EDEFAULT == null ? namespaceUri != null : !NAMESPACE_URI_EDEFAULT.equals(namespaceUri);
            case CorePackage.MODEL_ANNOTATION__PRODUCER_NAME:
                return PRODUCER_NAME_EDEFAULT == null ? producerName != null : !PRODUCER_NAME_EDEFAULT.equals(producerName);
            case CorePackage.MODEL_ANNOTATION__PRODUCER_VERSION:
                return PRODUCER_VERSION_EDEFAULT == null ? producerVersion != null : !PRODUCER_VERSION_EDEFAULT.equals(producerVersion);
            case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
                return modelImports != null && !modelImports.isEmpty();
            case CorePackage.MODEL_ANNOTATION__EXTENSION_PACKAGE:
                return extensionPackage != null;
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
        result.append(" (description: "); //$NON-NLS-1$
        result.append(description);
        result.append(", nameInSource: "); //$NON-NLS-1$
        result.append(nameInSource);
        result.append(", primaryMetamodelUri: "); //$NON-NLS-1$
        result.append(primaryMetamodelUri);
        result.append(", modelType: "); //$NON-NLS-1$
        result.append(modelType);
        result.append(", maxSetSize: "); //$NON-NLS-1$
        result.append(maxSetSize);
        result.append(", visible: "); //$NON-NLS-1$
        result.append(visible);
        result.append(", supportsDistinct: "); //$NON-NLS-1$
        result.append(supportsDistinct);
        result.append(", supportsJoin: "); //$NON-NLS-1$
        result.append(supportsJoin);
        result.append(", supportsOrderBy: "); //$NON-NLS-1$
        result.append(supportsOrderBy);
        result.append(", supportsOuterJoin: "); //$NON-NLS-1$
        result.append(supportsOuterJoin);
        result.append(", supportsWhereAll: "); //$NON-NLS-1$
        result.append(supportsWhereAll);
        result.append(", namespaceUri: "); //$NON-NLS-1$
        result.append(namespaceUri);
        result.append(", ProducerName: "); //$NON-NLS-1$
        result.append(producerName);
        result.append(", ProducerVersion: "); //$NON-NLS-1$
        result.append(producerVersion);
        result.append(')');
        return result.toString();
    }

} // ModelAnnotationImpl
