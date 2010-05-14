/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.impl.MappingHelperImpl;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.metamodels.transformation.XQueryTransformation;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>XQuery Transformation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.transformation.impl.XQueryTransformationImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class XQueryTransformationImpl extends MappingHelperImpl implements XQueryTransformation {

    /**
     * The default value of the '{@link #getExpression() <em>Expression</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getExpression()
     * @generated
     * @ordered
     */
    protected static final String EXPRESSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getExpression() <em>Expression</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getExpression()
     * @generated
     * @ordered
     */
    protected String expression = EXPRESSION_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected XQueryTransformationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getXQueryTransformation();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getExpression() {
        return expression;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setExpression( String newExpression ) {
        String oldExpression = expression;
        expression = newExpression;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   TransformationPackage.XQUERY_TRANSFORMATION__EXPRESSION,
                                                                   oldExpression, expression));
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
                case TransformationPackage.XQUERY_TRANSFORMATION__MAPPER:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.XQUERY_TRANSFORMATION__MAPPER, msgs);
                case TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN, msgs);
                case TransformationPackage.XQUERY_TRANSFORMATION__NESTED:
                    return ((InternalEList)getNested()).basicAdd(otherEnd, msgs);
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
                case TransformationPackage.XQUERY_TRANSFORMATION__MAPPER:
                    return eBasicSetContainer(null, TransformationPackage.XQUERY_TRANSFORMATION__MAPPER, msgs);
                case TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN:
                    return eBasicSetContainer(null, TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN, msgs);
                case TransformationPackage.XQUERY_TRANSFORMATION__NESTED:
                    return ((InternalEList)getNested()).basicRemove(otherEnd, msgs);
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
                case TransformationPackage.XQUERY_TRANSFORMATION__MAPPER:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING__HELPER, Mapping.class, msgs);
                case TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING_HELPER__NESTED, MappingHelper.class, msgs);
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
            case TransformationPackage.XQUERY_TRANSFORMATION__MAPPER:
                return getMapper();
            case TransformationPackage.XQUERY_TRANSFORMATION__HELPED_OBJECT:
                if (resolve) return getHelpedObject();
                return basicGetHelpedObject();
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN:
                return getNestedIn();
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED:
                return getNested();
            case TransformationPackage.XQUERY_TRANSFORMATION__EXPRESSION:
                return getExpression();
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
            case TransformationPackage.XQUERY_TRANSFORMATION__MAPPER:
                setMapper((Mapping)newValue);
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__HELPED_OBJECT:
                setHelpedObject((EObject)newValue);
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN:
                setNestedIn((MappingHelper)newValue);
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED:
                getNested().clear();
                getNested().addAll((Collection)newValue);
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__EXPRESSION:
                setExpression((String)newValue);
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
            case TransformationPackage.XQUERY_TRANSFORMATION__MAPPER:
                setMapper((Mapping)null);
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__HELPED_OBJECT:
                setHelpedObject((EObject)null);
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN:
                setNestedIn((MappingHelper)null);
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED:
                getNested().clear();
                return;
            case TransformationPackage.XQUERY_TRANSFORMATION__EXPRESSION:
                setExpression(EXPRESSION_EDEFAULT);
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
            case TransformationPackage.XQUERY_TRANSFORMATION__MAPPER:
                return getMapper() != null;
            case TransformationPackage.XQUERY_TRANSFORMATION__HELPED_OBJECT:
                return helpedObject != null;
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED_IN:
                return getNestedIn() != null;
            case TransformationPackage.XQUERY_TRANSFORMATION__NESTED:
                return nested != null && !nested.isEmpty();
            case TransformationPackage.XQUERY_TRANSFORMATION__EXPRESSION:
                return EXPRESSION_EDEFAULT == null ? expression != null : !EXPRESSION_EDEFAULT.equals(expression);
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
        result.append(" (expression: "); //$NON-NLS-1$
        result.append(expression);
        result.append(')');
        return result.toString();
    }

} // XQueryTransformationImpl
