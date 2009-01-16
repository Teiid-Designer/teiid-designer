/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.diagram.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.diagram.DiagramPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Entity</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl#getXPosition <em>XPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl#getYPosition <em>YPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl#getHeight <em>Height</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl#getDiagram <em>Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DiagramEntityImpl extends AbstractDiagramEntityImpl implements DiagramEntity {

    /**
     * The default value of the '{@link #getXPosition() <em>XPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getXPosition()
     * @generated
     * @ordered
     */
    protected static final int XPOSITION_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getXPosition() <em>XPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getXPosition()
     * @generated
     * @ordered
     */
    protected int xPosition = XPOSITION_EDEFAULT;

    /**
     * The default value of the '{@link #getYPosition() <em>YPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getYPosition()
     * @generated
     * @ordered
     */
    protected static final int YPOSITION_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getYPosition() <em>YPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getYPosition()
     * @generated
     * @ordered
     */
    protected int yPosition = YPOSITION_EDEFAULT;

    /**
     * The default value of the '{@link #getHeight() <em>Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHeight()
     * @generated
     * @ordered
     */
    protected static final int HEIGHT_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getHeight() <em>Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHeight()
     * @generated
     * @ordered
     */
    protected int height = HEIGHT_EDEFAULT;

    /**
     * The default value of the '{@link #getWidth() <em>Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWidth()
     * @generated
     * @ordered
     */
    protected static final int WIDTH_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getWidth() <em>Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWidth()
     * @generated
     * @ordered
     */
    protected int width = WIDTH_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DiagramEntityImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return DiagramPackage.eINSTANCE.getDiagramEntity();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setXPosition(int newXPosition) {
        int oldXPosition = xPosition;
        xPosition = newXPosition;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_ENTITY__XPOSITION, oldXPosition, xPosition));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setYPosition(int newYPosition) {
        int oldYPosition = yPosition;
        yPosition = newYPosition;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_ENTITY__YPOSITION, oldYPosition, yPosition));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getHeight() {
        return height;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setHeight(int newHeight) {
        int oldHeight = height;
        height = newHeight;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_ENTITY__HEIGHT, oldHeight, height));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getWidth() {
        return width;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWidth(int newWidth) {
        int oldWidth = width;
        width = newWidth;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_ENTITY__WIDTH, oldWidth, width));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Diagram getDiagram() {
        if (eContainerFeatureID != DiagramPackage.DIAGRAM_ENTITY__DIAGRAM) return null;
        return (Diagram)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDiagram(Diagram newDiagram) {
        if (newDiagram != eContainer || (eContainerFeatureID != DiagramPackage.DIAGRAM_ENTITY__DIAGRAM && newDiagram != null)) {
            if (EcoreUtil.isAncestor(this, newDiagram))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newDiagram != null)
                msgs = ((InternalEObject)newDiagram).eInverseAdd(this, DiagramPackage.DIAGRAM__DIAGRAM_ENTITY, Diagram.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newDiagram, DiagramPackage.DIAGRAM_ENTITY__DIAGRAM, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_ENTITY__DIAGRAM, newDiagram, newDiagram));
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
                case DiagramPackage.DIAGRAM_ENTITY__DIAGRAM:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, DiagramPackage.DIAGRAM_ENTITY__DIAGRAM, msgs);
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
                case DiagramPackage.DIAGRAM_ENTITY__DIAGRAM:
                    return eBasicSetContainer(null, DiagramPackage.DIAGRAM_ENTITY__DIAGRAM, msgs);
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
                case DiagramPackage.DIAGRAM_ENTITY__DIAGRAM:
                    return eContainer.eInverseRemove(this, DiagramPackage.DIAGRAM__DIAGRAM_ENTITY, Diagram.class, msgs);
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
            case DiagramPackage.DIAGRAM_ENTITY__NAME:
                return getName();
            case DiagramPackage.DIAGRAM_ENTITY__ALIAS:
                return getAlias();
            case DiagramPackage.DIAGRAM_ENTITY__USER_STRING:
                return getUserString();
            case DiagramPackage.DIAGRAM_ENTITY__USER_TYPE:
                return getUserType();
            case DiagramPackage.DIAGRAM_ENTITY__MODEL_OBJECT:
                if (resolve) return getModelObject();
                return basicGetModelObject();
            case DiagramPackage.DIAGRAM_ENTITY__XPOSITION:
                return new Integer(getXPosition());
            case DiagramPackage.DIAGRAM_ENTITY__YPOSITION:
                return new Integer(getYPosition());
            case DiagramPackage.DIAGRAM_ENTITY__HEIGHT:
                return new Integer(getHeight());
            case DiagramPackage.DIAGRAM_ENTITY__WIDTH:
                return new Integer(getWidth());
            case DiagramPackage.DIAGRAM_ENTITY__DIAGRAM:
                return getDiagram();
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
            case DiagramPackage.DIAGRAM_ENTITY__NAME:
                setName((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__ALIAS:
                setAlias((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__USER_STRING:
                setUserString((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__USER_TYPE:
                setUserType((String)newValue);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__MODEL_OBJECT:
                setModelObject((EObject)newValue);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__XPOSITION:
                setXPosition(((Integer)newValue).intValue());
                return;
            case DiagramPackage.DIAGRAM_ENTITY__YPOSITION:
                setYPosition(((Integer)newValue).intValue());
                return;
            case DiagramPackage.DIAGRAM_ENTITY__HEIGHT:
                setHeight(((Integer)newValue).intValue());
                return;
            case DiagramPackage.DIAGRAM_ENTITY__WIDTH:
                setWidth(((Integer)newValue).intValue());
                return;
            case DiagramPackage.DIAGRAM_ENTITY__DIAGRAM:
                setDiagram((Diagram)newValue);
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
            case DiagramPackage.DIAGRAM_ENTITY__NAME:
                setName(NAME_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__ALIAS:
                setAlias(ALIAS_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__USER_STRING:
                setUserString(USER_STRING_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__USER_TYPE:
                setUserType(USER_TYPE_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__MODEL_OBJECT:
                setModelObject((EObject)null);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__XPOSITION:
                setXPosition(XPOSITION_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__YPOSITION:
                setYPosition(YPOSITION_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__HEIGHT:
                setHeight(HEIGHT_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__WIDTH:
                setWidth(WIDTH_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_ENTITY__DIAGRAM:
                setDiagram((Diagram)null);
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
            case DiagramPackage.DIAGRAM_ENTITY__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case DiagramPackage.DIAGRAM_ENTITY__ALIAS:
                return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
            case DiagramPackage.DIAGRAM_ENTITY__USER_STRING:
                return USER_STRING_EDEFAULT == null ? userString != null : !USER_STRING_EDEFAULT.equals(userString);
            case DiagramPackage.DIAGRAM_ENTITY__USER_TYPE:
                return USER_TYPE_EDEFAULT == null ? userType != null : !USER_TYPE_EDEFAULT.equals(userType);
            case DiagramPackage.DIAGRAM_ENTITY__MODEL_OBJECT:
                return modelObject != null;
            case DiagramPackage.DIAGRAM_ENTITY__XPOSITION:
                return xPosition != XPOSITION_EDEFAULT;
            case DiagramPackage.DIAGRAM_ENTITY__YPOSITION:
                return yPosition != YPOSITION_EDEFAULT;
            case DiagramPackage.DIAGRAM_ENTITY__HEIGHT:
                return height != HEIGHT_EDEFAULT;
            case DiagramPackage.DIAGRAM_ENTITY__WIDTH:
                return width != WIDTH_EDEFAULT;
            case DiagramPackage.DIAGRAM_ENTITY__DIAGRAM:
                return getDiagram() != null;
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
        result.append(" (xPosition: "); //$NON-NLS-1$
        result.append(xPosition);
        result.append(", yPosition: "); //$NON-NLS-1$
        result.append(yPosition);
        result.append(", height: "); //$NON-NLS-1$
        result.append(height);
        result.append(", width: "); //$NON-NLS-1$
        result.append(width);
        result.append(')');
        return result.toString();
    }

} //DiagramEntityImpl
