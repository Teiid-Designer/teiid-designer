/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.jdbc.impl;

import com.metamatrix.modeler.jdbc.CaseConversion;
import com.metamatrix.modeler.jdbc.JdbcImportOptions;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPackage;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.SourceNames;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Import Settings</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getSource <em>Source</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getOptions <em>Options</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#isCreateCatalogsInModel <em>Create Catalogs In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#isCreateSchemasInModel <em>Create Schemas In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getConvertCaseInModel <em>Convert Case In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getGenerateSourceNamesInModel <em>Generate Source Names In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getIncludedCatalogPaths <em>Included Catalog Paths</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getIncludedSchemaPaths <em>Included Schema Paths</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getExcludedObjectPaths <em>Excluded Object Paths</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#isIncludeForeignKeys <em>Include Foreign Keys</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#isIncludeIndexes <em>Include Indexes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#isIncludeProcedures <em>Include Procedures</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#isIncludeApproximateIndexes <em>Include Approximate Indexes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#isIncludeUniqueIndexes <em>Include Unique Indexes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcImportSettingsImpl#getIncludedTableTypes <em>Included Table Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JdbcImportSettingsImpl extends EObjectImpl implements JdbcImportSettings {
    /**
     * The cached value of the '{@link #getOptions() <em>Options</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOptions()
     * @generated
     * @ordered
     */
    protected EList options = null;

    /**
     * The default value of the '{@link #isCreateCatalogsInModel() <em>Create Catalogs In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCreateCatalogsInModel()
     * @generated
     * @ordered
     */
    protected static final boolean CREATE_CATALOGS_IN_MODEL_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isCreateCatalogsInModel() <em>Create Catalogs In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCreateCatalogsInModel()
     * @generated
     * @ordered
     */
    protected boolean createCatalogsInModel = CREATE_CATALOGS_IN_MODEL_EDEFAULT;

    /**
     * The default value of the '{@link #isCreateSchemasInModel() <em>Create Schemas In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCreateSchemasInModel()
     * @generated
     * @ordered
     */
    protected static final boolean CREATE_SCHEMAS_IN_MODEL_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isCreateSchemasInModel() <em>Create Schemas In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCreateSchemasInModel()
     * @generated
     * @ordered
     */
    protected boolean createSchemasInModel = CREATE_SCHEMAS_IN_MODEL_EDEFAULT;

    /**
     * The default value of the '{@link #getConvertCaseInModel() <em>Convert Case In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConvertCaseInModel()
     * @generated
     * @ordered
     */
    protected static final CaseConversion CONVERT_CASE_IN_MODEL_EDEFAULT = CaseConversion.NONE_LITERAL;

    /**
     * The cached value of the '{@link #getConvertCaseInModel() <em>Convert Case In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConvertCaseInModel()
     * @generated
     * @ordered
     */
    protected CaseConversion convertCaseInModel = CONVERT_CASE_IN_MODEL_EDEFAULT;

    /**
     * The default value of the '{@link #getGenerateSourceNamesInModel() <em>Generate Source Names In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getGenerateSourceNamesInModel()
     * @generated
     * @ordered
     */
    protected static final SourceNames GENERATE_SOURCE_NAMES_IN_MODEL_EDEFAULT = SourceNames.UNQUALIFIED_LITERAL;

    /**
     * The cached value of the '{@link #getGenerateSourceNamesInModel() <em>Generate Source Names In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getGenerateSourceNamesInModel()
     * @generated
     * @ordered
     */
    protected SourceNames generateSourceNamesInModel = GENERATE_SOURCE_NAMES_IN_MODEL_EDEFAULT;

    /**
     * The cached value of the '{@link #getIncludedCatalogPaths() <em>Included Catalog Paths</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIncludedCatalogPaths()
     * @generated
     * @ordered
     */
    protected EList includedCatalogPaths = null;

    /**
     * The cached value of the '{@link #getIncludedSchemaPaths() <em>Included Schema Paths</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIncludedSchemaPaths()
     * @generated
     * @ordered
     */
    protected EList includedSchemaPaths = null;

    /**
     * The cached value of the '{@link #getExcludedObjectPaths() <em>Excluded Object Paths</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExcludedObjectPaths()
     * @generated
     * @ordered
     */
    protected EList excludedObjectPaths = null;

    /**
     * The default value of the '{@link #isIncludeForeignKeys() <em>Include Foreign Keys</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeForeignKeys()
     * @generated
     * @ordered
     */
    protected static final boolean INCLUDE_FOREIGN_KEYS_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isIncludeForeignKeys() <em>Include Foreign Keys</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeForeignKeys()
     * @generated
     * @ordered
     */
    protected boolean includeForeignKeys = INCLUDE_FOREIGN_KEYS_EDEFAULT;

    /**
     * The default value of the '{@link #isIncludeIndexes() <em>Include Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeIndexes()
     * @generated
     * @ordered
     */
    protected static final boolean INCLUDE_INDEXES_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isIncludeIndexes() <em>Include Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeIndexes()
     * @generated
     * @ordered
     */
    protected boolean includeIndexes = INCLUDE_INDEXES_EDEFAULT;

    /**
     * The default value of the '{@link #isIncludeProcedures() <em>Include Procedures</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeProcedures()
     * @generated
     * @ordered
     */
    protected static final boolean INCLUDE_PROCEDURES_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIncludeProcedures() <em>Include Procedures</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeProcedures()
     * @generated
     * @ordered
     */
    protected boolean includeProcedures = INCLUDE_PROCEDURES_EDEFAULT;

    /**
     * The default value of the '{@link #isIncludeApproximateIndexes() <em>Include Approximate Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeApproximateIndexes()
     * @generated
     * @ordered
     */
    protected static final boolean INCLUDE_APPROXIMATE_INDEXES_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isIncludeApproximateIndexes() <em>Include Approximate Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeApproximateIndexes()
     * @generated
     * @ordered
     */
    protected boolean includeApproximateIndexes = INCLUDE_APPROXIMATE_INDEXES_EDEFAULT;

    /**
     * The default value of the '{@link #isIncludeUniqueIndexes() <em>Include Unique Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeUniqueIndexes()
     * @generated
     * @ordered
     */
    protected static final boolean INCLUDE_UNIQUE_INDEXES_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIncludeUniqueIndexes() <em>Include Unique Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIncludeUniqueIndexes()
     * @generated
     * @ordered
     */
    protected boolean includeUniqueIndexes = INCLUDE_UNIQUE_INDEXES_EDEFAULT;

    /**
     * The cached value of the '{@link #getIncludedTableTypes() <em>Included Table Types</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIncludedTableTypes()
     * @generated
     * @ordered
     */
    protected EList includedTableTypes = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected JdbcImportSettingsImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JdbcPackage.eINSTANCE.getJdbcImportSettings();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcSource getSource() {
        if (eContainerFeatureID != JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE) return null;
        return (JdbcSource)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSource(JdbcSource newSource) {
        if (newSource != eContainer || (eContainerFeatureID != JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE && newSource != null)) {
            if (EcoreUtil.isAncestor(this, newSource))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSource != null)
                msgs = ((InternalEObject)newSource).eInverseAdd(this, JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS, JdbcSource.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSource, JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE, newSource, newSource));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getOptions() {
        if (options == null) {
            options = new EObjectContainmentWithInverseEList(JdbcImportOptions.class, this, JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS, JdbcPackage.JDBC_IMPORT_OPTIONS__IMPORT_SETTINGS);
        }
        return options;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isCreateCatalogsInModel() {
        return createCatalogsInModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCreateCatalogsInModel(boolean newCreateCatalogsInModel) {
        boolean oldCreateCatalogsInModel = createCatalogsInModel;
        createCatalogsInModel = newCreateCatalogsInModel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL, oldCreateCatalogsInModel, createCatalogsInModel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isCreateSchemasInModel() {
        return createSchemasInModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCreateSchemasInModel(boolean newCreateSchemasInModel) {
        boolean oldCreateSchemasInModel = createSchemasInModel;
        createSchemasInModel = newCreateSchemasInModel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL, oldCreateSchemasInModel, createSchemasInModel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CaseConversion getConvertCaseInModel() {
        return convertCaseInModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setConvertCaseInModel(CaseConversion newConvertCaseInModel) {
        CaseConversion oldConvertCaseInModel = convertCaseInModel;
        convertCaseInModel = newConvertCaseInModel == null ? CONVERT_CASE_IN_MODEL_EDEFAULT : newConvertCaseInModel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL, oldConvertCaseInModel, convertCaseInModel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SourceNames getGenerateSourceNamesInModel() {
        return generateSourceNamesInModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setGenerateSourceNamesInModel(SourceNames newGenerateSourceNamesInModel) {
        SourceNames oldGenerateSourceNamesInModel = generateSourceNamesInModel;
        generateSourceNamesInModel = newGenerateSourceNamesInModel == null ? GENERATE_SOURCE_NAMES_IN_MODEL_EDEFAULT : newGenerateSourceNamesInModel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL, oldGenerateSourceNamesInModel, generateSourceNamesInModel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getIncludedCatalogPaths() {
        if (includedCatalogPaths == null) {
            includedCatalogPaths = new EDataTypeUniqueEList(String.class, this, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS);
        }
        return includedCatalogPaths;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getIncludedSchemaPaths() {
        if (includedSchemaPaths == null) {
            includedSchemaPaths = new EDataTypeUniqueEList(String.class, this, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS);
        }
        return includedSchemaPaths;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getExcludedObjectPaths() {
        if (excludedObjectPaths == null) {
            excludedObjectPaths = new EDataTypeUniqueEList(String.class, this, JdbcPackage.JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS);
        }
        return excludedObjectPaths;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIncludeForeignKeys() {
        return includeForeignKeys;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIncludeForeignKeys(boolean newIncludeForeignKeys) {
        boolean oldIncludeForeignKeys = includeForeignKeys;
        includeForeignKeys = newIncludeForeignKeys;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS, oldIncludeForeignKeys, includeForeignKeys));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIncludeIndexes() {
        return includeIndexes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIncludeIndexes(boolean newIncludeIndexes) {
        boolean oldIncludeIndexes = includeIndexes;
        includeIndexes = newIncludeIndexes;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES, oldIncludeIndexes, includeIndexes));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIncludeProcedures() {
        return includeProcedures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIncludeProcedures(boolean newIncludeProcedures) {
        boolean oldIncludeProcedures = includeProcedures;
        includeProcedures = newIncludeProcedures;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES, oldIncludeProcedures, includeProcedures));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIncludeApproximateIndexes() {
        return includeApproximateIndexes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIncludeApproximateIndexes(boolean newIncludeApproximateIndexes) {
        boolean oldIncludeApproximateIndexes = includeApproximateIndexes;
        includeApproximateIndexes = newIncludeApproximateIndexes;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES, oldIncludeApproximateIndexes, includeApproximateIndexes));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIncludeUniqueIndexes() {
        return includeUniqueIndexes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIncludeUniqueIndexes(boolean newIncludeUniqueIndexes) {
        boolean oldIncludeUniqueIndexes = includeUniqueIndexes;
        includeUniqueIndexes = newIncludeUniqueIndexes;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES, oldIncludeUniqueIndexes, includeUniqueIndexes));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getIncludedTableTypes() {
        if (includedTableTypes == null) {
            includedTableTypes = new EDataTypeUniqueEList(String.class, this, JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES);
        }
        return includedTableTypes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE, msgs);
                case JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS:
                    return ((InternalEList)getOptions()).basicAdd(otherEnd, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE:
                    return eBasicSetContainer(null, JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE, msgs);
                case JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS:
                    return ((InternalEList)getOptions()).basicRemove(otherEnd, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE:
                    return eContainer.eInverseRemove(this, JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS, JdbcSource.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE:
                return getSource();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS:
                return getOptions();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL:
                return isCreateCatalogsInModel() ? Boolean.TRUE : Boolean.FALSE;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL:
                return isCreateSchemasInModel() ? Boolean.TRUE : Boolean.FALSE;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL:
                return getConvertCaseInModel();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL:
                return getGenerateSourceNamesInModel();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS:
                return getIncludedCatalogPaths();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS:
                return getIncludedSchemaPaths();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS:
                return getExcludedObjectPaths();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS:
                return isIncludeForeignKeys() ? Boolean.TRUE : Boolean.FALSE;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES:
                return isIncludeIndexes() ? Boolean.TRUE : Boolean.FALSE;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES:
                return isIncludeProcedures() ? Boolean.TRUE : Boolean.FALSE;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES:
                return isIncludeApproximateIndexes() ? Boolean.TRUE : Boolean.FALSE;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES:
                return isIncludeUniqueIndexes() ? Boolean.TRUE : Boolean.FALSE;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES:
                return getIncludedTableTypes();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE:
                setSource((JdbcSource)newValue);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS:
                getOptions().clear();
                getOptions().addAll((Collection)newValue);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL:
                setCreateCatalogsInModel(((Boolean)newValue).booleanValue());
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL:
                setCreateSchemasInModel(((Boolean)newValue).booleanValue());
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL:
                setConvertCaseInModel((CaseConversion)newValue);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL:
                setGenerateSourceNamesInModel((SourceNames)newValue);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS:
                getIncludedCatalogPaths().clear();
                getIncludedCatalogPaths().addAll((Collection)newValue);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS:
                getIncludedSchemaPaths().clear();
                getIncludedSchemaPaths().addAll((Collection)newValue);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS:
                getExcludedObjectPaths().clear();
                getExcludedObjectPaths().addAll((Collection)newValue);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS:
                setIncludeForeignKeys(((Boolean)newValue).booleanValue());
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES:
                setIncludeIndexes(((Boolean)newValue).booleanValue());
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES:
                setIncludeProcedures(((Boolean)newValue).booleanValue());
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES:
                setIncludeApproximateIndexes(((Boolean)newValue).booleanValue());
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES:
                setIncludeUniqueIndexes(((Boolean)newValue).booleanValue());
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES:
                getIncludedTableTypes().clear();
                getIncludedTableTypes().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE:
                setSource((JdbcSource)null);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS:
                getOptions().clear();
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL:
                setCreateCatalogsInModel(CREATE_CATALOGS_IN_MODEL_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL:
                setCreateSchemasInModel(CREATE_SCHEMAS_IN_MODEL_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL:
                setConvertCaseInModel(CONVERT_CASE_IN_MODEL_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL:
                setGenerateSourceNamesInModel(GENERATE_SOURCE_NAMES_IN_MODEL_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS:
                getIncludedCatalogPaths().clear();
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS:
                getIncludedSchemaPaths().clear();
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS:
                getExcludedObjectPaths().clear();
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS:
                setIncludeForeignKeys(INCLUDE_FOREIGN_KEYS_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES:
                setIncludeIndexes(INCLUDE_INDEXES_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES:
                setIncludeProcedures(INCLUDE_PROCEDURES_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES:
                setIncludeApproximateIndexes(INCLUDE_APPROXIMATE_INDEXES_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES:
                setIncludeUniqueIndexes(INCLUDE_UNIQUE_INDEXES_EDEFAULT);
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES:
                getIncludedTableTypes().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE:
                return getSource() != null;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS:
                return options != null && !options.isEmpty();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL:
                return createCatalogsInModel != CREATE_CATALOGS_IN_MODEL_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL:
                return createSchemasInModel != CREATE_SCHEMAS_IN_MODEL_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL:
                return convertCaseInModel != CONVERT_CASE_IN_MODEL_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL:
                return generateSourceNamesInModel != GENERATE_SOURCE_NAMES_IN_MODEL_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS:
                return includedCatalogPaths != null && !includedCatalogPaths.isEmpty();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS:
                return includedSchemaPaths != null && !includedSchemaPaths.isEmpty();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS:
                return excludedObjectPaths != null && !excludedObjectPaths.isEmpty();
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS:
                return includeForeignKeys != INCLUDE_FOREIGN_KEYS_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES:
                return includeIndexes != INCLUDE_INDEXES_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES:
                return includeProcedures != INCLUDE_PROCEDURES_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES:
                return includeApproximateIndexes != INCLUDE_APPROXIMATE_INDEXES_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES:
                return includeUniqueIndexes != INCLUDE_UNIQUE_INDEXES_EDEFAULT;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES:
                return includedTableTypes != null && !includedTableTypes.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (createCatalogsInModel: "); //$NON-NLS-1$
        result.append(createCatalogsInModel);
        result.append(", createSchemasInModel: "); //$NON-NLS-1$
        result.append(createSchemasInModel);
        result.append(", convertCaseInModel: "); //$NON-NLS-1$
        result.append(convertCaseInModel);
        result.append(", generateSourceNamesInModel: "); //$NON-NLS-1$
        result.append(generateSourceNamesInModel);
        result.append(", includedCatalogPaths: "); //$NON-NLS-1$
        result.append(includedCatalogPaths);
        result.append(", includedSchemaPaths: "); //$NON-NLS-1$
        result.append(includedSchemaPaths);
        result.append(", excludedObjectPaths: "); //$NON-NLS-1$
        result.append(excludedObjectPaths);
        result.append(", includeForeignKeys: "); //$NON-NLS-1$
        result.append(includeForeignKeys);
        result.append(", includeIndexes: "); //$NON-NLS-1$
        result.append(includeIndexes);
        result.append(", includeProcedures: "); //$NON-NLS-1$
        result.append(includeProcedures);
        result.append(", includeApproximateIndexes: "); //$NON-NLS-1$
        result.append(includeApproximateIndexes);
        result.append(", includeUniqueIndexes: "); //$NON-NLS-1$
        result.append(includeUniqueIndexes);
        result.append(", includedTableTypes: "); //$NON-NLS-1$
        result.append(includedTableTypes);
        result.append(')');
        return result.toString();
    }

} //JdbcImportSettingsImpl
