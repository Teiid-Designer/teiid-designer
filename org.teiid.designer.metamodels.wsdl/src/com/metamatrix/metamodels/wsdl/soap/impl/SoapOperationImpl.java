/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapOperation;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapStyleType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapOperationImpl#getBindingOperation <em>Binding Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapOperationImpl#getStyle <em>Style</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapOperationImpl#getAction <em>Action</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SoapOperationImpl extends EObjectImpl implements SoapOperation {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getStyle() <em>Style</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getStyle()
     * @generated
     * @ordered
     */
	protected static final SoapStyleType STYLE_EDEFAULT = SoapStyleType.DOCUMENT_LITERAL;

    /**
     * The cached value of the '{@link #getStyle() <em>Style</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getStyle()
     * @generated
     * @ordered
     */
	protected SoapStyleType style = STYLE_EDEFAULT;

    /**
     * The default value of the '{@link #getAction() <em>Action</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getAction()
     * @generated
     * @ordered
     */
	protected static final String ACTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAction() <em>Action</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getAction()
     * @generated
     * @ordered
     */
	protected String action = ACTION_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SoapOperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return SoapPackage.eINSTANCE.getSoapOperation();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapStyleType getStyle() {
        return style;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setStyle(SoapStyleType newStyle) {
        SoapStyleType oldStyle = style;
        style = newStyle == null ? STYLE_EDEFAULT : newStyle;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_OPERATION__STYLE, oldStyle, style));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getAction() {
        return action;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setAction(String newAction) {
        String oldAction = action;
        action = newAction;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_OPERATION__ACTION, oldAction, action));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BindingOperation getBindingOperation() {
        if (eContainerFeatureID != SoapPackage.SOAP_OPERATION__BINDING_OPERATION) return null;
        return (BindingOperation)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBindingOperation(BindingOperation newBindingOperation) {
        if (newBindingOperation != eContainer || (eContainerFeatureID != SoapPackage.SOAP_OPERATION__BINDING_OPERATION && newBindingOperation != null)) {
            if (EcoreUtil.isAncestor(this, newBindingOperation))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBindingOperation != null)
                msgs = ((InternalEObject)newBindingOperation).eInverseAdd(this, WsdlPackage.BINDING_OPERATION__SOAP_OPERATION, BindingOperation.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBindingOperation, SoapPackage.SOAP_OPERATION__BINDING_OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_OPERATION__BINDING_OPERATION, newBindingOperation, newBindingOperation));
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
                case SoapPackage.SOAP_OPERATION__BINDING_OPERATION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, SoapPackage.SOAP_OPERATION__BINDING_OPERATION, msgs);
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
                case SoapPackage.SOAP_OPERATION__BINDING_OPERATION:
                    return eBasicSetContainer(null, SoapPackage.SOAP_OPERATION__BINDING_OPERATION, msgs);
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
                case SoapPackage.SOAP_OPERATION__BINDING_OPERATION:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING_OPERATION__SOAP_OPERATION, BindingOperation.class, msgs);
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
            case SoapPackage.SOAP_OPERATION__BINDING_OPERATION:
                return getBindingOperation();
            case SoapPackage.SOAP_OPERATION__STYLE:
                return getStyle();
            case SoapPackage.SOAP_OPERATION__ACTION:
                return getAction();
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
            case SoapPackage.SOAP_OPERATION__BINDING_OPERATION:
                setBindingOperation((BindingOperation)newValue);
                return;
            case SoapPackage.SOAP_OPERATION__STYLE:
                setStyle((SoapStyleType)newValue);
                return;
            case SoapPackage.SOAP_OPERATION__ACTION:
                setAction((String)newValue);
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
            case SoapPackage.SOAP_OPERATION__BINDING_OPERATION:
                setBindingOperation((BindingOperation)null);
                return;
            case SoapPackage.SOAP_OPERATION__STYLE:
                setStyle(STYLE_EDEFAULT);
                return;
            case SoapPackage.SOAP_OPERATION__ACTION:
                setAction(ACTION_EDEFAULT);
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
            case SoapPackage.SOAP_OPERATION__BINDING_OPERATION:
                return getBindingOperation() != null;
            case SoapPackage.SOAP_OPERATION__STYLE:
                return style != STYLE_EDEFAULT;
            case SoapPackage.SOAP_OPERATION__ACTION:
                return ACTION_EDEFAULT == null ? action != null : !ACTION_EDEFAULT.equals(action);
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
        result.append(" (style: "); //$NON-NLS-1$
        result.append(style);
        result.append(", action: "); //$NON-NLS-1$
        result.append(action);
        result.append(')');
        return result.toString();
    }

} //SoapOperationImpl
