/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

import com.metamatrix.metamodels.wsdl.Attribute;
import com.metamatrix.metamodels.wsdl.AttributeOwner;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Message;
import com.metamatrix.metamodels.wsdl.MessagePart;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

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
 * An implementation of the model object '<em><b>Message Part</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl#getElement <em>Element</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl#getMessage <em>Message</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MessagePartImpl extends EObjectImpl implements MessagePart {
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
     * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getAttributes()
     * @generated
     * @ordered
     */
	protected EList attributes = null;

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
     * The default value of the '{@link #getElement() <em>Element</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getElement()
     * @generated
     * @ordered
     */
	protected static final String ELEMENT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getElement() <em>Element</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getElement()
     * @generated
     * @ordered
     */
	protected String element = ELEMENT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected MessagePartImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getMessagePart();
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
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.MESSAGE_PART__DOCUMENTATION, oldDocumentation, newDocumentation);
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.MESSAGE_PART__DOCUMENTATION, newDocumentation, newDocumentation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this, WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES, WsdlPackage.NAMESPACE_DECLARATION__OWNER);
        }
        return declaredNamespaces;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getAttributes() {
        if (attributes == null) {
            attributes = new EObjectContainmentWithInverseEList(Attribute.class, this, WsdlPackage.MESSAGE_PART__ATTRIBUTES, WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER);
        }
        return attributes;
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.MESSAGE_PART__NAME, oldName, name));
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
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.MESSAGE_PART__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getElement() {
        return element;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setElement(String newElement) {
        String oldElement = element;
        element = newElement;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.MESSAGE_PART__ELEMENT, oldElement, element));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Message getMessage() {
        if (eContainerFeatureID != WsdlPackage.MESSAGE_PART__MESSAGE) return null;
        return (Message)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setMessage(Message newMessage) {
        if (newMessage != eContainer || (eContainerFeatureID != WsdlPackage.MESSAGE_PART__MESSAGE && newMessage != null)) {
            if (EcoreUtil.isAncestor(this, newMessage))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMessage != null)
                msgs = ((InternalEObject)newMessage).eInverseAdd(this, WsdlPackage.MESSAGE__PARTS, Message.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMessage, WsdlPackage.MESSAGE_PART__MESSAGE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.MESSAGE_PART__MESSAGE, newMessage, newMessage));
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
                case WsdlPackage.MESSAGE_PART__DOCUMENTATION:
                    if (documentation != null)
                        msgs = ((InternalEObject)documentation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WsdlPackage.MESSAGE_PART__DOCUMENTATION, null, msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.MESSAGE_PART__ATTRIBUTES:
                    return ((InternalEList)getAttributes()).basicAdd(otherEnd, msgs);
                case WsdlPackage.MESSAGE_PART__MESSAGE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.MESSAGE_PART__MESSAGE, msgs);
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
                case WsdlPackage.MESSAGE_PART__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.MESSAGE_PART__ATTRIBUTES:
                    return ((InternalEList)getAttributes()).basicRemove(otherEnd, msgs);
                case WsdlPackage.MESSAGE_PART__MESSAGE:
                    return eBasicSetContainer(null, WsdlPackage.MESSAGE_PART__MESSAGE, msgs);
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
                case WsdlPackage.MESSAGE_PART__MESSAGE:
                    return eContainer.eInverseRemove(this, WsdlPackage.MESSAGE__PARTS, Message.class, msgs);
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
            case WsdlPackage.MESSAGE_PART__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.MESSAGE_PART__ATTRIBUTES:
                return getAttributes();
            case WsdlPackage.MESSAGE_PART__NAME:
                return getName();
            case WsdlPackage.MESSAGE_PART__TYPE:
                return getType();
            case WsdlPackage.MESSAGE_PART__ELEMENT:
                return getElement();
            case WsdlPackage.MESSAGE_PART__MESSAGE:
                return getMessage();
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
            case WsdlPackage.MESSAGE_PART__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.MESSAGE_PART__ATTRIBUTES:
                getAttributes().clear();
                getAttributes().addAll((Collection)newValue);
                return;
            case WsdlPackage.MESSAGE_PART__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.MESSAGE_PART__TYPE:
                setType((String)newValue);
                return;
            case WsdlPackage.MESSAGE_PART__ELEMENT:
                setElement((String)newValue);
                return;
            case WsdlPackage.MESSAGE_PART__MESSAGE:
                setMessage((Message)newValue);
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
            case WsdlPackage.MESSAGE_PART__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.MESSAGE_PART__ATTRIBUTES:
                getAttributes().clear();
                return;
            case WsdlPackage.MESSAGE_PART__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.MESSAGE_PART__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case WsdlPackage.MESSAGE_PART__ELEMENT:
                setElement(ELEMENT_EDEFAULT);
                return;
            case WsdlPackage.MESSAGE_PART__MESSAGE:
                setMessage((Message)null);
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
            case WsdlPackage.MESSAGE_PART__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.MESSAGE_PART__ATTRIBUTES:
                return attributes != null && !attributes.isEmpty();
            case WsdlPackage.MESSAGE_PART__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.MESSAGE_PART__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case WsdlPackage.MESSAGE_PART__ELEMENT:
                return ELEMENT_EDEFAULT == null ? element != null : !ELEMENT_EDEFAULT.equals(element);
            case WsdlPackage.MESSAGE_PART__MESSAGE:
                return getMessage() != null;
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
                case WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES: return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == AttributeOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.MESSAGE_PART__ATTRIBUTES: return WsdlPackage.ATTRIBUTE_OWNER__ATTRIBUTES;
                default: return -1;
            }
        }
        if (baseClass == WsdlNameOptionalEntity.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.MESSAGE_PART__NAME: return WsdlPackage.WSDL_NAME_OPTIONAL_ENTITY__NAME;
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
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES: return WsdlPackage.MESSAGE_PART__DECLARED_NAMESPACES;
                default: return -1;
            }
        }
        if (baseClass == AttributeOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ATTRIBUTE_OWNER__ATTRIBUTES: return WsdlPackage.MESSAGE_PART__ATTRIBUTES;
                default: return -1;
            }
        }
        if (baseClass == WsdlNameOptionalEntity.class) {
            switch (baseFeatureID) {
                case WsdlPackage.WSDL_NAME_OPTIONAL_ENTITY__NAME: return WsdlPackage.MESSAGE_PART__NAME;
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
        result.append(", element: "); //$NON-NLS-1$
        result.append(element);
        result.append(')');
        return result.toString();
    }

} //MessagePartImpl
