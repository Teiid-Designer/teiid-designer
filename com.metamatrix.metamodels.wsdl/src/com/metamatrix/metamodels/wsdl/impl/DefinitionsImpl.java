/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.Element;
import com.metamatrix.metamodels.wsdl.ElementOwner;
import com.metamatrix.metamodels.wsdl.ExtensibleDocumented;
import com.metamatrix.metamodels.wsdl.Import;
import com.metamatrix.metamodels.wsdl.Message;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.PortType;
import com.metamatrix.metamodels.wsdl.Service;
import com.metamatrix.metamodels.wsdl.Types;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Definitions</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getElements <em>Elements</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getTargetNamespace <em>Target Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getMessages <em>Messages</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getPortTypes <em>Port Types</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getBindings <em>Bindings</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getServices <em>Services</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getImports <em>Imports</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl#getTypes <em>Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DefinitionsImpl extends EObjectImpl implements Definitions {
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
     * The default value of the '{@link #getTargetNamespace() <em>Target Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTargetNamespace()
     * @generated
     * @ordered
     */
	protected static final String TARGET_NAMESPACE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTargetNamespace() <em>Target Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTargetNamespace()
     * @generated
     * @ordered
     */
	protected String targetNamespace = TARGET_NAMESPACE_EDEFAULT;

    /**
     * The cached value of the '{@link #getMessages() <em>Messages</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getMessages()
     * @generated
     * @ordered
     */
	protected EList messages = null;

    /**
     * The cached value of the '{@link #getPortTypes() <em>Port Types</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getPortTypes()
     * @generated
     * @ordered
     */
	protected EList portTypes = null;

    /**
     * The cached value of the '{@link #getBindings() <em>Bindings</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getBindings()
     * @generated
     * @ordered
     */
	protected EList bindings = null;

    /**
     * The cached value of the '{@link #getServices() <em>Services</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getServices()
     * @generated
     * @ordered
     */
	protected EList services = null;

    /**
     * The cached value of the '{@link #getImports() <em>Imports</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getImports()
     * @generated
     * @ordered
     */
	protected EList imports = null;

    /**
     * The cached value of the '{@link #getTypes() <em>Types</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTypes()
     * @generated
     * @ordered
     */
	protected Types types = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected DefinitionsImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getDefinitions();
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.DEFINITIONS__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this, WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES, WsdlPackage.NAMESPACE_DECLARATION__OWNER);
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
            elements = new EObjectContainmentWithInverseEList(Element.class, this, WsdlPackage.DEFINITIONS__ELEMENTS, WsdlPackage.ELEMENT__ELEMENT_OWNER);
        }
        return elements;
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.DEFINITIONS__DOCUMENTATION, oldDocumentation, newDocumentation);
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.DEFINITIONS__DOCUMENTATION, newDocumentation, newDocumentation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setTargetNamespace(String newTargetNamespace) {
        String oldTargetNamespace = targetNamespace;
        targetNamespace = newTargetNamespace;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.DEFINITIONS__TARGET_NAMESPACE, oldTargetNamespace, targetNamespace));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getMessages() {
        if (messages == null) {
            messages = new EObjectContainmentWithInverseEList(Message.class, this, WsdlPackage.DEFINITIONS__MESSAGES, WsdlPackage.MESSAGE__DEFINITIONS);
        }
        return messages;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getPortTypes() {
        if (portTypes == null) {
            portTypes = new EObjectContainmentWithInverseEList(PortType.class, this, WsdlPackage.DEFINITIONS__PORT_TYPES, WsdlPackage.PORT_TYPE__DEFINITIONS);
        }
        return portTypes;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getBindings() {
        if (bindings == null) {
            bindings = new EObjectContainmentWithInverseEList(Binding.class, this, WsdlPackage.DEFINITIONS__BINDINGS, WsdlPackage.BINDING__DEFINITIONS);
        }
        return bindings;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getServices() {
        if (services == null) {
            services = new EObjectContainmentWithInverseEList(Service.class, this, WsdlPackage.DEFINITIONS__SERVICES, WsdlPackage.SERVICE__DEFINITIONS);
        }
        return services;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getImports() {
        if (imports == null) {
            imports = new EObjectContainmentWithInverseEList(Import.class, this, WsdlPackage.DEFINITIONS__IMPORTS, WsdlPackage.IMPORT__DEFINITIONS);
        }
        return imports;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Types getTypes() {
        return types;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetTypes(Types newTypes, NotificationChain msgs) {
        Types oldTypes = types;
        types = newTypes;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.DEFINITIONS__TYPES, oldTypes, newTypes);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setTypes(Types newTypes) {
        if (newTypes != types) {
            NotificationChain msgs = null;
            if (types != null)
                msgs = ((InternalEObject)types).eInverseRemove(this, WsdlPackage.TYPES__DEFINITIONS, Types.class, msgs);
            if (newTypes != null)
                msgs = ((InternalEObject)newTypes).eInverseAdd(this, WsdlPackage.TYPES__DEFINITIONS, Types.class, msgs);
            msgs = basicSetTypes(newTypes, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.DEFINITIONS__TYPES, newTypes, newTypes));
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
	public IStatus isValid() {
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
                case WsdlPackage.DEFINITIONS__DOCUMENTATION:
                    if (documentation != null)
                        msgs = ((InternalEObject)documentation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.DEFINITIONS__DOCUMENTATION, null, msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__MESSAGES:
                    return ((InternalEList)getMessages()).basicAdd(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__PORT_TYPES:
                    return ((InternalEList)getPortTypes()).basicAdd(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__BINDINGS:
                    return ((InternalEList)getBindings()).basicAdd(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__SERVICES:
                    return ((InternalEList)getServices()).basicAdd(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__IMPORTS:
                    return ((InternalEList)getImports()).basicAdd(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__TYPES:
                    if (types != null)
                        msgs = ((InternalEObject)types).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.DEFINITIONS__TYPES, null, msgs);
                    return basicSetTypes((Types)otherEnd, msgs);
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
                case WsdlPackage.DEFINITIONS__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__MESSAGES:
                    return ((InternalEList)getMessages()).basicRemove(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__PORT_TYPES:
                    return ((InternalEList)getPortTypes()).basicRemove(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__BINDINGS:
                    return ((InternalEList)getBindings()).basicRemove(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__SERVICES:
                    return ((InternalEList)getServices()).basicRemove(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__IMPORTS:
                    return ((InternalEList)getImports()).basicRemove(otherEnd, msgs);
                case WsdlPackage.DEFINITIONS__TYPES:
                    return basicSetTypes(null, msgs);
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
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WsdlPackage.DEFINITIONS__NAME:
                return getName();
            case WsdlPackage.DEFINITIONS__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.DEFINITIONS__ELEMENTS:
                return getElements();
            case WsdlPackage.DEFINITIONS__TARGET_NAMESPACE:
                return getTargetNamespace();
            case WsdlPackage.DEFINITIONS__MESSAGES:
                return getMessages();
            case WsdlPackage.DEFINITIONS__PORT_TYPES:
                return getPortTypes();
            case WsdlPackage.DEFINITIONS__BINDINGS:
                return getBindings();
            case WsdlPackage.DEFINITIONS__SERVICES:
                return getServices();
            case WsdlPackage.DEFINITIONS__IMPORTS:
                return getImports();
            case WsdlPackage.DEFINITIONS__TYPES:
                return getTypes();
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
            case WsdlPackage.DEFINITIONS__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.DEFINITIONS__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.DEFINITIONS__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case WsdlPackage.DEFINITIONS__TARGET_NAMESPACE:
                setTargetNamespace((String)newValue);
                return;
            case WsdlPackage.DEFINITIONS__MESSAGES:
                getMessages().clear();
                getMessages().addAll((Collection)newValue);
                return;
            case WsdlPackage.DEFINITIONS__PORT_TYPES:
                getPortTypes().clear();
                getPortTypes().addAll((Collection)newValue);
                return;
            case WsdlPackage.DEFINITIONS__BINDINGS:
                getBindings().clear();
                getBindings().addAll((Collection)newValue);
                return;
            case WsdlPackage.DEFINITIONS__SERVICES:
                getServices().clear();
                getServices().addAll((Collection)newValue);
                return;
            case WsdlPackage.DEFINITIONS__IMPORTS:
                getImports().clear();
                getImports().addAll((Collection)newValue);
                return;
            case WsdlPackage.DEFINITIONS__TYPES:
                setTypes((Types)newValue);
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
            case WsdlPackage.DEFINITIONS__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.DEFINITIONS__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.DEFINITIONS__ELEMENTS:
                getElements().clear();
                return;
            case WsdlPackage.DEFINITIONS__TARGET_NAMESPACE:
                setTargetNamespace(TARGET_NAMESPACE_EDEFAULT);
                return;
            case WsdlPackage.DEFINITIONS__MESSAGES:
                getMessages().clear();
                return;
            case WsdlPackage.DEFINITIONS__PORT_TYPES:
                getPortTypes().clear();
                return;
            case WsdlPackage.DEFINITIONS__BINDINGS:
                getBindings().clear();
                return;
            case WsdlPackage.DEFINITIONS__SERVICES:
                getServices().clear();
                return;
            case WsdlPackage.DEFINITIONS__IMPORTS:
                getImports().clear();
                return;
            case WsdlPackage.DEFINITIONS__TYPES:
                setTypes((Types)null);
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
            case WsdlPackage.DEFINITIONS__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.DEFINITIONS__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.DEFINITIONS__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case WsdlPackage.DEFINITIONS__TARGET_NAMESPACE:
                return TARGET_NAMESPACE_EDEFAULT == null ? targetNamespace != null : !TARGET_NAMESPACE_EDEFAULT.equals(targetNamespace);
            case WsdlPackage.DEFINITIONS__MESSAGES:
                return messages != null && !messages.isEmpty();
            case WsdlPackage.DEFINITIONS__PORT_TYPES:
                return portTypes != null && !portTypes.isEmpty();
            case WsdlPackage.DEFINITIONS__BINDINGS:
                return bindings != null && !bindings.isEmpty();
            case WsdlPackage.DEFINITIONS__SERVICES:
                return services != null && !services.isEmpty();
            case WsdlPackage.DEFINITIONS__IMPORTS:
                return imports != null && !imports.isEmpty();
            case WsdlPackage.DEFINITIONS__TYPES:
                return types != null;
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
                case WsdlPackage.DEFINITIONS__DOCUMENTATION: return WsdlPackage.DOCUMENTED__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES: return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.DEFINITIONS__ELEMENTS: return WsdlPackage.ELEMENT_OWNER__ELEMENTS;
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
                case WsdlPackage.DOCUMENTED__DOCUMENTATION: return WsdlPackage.DEFINITIONS__DOCUMENTATION;
                default: return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES: return WsdlPackage.DEFINITIONS__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == ElementOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ELEMENT_OWNER__ELEMENTS: return WsdlPackage.DEFINITIONS__ELEMENTS;
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
        result.append(", targetNamespace: "); //$NON-NLS-1$
        result.append(targetNamespace);
        result.append(')');
        return result.toString();
    }

} //DefinitionsImpl
