/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.metamodels.diagram.DiagramPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl#getNotation <em>Notation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl#getLinkType <em>Link Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl#getDiagramEntity <em>Diagram Entity</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl#getDiagramContainer <em>Diagram Container</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl#getDiagramLinks <em>Diagram Links</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DiagramImpl extends PresentationEntityImpl implements Diagram {

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
     * The default value of the '{@link #getNotation() <em>Notation</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNotation()
     * @generated
     * @ordered
     */
    protected static final String NOTATION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNotation() <em>Notation</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNotation()
     * @generated
     * @ordered
     */
    protected String notation = NOTATION_EDEFAULT;

    /**
     * The default value of the '{@link #getLinkType() <em>Link Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLinkType()
     * @generated
     * @ordered
     */
	protected static final DiagramLinkType LINK_TYPE_EDEFAULT = DiagramLinkType.ORTHOGONAL_LITERAL;

    /**
     * The cached value of the '{@link #getLinkType() <em>Link Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLinkType()
     * @generated
     * @ordered
     */
	protected DiagramLinkType linkType = LINK_TYPE_EDEFAULT;

    /**
     * The cached value of the '{@link #getDiagramEntity() <em>Diagram Entity</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDiagramEntity()
     * @generated
     * @ordered
     */
    protected EList diagramEntity = null;

    /**
     * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected EObject target = null;

    /**
     * The cached value of the '{@link #getDiagramLinks() <em>Diagram Links</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDiagramLinks()
     * @generated
     * @ordered
     */
    protected EList diagramLinks = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DiagramImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return DiagramPackage.eINSTANCE.getDiagram();
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
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getNotation() {
        return notation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNotation(String newNotation) {
        String oldNotation = notation;
        notation = newNotation;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM__NOTATION, oldNotation, notation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public DiagramLinkType getLinkType() {
        return linkType;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setLinkType(DiagramLinkType newLinkType) {
        DiagramLinkType oldLinkType = linkType;
        linkType = newLinkType == null ? LINK_TYPE_EDEFAULT : newLinkType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM__LINK_TYPE, oldLinkType, linkType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getDiagramEntity() {
        if (diagramEntity == null) {
            diagramEntity = new EObjectContainmentWithInverseEList(DiagramEntity.class, this, DiagramPackage.DIAGRAM__DIAGRAM_ENTITY, DiagramPackage.DIAGRAM_ENTITY__DIAGRAM);
        }
        return diagramEntity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getTarget() {
        if (target != null && target.eIsProxy()) {
            EObject oldTarget = target;
            target = eResolveProxy((InternalEObject)target);
            if (target != oldTarget) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, DiagramPackage.DIAGRAM__TARGET, oldTarget, target));
            }
        }
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetTarget() {
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTarget(EObject newTarget) {
        EObject oldTarget = target;
        target = newTarget;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM__TARGET, oldTarget, target));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DiagramContainer getDiagramContainer() {
        if (eContainerFeatureID != DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER) return null;
        return (DiagramContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDiagramContainer(DiagramContainer newDiagramContainer) {
        if (newDiagramContainer != eContainer || (eContainerFeatureID != DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER && newDiagramContainer != null)) {
            if (EcoreUtil.isAncestor(this, newDiagramContainer))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newDiagramContainer != null)
                msgs = ((InternalEObject)newDiagramContainer).eInverseAdd(this, DiagramPackage.DIAGRAM_CONTAINER__DIAGRAM, DiagramContainer.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newDiagramContainer, DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER, newDiagramContainer, newDiagramContainer));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getDiagramLinks() {
        if (diagramLinks == null) {
            diagramLinks = new EObjectContainmentWithInverseEList(DiagramLink.class, this, DiagramPackage.DIAGRAM__DIAGRAM_LINKS, DiagramPackage.DIAGRAM_LINK__DIAGRAM);
        }
        return diagramLinks;
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
                case DiagramPackage.DIAGRAM__DIAGRAM_ENTITY:
                    return ((InternalEList)getDiagramEntity()).basicAdd(otherEnd, msgs);
                case DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER, msgs);
                case DiagramPackage.DIAGRAM__DIAGRAM_LINKS:
                    return ((InternalEList)getDiagramLinks()).basicAdd(otherEnd, msgs);
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
                case DiagramPackage.DIAGRAM__DIAGRAM_ENTITY:
                    return ((InternalEList)getDiagramEntity()).basicRemove(otherEnd, msgs);
                case DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER:
                    return eBasicSetContainer(null, DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER, msgs);
                case DiagramPackage.DIAGRAM__DIAGRAM_LINKS:
                    return ((InternalEList)getDiagramLinks()).basicRemove(otherEnd, msgs);
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
                case DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER:
                    return eContainer.eInverseRemove(this, DiagramPackage.DIAGRAM_CONTAINER__DIAGRAM, DiagramContainer.class, msgs);
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
            case DiagramPackage.DIAGRAM__NAME:
                return getName();
            case DiagramPackage.DIAGRAM__TYPE:
                return getType();
            case DiagramPackage.DIAGRAM__NOTATION:
                return getNotation();
            case DiagramPackage.DIAGRAM__LINK_TYPE:
                return getLinkType();
            case DiagramPackage.DIAGRAM__DIAGRAM_ENTITY:
                return getDiagramEntity();
            case DiagramPackage.DIAGRAM__TARGET:
                if (resolve) return getTarget();
                return basicGetTarget();
            case DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER:
                return getDiagramContainer();
            case DiagramPackage.DIAGRAM__DIAGRAM_LINKS:
                return getDiagramLinks();
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
            case DiagramPackage.DIAGRAM__NAME:
                setName((String)newValue);
                return;
            case DiagramPackage.DIAGRAM__TYPE:
                setType((String)newValue);
                return;
            case DiagramPackage.DIAGRAM__NOTATION:
                setNotation((String)newValue);
                return;
            case DiagramPackage.DIAGRAM__LINK_TYPE:
                setLinkType((DiagramLinkType)newValue);
                return;
            case DiagramPackage.DIAGRAM__DIAGRAM_ENTITY:
                getDiagramEntity().clear();
                getDiagramEntity().addAll((Collection)newValue);
                return;
            case DiagramPackage.DIAGRAM__TARGET:
                setTarget((EObject)newValue);
                return;
            case DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER:
                setDiagramContainer((DiagramContainer)newValue);
                return;
            case DiagramPackage.DIAGRAM__DIAGRAM_LINKS:
                getDiagramLinks().clear();
                getDiagramLinks().addAll((Collection)newValue);
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
            case DiagramPackage.DIAGRAM__NAME:
                setName(NAME_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM__NOTATION:
                setNotation(NOTATION_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM__LINK_TYPE:
                setLinkType(LINK_TYPE_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM__DIAGRAM_ENTITY:
                getDiagramEntity().clear();
                return;
            case DiagramPackage.DIAGRAM__TARGET:
                setTarget((EObject)null);
                return;
            case DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER:
                setDiagramContainer((DiagramContainer)null);
                return;
            case DiagramPackage.DIAGRAM__DIAGRAM_LINKS:
                getDiagramLinks().clear();
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
            case DiagramPackage.DIAGRAM__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case DiagramPackage.DIAGRAM__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case DiagramPackage.DIAGRAM__NOTATION:
                return NOTATION_EDEFAULT == null ? notation != null : !NOTATION_EDEFAULT.equals(notation);
            case DiagramPackage.DIAGRAM__LINK_TYPE:
                return linkType != LINK_TYPE_EDEFAULT;
            case DiagramPackage.DIAGRAM__DIAGRAM_ENTITY:
                return diagramEntity != null && !diagramEntity.isEmpty();
            case DiagramPackage.DIAGRAM__TARGET:
                return target != null;
            case DiagramPackage.DIAGRAM__DIAGRAM_CONTAINER:
                return getDiagramContainer() != null;
            case DiagramPackage.DIAGRAM__DIAGRAM_LINKS:
                return diagramLinks != null && !diagramLinks.isEmpty();
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
        result.append(" (type: "); //$NON-NLS-1$
        result.append(type);
        result.append(", notation: "); //$NON-NLS-1$
        result.append(notation);
        result.append(", linkType: "); //$NON-NLS-1$
        result.append(linkType);
        result.append(')');
        return result.toString();
    }

} //DiagramImpl
