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
import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.metamodels.diagram.DiagramPackage;
import com.metamatrix.metamodels.diagram.DiagramPosition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Link</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramLinkImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramLinkImpl#getDiagram <em>Diagram</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramLinkImpl#getRoutePoints <em>Route Points</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DiagramLinkImpl extends AbstractDiagramEntityImpl implements DiagramLink {

    /**
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
	protected static final DiagramLinkType TYPE_EDEFAULT = DiagramLinkType.ORTHOGONAL_LITERAL;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
	protected DiagramLinkType type = TYPE_EDEFAULT;

    /**
     * The cached value of the '{@link #getRoutePoints() <em>Route Points</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRoutePoints()
     * @generated
     * @ordered
     */
    protected EList routePoints = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DiagramLinkImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return DiagramPackage.eINSTANCE.getDiagramLink();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public DiagramLinkType getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setType(DiagramLinkType newType) {
        DiagramLinkType oldType = type;
        type = newType == null ? TYPE_EDEFAULT : newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_LINK__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Diagram getDiagram() {
        if (eContainerFeatureID != DiagramPackage.DIAGRAM_LINK__DIAGRAM) return null;
        return (Diagram)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDiagram(Diagram newDiagram) {
        if (newDiagram != eContainer || (eContainerFeatureID != DiagramPackage.DIAGRAM_LINK__DIAGRAM && newDiagram != null)) {
            if (EcoreUtil.isAncestor(this, newDiagram))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newDiagram != null)
                msgs = ((InternalEObject)newDiagram).eInverseAdd(this, DiagramPackage.DIAGRAM__DIAGRAM_LINKS, Diagram.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newDiagram, DiagramPackage.DIAGRAM_LINK__DIAGRAM, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_LINK__DIAGRAM, newDiagram, newDiagram));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getRoutePoints() {
        if (routePoints == null) {
            routePoints = new EObjectContainmentWithInverseEList(DiagramPosition.class, this, DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS, DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK);
        }
        return routePoints;
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
                case DiagramPackage.DIAGRAM_LINK__DIAGRAM:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, DiagramPackage.DIAGRAM_LINK__DIAGRAM, msgs);
                case DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS:
                    return ((InternalEList)getRoutePoints()).basicAdd(otherEnd, msgs);
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
                case DiagramPackage.DIAGRAM_LINK__DIAGRAM:
                    return eBasicSetContainer(null, DiagramPackage.DIAGRAM_LINK__DIAGRAM, msgs);
                case DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS:
                    return ((InternalEList)getRoutePoints()).basicRemove(otherEnd, msgs);
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
                case DiagramPackage.DIAGRAM_LINK__DIAGRAM:
                    return eContainer.eInverseRemove(this, DiagramPackage.DIAGRAM__DIAGRAM_LINKS, Diagram.class, msgs);
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
            case DiagramPackage.DIAGRAM_LINK__NAME:
                return getName();
            case DiagramPackage.DIAGRAM_LINK__ALIAS:
                return getAlias();
            case DiagramPackage.DIAGRAM_LINK__USER_STRING:
                return getUserString();
            case DiagramPackage.DIAGRAM_LINK__USER_TYPE:
                return getUserType();
            case DiagramPackage.DIAGRAM_LINK__MODEL_OBJECT:
                if (resolve) return getModelObject();
                return basicGetModelObject();
            case DiagramPackage.DIAGRAM_LINK__TYPE:
                return getType();
            case DiagramPackage.DIAGRAM_LINK__DIAGRAM:
                return getDiagram();
            case DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS:
                return getRoutePoints();
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
            case DiagramPackage.DIAGRAM_LINK__NAME:
                setName((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_LINK__ALIAS:
                setAlias((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_LINK__USER_STRING:
                setUserString((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_LINK__USER_TYPE:
                setUserType((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_LINK__MODEL_OBJECT:
                setModelObject((EObject)newValue);
                return;
            case DiagramPackage.DIAGRAM_LINK__TYPE:
                setType((DiagramLinkType)newValue);
                return;
            case DiagramPackage.DIAGRAM_LINK__DIAGRAM:
                setDiagram((Diagram)newValue);
                return;
            case DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS:
                getRoutePoints().clear();
                getRoutePoints().addAll((Collection)newValue);
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
            case DiagramPackage.DIAGRAM_LINK__NAME:
                setName(NAME_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_LINK__ALIAS:
                setAlias(ALIAS_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_LINK__USER_STRING:
                setUserString(USER_STRING_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_LINK__USER_TYPE:
                setUserType(USER_TYPE_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_LINK__MODEL_OBJECT:
                setModelObject((EObject)null);
                return;
            case DiagramPackage.DIAGRAM_LINK__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_LINK__DIAGRAM:
                setDiagram((Diagram)null);
                return;
            case DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS:
                getRoutePoints().clear();
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
            case DiagramPackage.DIAGRAM_LINK__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case DiagramPackage.DIAGRAM_LINK__ALIAS:
                return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
            case DiagramPackage.DIAGRAM_LINK__USER_STRING:
                return USER_STRING_EDEFAULT == null ? userString != null : !USER_STRING_EDEFAULT.equals(userString);
            case DiagramPackage.DIAGRAM_LINK__USER_TYPE:
                return USER_TYPE_EDEFAULT == null ? userType != null : !USER_TYPE_EDEFAULT.equals(userType);
            case DiagramPackage.DIAGRAM_LINK__MODEL_OBJECT:
                return modelObject != null;
            case DiagramPackage.DIAGRAM_LINK__TYPE:
                return type != TYPE_EDEFAULT;
            case DiagramPackage.DIAGRAM_LINK__DIAGRAM:
                return getDiagram() != null;
            case DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS:
                return routePoints != null && !routePoints.isEmpty();
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
        result.append(')');
        return result.toString();
    }

} //DiagramLinkImpl
