/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.http.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.wsdl.BindingOperation;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpOperation;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Operation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.http.impl.HttpOperationImpl#getBindingOperation <em>Binding Operation</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.http.impl.HttpOperationImpl#getLocation <em>Location</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class HttpOperationImpl extends EObjectImpl implements HttpOperation {

    /**
     * The default value of the '{@link #getLocation() <em>Location</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getLocation()
     * @generated
     * @ordered
     */
    protected static final String LOCATION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getLocation()
     * @generated
     * @ordered
     */
    protected String location = LOCATION_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected HttpOperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HttpPackage.eINSTANCE.getHttpOperation();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getLocation() {
        return location;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setLocation( String newLocation ) {
        String oldLocation = location;
        location = newLocation;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, HttpPackage.HTTP_OPERATION__LOCATION,
                                                                   oldLocation, location));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public BindingOperation getBindingOperation() {
        if (eContainerFeatureID != HttpPackage.HTTP_OPERATION__BINDING_OPERATION) return null;
        return (BindingOperation)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setBindingOperation( BindingOperation newBindingOperation ) {
        if (newBindingOperation != eContainer
            || (eContainerFeatureID != HttpPackage.HTTP_OPERATION__BINDING_OPERATION && newBindingOperation != null)) {
            if (EcoreUtil.isAncestor(this, newBindingOperation)) throw new IllegalArgumentException(
                                                                                                    "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newBindingOperation != null) msgs = ((InternalEObject)newBindingOperation).eInverseAdd(this,
                                                                                                       WsdlPackage.BINDING_OPERATION__HTTP_OPERATION,
                                                                                                       BindingOperation.class,
                                                                                                       msgs);
            msgs = eBasicSetContainer((InternalEObject)newBindingOperation, HttpPackage.HTTP_OPERATION__BINDING_OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          HttpPackage.HTTP_OPERATION__BINDING_OPERATION,
                                                                          newBindingOperation, newBindingOperation));
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
                case HttpPackage.HTTP_OPERATION__BINDING_OPERATION:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HttpPackage.HTTP_OPERATION__BINDING_OPERATION, msgs);
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
                case HttpPackage.HTTP_OPERATION__BINDING_OPERATION:
                    return eBasicSetContainer(null, HttpPackage.HTTP_OPERATION__BINDING_OPERATION, msgs);
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
                case HttpPackage.HTTP_OPERATION__BINDING_OPERATION:
                    return eContainer.eInverseRemove(this,
                                                     WsdlPackage.BINDING_OPERATION__HTTP_OPERATION,
                                                     BindingOperation.class,
                                                     msgs);
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
            case HttpPackage.HTTP_OPERATION__BINDING_OPERATION:
                return getBindingOperation();
            case HttpPackage.HTTP_OPERATION__LOCATION:
                return getLocation();
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
            case HttpPackage.HTTP_OPERATION__BINDING_OPERATION:
                setBindingOperation((BindingOperation)newValue);
                return;
            case HttpPackage.HTTP_OPERATION__LOCATION:
                setLocation((String)newValue);
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
            case HttpPackage.HTTP_OPERATION__BINDING_OPERATION:
                setBindingOperation((BindingOperation)null);
                return;
            case HttpPackage.HTTP_OPERATION__LOCATION:
                setLocation(LOCATION_EDEFAULT);
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
            case HttpPackage.HTTP_OPERATION__BINDING_OPERATION:
                return getBindingOperation() != null;
            case HttpPackage.HTTP_OPERATION__LOCATION:
                return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
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
        result.append(" (location: "); //$NON-NLS-1$
        result.append(location);
        result.append(')');
        return result.toString();
    }

} // HttpOperationImpl
