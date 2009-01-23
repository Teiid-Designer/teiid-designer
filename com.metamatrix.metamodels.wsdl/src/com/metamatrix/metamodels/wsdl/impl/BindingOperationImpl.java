/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.BindingInput;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.BindingOutput;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.http.HttpOperation;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;

import com.metamatrix.metamodels.wsdl.soap.SoapOperation;
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
 * An implementation of the model object '<em><b>Binding Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getBinding <em>Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getBindingInput <em>Binding Input</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getBindingFaults <em>Binding Faults</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getBindingOutput <em>Binding Output</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getSoapOperation <em>Soap Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl#getHttpOperation <em>Http Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BindingOperationImpl extends EObjectImpl implements BindingOperation {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

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
     * The cached value of the '{@link #getBindingInput() <em>Binding Input</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getBindingInput()
     * @generated
     * @ordered
     */
	protected BindingInput bindingInput = null;

    /**
     * The cached value of the '{@link #getBindingFaults() <em>Binding Faults</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getBindingFaults()
     * @generated
     * @ordered
     */
	protected EList bindingFaults = null;

    /**
     * The cached value of the '{@link #getBindingOutput() <em>Binding Output</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getBindingOutput()
     * @generated
     * @ordered
     */
	protected BindingOutput bindingOutput = null;

    /**
     * The cached value of the '{@link #getSoapOperation() <em>Soap Operation</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSoapOperation()
     * @generated
     * @ordered
     */
	protected SoapOperation soapOperation = null;

    /**
     * The cached value of the '{@link #getHttpOperation() <em>Http Operation</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getHttpOperation()
     * @generated
     * @ordered
     */
	protected HttpOperation httpOperation = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected BindingOperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getBindingOperation();
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__DOCUMENTATION, oldDocumentation, newDocumentation);
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__DOCUMENTATION, newDocumentation, newDocumentation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this, WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES, WsdlPackage.NAMESPACE_DECLARATION__OWNER);
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
            elements = new EObjectContainmentWithInverseEList(Element.class, this, WsdlPackage.BINDING_OPERATION__ELEMENTS, WsdlPackage.ELEMENT__ELEMENT_OWNER);
        }
        return elements;
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Binding getBinding() {
        if (eContainerFeatureID != WsdlPackage.BINDING_OPERATION__BINDING) return null;
        return (Binding)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBinding(Binding newBinding) {
        if (newBinding != eContainer || (eContainerFeatureID != WsdlPackage.BINDING_OPERATION__BINDING && newBinding != null)) {
            if (EcoreUtil.isAncestor(this, newBinding))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBinding != null)
                msgs = ((InternalEObject)newBinding).eInverseAdd(this, WsdlPackage.BINDING__BINDING_OPERATIONS, Binding.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBinding, WsdlPackage.BINDING_OPERATION__BINDING, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__BINDING, newBinding, newBinding));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BindingInput getBindingInput() {
        return bindingInput;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetBindingInput(BindingInput newBindingInput, NotificationChain msgs) {
        BindingInput oldBindingInput = bindingInput;
        bindingInput = newBindingInput;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__BINDING_INPUT, oldBindingInput, newBindingInput);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBindingInput(BindingInput newBindingInput) {
        if (newBindingInput != bindingInput) {
            NotificationChain msgs = null;
            if (bindingInput != null)
                msgs = ((InternalEObject)bindingInput).eInverseRemove(this, WsdlPackage.BINDING_INPUT__BINDING_OPERATION, BindingInput.class, msgs);
            if (newBindingInput != null)
                msgs = ((InternalEObject)newBindingInput).eInverseAdd(this, WsdlPackage.BINDING_INPUT__BINDING_OPERATION, BindingInput.class, msgs);
            msgs = basicSetBindingInput(newBindingInput, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__BINDING_INPUT, newBindingInput, newBindingInput));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getBindingFaults() {
        if (bindingFaults == null) {
            bindingFaults = new EObjectContainmentWithInverseEList(BindingFault.class, this, WsdlPackage.BINDING_OPERATION__BINDING_FAULTS, WsdlPackage.BINDING_FAULT__BINDING_OPERATION);
        }
        return bindingFaults;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BindingOutput getBindingOutput() {
        return bindingOutput;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetBindingOutput(BindingOutput newBindingOutput, NotificationChain msgs) {
        BindingOutput oldBindingOutput = bindingOutput;
        bindingOutput = newBindingOutput;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT, oldBindingOutput, newBindingOutput);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBindingOutput(BindingOutput newBindingOutput) {
        if (newBindingOutput != bindingOutput) {
            NotificationChain msgs = null;
            if (bindingOutput != null)
                msgs = ((InternalEObject)bindingOutput).eInverseRemove(this, WsdlPackage.BINDING_OUTPUT__BINDING_OPERATION, BindingOutput.class, msgs);
            if (newBindingOutput != null)
                msgs = ((InternalEObject)newBindingOutput).eInverseAdd(this, WsdlPackage.BINDING_OUTPUT__BINDING_OPERATION, BindingOutput.class, msgs);
            msgs = basicSetBindingOutput(newBindingOutput, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT, newBindingOutput, newBindingOutput));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapOperation getSoapOperation() {
        return soapOperation;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetSoapOperation(SoapOperation newSoapOperation, NotificationChain msgs) {
        SoapOperation oldSoapOperation = soapOperation;
        soapOperation = newSoapOperation;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__SOAP_OPERATION, oldSoapOperation, newSoapOperation);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSoapOperation(SoapOperation newSoapOperation) {
        if (newSoapOperation != soapOperation) {
            NotificationChain msgs = null;
            if (soapOperation != null)
                msgs = ((InternalEObject)soapOperation).eInverseRemove(this, SoapPackage.SOAP_OPERATION__BINDING_OPERATION, SoapOperation.class, msgs);
            if (newSoapOperation != null)
                msgs = ((InternalEObject)newSoapOperation).eInverseAdd(this, SoapPackage.SOAP_OPERATION__BINDING_OPERATION, SoapOperation.class, msgs);
            msgs = basicSetSoapOperation(newSoapOperation, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__SOAP_OPERATION, newSoapOperation, newSoapOperation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public HttpOperation getHttpOperation() {
        return httpOperation;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetHttpOperation(HttpOperation newHttpOperation, NotificationChain msgs) {
        HttpOperation oldHttpOperation = httpOperation;
        httpOperation = newHttpOperation;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__HTTP_OPERATION, oldHttpOperation, newHttpOperation);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setHttpOperation(HttpOperation newHttpOperation) {
        if (newHttpOperation != httpOperation) {
            NotificationChain msgs = null;
            if (httpOperation != null)
                msgs = ((InternalEObject)httpOperation).eInverseRemove(this, HttpPackage.HTTP_OPERATION__BINDING_OPERATION, HttpOperation.class, msgs);
            if (newHttpOperation != null)
                msgs = ((InternalEObject)newHttpOperation).eInverseAdd(this, HttpPackage.HTTP_OPERATION__BINDING_OPERATION, HttpOperation.class, msgs);
            msgs = basicSetHttpOperation(newHttpOperation, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.BINDING_OPERATION__HTTP_OPERATION, newHttpOperation, newHttpOperation));
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
                case WsdlPackage.BINDING_OPERATION__DOCUMENTATION:
                    if (documentation != null)
                        msgs = ((InternalEObject)documentation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_OPERATION__DOCUMENTATION, null, msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.BINDING_OPERATION__BINDING, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING_INPUT:
                    if (bindingInput != null)
                        msgs = ((InternalEObject)bindingInput).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_OPERATION__BINDING_INPUT, null, msgs);
                    return basicSetBindingInput((BindingInput)otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING_FAULTS:
                    return ((InternalEList)getBindingFaults()).basicAdd(otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT:
                    if (bindingOutput != null)
                        msgs = ((InternalEObject)bindingOutput).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT, null, msgs);
                    return basicSetBindingOutput((BindingOutput)otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__SOAP_OPERATION:
                    if (soapOperation != null)
                        msgs = ((InternalEObject)soapOperation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_OPERATION__SOAP_OPERATION, null, msgs);
                    return basicSetSoapOperation((SoapOperation)otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__HTTP_OPERATION:
                    if (httpOperation != null)
                        msgs = ((InternalEObject)httpOperation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.BINDING_OPERATION__HTTP_OPERATION, null, msgs);
                    return basicSetHttpOperation((HttpOperation)otherEnd, msgs);
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
                case WsdlPackage.BINDING_OPERATION__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING:
                    return eBasicSetContainer(null, WsdlPackage.BINDING_OPERATION__BINDING, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING_INPUT:
                    return basicSetBindingInput(null, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING_FAULTS:
                    return ((InternalEList)getBindingFaults()).basicRemove(otherEnd, msgs);
                case WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT:
                    return basicSetBindingOutput(null, msgs);
                case WsdlPackage.BINDING_OPERATION__SOAP_OPERATION:
                    return basicSetSoapOperation(null, msgs);
                case WsdlPackage.BINDING_OPERATION__HTTP_OPERATION:
                    return basicSetHttpOperation(null, msgs);
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
                case WsdlPackage.BINDING_OPERATION__BINDING:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING__BINDING_OPERATIONS, Binding.class, msgs);
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
            case WsdlPackage.BINDING_OPERATION__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.BINDING_OPERATION__ELEMENTS:
                return getElements();
            case WsdlPackage.BINDING_OPERATION__NAME:
                return getName();
            case WsdlPackage.BINDING_OPERATION__BINDING:
                return getBinding();
            case WsdlPackage.BINDING_OPERATION__BINDING_INPUT:
                return getBindingInput();
            case WsdlPackage.BINDING_OPERATION__BINDING_FAULTS:
                return getBindingFaults();
            case WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT:
                return getBindingOutput();
            case WsdlPackage.BINDING_OPERATION__SOAP_OPERATION:
                return getSoapOperation();
            case WsdlPackage.BINDING_OPERATION__HTTP_OPERATION:
                return getHttpOperation();
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
            case WsdlPackage.BINDING_OPERATION__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING:
                setBinding((Binding)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING_INPUT:
                setBindingInput((BindingInput)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING_FAULTS:
                getBindingFaults().clear();
                getBindingFaults().addAll((Collection)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT:
                setBindingOutput((BindingOutput)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__SOAP_OPERATION:
                setSoapOperation((SoapOperation)newValue);
                return;
            case WsdlPackage.BINDING_OPERATION__HTTP_OPERATION:
                setHttpOperation((HttpOperation)newValue);
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
            case WsdlPackage.BINDING_OPERATION__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.BINDING_OPERATION__ELEMENTS:
                getElements().clear();
                return;
            case WsdlPackage.BINDING_OPERATION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING:
                setBinding((Binding)null);
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING_INPUT:
                setBindingInput((BindingInput)null);
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING_FAULTS:
                getBindingFaults().clear();
                return;
            case WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT:
                setBindingOutput((BindingOutput)null);
                return;
            case WsdlPackage.BINDING_OPERATION__SOAP_OPERATION:
                setSoapOperation((SoapOperation)null);
                return;
            case WsdlPackage.BINDING_OPERATION__HTTP_OPERATION:
                setHttpOperation((HttpOperation)null);
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
            case WsdlPackage.BINDING_OPERATION__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.BINDING_OPERATION__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case WsdlPackage.BINDING_OPERATION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.BINDING_OPERATION__BINDING:
                return getBinding() != null;
            case WsdlPackage.BINDING_OPERATION__BINDING_INPUT:
                return bindingInput != null;
            case WsdlPackage.BINDING_OPERATION__BINDING_FAULTS:
                return bindingFaults != null && !bindingFaults.isEmpty();
            case WsdlPackage.BINDING_OPERATION__BINDING_OUTPUT:
                return bindingOutput != null;
            case WsdlPackage.BINDING_OPERATION__SOAP_OPERATION:
                return soapOperation != null;
            case WsdlPackage.BINDING_OPERATION__HTTP_OPERATION:
                return httpOperation != null;
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
                case WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES: return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING_OPERATION__ELEMENTS: return WsdlPackage.ELEMENT_OWNER__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == WsdlNameRequiredEntity.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.BINDING_OPERATION__NAME: return WsdlPackage.WSDL_NAME_REQUIRED_ENTITY__NAME;
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
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES: return WsdlPackage.BINDING_OPERATION__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ELEMENT_OWNER__ELEMENTS: return WsdlPackage.BINDING_OPERATION__ELEMENTS;
                default: return -1;
            }
        }
        if (baseClass == WsdlNameRequiredEntity.class) {
            switch (baseFeatureID) {
                case WsdlPackage.WSDL_NAME_REQUIRED_ENTITY__NAME: return WsdlPackage.BINDING_OPERATION__NAME;
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

} //BindingOperationImpl
