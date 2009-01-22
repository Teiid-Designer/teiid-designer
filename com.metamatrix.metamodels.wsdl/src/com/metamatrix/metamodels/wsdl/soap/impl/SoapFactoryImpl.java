/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.metamodels.wsdl.soap.SoapAddress;
import com.metamatrix.metamodels.wsdl.soap.SoapBinding;
import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapFactory;
import com.metamatrix.metamodels.wsdl.soap.SoapFault;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;
import com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault;
import com.metamatrix.metamodels.wsdl.soap.SoapOperation;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapStyleType;
import com.metamatrix.metamodels.wsdl.soap.SoapUseType;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class SoapFactoryImpl extends EFactoryImpl implements SoapFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create( EClass eClass ) {
        switch (eClass.getClassifierID()) {
            case SoapPackage.SOAP_ADDRESS:
                return createSoapAddress();
            case SoapPackage.SOAP_HEADER_FAULT:
                return createSoapHeaderFault();
            case SoapPackage.SOAP_FAULT:
                return createSoapFault();
            case SoapPackage.SOAP_HEADER:
                return createSoapHeader();
            case SoapPackage.SOAP_BODY:
                return createSoapBody();
            case SoapPackage.SOAP_OPERATION:
                return createSoapOperation();
            case SoapPackage.SOAP_BINDING:
                return createSoapBinding();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object createFromString( EDataType eDataType,
                                    String initialValue ) {
        switch (eDataType.getClassifierID()) {
            case SoapPackage.SOAP_STYLE_TYPE: {
                SoapStyleType result = SoapStyleType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case SoapPackage.SOAP_USE_TYPE: {
                SoapUseType result = SoapUseType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String convertToString( EDataType eDataType,
                                   Object instanceValue ) {
        switch (eDataType.getClassifierID()) {
            case SoapPackage.SOAP_STYLE_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case SoapPackage.SOAP_USE_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapAddress createSoapAddress() {
        SoapAddressImpl soapAddress = new SoapAddressImpl();
        return soapAddress;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapHeaderFault createSoapHeaderFault() {
        SoapHeaderFaultImpl soapHeaderFault = new SoapHeaderFaultImpl();
        return soapHeaderFault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapFault createSoapFault() {
        SoapFaultImpl soapFault = new SoapFaultImpl();
        return soapFault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapHeader createSoapHeader() {
        SoapHeaderImpl soapHeader = new SoapHeaderImpl();
        return soapHeader;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapBody createSoapBody() {
        SoapBodyImpl soapBody = new SoapBodyImpl();
        return soapBody;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapOperation createSoapOperation() {
        SoapOperationImpl soapOperation = new SoapOperationImpl();
        return soapOperation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapBinding createSoapBinding() {
        SoapBindingImpl soapBinding = new SoapBindingImpl();
        return soapBinding;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SoapPackage getSoapPackage() {
        return (SoapPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static SoapPackage getPackage() {
        return SoapPackage.eINSTANCE;
    }

} // SoapFactoryImpl
