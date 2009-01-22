/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

import com.metamatrix.metamodels.wsdl.BindingInput;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.mime.MimeElement;
import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;

import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;
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
 * An implementation of the model object '<em><b>Binding Input</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getMimeElements <em>Mime Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getSoapHeader <em>Soap Header</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getSoapBody <em>Soap Body</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl#getBindingOperation <em>Binding Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BindingInputImpl extends EObjectImpl implements BindingInput {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

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
     * The cached value of the '{@link #getMimeElements() <em>Mime Elements</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getMimeElements()
     * @generated
     * @ordered
     */
	protected EList mimeElements = null;

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
     * The cached value of the '{@link #getSoapHeader() <em>Soap Header</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSoapHeader()
     * @generated
     * @ordered
     */
	protected SoapHeader soapHeader = null;

    /**
     * The cached value of the '{@link #getSoapBody() <em>Soap Body</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSoapBody()
     * @generated
     * @ordered
     */
	protected SoapBody soapBody = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected BindingInputImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getBindingInput();
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__DOCUMENTATION, oldDocumentation, newDocumentation);
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__DOCUMENTATION, newDocumentation, newDocumentation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this, WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES, WsdlPackage.NAMESPACE_DECLARATION__OWNER);
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
            elements = new EObjectContainmentWithInverseEList(Element.class, this, WsdlPackage.BINDING_INPUT__ELEMENTS, WsdlPackage.ELEMENT__ELEMENT_OWNER);
        }
        return elements;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getMimeElements() {
        if (mimeElements == null) {
            mimeElements = new EObjectContainmentWithInverseEList(MimeElement.class, this, WsdlPackage.BINDING_INPUT__MIME_ELEMENTS, MimePackage.MIME_ELEMENT__MIME_ELEMENT_OWNER);
        }
        return mimeElements;
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapHeader getSoapHeader() {
        return soapHeader;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetSoapHeader(SoapHeader newSoapHeader, NotificationChain msgs) {
        SoapHeader oldSoapHeader = soapHeader;
        soapHeader = newSoapHeader;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__SOAP_HEADER, oldSoapHeader, newSoapHeader);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSoapHeader(SoapHeader newSoapHeader) {
        if (newSoapHeader != soapHeader) {
            NotificationChain msgs = null;
            if (soapHeader != null)
                msgs = ((InternalEObject)soapHeader).eInverseRemove(this, SoapPackage.SOAP_HEADER__BINDING_PARAM, SoapHeader.class, msgs);
            if (newSoapHeader != null)
                msgs = ((InternalEObject)newSoapHeader).eInverseAdd(this, SoapPackage.SOAP_HEADER__BINDING_PARAM, SoapHeader.class, msgs);
            msgs = basicSetSoapHeader(newSoapHeader, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__SOAP_HEADER, newSoapHeader, newSoapHeader));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapBody getSoapBody() {
        return soapBody;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetSoapBody(SoapBody newSoapBody, NotificationChain msgs) {
        SoapBody oldSoapBody = soapBody;
        soapBody = newSoapBody;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__SOAP_BODY, oldSoapBody, newSoapBody);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSoapBody(SoapBody newSoapBody) {
        if (newSoapBody != soapBody) {
            NotificationChain msgs = null;
            if (soapBody != null)
                msgs = ((InternalEObject)soapBody).eInverseRemove(this, SoapPackage.SOAP_BODY__BINDING_PARAM, SoapBody.class, msgs);
            if (newSoapBody != null)
                msgs = ((InternalEObject)newSoapBody).eInverseAdd(this, SoapPackage.SOAP_BODY__BINDING_PARAM, SoapBody.class, msgs);
            msgs = basicSetSoapBody(newSoapBody, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__SOAP_BODY, newSoapBody, newSoapBody));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BindingOperation getBindingOperation() {
        if (eContainerFeatureID != WsdlPackage.BINDING_INPUT__BINDING_OPERATION) return null;
        return (BindingOperation)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBindingOperation(BindingOperation newBindingOperation) {
        if (newBindingOperation != eContainer || (eContainerFeatureID != WsdlPackage.BINDING_INPUT__BINDING_OPERATION && newBindingOperation != null)) {
            if (EcoreUtil.isAncestor(this, newBindingOperation))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBindingOperation != null)
                msgs = ((InternalEObject)newBindingOperation).eInverseAdd(this, WsdlPackage.BINDING_OPERATION__BINDING_INPUT, BindingOperation.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBindingOperation, WsdlPackage.BINDING_INPUT__BINDING_OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_INPUT__BINDING_OPERATION, newBindingOperation, newBindingOperation));
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
                case WsdlPackage.BINDING_INPUT__DOCUMENTATION:
                    if (documentation != null)
                        msgs = ((InternalEObject)documentation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_INPUT__DOCUMENTATION, null, msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__MIME_ELEMENTS:
                    return ((InternalEList)getMimeElements()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__SOAP_HEADER:
                    if (soapHeader != null)
                        msgs = ((InternalEObject)soapHeader).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_INPUT__SOAP_HEADER, null, msgs);
                    return basicSetSoapHeader((SoapHeader)otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__SOAP_BODY:
                    if (soapBody != null)
                        msgs = ((InternalEObject)soapBody).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_INPUT__SOAP_BODY, null, msgs);
                    return basicSetSoapBody((SoapBody)otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__BINDING_OPERATION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.BINDING_INPUT__BINDING_OPERATION, msgs);
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
                case WsdlPackage.BINDING_INPUT__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__MIME_ELEMENTS:
                    return ((InternalEList)getMimeElements()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING_INPUT__SOAP_HEADER:
                    return basicSetSoapHeader(null, msgs);
                case WsdlPackage.BINDING_INPUT__SOAP_BODY:
                    return basicSetSoapBody(null, msgs);
                case WsdlPackage.BINDING_INPUT__BINDING_OPERATION:
                    return eBasicSetContainer(null, WsdlPackage.BINDING_INPUT__BINDING_OPERATION, msgs);
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
                case WsdlPackage.BINDING_INPUT__BINDING_OPERATION:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING_OPERATION__BINDING_INPUT, BindingOperation.class, msgs);
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
            case WsdlPackage.BINDING_INPUT__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.BINDING_INPUT__ELEMENTS:
                return getElements();
            case WsdlPackage.BINDING_INPUT__MIME_ELEMENTS:
                return getMimeElements();
            case WsdlPackage.BINDING_INPUT__NAME:
                return getName();
            case WsdlPackage.BINDING_INPUT__SOAP_HEADER:
                return getSoapHeader();
            case WsdlPackage.BINDING_INPUT__SOAP_BODY:
                return getSoapBody();
            case WsdlPackage.BINDING_INPUT__BINDING_OPERATION:
                return getBindingOperation();
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
            case WsdlPackage.BINDING_INPUT__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING_INPUT__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING_INPUT__MIME_ELEMENTS:
                getMimeElements().clear();
                getMimeElements().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING_INPUT__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.BINDING_INPUT__SOAP_HEADER:
                setSoapHeader((SoapHeader)newValue);
                return;
            case WsdlPackage.BINDING_INPUT__SOAP_BODY:
                setSoapBody((SoapBody)newValue);
                return;
            case WsdlPackage.BINDING_INPUT__BINDING_OPERATION:
                setBindingOperation((BindingOperation)newValue);
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
            case WsdlPackage.BINDING_INPUT__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.BINDING_INPUT__ELEMENTS:
                getElements().clear();
                return;
            case WsdlPackage.BINDING_INPUT__MIME_ELEMENTS:
                getMimeElements().clear();
                return;
            case WsdlPackage.BINDING_INPUT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.BINDING_INPUT__SOAP_HEADER:
                setSoapHeader((SoapHeader)null);
                return;
            case WsdlPackage.BINDING_INPUT__SOAP_BODY:
                setSoapBody((SoapBody)null);
                return;
            case WsdlPackage.BINDING_INPUT__BINDING_OPERATION:
                setBindingOperation((BindingOperation)null);
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
            case WsdlPackage.BINDING_INPUT__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.BINDING_INPUT__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case WsdlPackage.BINDING_INPUT__MIME_ELEMENTS:
                return mimeElements != null && !mimeElements.isEmpty();
            case WsdlPackage.BINDING_INPUT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.BINDING_INPUT__SOAP_HEADER:
                return soapHeader != null;
            case WsdlPackage.BINDING_INPUT__SOAP_BODY:
                return soapBody != null;
            case WsdlPackage.BINDING_INPUT__BINDING_OPERATION:
                return getBindingOperation() != null;
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
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES: return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING_INPUT__ELEMENTS: return WsdlPackage.ELEMENT_OWNER__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == MimeElementOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING_INPUT__MIME_ELEMENTS: return MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == WsdlNameOptionalEntity.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING_INPUT__NAME: return WsdlPackage.WSDL_NAME_OPTIONAL_ENTITY__NAME;
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
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES: return WsdlPackage.BINDING_INPUT__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ELEMENT_OWNER__ELEMENTS: return WsdlPackage.BINDING_INPUT__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == MimeElementOwner.class) {
            switch (baseFeatureID) {
                case MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS: return WsdlPackage.BINDING_INPUT__MIME_ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == WsdlNameOptionalEntity.class) {
            switch (baseFeatureID) {
                case WsdlPackage.WSDL_NAME_OPTIONAL_ENTITY__NAME: return WsdlPackage.BINDING_INPUT__NAME;
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
        result.append(')');
        return result.toString();
    }

} //BindingInputImpl
