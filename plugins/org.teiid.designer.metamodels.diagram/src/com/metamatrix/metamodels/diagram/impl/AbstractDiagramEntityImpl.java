/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import com.metamatrix.metamodels.diagram.AbstractDiagramEntity;
import com.metamatrix.metamodels.diagram.DiagramPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Abstract Diagram Entity</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.AbstractDiagramEntityImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.AbstractDiagramEntityImpl#getUserString <em>User String</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.AbstractDiagramEntityImpl#getUserType <em>User Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.AbstractDiagramEntityImpl#getModelObject <em>Model Object</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class AbstractDiagramEntityImpl extends PresentationEntityImpl implements AbstractDiagramEntity {

    /**
     * The default value of the '{@link #getAlias() <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAlias()
     * @generated
     * @ordered
     */
    protected static final String ALIAS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAlias() <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAlias()
     * @generated
     * @ordered
     */
    protected String alias = ALIAS_EDEFAULT;

    /**
     * The default value of the '{@link #getUserString() <em>User String</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUserString()
     * @generated
     * @ordered
     */
    protected static final String USER_STRING_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUserString() <em>User String</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUserString()
     * @generated
     * @ordered
     */
    protected String userString = USER_STRING_EDEFAULT;

    /**
     * The default value of the '{@link #getUserType() <em>User Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUserType()
     * @generated
     * @ordered
     */
    protected static final String USER_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUserType() <em>User Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUserType()
     * @generated
     * @ordered
     */
    protected String userType = USER_TYPE_EDEFAULT;

    /**
     * The cached value of the '{@link #getModelObject() <em>Model Object</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getModelObject()
     * @generated
     * @ordered
     */
    protected EObject modelObject = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AbstractDiagramEntityImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return DiagramPackage.eINSTANCE.getAbstractDiagramEntity();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAlias() {
        return alias;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAlias(String newAlias) {
        String oldAlias = alias;
        alias = newAlias;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__ALIAS, oldAlias, alias));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUserString() {
        return userString;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUserString(String newUserString) {
        String oldUserString = userString;
        userString = newUserString;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_STRING, oldUserString, userString));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUserType() {
        return userType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUserType(String newUserType) {
        String oldUserType = userType;
        userType = newUserType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_TYPE, oldUserType, userType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getModelObject() {
        if (modelObject != null && modelObject.eIsProxy()) {
            EObject oldModelObject = modelObject;
            modelObject = eResolveProxy((InternalEObject)modelObject);
            if (modelObject != oldModelObject) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT, oldModelObject, modelObject));
            }
        }
        return modelObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetModelObject() {
        return modelObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setModelObject(EObject newModelObject) {
        EObject oldModelObject = modelObject;
        modelObject = newModelObject;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT, oldModelObject, modelObject));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__NAME:
                return getName();
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__ALIAS:
                return getAlias();
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_STRING:
                return getUserString();
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_TYPE:
                return getUserType();
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT:
                if (resolve) return getModelObject();
                return basicGetModelObject();
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
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__NAME:
                setName((String)newValue);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__ALIAS:
                setAlias((String)newValue);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_STRING:
                setUserString((String)newValue);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_TYPE:
                setUserType((String)newValue);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT:
                setModelObject((EObject)newValue);
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
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__NAME:
                setName(NAME_EDEFAULT);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__ALIAS:
                setAlias(ALIAS_EDEFAULT);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_STRING:
                setUserString(USER_STRING_EDEFAULT);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_TYPE:
                setUserType(USER_TYPE_EDEFAULT);
                return;
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT:
                setModelObject((EObject)null);
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
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__ALIAS:
                return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_STRING:
                return USER_STRING_EDEFAULT == null ? userString != null : !USER_STRING_EDEFAULT.equals(userString);
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__USER_TYPE:
                return USER_TYPE_EDEFAULT == null ? userType != null : !USER_TYPE_EDEFAULT.equals(userType);
            case DiagramPackage.ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT:
                return modelObject != null;
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
        result.append(" (alias: "); //$NON-NLS-1$
        result.append(alias);
        result.append(", userString: "); //$NON-NLS-1$
        result.append(userString);
        result.append(", userType: "); //$NON-NLS-1$
        result.append(userType);
        result.append(')');
        return result.toString();
    }

} //AbstractDiagramEntityImpl
