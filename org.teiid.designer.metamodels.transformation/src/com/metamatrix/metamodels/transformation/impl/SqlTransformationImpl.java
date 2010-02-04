/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.impl.MappingHelperImpl;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sql Transformation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#getSelectSql <em>Select Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#getInsertSql <em>Insert Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#getUpdateSql <em>Update Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#getDeleteSql <em>Delete Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#isInsertAllowed <em>Insert Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#isUpdateAllowed <em>Update Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#isDeleteAllowed <em>Delete Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#isOutputLocked <em>Output Locked</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#isInsertSqlDefault <em>Insert Sql Default</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#isUpdateSqlDefault <em>Update Sql Default</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#isDeleteSqlDefault <em>Delete Sql Default</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl#getAliases <em>Aliases</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SqlTransformationImpl extends MappingHelperImpl implements SqlTransformation {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getSelectSql() <em>Select Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSelectSql()
     * @generated
     * @ordered
     */
    protected static final String SELECT_SQL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getSelectSql() <em>Select Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSelectSql()
     * @generated
     * @ordered
     */
    protected String selectSql = SELECT_SQL_EDEFAULT;

    /**
     * The default value of the '{@link #getInsertSql() <em>Insert Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInsertSql()
     * @generated
     * @ordered
     */
    protected static final String INSERT_SQL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInsertSql() <em>Insert Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInsertSql()
     * @generated
     * @ordered
     */
    protected String insertSql = INSERT_SQL_EDEFAULT;

    /**
     * The default value of the '{@link #getUpdateSql() <em>Update Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpdateSql()
     * @generated
     * @ordered
     */
    protected static final String UPDATE_SQL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUpdateSql() <em>Update Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpdateSql()
     * @generated
     * @ordered
     */
    protected String updateSql = UPDATE_SQL_EDEFAULT;

    /**
     * The default value of the '{@link #getDeleteSql() <em>Delete Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeleteSql()
     * @generated
     * @ordered
     */
    protected static final String DELETE_SQL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDeleteSql() <em>Delete Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeleteSql()
     * @generated
     * @ordered
     */
    protected String deleteSql = DELETE_SQL_EDEFAULT;

    /**
     * The default value of the '{@link #isInsertAllowed() <em>Insert Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isInsertAllowed()
     * @generated
     * @ordered
     */
    protected static final boolean INSERT_ALLOWED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isInsertAllowed() <em>Insert Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isInsertAllowed()
     * @generated
     * @ordered
     */
    protected boolean insertAllowed = INSERT_ALLOWED_EDEFAULT;

    /**
     * The default value of the '{@link #isUpdateAllowed() <em>Update Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUpdateAllowed()
     * @generated
     * @ordered
     */
    protected static final boolean UPDATE_ALLOWED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isUpdateAllowed() <em>Update Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUpdateAllowed()
     * @generated
     * @ordered
     */
    protected boolean updateAllowed = UPDATE_ALLOWED_EDEFAULT;

    /**
     * The default value of the '{@link #isDeleteAllowed() <em>Delete Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeleteAllowed()
     * @generated
     * @ordered
     */
    protected static final boolean DELETE_ALLOWED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isDeleteAllowed() <em>Delete Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeleteAllowed()
     * @generated
     * @ordered
     */
    protected boolean deleteAllowed = DELETE_ALLOWED_EDEFAULT;

    /**
     * The default value of the '{@link #isOutputLocked() <em>Output Locked</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isOutputLocked()
     * @generated
     * @ordered
     */
    protected static final boolean OUTPUT_LOCKED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isOutputLocked() <em>Output Locked</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isOutputLocked()
     * @generated
     * @ordered
     */
    protected boolean outputLocked = OUTPUT_LOCKED_EDEFAULT;

    /**
     * The default value of the '{@link #isInsertSqlDefault() <em>Insert Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isInsertSqlDefault()
     * @generated
     * @ordered
     */
    protected static final boolean INSERT_SQL_DEFAULT_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isInsertSqlDefault() <em>Insert Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isInsertSqlDefault()
     * @generated
     * @ordered
     */
    protected boolean insertSqlDefault = INSERT_SQL_DEFAULT_EDEFAULT;

    /**
     * The default value of the '{@link #isUpdateSqlDefault() <em>Update Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUpdateSqlDefault()
     * @generated
     * @ordered
     */
    protected static final boolean UPDATE_SQL_DEFAULT_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isUpdateSqlDefault() <em>Update Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUpdateSqlDefault()
     * @generated
     * @ordered
     */
    protected boolean updateSqlDefault = UPDATE_SQL_DEFAULT_EDEFAULT;

    /**
     * The default value of the '{@link #isDeleteSqlDefault() <em>Delete Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeleteSqlDefault()
     * @generated
     * @ordered
     */
    protected static final boolean DELETE_SQL_DEFAULT_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isDeleteSqlDefault() <em>Delete Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeleteSqlDefault()
     * @generated
     * @ordered
     */
    protected boolean deleteSqlDefault = DELETE_SQL_DEFAULT_EDEFAULT;

    /**
     * The cached value of the '{@link #getAliases() <em>Aliases</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAliases()
     * @generated
     * @ordered
     */
    protected EList aliases = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SqlTransformationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getSqlTransformation();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getSelectSql() {
        return selectSql;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSelectSql(String newSelectSql) {
        String oldSelectSql = selectSql;
        selectSql = newSelectSql;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL, oldSelectSql, selectSql));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getInsertSql() {
        return insertSql;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInsertSql(String newInsertSql) {
        String oldInsertSql = insertSql;
        insertSql = newInsertSql;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL, oldInsertSql, insertSql));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUpdateSql() {
        return updateSql;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUpdateSql(String newUpdateSql) {
        String oldUpdateSql = updateSql;
        updateSql = newUpdateSql;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL, oldUpdateSql, updateSql));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDeleteSql() {
        return deleteSql;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDeleteSql(String newDeleteSql) {
        String oldDeleteSql = deleteSql;
        deleteSql = newDeleteSql;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL, oldDeleteSql, deleteSql));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isInsertAllowed() {
        return insertAllowed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInsertAllowed(boolean newInsertAllowed) {
        boolean oldInsertAllowed = insertAllowed;
        insertAllowed = newInsertAllowed;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__INSERT_ALLOWED, oldInsertAllowed, insertAllowed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isUpdateAllowed() {
        return updateAllowed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUpdateAllowed(boolean newUpdateAllowed) {
        boolean oldUpdateAllowed = updateAllowed;
        updateAllowed = newUpdateAllowed;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__UPDATE_ALLOWED, oldUpdateAllowed, updateAllowed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDeleteAllowed(boolean newDeleteAllowed) {
        boolean oldDeleteAllowed = deleteAllowed;
        deleteAllowed = newDeleteAllowed;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__DELETE_ALLOWED, oldDeleteAllowed, deleteAllowed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isOutputLocked() {
        return outputLocked;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOutputLocked(boolean newOutputLocked) {
        boolean oldOutputLocked = outputLocked;
        outputLocked = newOutputLocked;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__OUTPUT_LOCKED, oldOutputLocked, outputLocked));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isInsertSqlDefault() {
        return insertSqlDefault;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInsertSqlDefault(boolean newInsertSqlDefault) {
        boolean oldInsertSqlDefault = insertSqlDefault;
        insertSqlDefault = newInsertSqlDefault;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL_DEFAULT, oldInsertSqlDefault, insertSqlDefault));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isUpdateSqlDefault() {
        return updateSqlDefault;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUpdateSqlDefault(boolean newUpdateSqlDefault) {
        boolean oldUpdateSqlDefault = updateSqlDefault;
        updateSqlDefault = newUpdateSqlDefault;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT, oldUpdateSqlDefault, updateSqlDefault));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isDeleteSqlDefault() {
        return deleteSqlDefault;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDeleteSqlDefault(boolean newDeleteSqlDefault) {
        boolean oldDeleteSqlDefault = deleteSqlDefault;
        deleteSqlDefault = newDeleteSqlDefault;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL_DEFAULT, oldDeleteSqlDefault, deleteSqlDefault));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getAliases() {
        if (aliases == null) {
            aliases = new EObjectContainmentWithInverseEList(SqlAlias.class, this, TransformationPackage.SQL_TRANSFORMATION__ALIASES, TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION);
        }
        return aliases;
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
                case TransformationPackage.SQL_TRANSFORMATION__MAPPER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.SQL_TRANSFORMATION__MAPPER, msgs);
                case TransformationPackage.SQL_TRANSFORMATION__NESTED_IN:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.SQL_TRANSFORMATION__NESTED_IN, msgs);
                case TransformationPackage.SQL_TRANSFORMATION__NESTED:
                    return ((InternalEList)getNested()).basicAdd(otherEnd, msgs);
                case TransformationPackage.SQL_TRANSFORMATION__ALIASES:
                    return ((InternalEList)getAliases()).basicAdd(otherEnd, msgs);
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
                case TransformationPackage.SQL_TRANSFORMATION__MAPPER:
                    return eBasicSetContainer(null, TransformationPackage.SQL_TRANSFORMATION__MAPPER, msgs);
                case TransformationPackage.SQL_TRANSFORMATION__NESTED_IN:
                    return eBasicSetContainer(null, TransformationPackage.SQL_TRANSFORMATION__NESTED_IN, msgs);
                case TransformationPackage.SQL_TRANSFORMATION__NESTED:
                    return ((InternalEList)getNested()).basicRemove(otherEnd, msgs);
                case TransformationPackage.SQL_TRANSFORMATION__ALIASES:
                    return ((InternalEList)getAliases()).basicRemove(otherEnd, msgs);
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
                case TransformationPackage.SQL_TRANSFORMATION__MAPPER:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING__HELPER, Mapping.class, msgs);
                case TransformationPackage.SQL_TRANSFORMATION__NESTED_IN:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING_HELPER__NESTED, MappingHelper.class, msgs);
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
            case TransformationPackage.SQL_TRANSFORMATION__MAPPER:
                return getMapper();
            case TransformationPackage.SQL_TRANSFORMATION__HELPED_OBJECT:
                if (resolve) return getHelpedObject();
                return basicGetHelpedObject();
            case TransformationPackage.SQL_TRANSFORMATION__NESTED_IN:
                return getNestedIn();
            case TransformationPackage.SQL_TRANSFORMATION__NESTED:
                return getNested();
            case TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL:
                return getSelectSql();
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL:
                return getInsertSql();
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL:
                return getUpdateSql();
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL:
                return getDeleteSql();
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_ALLOWED:
                return isInsertAllowed() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_ALLOWED:
                return isUpdateAllowed() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_ALLOWED:
                return isDeleteAllowed() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.SQL_TRANSFORMATION__OUTPUT_LOCKED:
                return isOutputLocked() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL_DEFAULT:
                return isInsertSqlDefault() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT:
                return isUpdateSqlDefault() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL_DEFAULT:
                return isDeleteSqlDefault() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.SQL_TRANSFORMATION__ALIASES:
                return getAliases();
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
            case TransformationPackage.SQL_TRANSFORMATION__MAPPER:
                setMapper((Mapping)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__HELPED_OBJECT:
                setHelpedObject((EObject)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__NESTED_IN:
                setNestedIn((MappingHelper)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__NESTED:
                getNested().clear();
                getNested().addAll((Collection)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL:
                setSelectSql((String)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL:
                setInsertSql((String)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL:
                setUpdateSql((String)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL:
                setDeleteSql((String)newValue);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_ALLOWED:
                setInsertAllowed(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_ALLOWED:
                setUpdateAllowed(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_ALLOWED:
                setDeleteAllowed(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.SQL_TRANSFORMATION__OUTPUT_LOCKED:
                setOutputLocked(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL_DEFAULT:
                setInsertSqlDefault(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT:
                setUpdateSqlDefault(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL_DEFAULT:
                setDeleteSqlDefault(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.SQL_TRANSFORMATION__ALIASES:
                getAliases().clear();
                getAliases().addAll((Collection)newValue);
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
            case TransformationPackage.SQL_TRANSFORMATION__MAPPER:
                setMapper((Mapping)null);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__HELPED_OBJECT:
                setHelpedObject((EObject)null);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__NESTED_IN:
                setNestedIn((MappingHelper)null);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__NESTED:
                getNested().clear();
                return;
            case TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL:
                setSelectSql(SELECT_SQL_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL:
                setInsertSql(INSERT_SQL_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL:
                setUpdateSql(UPDATE_SQL_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL:
                setDeleteSql(DELETE_SQL_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_ALLOWED:
                setInsertAllowed(INSERT_ALLOWED_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_ALLOWED:
                setUpdateAllowed(UPDATE_ALLOWED_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_ALLOWED:
                setDeleteAllowed(DELETE_ALLOWED_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__OUTPUT_LOCKED:
                setOutputLocked(OUTPUT_LOCKED_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL_DEFAULT:
                setInsertSqlDefault(INSERT_SQL_DEFAULT_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT:
                setUpdateSqlDefault(UPDATE_SQL_DEFAULT_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL_DEFAULT:
                setDeleteSqlDefault(DELETE_SQL_DEFAULT_EDEFAULT);
                return;
            case TransformationPackage.SQL_TRANSFORMATION__ALIASES:
                getAliases().clear();
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
            case TransformationPackage.SQL_TRANSFORMATION__MAPPER:
                return getMapper() != null;
            case TransformationPackage.SQL_TRANSFORMATION__HELPED_OBJECT:
                return helpedObject != null;
            case TransformationPackage.SQL_TRANSFORMATION__NESTED_IN:
                return getNestedIn() != null;
            case TransformationPackage.SQL_TRANSFORMATION__NESTED:
                return nested != null && !nested.isEmpty();
            case TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL:
                return SELECT_SQL_EDEFAULT == null ? selectSql != null : !SELECT_SQL_EDEFAULT.equals(selectSql);
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL:
                return INSERT_SQL_EDEFAULT == null ? insertSql != null : !INSERT_SQL_EDEFAULT.equals(insertSql);
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL:
                return UPDATE_SQL_EDEFAULT == null ? updateSql != null : !UPDATE_SQL_EDEFAULT.equals(updateSql);
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL:
                return DELETE_SQL_EDEFAULT == null ? deleteSql != null : !DELETE_SQL_EDEFAULT.equals(deleteSql);
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_ALLOWED:
                return insertAllowed != INSERT_ALLOWED_EDEFAULT;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_ALLOWED:
                return updateAllowed != UPDATE_ALLOWED_EDEFAULT;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_ALLOWED:
                return deleteAllowed != DELETE_ALLOWED_EDEFAULT;
            case TransformationPackage.SQL_TRANSFORMATION__OUTPUT_LOCKED:
                return outputLocked != OUTPUT_LOCKED_EDEFAULT;
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL_DEFAULT:
                return insertSqlDefault != INSERT_SQL_DEFAULT_EDEFAULT;
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT:
                return updateSqlDefault != UPDATE_SQL_DEFAULT_EDEFAULT;
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL_DEFAULT:
                return deleteSqlDefault != DELETE_SQL_DEFAULT_EDEFAULT;
            case TransformationPackage.SQL_TRANSFORMATION__ALIASES:
                return aliases != null && !aliases.isEmpty();
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
        result.append(" (selectSql: "); //$NON-NLS-1$
        result.append(selectSql);
        result.append(", insertSql: "); //$NON-NLS-1$
        result.append(insertSql);
        result.append(", updateSql: "); //$NON-NLS-1$
        result.append(updateSql);
        result.append(", deleteSql: "); //$NON-NLS-1$
        result.append(deleteSql);
        result.append(", insertAllowed: "); //$NON-NLS-1$
        result.append(insertAllowed);
        result.append(", updateAllowed: "); //$NON-NLS-1$
        result.append(updateAllowed);
        result.append(", deleteAllowed: "); //$NON-NLS-1$
        result.append(deleteAllowed);
        result.append(", outputLocked: "); //$NON-NLS-1$
        result.append(outputLocked);
        result.append(", insertSqlDefault: "); //$NON-NLS-1$
        result.append(insertSqlDefault);
        result.append(", updateSqlDefault: "); //$NON-NLS-1$
        result.append(updateSqlDefault);
        result.append(", deleteSqlDefault: "); //$NON-NLS-1$
        result.append(deleteSqlDefault);
        result.append(')');
        return result.toString();
    }

} //SqlTransformationImpl
