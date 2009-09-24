/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.ProcedureUpdateCount;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Procedure</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureImpl#isFunction <em>Function</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureImpl#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProcedureImpl extends RelationalEntityImpl implements Procedure {
    /**
     * The default value of the '{@link #isFunction() <em>Function</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFunction()
     * @generated
     * @ordered
     */
    protected static final boolean FUNCTION_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isFunction() <em>Function</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFunction()
     * @generated
     * @ordered
     */
    protected boolean function = FUNCTION_EDEFAULT;

    /**
     * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getParameters()
     * @generated
     * @ordered
     */
    protected EList parameters = null;

    /**
     * The cached value of the '{@link #getResult() <em>Result</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResult()
     * @generated
     * @ordered
     */
    protected ProcedureResult result = null;

    /**
     * The default value of the '{@link #getUpdateCount() <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpdateCount()
     * @generated
     * @ordered
     */
    protected static final ProcedureUpdateCount UPDATE_COUNT_EDEFAULT = ProcedureUpdateCount.AUTO_LITERAL;

    /**
     * The cached value of the '{@link #getUpdateCount() <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpdateCount()
     * @generated
     * @ordered
     */
    protected ProcedureUpdateCount updateCount = UPDATE_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProcedureImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getProcedure();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isFunction() {
        return function;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFunction(boolean newFunction) {
        boolean oldFunction = function;
        function = newFunction;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE__FUNCTION, oldFunction, function));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Schema getSchema() {
        if (eContainerFeatureID != RelationalPackage.PROCEDURE__SCHEMA) return null;
        return (Schema)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSchema(Schema newSchema) {
        if (newSchema != eContainer || (eContainerFeatureID != RelationalPackage.PROCEDURE__SCHEMA && newSchema != null)) {
            if (EcoreUtil.isAncestor(this, newSchema))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSchema != null)
                msgs = ((InternalEObject)newSchema).eInverseAdd(this, RelationalPackage.SCHEMA__PROCEDURES, Schema.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSchema, RelationalPackage.PROCEDURE__SCHEMA, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE__SCHEMA, newSchema, newSchema));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getParameters() {
        if (parameters == null) {
            parameters = new EObjectContainmentWithInverseEList(ProcedureParameter.class, this, RelationalPackage.PROCEDURE__PARAMETERS, RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE);
        }
        return parameters;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Catalog getCatalog() {
        if (eContainerFeatureID != RelationalPackage.PROCEDURE__CATALOG) return null;
        return (Catalog)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCatalog(Catalog newCatalog) {
        if (newCatalog != eContainer || (eContainerFeatureID != RelationalPackage.PROCEDURE__CATALOG && newCatalog != null)) {
            if (EcoreUtil.isAncestor(this, newCatalog))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newCatalog != null)
                msgs = ((InternalEObject)newCatalog).eInverseAdd(this, RelationalPackage.CATALOG__PROCEDURES, Catalog.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newCatalog, RelationalPackage.PROCEDURE__CATALOG, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE__CATALOG, newCatalog, newCatalog));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProcedureResult getResult() {
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetResult(ProcedureResult newResult, NotificationChain msgs) {
        ProcedureResult oldResult = result;
        result = newResult;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE__RESULT, oldResult, newResult);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setResult(ProcedureResult newResult) {
        if (newResult != result) {
            NotificationChain msgs = null;
            if (result != null)
                msgs = ((InternalEObject)result).eInverseRemove(this, RelationalPackage.PROCEDURE_RESULT__PROCEDURE, ProcedureResult.class, msgs);
            if (newResult != null)
                msgs = ((InternalEObject)newResult).eInverseAdd(this, RelationalPackage.PROCEDURE_RESULT__PROCEDURE, ProcedureResult.class, msgs);
            msgs = basicSetResult(newResult, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE__RESULT, newResult, newResult));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProcedureUpdateCount getUpdateCount() {
        return updateCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUpdateCount(ProcedureUpdateCount newUpdateCount) {
        ProcedureUpdateCount oldUpdateCount = updateCount;
        updateCount = newUpdateCount == null ? UPDATE_COUNT_EDEFAULT : newUpdateCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE__UPDATE_COUNT, oldUpdateCount, updateCount));
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
                case RelationalPackage.PROCEDURE__SCHEMA:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.PROCEDURE__SCHEMA, msgs);
                case RelationalPackage.PROCEDURE__PARAMETERS:
                    return ((InternalEList)getParameters()).basicAdd(otherEnd, msgs);
                case RelationalPackage.PROCEDURE__CATALOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.PROCEDURE__CATALOG, msgs);
                case RelationalPackage.PROCEDURE__RESULT:
                    if (result != null)
                        msgs = ((InternalEObject)result).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - RelationalPackage.PROCEDURE__RESULT, null, msgs);
                    return basicSetResult((ProcedureResult)otherEnd, msgs);
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
                case RelationalPackage.PROCEDURE__SCHEMA:
                    return eBasicSetContainer(null, RelationalPackage.PROCEDURE__SCHEMA, msgs);
                case RelationalPackage.PROCEDURE__PARAMETERS:
                    return ((InternalEList)getParameters()).basicRemove(otherEnd, msgs);
                case RelationalPackage.PROCEDURE__CATALOG:
                    return eBasicSetContainer(null, RelationalPackage.PROCEDURE__CATALOG, msgs);
                case RelationalPackage.PROCEDURE__RESULT:
                    return basicSetResult(null, msgs);
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
                case RelationalPackage.PROCEDURE__SCHEMA:
                    return eContainer.eInverseRemove(this, RelationalPackage.SCHEMA__PROCEDURES, Schema.class, msgs);
                case RelationalPackage.PROCEDURE__CATALOG:
                    return eContainer.eInverseRemove(this, RelationalPackage.CATALOG__PROCEDURES, Catalog.class, msgs);
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
            case RelationalPackage.PROCEDURE__NAME:
                return getName();
            case RelationalPackage.PROCEDURE__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.PROCEDURE__FUNCTION:
                return isFunction() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.PROCEDURE__SCHEMA:
                return getSchema();
            case RelationalPackage.PROCEDURE__PARAMETERS:
                return getParameters();
            case RelationalPackage.PROCEDURE__CATALOG:
                return getCatalog();
            case RelationalPackage.PROCEDURE__RESULT:
                return getResult();
            case RelationalPackage.PROCEDURE__UPDATE_COUNT:
                return getUpdateCount();
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
            case RelationalPackage.PROCEDURE__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.PROCEDURE__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.PROCEDURE__FUNCTION:
                setFunction(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.PROCEDURE__SCHEMA:
                setSchema((Schema)newValue);
                return;
            case RelationalPackage.PROCEDURE__PARAMETERS:
                getParameters().clear();
                getParameters().addAll((Collection)newValue);
                return;
            case RelationalPackage.PROCEDURE__CATALOG:
                setCatalog((Catalog)newValue);
                return;
            case RelationalPackage.PROCEDURE__RESULT:
                setResult((ProcedureResult)newValue);
                return;
            case RelationalPackage.PROCEDURE__UPDATE_COUNT:
                setUpdateCount((ProcedureUpdateCount)newValue);
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
            case RelationalPackage.PROCEDURE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE__FUNCTION:
                setFunction(FUNCTION_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE__SCHEMA:
                setSchema((Schema)null);
                return;
            case RelationalPackage.PROCEDURE__PARAMETERS:
                getParameters().clear();
                return;
            case RelationalPackage.PROCEDURE__CATALOG:
                setCatalog((Catalog)null);
                return;
            case RelationalPackage.PROCEDURE__RESULT:
                setResult((ProcedureResult)null);
                return;
            case RelationalPackage.PROCEDURE__UPDATE_COUNT:
                setUpdateCount(UPDATE_COUNT_EDEFAULT);
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
            case RelationalPackage.PROCEDURE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.PROCEDURE__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.PROCEDURE__FUNCTION:
                return function != FUNCTION_EDEFAULT;
            case RelationalPackage.PROCEDURE__SCHEMA:
                return getSchema() != null;
            case RelationalPackage.PROCEDURE__PARAMETERS:
                return parameters != null && !parameters.isEmpty();
            case RelationalPackage.PROCEDURE__CATALOG:
                return getCatalog() != null;
            case RelationalPackage.PROCEDURE__RESULT:
                return result != null;
            case RelationalPackage.PROCEDURE__UPDATE_COUNT:
                return updateCount != UPDATE_COUNT_EDEFAULT;
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
        result.append(" (function: "); //$NON-NLS-1$
        result.append(function);
        result.append(", updateCount: "); //$NON-NLS-1$
        result.append(updateCount);
        result.append(')');
        return result.toString();
    }

} //ProcedureImpl
