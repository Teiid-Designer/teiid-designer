/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.webservice.aspects.validation.WebServiceComponentAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Component</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.WebServiceComponentImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class WebServiceComponentImpl extends EObjectImpl implements WebServiceComponent {

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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected WebServiceComponentImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getWebServiceComponent();
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
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   WebServicePackage.WEB_SERVICE_COMPONENT__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public IStatus isValid() {
        // construct a validation context into which we are the rules would write their results
        final ValidationContext validationContext = new ValidationContext();

        // run the name validation rules
        // structural feature is
        final EStructuralFeature structuralFeature = eStaticClass().getEStructuralFeature(WebServicePackage.WEB_SERVICE_COMPONENT__NAME);
        // run the name rule
        WebServiceComponentAspect.NAME_RULE.validate(structuralFeature, this, this.eGet(structuralFeature), validationContext);
        // run the length rule
        WebServiceComponentAspect.LENGTH_RULE.validate(structuralFeature, this, this.eGet(structuralFeature), validationContext);

        return validationContext.getTargetStatus(this);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public IStatus isValidGen() { // NO_UCD
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
    public Object eGet( EStructuralFeature eFeature,
                        boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case WebServicePackage.WEB_SERVICE_COMPONENT__NAME:
                return getName();
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
            case WebServicePackage.WEB_SERVICE_COMPONENT__NAME:
                setName((String)newValue);
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
            case WebServicePackage.WEB_SERVICE_COMPONENT__NAME:
                setName(NAME_EDEFAULT);
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
            case WebServicePackage.WEB_SERVICE_COMPONENT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
        result.append(')');
        return result.toString();
    }

} // WebServiceComponentImpl
