/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.Link;
import com.metamatrix.metamodels.core.LinkContainer;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Link</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.impl.LinkImpl#getName <em>Name</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.LinkImpl#getDescription <em>Description</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.LinkImpl#getReferences <em>References</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.LinkImpl#getLinkedObjects <em>Linked Objects</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.impl.LinkImpl#getLinkContainer <em>Link Container</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class LinkImpl extends EObjectImpl implements Link {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

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
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

    /**
     * The cached value of the '{@link #getReferences() <em>References</em>}' attribute list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getReferences()
     * @generated
     * @ordered
     */
    protected EList references = null;

    /**
     * The cached value of the '{@link #getLinkedObjects() <em>Linked Objects</em>}' reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getLinkedObjects()
     * @generated
     * @ordered
     */
    protected EList linkedObjects = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected LinkImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return CorePackage.eINSTANCE.getLink();
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
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.LINK__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setDescription( String newDescription ) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.LINK__DESCRIPTION,
                                                                   oldDescription, description));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getReferences() {
        if (references == null) {
            references = new EDataTypeUniqueEList(String.class, this, CorePackage.LINK__REFERENCES);
        }
        return references;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getLinkedObjects() {
        if (linkedObjects == null) {
            linkedObjects = new EObjectResolvingEList(EObject.class, this, CorePackage.LINK__LINKED_OBJECTS);
        }
        return linkedObjects;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public LinkContainer getLinkContainer() {
        if (eContainerFeatureID != CorePackage.LINK__LINK_CONTAINER) return null;
        return (LinkContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setLinkContainer( LinkContainer newLinkContainer ) {
        if (newLinkContainer != eContainer
            || (eContainerFeatureID != CorePackage.LINK__LINK_CONTAINER && newLinkContainer != null)) {
            if (EcoreUtil.isAncestor(this, newLinkContainer)) throw new IllegalArgumentException(
                                                                                                 "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newLinkContainer != null) msgs = ((InternalEObject)newLinkContainer).eInverseAdd(this,
                                                                                                 CorePackage.LINK_CONTAINER__LINKS,
                                                                                                 LinkContainer.class,
                                                                                                 msgs);
            msgs = eBasicSetContainer((InternalEObject)newLinkContainer, CorePackage.LINK__LINK_CONTAINER, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          CorePackage.LINK__LINK_CONTAINER, newLinkContainer,
                                                                          newLinkContainer));
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
                case CorePackage.LINK__LINK_CONTAINER:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, CorePackage.LINK__LINK_CONTAINER, msgs);
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
                case CorePackage.LINK__LINK_CONTAINER:
                    return eBasicSetContainer(null, CorePackage.LINK__LINK_CONTAINER, msgs);
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
                case CorePackage.LINK__LINK_CONTAINER:
                    return eContainer.eInverseRemove(this, CorePackage.LINK_CONTAINER__LINKS, LinkContainer.class, msgs);
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
            case CorePackage.LINK__NAME:
                return getName();
            case CorePackage.LINK__DESCRIPTION:
                return getDescription();
            case CorePackage.LINK__REFERENCES:
                return getReferences();
            case CorePackage.LINK__LINKED_OBJECTS:
                return getLinkedObjects();
            case CorePackage.LINK__LINK_CONTAINER:
                return getLinkContainer();
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
            case CorePackage.LINK__NAME:
                setName((String)newValue);
                return;
            case CorePackage.LINK__DESCRIPTION:
                setDescription((String)newValue);
                return;
            case CorePackage.LINK__REFERENCES:
                getReferences().clear();
                getReferences().addAll((Collection)newValue);
                return;
            case CorePackage.LINK__LINKED_OBJECTS:
                getLinkedObjects().clear();
                getLinkedObjects().addAll((Collection)newValue);
                return;
            case CorePackage.LINK__LINK_CONTAINER:
                setLinkContainer((LinkContainer)newValue);
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
            case CorePackage.LINK__NAME:
                setName(NAME_EDEFAULT);
                return;
            case CorePackage.LINK__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case CorePackage.LINK__REFERENCES:
                getReferences().clear();
                return;
            case CorePackage.LINK__LINKED_OBJECTS:
                getLinkedObjects().clear();
                return;
            case CorePackage.LINK__LINK_CONTAINER:
                setLinkContainer((LinkContainer)null);
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
            case CorePackage.LINK__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case CorePackage.LINK__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
            case CorePackage.LINK__REFERENCES:
                return references != null && !references.isEmpty();
            case CorePackage.LINK__LINKED_OBJECTS:
                return linkedObjects != null && !linkedObjects.isEmpty();
            case CorePackage.LINK__LINK_CONTAINER:
                return getLinkContainer() != null;
        }
        return eDynamicIsSet(eFeature);
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
        result.append(", description: "); //$NON-NLS-1$
        result.append(description);
        result.append(", references: "); //$NON-NLS-1$
        result.append(references);
        result.append(')');
        return result.toString();
    }

} // LinkImpl
