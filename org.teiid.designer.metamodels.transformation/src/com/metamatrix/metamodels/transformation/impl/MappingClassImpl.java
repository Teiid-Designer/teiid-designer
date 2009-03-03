/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.RecursionErrorMode;
import com.metamatrix.metamodels.transformation.TransformationPackage;

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

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping Class</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#isRecursive <em>Recursive</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#isRecursionAllowed <em>Recursion Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#getRecursionCriteria <em>Recursion Criteria</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#getRecursionLimit <em>Recursion Limit</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#getRecursionLimitErrorMode <em>Recursion Limit Error Mode</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#getColumns <em>Columns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#getMappingClassSet <em>Mapping Class Set</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl#getInputSet <em>Input Set</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MappingClassImpl extends MappingClassObjectImpl implements MappingClass {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #isRecursive() <em>Recursive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isRecursive()
     * @generated
     * @ordered
     */
    protected static final boolean RECURSIVE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isRecursive() <em>Recursive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isRecursive()
     * @generated
     * @ordered
     */
    protected boolean recursive = RECURSIVE_EDEFAULT;

    /**
     * The default value of the '{@link #isRecursionAllowed() <em>Recursion Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isRecursionAllowed()
     * @generated
     * @ordered
     */
    protected static final boolean RECURSION_ALLOWED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isRecursionAllowed() <em>Recursion Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isRecursionAllowed()
     * @generated
     * @ordered
     */
    protected boolean recursionAllowed = RECURSION_ALLOWED_EDEFAULT;

    /**
     * The default value of the '{@link #getRecursionCriteria() <em>Recursion Criteria</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRecursionCriteria()
     * @generated
     * @ordered
     */
    protected static final String RECURSION_CRITERIA_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRecursionCriteria() <em>Recursion Criteria</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRecursionCriteria()
     * @generated
     * @ordered
     */
    protected String recursionCriteria = RECURSION_CRITERIA_EDEFAULT;

    /**
     * The default value of the '{@link #getRecursionLimit() <em>Recursion Limit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRecursionLimit()
     * @generated
     * @ordered
     */
    protected static final int RECURSION_LIMIT_EDEFAULT = 5;

    /**
     * The cached value of the '{@link #getRecursionLimit() <em>Recursion Limit</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRecursionLimit()
     * @generated
     * @ordered
     */
    protected int recursionLimit = RECURSION_LIMIT_EDEFAULT;

    /**
     * The default value of the '{@link #getRecursionLimitErrorMode() <em>Recursion Limit Error Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRecursionLimitErrorMode()
     * @generated
     * @ordered
     */
    protected static final RecursionErrorMode RECURSION_LIMIT_ERROR_MODE_EDEFAULT = RecursionErrorMode.THROW_LITERAL;

    /**
     * The cached value of the '{@link #getRecursionLimitErrorMode() <em>Recursion Limit Error Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRecursionLimitErrorMode()
     * @generated
     * @ordered
     */
    protected RecursionErrorMode recursionLimitErrorMode = RECURSION_LIMIT_ERROR_MODE_EDEFAULT;

    /**
     * The cached value of the '{@link #getColumns() <em>Columns</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getColumns()
     * @generated
     * @ordered
     */
    protected EList columns = null;

    /**
     * The cached value of the '{@link #getInputSet() <em>Input Set</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputSet()
     * @generated
     * @ordered
     */
    protected InputSet inputSet = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MappingClassImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getMappingClass();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRecursive(boolean newRecursive) {
        boolean oldRecursive = recursive;
        recursive = newRecursive;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__RECURSIVE, oldRecursive, recursive));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isRecursionAllowed() {
        return recursionAllowed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRecursionAllowed(boolean newRecursionAllowed) {
        boolean oldRecursionAllowed = recursionAllowed;
        recursionAllowed = newRecursionAllowed;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__RECURSION_ALLOWED, oldRecursionAllowed, recursionAllowed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getRecursionCriteria() {
        return recursionCriteria;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRecursionCriteria(String newRecursionCriteria) {
        String oldRecursionCriteria = recursionCriteria;
        recursionCriteria = newRecursionCriteria;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__RECURSION_CRITERIA, oldRecursionCriteria, recursionCriteria));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getRecursionLimit() {
        return recursionLimit;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRecursionLimit(int newRecursionLimit) {
        int oldRecursionLimit = recursionLimit;
        recursionLimit = newRecursionLimit;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT, oldRecursionLimit, recursionLimit));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RecursionErrorMode getRecursionLimitErrorMode() {
        return recursionLimitErrorMode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRecursionLimitErrorMode(RecursionErrorMode newRecursionLimitErrorMode) {
        RecursionErrorMode oldRecursionLimitErrorMode = recursionLimitErrorMode;
        recursionLimitErrorMode = newRecursionLimitErrorMode == null ? RECURSION_LIMIT_ERROR_MODE_EDEFAULT : newRecursionLimitErrorMode;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE, oldRecursionLimitErrorMode, recursionLimitErrorMode));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getColumns() {
        if (columns == null) {
            columns = new EObjectContainmentWithInverseEList(MappingClassColumn.class, this, TransformationPackage.MAPPING_CLASS__COLUMNS, TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS);
        }
        return columns;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MappingClassSet getMappingClassSet() {
        if (eContainerFeatureID != TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET) return null;
        return (MappingClassSet)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMappingClassSet(MappingClassSet newMappingClassSet) {
        if (newMappingClassSet != eContainer || (eContainerFeatureID != TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET && newMappingClassSet != null)) {
            if (EcoreUtil.isAncestor(this, newMappingClassSet))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMappingClassSet != null)
                msgs = ((InternalEObject)newMappingClassSet).eInverseAdd(this, TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES, MappingClassSet.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMappingClassSet, TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET, newMappingClassSet, newMappingClassSet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InputSet getInputSet() {
        return inputSet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetInputSet(InputSet newInputSet, NotificationChain msgs) {
        InputSet oldInputSet = inputSet;
        inputSet = newInputSet;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__INPUT_SET, oldInputSet, newInputSet);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInputSet(InputSet newInputSet) {
        if (newInputSet != inputSet) {
            NotificationChain msgs = null;
            if (inputSet != null)
                msgs = ((InternalEObject)inputSet).eInverseRemove(this, TransformationPackage.INPUT_SET__MAPPING_CLASS, InputSet.class, msgs);
            if (newInputSet != null)
                msgs = ((InternalEObject)newInputSet).eInverseAdd(this, TransformationPackage.INPUT_SET__MAPPING_CLASS, InputSet.class, msgs);
            msgs = basicSetInputSet(newInputSet, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS__INPUT_SET, newInputSet, newInputSet));
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
                case TransformationPackage.MAPPING_CLASS__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET, msgs);
                case TransformationPackage.MAPPING_CLASS__INPUT_SET:
                    if (inputSet != null)
                        msgs = ((InternalEObject)inputSet).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TransformationPackage.MAPPING_CLASS__INPUT_SET, null, msgs);
                    return basicSetInputSet((InputSet)otherEnd, msgs);
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
                case TransformationPackage.MAPPING_CLASS__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET:
                    return eBasicSetContainer(null, TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET, msgs);
                case TransformationPackage.MAPPING_CLASS__INPUT_SET:
                    return basicSetInputSet(null, msgs);
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
                case TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET:
                    return eContainer.eInverseRemove(this, TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES, MappingClassSet.class, msgs);
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
            case TransformationPackage.MAPPING_CLASS__NAME:
                return getName();
            case TransformationPackage.MAPPING_CLASS__RECURSIVE:
                return isRecursive() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.MAPPING_CLASS__RECURSION_ALLOWED:
                return isRecursionAllowed() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.MAPPING_CLASS__RECURSION_CRITERIA:
                return getRecursionCriteria();
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT:
                return new Integer(getRecursionLimit());
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE:
                return getRecursionLimitErrorMode();
            case TransformationPackage.MAPPING_CLASS__COLUMNS:
                return getColumns();
            case TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET:
                return getMappingClassSet();
            case TransformationPackage.MAPPING_CLASS__INPUT_SET:
                return getInputSet();
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
            case TransformationPackage.MAPPING_CLASS__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSIVE:
                setRecursive(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_ALLOWED:
                setRecursionAllowed(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_CRITERIA:
                setRecursionCriteria((String)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT:
                setRecursionLimit(((Integer)newValue).intValue());
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE:
                setRecursionLimitErrorMode((RecursionErrorMode)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET:
                setMappingClassSet((MappingClassSet)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS__INPUT_SET:
                setInputSet((InputSet)newValue);
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
            case TransformationPackage.MAPPING_CLASS__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSIVE:
                setRecursive(RECURSIVE_EDEFAULT);
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_ALLOWED:
                setRecursionAllowed(RECURSION_ALLOWED_EDEFAULT);
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_CRITERIA:
                setRecursionCriteria(RECURSION_CRITERIA_EDEFAULT);
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT:
                setRecursionLimit(RECURSION_LIMIT_EDEFAULT);
                return;
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE:
                setRecursionLimitErrorMode(RECURSION_LIMIT_ERROR_MODE_EDEFAULT);
                return;
            case TransformationPackage.MAPPING_CLASS__COLUMNS:
                getColumns().clear();
                return;
            case TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET:
                setMappingClassSet((MappingClassSet)null);
                return;
            case TransformationPackage.MAPPING_CLASS__INPUT_SET:
                setInputSet((InputSet)null);
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
            case TransformationPackage.MAPPING_CLASS__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.MAPPING_CLASS__RECURSIVE:
                return recursive != RECURSIVE_EDEFAULT;
            case TransformationPackage.MAPPING_CLASS__RECURSION_ALLOWED:
                return recursionAllowed != RECURSION_ALLOWED_EDEFAULT;
            case TransformationPackage.MAPPING_CLASS__RECURSION_CRITERIA:
                return RECURSION_CRITERIA_EDEFAULT == null ? recursionCriteria != null : !RECURSION_CRITERIA_EDEFAULT.equals(recursionCriteria);
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT:
                return recursionLimit != RECURSION_LIMIT_EDEFAULT;
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE:
                return recursionLimitErrorMode != RECURSION_LIMIT_ERROR_MODE_EDEFAULT;
            case TransformationPackage.MAPPING_CLASS__COLUMNS:
                return columns != null && !columns.isEmpty();
            case TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET:
                return getMappingClassSet() != null;
            case TransformationPackage.MAPPING_CLASS__INPUT_SET:
                return inputSet != null;
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
        result.append(" (recursive: "); //$NON-NLS-1$
        result.append(recursive);
        result.append(", recursionAllowed: "); //$NON-NLS-1$
        result.append(recursionAllowed);
        result.append(", recursionCriteria: "); //$NON-NLS-1$
        result.append(recursionCriteria);
        result.append(", recursionLimit: "); //$NON-NLS-1$
        result.append(recursionLimit);
        result.append(", recursionLimitErrorMode: "); //$NON-NLS-1$
        result.append(recursionLimitErrorMode);
        result.append(')');
        return result.toString();
    }

} //MappingClassImpl
