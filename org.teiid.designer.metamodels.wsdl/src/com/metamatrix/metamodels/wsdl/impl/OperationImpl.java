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
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
import com.metamatrix.metamodels.wsdl.ExtensibleDocumented;
import com.metamatrix.metamodels.wsdl.Fault;
import com.metamatrix.metamodels.wsdl.Input;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.Operation;
import com.metamatrix.metamodels.wsdl.Output;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getParameterOrder <em>Parameter Order</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getPortType <em>Port Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getInput <em>Input</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getOutput <em>Output</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl#getFaults <em>Faults</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OperationImpl extends EObjectImpl implements Operation {
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
     * The default value of the '{@link #getParameterOrder() <em>Parameter Order</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getParameterOrder()
     * @generated
     * @ordered
     */
	protected static final String PARAMETER_ORDER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getParameterOrder() <em>Parameter Order</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getParameterOrder()
     * @generated
     * @ordered
     */
	protected String parameterOrder = PARAMETER_ORDER_EDEFAULT;

    /**
     * The cached value of the '{@link #getInput() <em>Input</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getInput()
     * @generated
     * @ordered
     */
	protected Input input = null;

    /**
     * The cached value of the '{@link #getOutput() <em>Output</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getOutput()
     * @generated
     * @ordered
     */
	protected Output output = null;

    /**
     * The cached value of the '{@link #getFaults() <em>Faults</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getFaults()
     * @generated
     * @ordered
     */
	protected EList faults = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected OperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getOperation();
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__DOCUMENTATION, oldDocumentation, newDocumentation);
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__DOCUMENTATION, newDocumentation, newDocumentation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this, WsdlPackage.OPERATION__DECLARED_NAMESPACES, WsdlPackage.NAMESPACE_DECLARATION__OWNER);
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
            elements = new EObjectContainmentWithInverseEList(Element.class, this, WsdlPackage.OPERATION__ELEMENTS, WsdlPackage.ELEMENT__ELEMENT_OWNER);
        }
        return elements;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getParameterOrder() {
        return parameterOrder;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setParameterOrder(String newParameterOrder) {
        String oldParameterOrder = parameterOrder;
        parameterOrder = newParameterOrder;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__PARAMETER_ORDER, oldParameterOrder, parameterOrder));
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public PortType getPortType() {
        if (eContainerFeatureID != WsdlPackage.OPERATION__PORT_TYPE) return null;
        return (PortType)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setPortType(PortType newPortType) {
        if (newPortType != eContainer || (eContainerFeatureID != WsdlPackage.OPERATION__PORT_TYPE && newPortType != null)) {
            if (EcoreUtil.isAncestor(this, newPortType))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newPortType != null)
                msgs = ((InternalEObject)newPortType).eInverseAdd(this, WsdlPackage.PORT_TYPE__OPERATIONS, PortType.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newPortType, WsdlPackage.OPERATION__PORT_TYPE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__PORT_TYPE, newPortType, newPortType));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Input getInput() {
        return input;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetInput(Input newInput, NotificationChain msgs) {
        Input oldInput = input;
        input = newInput;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__INPUT, oldInput, newInput);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setInput(Input newInput) {
        if (newInput != input) {
            NotificationChain msgs = null;
            if (input != null)
                msgs = ((InternalEObject)input).eInverseRemove(this, WsdlPackage.INPUT__OPERATION, Input.class, msgs);
            if (newInput != null)
                msgs = ((InternalEObject)newInput).eInverseAdd(this, WsdlPackage.INPUT__OPERATION, Input.class, msgs);
            msgs = basicSetInput(newInput, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__INPUT, newInput, newInput));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Output getOutput() {
        return output;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetOutput(Output newOutput, NotificationChain msgs) {
        Output oldOutput = output;
        output = newOutput;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__OUTPUT, oldOutput, newOutput);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setOutput(Output newOutput) {
        if (newOutput != output) {
            NotificationChain msgs = null;
            if (output != null)
                msgs = ((InternalEObject)output).eInverseRemove(this, WsdlPackage.OUTPUT__OPERATION, Output.class, msgs);
            if (newOutput != null)
                msgs = ((InternalEObject)newOutput).eInverseAdd(this, WsdlPackage.OUTPUT__OPERATION, Output.class, msgs);
            msgs = basicSetOutput(newOutput, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.OPERATION__OUTPUT, newOutput, newOutput));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getFaults() {
        if (faults == null) {
            faults = new EObjectContainmentWithInverseEList(Fault.class, this, WsdlPackage.OPERATION__FAULTS, WsdlPackage.FAULT__OPERATION);
        }
        return faults;
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
                case WsdlPackage.OPERATION__DOCUMENTATION:
                    if (documentation != null)
                        msgs = ((InternalEObject)documentation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.OPERATION__DOCUMENTATION, null, msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.OPERATION__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.OPERATION__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case WsdlPackage.OPERATION__PORT_TYPE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.OPERATION__PORT_TYPE, msgs);
                case WsdlPackage.OPERATION__INPUT:
                    if (input != null)
                        msgs = ((InternalEObject)input).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.OPERATION__INPUT, null, msgs);
                    return basicSetInput((Input)otherEnd, msgs);
                case WsdlPackage.OPERATION__OUTPUT:
                    if (output != null)
                        msgs = ((InternalEObject)output).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.OPERATION__OUTPUT, null, msgs);
                    return basicSetOutput((Output)otherEnd, msgs);
                case WsdlPackage.OPERATION__FAULTS:
                    return ((InternalEList)getFaults()).basicAdd(otherEnd, msgs);
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
                case WsdlPackage.OPERATION__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.OPERATION__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.OPERATION__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case WsdlPackage.OPERATION__PORT_TYPE:
                    return eBasicSetContainer(null, WsdlPackage.OPERATION__PORT_TYPE, msgs);
                case WsdlPackage.OPERATION__INPUT:
                    return basicSetInput(null, msgs);
                case WsdlPackage.OPERATION__OUTPUT:
                    return basicSetOutput(null, msgs);
                case WsdlPackage.OPERATION__FAULTS:
                    return ((InternalEList)getFaults()).basicRemove(otherEnd, msgs);
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
                case WsdlPackage.OPERATION__PORT_TYPE:
                    return eContainer.eInverseRemove(this, WsdlPackage.PORT_TYPE__OPERATIONS, PortType.class, msgs);
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
            case WsdlPackage.OPERATION__NAME:
                return getName();
            case WsdlPackage.OPERATION__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.OPERATION__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.OPERATION__ELEMENTS:
                return getElements();
            case WsdlPackage.OPERATION__PARAMETER_ORDER:
                return getParameterOrder();
            case WsdlPackage.OPERATION__PORT_TYPE:
                return getPortType();
            case WsdlPackage.OPERATION__INPUT:
                return getInput();
            case WsdlPackage.OPERATION__OUTPUT:
                return getOutput();
            case WsdlPackage.OPERATION__FAULTS:
                return getFaults();
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
            case WsdlPackage.OPERATION__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.OPERATION__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.OPERATION__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.OPERATION__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case WsdlPackage.OPERATION__PARAMETER_ORDER:
                setParameterOrder((String)newValue);
                return;
            case WsdlPackage.OPERATION__PORT_TYPE:
                setPortType((PortType)newValue);
                return;
            case WsdlPackage.OPERATION__INPUT:
                setInput((Input)newValue);
                return;
            case WsdlPackage.OPERATION__OUTPUT:
                setOutput((Output)newValue);
                return;
            case WsdlPackage.OPERATION__FAULTS:
                getFaults().clear();
                getFaults().addAll((Collection)newValue);
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
            case WsdlPackage.OPERATION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.OPERATION__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.OPERATION__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.OPERATION__ELEMENTS:
                getElements().clear();
                return;
            case WsdlPackage.OPERATION__PARAMETER_ORDER:
                setParameterOrder(PARAMETER_ORDER_EDEFAULT);
                return;
            case WsdlPackage.OPERATION__PORT_TYPE:
                setPortType((PortType)null);
                return;
            case WsdlPackage.OPERATION__INPUT:
                setInput((Input)null);
                return;
            case WsdlPackage.OPERATION__OUTPUT:
                setOutput((Output)null);
                return;
            case WsdlPackage.OPERATION__FAULTS:
                getFaults().clear();
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
            case WsdlPackage.OPERATION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.OPERATION__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.OPERATION__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.OPERATION__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case WsdlPackage.OPERATION__PARAMETER_ORDER:
                return PARAMETER_ORDER_EDEFAULT == null ? parameterOrder != null : !PARAMETER_ORDER_EDEFAULT.equals(parameterOrder);
            case WsdlPackage.OPERATION__PORT_TYPE:
                return getPortType() != null;
            case WsdlPackage.OPERATION__INPUT:
                return input != null;
            case WsdlPackage.OPERATION__OUTPUT:
                return output != null;
            case WsdlPackage.OPERATION__FAULTS:
                return faults != null && !faults.isEmpty();
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
                case WsdlPackage.OPERATION__DOCUMENTATION: return WsdlPackage.DOCUMENTED__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.OPERATION__DECLARED_NAMESPACES: return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.OPERATION__ELEMENTS: return WsdlPackage.ELEMENT_OWNER__ELEMENTS;
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
                case WsdlPackage.DOCUMENTED__DOCUMENTATION: return WsdlPackage.OPERATION__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES: return WsdlPackage.OPERATION__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ELEMENT_OWNER__ELEMENTS: return WsdlPackage.OPERATION__ELEMENTS;
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
        result.append(", parameterOrder: "); //$NON-NLS-1$
        result.append(parameterOrder);
        result.append(')');
        return result.toString();
    }

} //OperationImpl
