/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl.soap.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.teiid.designer.metamodels.wsdl.soap.SoapAddress;
import org.teiid.designer.metamodels.wsdl.soap.SoapBinding;
import org.teiid.designer.metamodels.wsdl.soap.SoapBody;
import org.teiid.designer.metamodels.wsdl.soap.SoapFactory;
import org.teiid.designer.metamodels.wsdl.soap.SoapFault;
import org.teiid.designer.metamodels.wsdl.soap.SoapHeader;
import org.teiid.designer.metamodels.wsdl.soap.SoapHeaderFault;
import org.teiid.designer.metamodels.wsdl.soap.SoapOperation;
import org.teiid.designer.metamodels.wsdl.soap.SoapPackage;
import org.teiid.designer.metamodels.wsdl.soap.SoapStyleType;
import org.teiid.designer.metamodels.wsdl.soap.SoapUseType;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 *
 * @since 8.0
 */
public class SoapFactoryImpl extends EFactoryImpl implements SoapFactory {

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
    @Override
	public SoapAddress createSoapAddress() {
        SoapAddressImpl soapAddress = new SoapAddressImpl();
        return soapAddress;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SoapHeaderFault createSoapHeaderFault() {
        SoapHeaderFaultImpl soapHeaderFault = new SoapHeaderFaultImpl();
        return soapHeaderFault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SoapFault createSoapFault() {
        SoapFaultImpl soapFault = new SoapFaultImpl();
        return soapFault;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SoapHeader createSoapHeader() {
        SoapHeaderImpl soapHeader = new SoapHeaderImpl();
        return soapHeader;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SoapBody createSoapBody() {
        SoapBodyImpl soapBody = new SoapBodyImpl();
        return soapBody;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SoapOperation createSoapOperation() {
        SoapOperationImpl soapOperation = new SoapOperationImpl();
        return soapOperation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SoapBinding createSoapBinding() {
        SoapBindingImpl soapBinding = new SoapBindingImpl();
        return soapBinding;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
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
    public static SoapPackage getPackage() { // NO_UCD
        return SoapPackage.eINSTANCE;
    }

} // SoapFactoryImpl
