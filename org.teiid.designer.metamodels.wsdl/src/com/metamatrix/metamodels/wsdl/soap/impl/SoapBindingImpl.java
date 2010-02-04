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
import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapBinding;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapStyleType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBindingImpl#getBinding <em>Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBindingImpl#getTransport <em>Transport</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBindingImpl#getStyle <em>Style</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SoapBindingImpl extends EObjectImpl implements SoapBinding {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getTransport() <em>Transport</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTransport()
     * @generated
     * @ordered
     */
	protected static final String TRANSPORT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTransport() <em>Transport</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTransport()
     * @generated
     * @ordered
     */
	protected String transport = TRANSPORT_EDEFAULT;

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
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SoapBindingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return SoapPackage.eINSTANCE.getSoapBinding();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getTransport() {
        return transport;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setTransport(String newTransport) {
        String oldTransport = transport;
        transport = newTransport;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_BINDING__TRANSPORT, oldTransport, transport));
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
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_BINDING__STYLE, oldStyle, style));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Binding getBinding() {
        if (eContainerFeatureID != SoapPackage.SOAP_BINDING__BINDING) return null;
        return (Binding)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBinding(Binding newBinding) {
        if (newBinding != eContainer || (eContainerFeatureID != SoapPackage.SOAP_BINDING__BINDING && newBinding != null)) {
            if (EcoreUtil.isAncestor(this, newBinding))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBinding != null)
                msgs = ((InternalEObject)newBinding).eInverseAdd(this, WsdlPackage.BINDING__SOAP_BINDING, Binding.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBinding, SoapPackage.SOAP_BINDING__BINDING, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_BINDING__BINDING, newBinding, newBinding));
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
                case SoapPackage.SOAP_BINDING__BINDING:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, SoapPackage.SOAP_BINDING__BINDING, msgs);
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
                case SoapPackage.SOAP_BINDING__BINDING:
                    return eBasicSetContainer(null, SoapPackage.SOAP_BINDING__BINDING, msgs);
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
                case SoapPackage.SOAP_BINDING__BINDING:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING__SOAP_BINDING, Binding.class, msgs);
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
            case SoapPackage.SOAP_BINDING__BINDING:
                return getBinding();
            case SoapPackage.SOAP_BINDING__TRANSPORT:
                return getTransport();
            case SoapPackage.SOAP_BINDING__STYLE:
                return getStyle();
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
            case SoapPackage.SOAP_BINDING__BINDING:
                setBinding((Binding)newValue);
                return;
            case SoapPackage.SOAP_BINDING__TRANSPORT:
                setTransport((String)newValue);
                return;
            case SoapPackage.SOAP_BINDING__STYLE:
                setStyle((SoapStyleType)newValue);
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
            case SoapPackage.SOAP_BINDING__BINDING:
                setBinding((Binding)null);
                return;
            case SoapPackage.SOAP_BINDING__TRANSPORT:
                setTransport(TRANSPORT_EDEFAULT);
                return;
            case SoapPackage.SOAP_BINDING__STYLE:
                setStyle(STYLE_EDEFAULT);
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
            case SoapPackage.SOAP_BINDING__BINDING:
                return getBinding() != null;
            case SoapPackage.SOAP_BINDING__TRANSPORT:
                return TRANSPORT_EDEFAULT == null ? transport != null : !TRANSPORT_EDEFAULT.equals(transport);
            case SoapPackage.SOAP_BINDING__STYLE:
                return style != STYLE_EDEFAULT;
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
        result.append(" (transport: "); //$NON-NLS-1$
        result.append(transport);
        result.append(", style: "); //$NON-NLS-1$
        result.append(style);
        result.append(')');
        return result.toString();
    }

} //SoapBindingImpl
