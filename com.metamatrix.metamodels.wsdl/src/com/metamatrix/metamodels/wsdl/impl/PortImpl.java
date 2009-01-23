/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
import com.metamatrix.metamodels.wsdl.ExtensibleDocumented;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.Port;
import com.metamatrix.metamodels.wsdl.Service;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.http.HttpAddress;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;

import com.metamatrix.metamodels.wsdl.soap.SoapAddress;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getBinding <em>Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getService <em>Service</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getSoapAddress <em>Soap Address</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.PortImpl#getHttpAddress <em>Http Address</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PortImpl extends EObjectImpl implements Port {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
	protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
	protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getDocumentation() <em>Documentation</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getDocumentation()
     * @generated
     * @ordered
     */
	protected Documentation documentation = null;

    /**
     * The cached value of the '{@link #getDeclaredNamespaces() <em>Declared Namespaces</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getDeclaredNamespaces()
     * @generated
     * @ordered
     */
	protected EList declaredNamespaces = null;

    /**
     * The cached value of the '{@link #getElements() <em>Elements</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getElements()
     * @generated
     * @ordered
     */
	protected EList elements = null;

    /**
     * The default value of the '{@link #getBinding() <em>Binding</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getBinding()
     * @generated
     * @ordered
     */
	protected static final String BINDING_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getBinding() <em>Binding</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getBinding()
     * @generated
     * @ordered
     */
	protected String binding = BINDING_EDEFAULT;

    /**
     * The cached value of the '{@link #getSoapAddress() <em>Soap Address</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSoapAddress()
     * @generated
     * @ordered
     */
	protected SoapAddress soapAddress = null;

    /**
     * The cached value of the '{@link #getHttpAddress() <em>Http Address</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getHttpAddress()
     * @generated
     * @ordered
     */
	protected HttpAddress httpAddress = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected PortImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getPort();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Documentation getDocumentation() {
        return documentation;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetDocumentation(Documentation newDocumentation, NotificationChain msgs) {
        Documentation oldDocumentation = documentation;
        documentation = newDocumentation;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__DOCUMENTATION, oldDocumentation, newDocumentation);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setDocumentation(Documentation newDocumentation) {
        if (newDocumentation != documentation) {
            NotificationChain msgs = null;
            if (documentation != null)
                msgs = ((InternalEObject)documentation).eInverseRemove(this, WsdlPackage.DOCUMENTATION__DOCUMENTED, Documentation.class, msgs);
            if (newDocumentation != null)
                msgs = ((InternalEObject)newDocumentation).eInverseAdd(this, WsdlPackage.DOCUMENTATION__DOCUMENTED, Documentation.class, msgs);
            msgs = basicSetDocumentation(newDocumentation, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__DOCUMENTATION, newDocumentation, newDocumentation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this, WsdlPackage.PORT__DECLARED_NAMESPACES, WsdlPackage.NAMESPACE_DECLARATION__OWNER);
        }
        return declaredNamespaces;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getElements() {
        if (elements == null) {
            elements = new EObjectContainmentWithInverseEList(Element.class, this, WsdlPackage.PORT__ELEMENTS, WsdlPackage.ELEMENT__ELEMENT_OWNER);
        }
        return elements;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getBinding() {
        return binding;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBinding(String newBinding) {
        String oldBinding = binding;
        binding = newBinding;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__BINDING, oldBinding, binding));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Service getService() {
        if (eContainerFeatureID != WsdlPackage.PORT__SERVICE) return null;
        return (Service)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setService(Service newService) {
        if (newService != eContainer || (eContainerFeatureID != WsdlPackage.PORT__SERVICE && newService != null)) {
            if (EcoreUtil.isAncestor(this, newService))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newService != null)
                msgs = ((InternalEObject)newService).eInverseAdd(this, WsdlPackage.SERVICE__PORTS, Service.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newService, WsdlPackage.PORT__SERVICE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__SERVICE, newService, newService));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapAddress getSoapAddress() {
        return soapAddress;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetSoapAddress(SoapAddress newSoapAddress, NotificationChain msgs) {
        SoapAddress oldSoapAddress = soapAddress;
        soapAddress = newSoapAddress;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__SOAP_ADDRESS, oldSoapAddress, newSoapAddress);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSoapAddress(SoapAddress newSoapAddress) {
        if (newSoapAddress != soapAddress) {
            NotificationChain msgs = null;
            if (soapAddress != null)
                msgs = ((InternalEObject)soapAddress).eInverseRemove(this, SoapPackage.SOAP_ADDRESS__PORT, SoapAddress.class, msgs);
            if (newSoapAddress != null)
                msgs = ((InternalEObject)newSoapAddress).eInverseAdd(this, SoapPackage.SOAP_ADDRESS__PORT, SoapAddress.class, msgs);
            msgs = basicSetSoapAddress(newSoapAddress, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__SOAP_ADDRESS, newSoapAddress, newSoapAddress));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public HttpAddress getHttpAddress() {
        return httpAddress;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetHttpAddress(HttpAddress newHttpAddress, NotificationChain msgs) {
        HttpAddress oldHttpAddress = httpAddress;
        httpAddress = newHttpAddress;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__HTTP_ADDRESS, oldHttpAddress, newHttpAddress);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setHttpAddress(HttpAddress newHttpAddress) {
        if (newHttpAddress != httpAddress) {
            NotificationChain msgs = null;
            if (httpAddress != null)
                msgs = ((InternalEObject)httpAddress).eInverseRemove(this, HttpPackage.HTTP_ADDRESS__PORT, HttpAddress.class, msgs);
            if (newHttpAddress != null)
                msgs = ((InternalEObject)newHttpAddress).eInverseAdd(this, HttpPackage.HTTP_ADDRESS__PORT, HttpAddress.class, msgs);
            msgs = basicSetHttpAddress(newHttpAddress, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.PORT__HTTP_ADDRESS, newHttpAddress, newHttpAddress));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public boolean isNameValid() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
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
                case WsdlPackage.PORT__DOCUMENTATION:
                    if (documentation != null)
                        msgs = ((InternalEObject)documentation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.PORT__DOCUMENTATION, null, msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.PORT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.PORT__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case WsdlPackage.PORT__SERVICE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.PORT__SERVICE, msgs);
                case WsdlPackage.PORT__SOAP_ADDRESS:
                    if (soapAddress != null)
                        msgs = ((InternalEObject)soapAddress).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.PORT__SOAP_ADDRESS, null, msgs);
                    return basicSetSoapAddress((SoapAddress)otherEnd, msgs);
                case WsdlPackage.PORT__HTTP_ADDRESS:
                    if (httpAddress != null)
                        msgs = ((InternalEObject)httpAddress).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.PORT__HTTP_ADDRESS, null, msgs);
                    return basicSetHttpAddress((HttpAddress)otherEnd, msgs);
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
                case WsdlPackage.PORT__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.PORT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.PORT__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case WsdlPackage.PORT__SERVICE:
                    return eBasicSetContainer(null, WsdlPackage.PORT__SERVICE, msgs);
                case WsdlPackage.PORT__SOAP_ADDRESS:
                    return basicSetSoapAddress(null, msgs);
                case WsdlPackage.PORT__HTTP_ADDRESS:
                    return basicSetHttpAddress(null, msgs);
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
                case WsdlPackage.PORT__SERVICE:
                    return eContainer.eInverseRemove(this, WsdlPackage.SERVICE__PORTS, Service.class, msgs);
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
            case WsdlPackage.PORT__NAME:
                return getName();
            case WsdlPackage.PORT__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.PORT__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.PORT__ELEMENTS:
                return getElements();
            case WsdlPackage.PORT__BINDING:
                return getBinding();
            case WsdlPackage.PORT__SERVICE:
                return getService();
            case WsdlPackage.PORT__SOAP_ADDRESS:
                return getSoapAddress();
            case WsdlPackage.PORT__HTTP_ADDRESS:
                return getHttpAddress();
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
            case WsdlPackage.PORT__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.PORT__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.PORT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.PORT__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case WsdlPackage.PORT__BINDING:
                setBinding((String)newValue);
                return;
            case WsdlPackage.PORT__SERVICE:
                setService((Service)newValue);
                return;
            case WsdlPackage.PORT__SOAP_ADDRESS:
                setSoapAddress((SoapAddress)newValue);
                return;
            case WsdlPackage.PORT__HTTP_ADDRESS:
                setHttpAddress((HttpAddress)newValue);
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
            case WsdlPackage.PORT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.PORT__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.PORT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.PORT__ELEMENTS:
                getElements().clear();
                return;
            case WsdlPackage.PORT__BINDING:
                setBinding(BINDING_EDEFAULT);
                return;
            case WsdlPackage.PORT__SERVICE:
                setService((Service)null);
                return;
            case WsdlPackage.PORT__SOAP_ADDRESS:
                setSoapAddress((SoapAddress)null);
                return;
            case WsdlPackage.PORT__HTTP_ADDRESS:
                setHttpAddress((HttpAddress)null);
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
            case WsdlPackage.PORT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.PORT__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.PORT__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.PORT__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case WsdlPackage.PORT__BINDING:
                return BINDING_EDEFAULT == null ? binding != null : !BINDING_EDEFAULT.equals(binding);
            case WsdlPackage.PORT__SERVICE:
                return getService() != null;
            case WsdlPackage.PORT__SOAP_ADDRESS:
                return soapAddress != null;
            case WsdlPackage.PORT__HTTP_ADDRESS:
                return httpAddress != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
        if (baseClass == Documented.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.PORT__DOCUMENTATION: return WsdlPackage.DOCUMENTED__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.PORT__DECLARED_NAMESPACES: return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.PORT__ELEMENTS: return WsdlPackage.ELEMENT_OWNER__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == ExtensibleDocumented.class) {
            switch (derivedFeatureID) {
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
        if (baseClass == Documented.class) {
            switch (baseFeatureID) {
                case WsdlPackage.DOCUMENTED__DOCUMENTATION: return WsdlPackage.PORT__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES: return WsdlPackage.PORT__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ELEMENT_OWNER__ELEMENTS: return WsdlPackage.PORT__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == ExtensibleDocumented.class) {
            switch (baseFeatureID) {
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", binding: "); //$NON-NLS-1$
        result.append(binding);
        result.append(')');
        return result.toString();
    }

} //PortImpl
