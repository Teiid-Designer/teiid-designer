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
import com.metamatrix.metamodels.wsdl.Attribute;
import com.metamatrix.metamodels.wsdl.AttributeOwner;
import com.metamatrix.metamodels.wsdl.Documentation;
import com.metamatrix.metamodels.wsdl.Documented;
import com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented;
import com.metamatrix.metamodels.wsdl.Input;
import com.metamatrix.metamodels.wsdl.NamespaceDeclaration;
import com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner;
import com.metamatrix.metamodels.wsdl.Operation;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Input</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.impl.InputImpl#getName <em>Name</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.impl.InputImpl#getDocumentation <em>Documentation</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.impl.InputImpl#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.impl.InputImpl#getAttributes <em>Attributes</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.impl.InputImpl#getMessage <em>Message</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.impl.InputImpl#getOperation <em>Operation</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class InputImpl extends EObjectImpl implements Input {

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getDocumentation() <em>Documentation</em>}' containment reference. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getDocumentation()
     * @generated
     * @ordered
     */
    protected Documentation documentation = null;

    /**
     * The cached value of the '{@link #getDeclaredNamespaces() <em>Declared Namespaces</em>}' containment reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDeclaredNamespaces()
     * @generated
     * @ordered
     */
    protected EList declaredNamespaces = null;

    /**
     * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getAttributes()
     * @generated
     * @ordered
     */
    protected EList attributes = null;

    /**
     * The default value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMessage()
     * @generated
     * @ordered
     */
    protected static final String MESSAGE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMessage()
     * @generated
     * @ordered
     */
    protected String message = MESSAGE_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected InputImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getInput();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setName( String newName ) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.INPUT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getMessage() {
        return message;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMessage( String newMessage ) {
        String oldMessage = message;
        message = newMessage;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.INPUT__MESSAGE,
                                                                   oldMessage, message));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Documentation getDocumentation() {
        return documentation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetDocumentation( Documentation newDocumentation,
                                                    NotificationChain msgs ) {
        Documentation oldDocumentation = documentation;
        documentation = newDocumentation;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WsdlPackage.INPUT__DOCUMENTATION,
                                                                   oldDocumentation, newDocumentation);
            if (msgs == null) msgs = notification;
            else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setDocumentation( Documentation newDocumentation ) {
        if (newDocumentation != documentation) {
            NotificationChain msgs = null;
            if (documentation != null) msgs = ((InternalEObject)documentation).eInverseRemove(this,
                                                                                              WsdlPackage.DOCUMENTATION__DOCUMENTED,
                                                                                              Documentation.class,
                                                                                              msgs);
            if (newDocumentation != null) msgs = ((InternalEObject)newDocumentation).eInverseAdd(this,
                                                                                                 WsdlPackage.DOCUMENTATION__DOCUMENTED,
                                                                                                 Documentation.class,
                                                                                                 msgs);
            msgs = basicSetDocumentation(newDocumentation, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          WsdlPackage.INPUT__DOCUMENTATION, newDocumentation,
                                                                          newDocumentation));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getDeclaredNamespaces() {
        if (declaredNamespaces == null) {
            declaredNamespaces = new EObjectContainmentWithInverseEList(NamespaceDeclaration.class, this,
                                                                        WsdlPackage.INPUT__DECLARED_NAMESPACES,
                                                                        WsdlPackage.NAMESPACE_DECLARATION__OWNER);
        }
        return declaredNamespaces;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getAttributes() {
        if (attributes == null) {
            attributes = new EObjectContainmentWithInverseEList(Attribute.class, this, WsdlPackage.INPUT__ATTRIBUTES,
                                                                WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER);
        }
        return attributes;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Operation getOperation() {
        if (eContainerFeatureID != WsdlPackage.INPUT__OPERATION) return null;
        return (Operation)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setOperation( Operation newOperation ) {
        if (newOperation != eContainer || (eContainerFeatureID != WsdlPackage.INPUT__OPERATION && newOperation != null)) {
            if (EcoreUtil.isAncestor(this, newOperation)) throw new IllegalArgumentException(
                                                                                             "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newOperation != null) msgs = ((InternalEObject)newOperation).eInverseAdd(this,
                                                                                         WsdlPackage.OPERATION__INPUT,
                                                                                         Operation.class,
                                                                                         msgs);
            msgs = eBasicSetContainer((InternalEObject)newOperation, WsdlPackage.INPUT__OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.INPUT__OPERATION,
                                                                          newOperation, newOperation));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isNameValid() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( InternalEObject otherEnd,
                                          int featureID,
                                          Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case WsdlPackage.INPUT__DOCUMENTATION:
                    if (documentation != null) msgs = ((InternalEObject)documentation).eInverseRemove(this,
                                                                                                      EOPPOSITE_FEATURE_BASE
                                                                                                      - WsdlPackage.INPUT__DOCUMENTATION,
                                                                                                      null,
                                                                                                      msgs);
                    return basicSetDocumentation((Documentation)otherEnd, msgs);
                case WsdlPackage.INPUT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case WsdlPackage.INPUT__ATTRIBUTES:
                    return ((InternalEList)getAttributes()).basicAdd(otherEnd, msgs);
                case WsdlPackage.INPUT__OPERATION:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.INPUT__OPERATION, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( InternalEObject otherEnd,
                                             int featureID,
                                             Class baseClass,
                                             NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case WsdlPackage.INPUT__DOCUMENTATION:
                    return basicSetDocumentation(null, msgs);
                case WsdlPackage.INPUT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case WsdlPackage.INPUT__ATTRIBUTES:
                    return ((InternalEList)getAttributes()).basicRemove(otherEnd, msgs);
                case WsdlPackage.INPUT__OPERATION:
                    return eBasicSetContainer(null, WsdlPackage.INPUT__OPERATION, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case WsdlPackage.INPUT__OPERATION:
                    return eContainer.eInverseRemove(this, WsdlPackage.OPERATION__INPUT, Operation.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet( EStructuralFeature eFeature,
                        boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WsdlPackage.INPUT__NAME:
                return getName();
            case WsdlPackage.INPUT__DOCUMENTATION:
                return getDocumentation();
            case WsdlPackage.INPUT__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case WsdlPackage.INPUT__ATTRIBUTES:
                return getAttributes();
            case WsdlPackage.INPUT__MESSAGE:
                return getMessage();
            case WsdlPackage.INPUT__OPERATION:
                return getOperation();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet( EStructuralFeature eFeature,
                      Object newValue ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WsdlPackage.INPUT__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.INPUT__DOCUMENTATION:
                setDocumentation((Documentation)newValue);
                return;
            case WsdlPackage.INPUT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case WsdlPackage.INPUT__ATTRIBUTES:
                getAttributes().clear();
                getAttributes().addAll((Collection)newValue);
                return;
            case WsdlPackage.INPUT__MESSAGE:
                setMessage((String)newValue);
                return;
            case WsdlPackage.INPUT__OPERATION:
                setOperation((Operation)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WsdlPackage.INPUT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.INPUT__DOCUMENTATION:
                setDocumentation((Documentation)null);
                return;
            case WsdlPackage.INPUT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case WsdlPackage.INPUT__ATTRIBUTES:
                getAttributes().clear();
                return;
            case WsdlPackage.INPUT__MESSAGE:
                setMessage(MESSAGE_EDEFAULT);
                return;
            case WsdlPackage.INPUT__OPERATION:
                setOperation((Operation)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WsdlPackage.INPUT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.INPUT__DOCUMENTATION:
                return documentation != null;
            case WsdlPackage.INPUT__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case WsdlPackage.INPUT__ATTRIBUTES:
                return attributes != null && !attributes.isEmpty();
            case WsdlPackage.INPUT__MESSAGE:
                return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
            case WsdlPackage.INPUT__OPERATION:
                return getOperation() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID( int derivedFeatureID,
                                         Class baseClass ) {
        if (baseClass == Documented.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.INPUT__DOCUMENTATION:
                    return WsdlPackage.DOCUMENTED__DOCUMENTATION;
                default:
                    return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.INPUT__DECLARED_NAMESPACES:
                    return WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;
                default:
                    return -1;
            }
        }
        if (baseClass == AttributeOwner.class) {
            switch (derivedFeatureID) {
                case WsdlPackage.INPUT__ATTRIBUTES:
                    return WsdlPackage.ATTRIBUTE_OWNER__ATTRIBUTES;
                default:
                    return -1;
            }
        }
        if (baseClass == ExtensibleAttributesDocumented.class) {
            switch (derivedFeatureID) {
                default:
                    return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID( int baseFeatureID,
                                            Class baseClass ) {
        if (baseClass == Documented.class) {
            switch (baseFeatureID) {
                case WsdlPackage.DOCUMENTED__DOCUMENTATION:
                    return WsdlPackage.INPUT__DOCUMENTATION;
                default:
                    return -1;
            }
        }
        if (baseClass == NamespaceDeclarationOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES:
                    return WsdlPackage.INPUT__DECLARED_NAMESPACES;
                default:
                    return -1;
            }
        }
        if (baseClass == AttributeOwner.class) {
            switch (baseFeatureID) {
                case WsdlPackage.ATTRIBUTE_OWNER__ATTRIBUTES:
                    return WsdlPackage.INPUT__ATTRIBUTES;
                default:
                    return -1;
            }
        }
        if (baseClass == ExtensibleAttributesDocumented.class) {
            switch (baseFeatureID) {
                default:
                    return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", message: "); //$NON-NLS-1$
        result.append(message);
        result.append(')');
        return result.toString();
    }

} // InputImpl
