/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

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
import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
import com.metamatrix.metamodels.wsdl.ExtensibleDocumented;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpBinding;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapBinding;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getBindingOperations <em>Binding Operations</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getSoapBinding <em>Soap Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl#getHttpBinding <em>Http Binding</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BindingImpl extends EObjectImpl implements Binding {
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
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
	protected static final String TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
	protected String type = TYPE_EDEFAULT;

    /**
     * The cached value of the '{@link #getBindingOperations() <em>Binding Operations</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getBindingOperations()
     * @generated
     * @ordered
     */
	protected EList bindingOperations = null;

    /**
     * The cached value of the '{@link #getSoapBinding() <em>Soap Binding</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSoapBinding()
     * @generated
     * @ordered
     */
	protected SoapBinding soapBinding = null;

    /**
     * The cached value of the '{@link #getHttpBinding() <em>Http Binding</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getHttpBinding()
     * @generated
     * @ordered
     */
	protected HttpBinding httpBinding = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected BindingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getBinding();
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__NAME, oldName, name));
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__DOCUMENTATION, oldDocumentation, newDocumentation);
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__DOCUMENTATION, newDocumentation, newDocumentation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this, WsdlPackage.BINDING__DECLARED_NAMESPACES, WsdlPackage.NAMESPACE_DECLARATION__OWNER);
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
            elements = new EObjectContainmentWithInverseEList(Element.class, this, WsdlPackage.BINDING__ELEMENTS, WsdlPackage.ELEMENT__ELEMENT_OWNER);
        }
        return elements;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setType(String newType) {
        String oldType = type;
        type = newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Definitions getDefinitions() {
        if (eContainerFeatureID != WsdlPackage.BINDING__DEFINITIONS) return null;
        return (Definitions)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setDefinitions(Definitions newDefinitions) {
        if (newDefinitions != eContainer || (eContainerFeatureID != WsdlPackage.BINDING__DEFINITIONS && newDefinitions != null)) {
            if (EcoreUtil.isAncestor(this, newDefinitions))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newDefinitions != null)
                msgs = ((InternalEObject)newDefinitions).eInverseAdd(this, WsdlPackage.DEFINITIONS__BINDINGS, Definitions.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newDefinitions, WsdlPackage.BINDING__DEFINITIONS, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__DEFINITIONS, newDefinitions, newDefinitions));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getBindingOperations() {
        if (bindingOperations == null) {
            bindingOperations = new EObjectContainmentWithInverseEList(BindingOperation.class, this, WsdlPackage.BINDING__BINDING_OPERATIONS, WsdlPackage.BINDING_OPERATION__BINDING);
        }
        return bindingOperations;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapBinding getSoapBinding() {
        return soapBinding;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetSoapBinding(SoapBinding newSoapBinding, NotificationChain msgs) {
        SoapBinding oldSoapBinding = soapBinding;
        soapBinding = newSoapBinding;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__SOAP_BINDING, oldSoapBinding, newSoapBinding);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSoapBinding(SoapBinding newSoapBinding) {
        if (newSoapBinding != soapBinding) {
            NotificationChain msgs = null;
            if (soapBinding != null)
                msgs = ((InternalEObject)soapBinding).eInverseRemove(this, SoapPackage.SOAP_BINDING__BINDING, SoapBinding.class, msgs);
            if (newSoapBinding != null)
                msgs = ((InternalEObject)newSoapBinding).eInverseAdd(this, SoapPackage.SOAP_BINDING__BINDING, SoapBinding.class, msgs);
            msgs = basicSetSoapBinding(newSoapBinding, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__SOAP_BINDING, newSoapBinding, newSoapBinding));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public HttpBinding getHttpBinding() {
        return httpBinding;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetHttpBinding(HttpBinding newHttpBinding, NotificationChain msgs) {
        HttpBinding oldHttpBinding = httpBinding;
        httpBinding = newHttpBinding;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__HTTP_BINDING, oldHttpBinding, newHttpBinding);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setHttpBinding(HttpBinding newHttpBinding) {
        if (newHttpBinding != httpBinding) {
            NotificationChain msgs = null;
            if (httpBinding != null)
                msgs = ((InternalEObject)httpBinding).eInverseRemove(this, HttpPackage.HTTP_BINDING__BINDING, HttpBinding.class, msgs);
            if (newHttpBinding != null)
                msgs = ((InternalEObject)newHttpBinding).eInverseAdd(this, HttpPackage.HTTP_BINDING__BINDING, HttpBinding.class, msgs);
            msgs = basicSetHttpBinding(newHttpBinding, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING__HTTP_BINDING, newHttpBinding, newHttpBinding));
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
                case WsdlPackage.BINDING__DOCUMENTATION:
                    if (documentation != null)
                        msgs = ((InternalEObject)documentation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING__DOCUMENTATION, null, msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.BINDING__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING__DEFINITIONS:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.BINDING__DEFINITIONS, msgs);
                case WsdlPackage.BINDING__BINDING_OPERATIONS:
                    return ((InternalEList)getBindingOperations()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING__SOAP_BINDING:
                    if (soapBinding != null)
                        msgs = ((InternalEObject)soapBinding).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING__SOAP_BINDING, null, msgs);
                    return basicSetSoapBinding((SoapBinding)otherEnd, msgs);
                case WsdlPackage.BINDING__HTTP_BINDING:
                    if (httpBinding != null)
                        msgs = ((InternalEObject)httpBinding).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING__HTTP_BINDING, null, msgs);
                    return basicSetHttpBinding((HttpBinding)otherEnd, msgs);
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
                case WsdlPackage.BINDING__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.BINDING__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING__DEFINITIONS:
                    return eBasicSetContainer(null, WsdlPackage.BINDING__DEFINITIONS, msgs);
                case WsdlPackage.BINDING__BINDING_OPERATIONS:
                    return ((InternalEList)getBindingOperations()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING__SOAP_BINDING:
                    return basicSetSoapBinding(null, msgs);
                case WsdlPackage.BINDING__HTTP_BINDING:
                    return basicSetHttpBinding(null, msgs);
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
                case WsdlPackage.BINDING__DEFINITIONS:
                    return eContainer.eInverseRemove(this, WsdlPackage.DEFINITIONS__BINDINGS, Definitions.class, msgs);
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
            case WsdlPackage.BINDING__NAME:
                return getName();
            case WsdlPackage.BINDING__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.BINDING__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.BINDING__ELEMENTS:
                return getElements();
            case WsdlPackage.BINDING__TYPE:
                return getType();
            case WsdlPackage.BINDING__DEFINITIONS:
                return getDefinitions();
            case WsdlPackage.BINDING__BINDING_OPERATIONS:
                return getBindingOperations();
            case WsdlPackage.BINDING__SOAP_BINDING:
                return getSoapBinding();
            case WsdlPackage.BINDING__HTTP_BINDING:
                return getHttpBinding();
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
            case WsdlPackage.BINDING__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.BINDING__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.BINDING__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING__TYPE:
                setType((String)newValue);
                return;
            case WsdlPackage.BINDING__DEFINITIONS:
                setDefinitions((Definitions)newValue);
                return;
            case WsdlPackage.BINDING__BINDING_OPERATIONS:
                getBindingOperations().clear();
                getBindingOperations().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING__SOAP_BINDING:
                setSoapBinding((SoapBinding)newValue);
                return;
            case WsdlPackage.BINDING__HTTP_BINDING:
                setHttpBinding((HttpBinding)newValue);
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
            case WsdlPackage.BINDING__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.BINDING__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.BINDING__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.BINDING__ELEMENTS:
                getElements().clear();
                return;
            case WsdlPackage.BINDING__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case WsdlPackage.BINDING__DEFINITIONS:
                setDefinitions((Definitions)null);
                return;
            case WsdlPackage.BINDING__BINDING_OPERATIONS:
                getBindingOperations().clear();
                return;
            case WsdlPackage.BINDING__SOAP_BINDING:
                setSoapBinding((SoapBinding)null);
                return;
            case WsdlPackage.BINDING__HTTP_BINDING:
                setHttpBinding((HttpBinding)null);
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
            case WsdlPackage.BINDING__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.BINDING__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.BINDING__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.BINDING__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case WsdlPackage.BINDING__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case WsdlPackage.BINDING__DEFINITIONS:
                return getDefinitions() != null;
            case WsdlPackage.BINDING__BINDING_OPERATIONS:
                return bindingOperations != null && !bindingOperations.isEmpty();
            case WsdlPackage.BINDING__SOAP_BINDING:
                return soapBinding != null;
            case WsdlPackage.BINDING__HTTP_BINDING:
                return httpBinding != null;
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
                case WsdlPackage.BINDING__DOCUMENTATION: return WsdlPackage.DOCUMENTED__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING__DECLARED_NAMESPACES: return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING__ELEMENTS: return WsdlPackage.ELEMENT_OWNER__ELEMENTS;
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
                case WsdlPackage.DOCUMENTED__DOCUMENTATION: return WsdlPackage.BINDING__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES: return WsdlPackage.BINDING__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ELEMENT_OWNER__ELEMENTS: return WsdlPackage.BINDING__ELEMENTS;
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
        result.append(", type: "); //$NON-NLS-1$
        result.append(type);
        result.append(')');
        return result.toString();
    }

} //BindingImpl
